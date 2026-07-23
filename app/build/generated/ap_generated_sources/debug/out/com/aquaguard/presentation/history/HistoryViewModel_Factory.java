package com.aquaguard.presentation.history;

import com.aquaguard.domain.repository.DeviceRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<DeviceRepository> deviceRepositoryProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  public HistoryViewModel_Factory(Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    this.deviceRepositoryProvider = deviceRepositoryProvider;
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(deviceRepositoryProvider.get(), firestoreProvider.get());
  }

  public static HistoryViewModel_Factory create(Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    return new HistoryViewModel_Factory(deviceRepositoryProvider, firestoreProvider);
  }

  public static HistoryViewModel newInstance(DeviceRepository deviceRepository,
      FirebaseFirestore firestore) {
    return new HistoryViewModel(deviceRepository, firestore);
  }
}
