package com.aquaguard.presentation.analytics;

import com.aquaguard.domain.repository.DeviceRepository;
import com.aquaguard.domain.repository.WaterRepository;
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
public final class AnalyticsViewModel_Factory implements Factory<AnalyticsViewModel> {
  private final Provider<DeviceRepository> deviceRepositoryProvider;

  private final Provider<WaterRepository> waterRepositoryProvider;

  public AnalyticsViewModel_Factory(Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<WaterRepository> waterRepositoryProvider) {
    this.deviceRepositoryProvider = deviceRepositoryProvider;
    this.waterRepositoryProvider = waterRepositoryProvider;
  }

  @Override
  public AnalyticsViewModel get() {
    return newInstance(deviceRepositoryProvider.get(), waterRepositoryProvider.get());
  }

  public static AnalyticsViewModel_Factory create(
      Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<WaterRepository> waterRepositoryProvider) {
    return new AnalyticsViewModel_Factory(deviceRepositoryProvider, waterRepositoryProvider);
  }

  public static AnalyticsViewModel newInstance(DeviceRepository deviceRepository,
      WaterRepository waterRepository) {
    return new AnalyticsViewModel(deviceRepository, waterRepository);
  }
}
