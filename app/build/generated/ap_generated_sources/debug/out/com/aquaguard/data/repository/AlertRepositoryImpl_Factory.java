package com.aquaguard.data.repository;

import com.aquaguard.data.local.dao.AlertDao;
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
public final class AlertRepositoryImpl_Factory implements Factory<AlertRepositoryImpl> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<AlertDao> alertDaoProvider;

  public AlertRepositoryImpl_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<AlertDao> alertDaoProvider) {
    this.firestoreProvider = firestoreProvider;
    this.alertDaoProvider = alertDaoProvider;
  }

  @Override
  public AlertRepositoryImpl get() {
    return newInstance(firestoreProvider.get(), alertDaoProvider.get());
  }

  public static AlertRepositoryImpl_Factory create(Provider<FirebaseFirestore> firestoreProvider,
      Provider<AlertDao> alertDaoProvider) {
    return new AlertRepositoryImpl_Factory(firestoreProvider, alertDaoProvider);
  }

  public static AlertRepositoryImpl newInstance(FirebaseFirestore firestore, AlertDao alertDao) {
    return new AlertRepositoryImpl(firestore, alertDao);
  }
}
