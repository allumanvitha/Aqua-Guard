package com.aquaguard.data.repository;

import android.content.Context;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import dagger.hilt.android.qualifiers.ApplicationContext;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aquaguard.domain.model.User;
import com.aquaguard.domain.repository.AuthRepository;
import com.aquaguard.domain.repository.RepositoryCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthRepositoryImpl implements AuthRepository {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<User> currentUserLiveData = new MutableLiveData<>();

    @Inject
    public AuthRepositoryImpl(FirebaseAuth firebaseAuth, FirebaseFirestore firestore, @ApplicationContext Context context) {
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;

        // Check and log FirebaseApp initialization
        try {
            FirebaseApp.getInstance();
            Log.e("FirebaseAuth", "FirebaseApp verified initialized successfully.");
        } catch (IllegalStateException e) {
            Log.e("FirebaseAuth", "FirebaseApp has not been initialized. Initializing manually...", e);
            try {
                FirebaseApp.initializeApp(context);
            } catch (Exception ex) {
                Log.e("FirebaseAuth", "Failed to initialize FirebaseApp manually: " + ex.getMessage(), ex);
            }
        }
        Log.e("FirebaseAuth", "FirebaseAuth instance: " + firebaseAuth + ", FirebaseFirestore instance: " + firestore);

        // Automatically create admin credentials on first launch
        android.content.SharedPreferences prefs = context.getSharedPreferences("aquaguard_prefs", Context.MODE_PRIVATE);
        boolean adminCreated = prefs.getBoolean("admin_created_v2", false);
        if (!adminCreated && firebaseAuth.getCurrentUser() == null) {
            firebaseAuth.createUserWithEmailAndPassword(DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASSWORD)
                    .addOnSuccessListener(authResult -> {
                        prefs.edit().putBoolean("admin_created_v2", true).apply();
                        FirebaseUser firebaseUser = authResult.getUser();
                        if (firebaseUser != null) {
                            Map<String, Object> household = new HashMap<>();
                            household.put("familyMembers", 4);
                            household.put("dailyTargetLiters", 300);

                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("uid", firebaseUser.getUid());
                            userMap.put("email", firebaseUser.getEmail());
                            userMap.put("displayName", "Administrator");
                            userMap.put("householdDetails", household);
                            userMap.put("connectedDevices", new ArrayList<String>());

                            firestore.collection("users").document(firebaseUser.getUid()).set(userMap)
                                    .addOnCompleteListener(task -> {
                                        if (!task.isSuccessful()) {
                                            Exception setEx = task.getException();
                                            Log.e("FirebaseAuth", "Silent background Firestore user creation failed: " + (setEx != null ? setEx.getMessage() : "unknown"), setEx);
                                        }
                                        firebaseAuth.signOut();
                                    });
                        } else {
                            firebaseAuth.signOut();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseAuth", "Silent background admin account registration failed: " + e.getMessage() + " / Localized: " + e.getLocalizedMessage(), e);
                        if (e instanceof com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                            prefs.edit().putBoolean("admin_created_v2", true).apply();
                        }
                    });
        }

        this.firebaseAuth.addAuthStateListener(auth -> {
            FirebaseUser firebaseUser = auth.getCurrentUser();
            if (firebaseUser != null) {
                firestore.collection("users").document(firebaseUser.getUid())
                        .get()
                        .addOnSuccessListener(document -> {
                            int familyMembers = 4;
                            int dailyTarget = 300;
                            List<String> devices = new ArrayList<>();

                            if (document.exists()) {
                                Long familyLong = document.getLong("householdDetails.familyMembers");
                                if (familyLong != null) familyMembers = familyLong.intValue();

                                Long targetLong = document.getLong("householdDetails.dailyTargetLiters");
                                if (targetLong != null) dailyTarget = targetLong.intValue();

                                List<?> devList = (List<?>) document.get("connectedDevices");
                                if (devList != null) {
                                    for (Object o : devList) {
                                        if (o instanceof String) {
                                            devices.add((String) o);
                                        }
                                    }
                                }
                            }

                            currentUserLiveData.postValue(new User(
                                    firebaseUser.getUid(),
                                    firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "",
                                    firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Aqua Guard User",
                                    firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null,
                                    familyMembers,
                                    dailyTarget,
                                    devices
                            ));
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirebaseAuth", "Fetching Firestore user profile in AuthStateListener failed: " + e.getMessage(), e);
                            currentUserLiveData.postValue(new User(
                                    firebaseUser.getUid(),
                                    firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "",
                                    firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Aqua Guard User",
                                    firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null,
                                    4,
                                    300,
                                    new ArrayList<>()
                            ));
                        });
            } else {
                currentUserLiveData.postValue(null);
            }
        });
    }

    @Override
    public LiveData<User> getCurrentUser() {
        return currentUserLiveData;
    }

    @Override
    public void signInWithEmail(String email, String password, RepositoryCallback<User> callback) {
        if (DEFAULT_ADMIN_EMAIL.equalsIgnoreCase(email) && DEFAULT_ADMIN_PASSWORD.equals(password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser firebaseUser = authResult.getUser();
                        if (firebaseUser == null) {
                            callback.onFailure(new Exception("Authentication succeeded, but user is null"));
                            return;
                        }
                        fetchFirestoreUser(firebaseUser, callback);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseAuth", "Admin signIn failed: " + e.getMessage() + " / Localized: " + e.getLocalizedMessage(), e);
                        // Admin user might not exist, auto-create
                        signUpWithEmail(email, password, "Administrator", new RepositoryCallback<User>() {
                            @Override
                            public void onSuccess(User result) {
                                callback.onSuccess(result);
                            }

                            @Override
                            public void onFailure(Exception signUpEx) {
                                Log.e("FirebaseAuth", "Admin auto-registration failed: " + signUpEx.getMessage() + " / Localized: " + signUpEx.getLocalizedMessage(), signUpEx);
                                callback.onFailure(signUpEx);
                            }
                        });
                    });
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        callback.onFailure(new Exception("Authentication succeeded, but user is null"));
                        return;
                    }
                    fetchFirestoreUser(firebaseUser, callback);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseAuth", "signInWithEmail failed: " + e.getMessage() + " / Localized: " + e.getLocalizedMessage(), e);
                    callback.onFailure(e);
                });
    }

    private void fetchFirestoreUser(FirebaseUser firebaseUser, RepositoryCallback<User> callback) {
        firestore.collection("users").document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (!userDoc.exists()) {
                        Map<String, Object> household = new HashMap<>();
                        household.put("familyMembers", 4);
                        household.put("dailyTargetLiters", 300);

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("uid", firebaseUser.getUid());
                        userMap.put("email", firebaseUser.getEmail());
                        userMap.put("displayName", firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Aqua Guard User");
                        userMap.put("householdDetails", household);
                        userMap.put("connectedDevices", new ArrayList<String>());

                        firestore.collection("users").document(firebaseUser.getUid()).set(userMap)
                                .addOnSuccessListener(aVoid -> {
                                    callback.onSuccess(new User(
                                            firebaseUser.getUid(),
                                            firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "",
                                            firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Aqua Guard User",
                                            firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null,
                                            4,
                                            300,
                                            new ArrayList<>()
                                    ));
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirebaseAuth", "Creating Firestore user document in fetchFirestoreUser failed: " + e.getMessage() + " / Localized: " + e.getLocalizedMessage(), e);
                                    callback.onFailure(e);
                                });
                    } else {
                        int familyMembers = 4;
                        int dailyTarget = 300;
                        List<String> devices = new ArrayList<>();

                        Long familyLong = userDoc.getLong("householdDetails.familyMembers");
                        if (familyLong != null) familyMembers = familyLong.intValue();

                        Long targetLong = userDoc.getLong("householdDetails.dailyTargetLiters");
                        if (targetLong != null) dailyTarget = targetLong.intValue();

                        List<?> devList = (List<?>) userDoc.get("connectedDevices");
                        if (devList != null) {
                            for (Object o : devList) {
                                if (o instanceof String) {
                                    devices.add((String) o);
                                }
                            }
                        }

                        callback.onSuccess(new User(
                                firebaseUser.getUid(),
                                firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "",
                                firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Aqua Guard User",
                                firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null,
                                familyMembers,
                                dailyTarget,
                                devices
                        ));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseAuth", "Fetching Firestore user profile in fetchFirestoreUser failed: " + e.getMessage() + " / Localized: " + e.getLocalizedMessage(), e);
                    callback.onFailure(e);
                });
    }

    @Override
    public void signUpWithEmail(String email, String password, String displayName, RepositoryCallback<User> callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        callback.onFailure(new Exception("Authentication succeeded, but user is null"));
                        return;
                    }
                    Map<String, Object> household = new HashMap<>();
                    household.put("familyMembers", 4);
                    household.put("dailyTargetLiters", 300);

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("uid", firebaseUser.getUid());
                    userMap.put("email", firebaseUser.getEmail());
                    userMap.put("displayName", displayName);
                    userMap.put("householdDetails", household);
                    userMap.put("connectedDevices", new ArrayList<String>());

                    firestore.collection("users").document(firebaseUser.getUid()).set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                callback.onSuccess(new User(
                                        firebaseUser.getUid(),
                                        firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "",
                                        displayName,
                                        null,
                                        4,
                                        300,
                                        new ArrayList<>()
                                ));
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FirebaseAuth", "Creating Firestore user document in signUpWithEmail failed: " + e.getMessage() + " / Localized: " + e.getLocalizedMessage(), e);
                                callback.onFailure(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseAuth", "createUserWithEmailAndPassword in signUpWithEmail failed: " + e.getMessage() + " / Localized: " + e.getLocalizedMessage(), e);
                    callback.onFailure(e);
                });
    }

    @Override
    public void signOut(RepositoryCallback<Void> callback) {
        try {
            firebaseAuth.signOut();
            callback.onSuccess(null);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    @Override
    public void updateHouseholdDetails(int familyMembers, int dailyTargetLiters, RepositoryCallback<Void> callback) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("householdDetails.familyMembers", familyMembers);
        updates.put("householdDetails.dailyTargetLiters", dailyTargetLiters);

        firestore.collection("users").document(firebaseUser.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void sendPasswordResetEmail(String email, RepositoryCallback<Void> callback) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }
}
