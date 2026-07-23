package com.aquaguard.presentation.alerts;

import com.aquaguard.domain.repository.AlertRepository;
import com.aquaguard.domain.repository.DeviceRepository;
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
public final class AlertsViewModel_Factory implements Factory<AlertsViewModel> {
  private final Provider<DeviceRepository> deviceRepositoryProvider;

  private final Provider<AlertRepository> alertRepositoryProvider;

  public AlertsViewModel_Factory(Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<AlertRepository> alertRepositoryProvider) {
    this.deviceRepositoryProvider = deviceRepositoryProvider;
    this.alertRepositoryProvider = alertRepositoryProvider;
  }

  @Override
  public AlertsViewModel get() {
    return newInstance(deviceRepositoryProvider.get(), alertRepositoryProvider.get());
  }

  public static AlertsViewModel_Factory create(Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<AlertRepository> alertRepositoryProvider) {
    return new AlertsViewModel_Factory(deviceRepositoryProvider, alertRepositoryProvider);
  }

  public static AlertsViewModel newInstance(DeviceRepository deviceRepository,
      AlertRepository alertRepository) {
    return new AlertsViewModel(deviceRepository, alertRepository);
  }
}
