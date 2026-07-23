package com.aquaguard.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class DeviceRepositoryImpl_Factory implements Factory<DeviceRepositoryImpl> {
  private final Provider<FirebaseAuth> firebaseAuthProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  public DeviceRepositoryImpl_Factory(Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    this.firebaseAuthProvider = firebaseAuthProvider;
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public DeviceRepositoryImpl get() {
    return newInstance(firebaseAuthProvider.get(), firestoreProvider.get());
  }

  public static DeviceRepositoryImpl_Factory create(Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    return new DeviceRepositoryImpl_Factory(firebaseAuthProvider, firestoreProvider);
  }

  public static DeviceRepositoryImpl newInstance(FirebaseAuth firebaseAuth,
      FirebaseFirestore firestore) {
    return new DeviceRepositoryImpl(firebaseAuth, firestore);
  }
}
