package com.aquaguard.data.repository;

import com.aquaguard.data.local.dao.UsageDao;
import com.google.firebase.database.FirebaseDatabase;
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
public final class WaterRepositoryImpl_Factory implements Factory<WaterRepositoryImpl> {
  private final Provider<FirebaseDatabase> firebaseDatabaseProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<UsageDao> usageDaoProvider;

  public WaterRepositoryImpl_Factory(Provider<FirebaseDatabase> firebaseDatabaseProvider,
      Provider<FirebaseFirestore> firestoreProvider, Provider<UsageDao> usageDaoProvider) {
    this.firebaseDatabaseProvider = firebaseDatabaseProvider;
    this.firestoreProvider = firestoreProvider;
    this.usageDaoProvider = usageDaoProvider;
  }

  @Override
  public WaterRepositoryImpl get() {
    return newInstance(firebaseDatabaseProvider.get(), firestoreProvider.get(), usageDaoProvider.get());
  }

  public static WaterRepositoryImpl_Factory create(
      Provider<FirebaseDatabase> firebaseDatabaseProvider,
      Provider<FirebaseFirestore> firestoreProvider, Provider<UsageDao> usageDaoProvider) {
    return new WaterRepositoryImpl_Factory(firebaseDatabaseProvider, firestoreProvider, usageDaoProvider);
  }

  public static WaterRepositoryImpl newInstance(FirebaseDatabase firebaseDatabase,
      FirebaseFirestore firestore, UsageDao usageDao) {
    return new WaterRepositoryImpl(firebaseDatabase, firestore, usageDao);
  }
}
