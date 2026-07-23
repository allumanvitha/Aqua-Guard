package com.aquaguard.presentation.dashboard;

import com.aquaguard.domain.repository.DeviceRepository;
import com.aquaguard.domain.repository.WaterRepository;
import com.aquaguard.domain.usecase.GetLiveDeviceStatusUseCase;
import com.aquaguard.domain.usecase.ToggleValveUseCase;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<DeviceRepository> deviceRepositoryProvider;

  private final Provider<GetLiveDeviceStatusUseCase> getLiveDeviceStatusUseCaseProvider;

  private final Provider<ToggleValveUseCase> toggleValveUseCaseProvider;

  private final Provider<WaterRepository> waterRepositoryProvider;

  public DashboardViewModel_Factory(Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<GetLiveDeviceStatusUseCase> getLiveDeviceStatusUseCaseProvider,
      Provider<ToggleValveUseCase> toggleValveUseCaseProvider,
      Provider<WaterRepository> waterRepositoryProvider) {
    this.deviceRepositoryProvider = deviceRepositoryProvider;
    this.getLiveDeviceStatusUseCaseProvider = getLiveDeviceStatusUseCaseProvider;
    this.toggleValveUseCaseProvider = toggleValveUseCaseProvider;
    this.waterRepositoryProvider = waterRepositoryProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(deviceRepositoryProvider.get(), getLiveDeviceStatusUseCaseProvider.get(), toggleValveUseCaseProvider.get(), waterRepositoryProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<DeviceRepository> deviceRepositoryProvider,
      Provider<GetLiveDeviceStatusUseCase> getLiveDeviceStatusUseCaseProvider,
      Provider<ToggleValveUseCase> toggleValveUseCaseProvider,
      Provider<WaterRepository> waterRepositoryProvider) {
    return new DashboardViewModel_Factory(deviceRepositoryProvider, getLiveDeviceStatusUseCaseProvider, toggleValveUseCaseProvider, waterRepositoryProvider);
  }

  public static DashboardViewModel newInstance(DeviceRepository deviceRepository,
      GetLiveDeviceStatusUseCase getLiveDeviceStatusUseCase, ToggleValveUseCase toggleValveUseCase,
      WaterRepository waterRepository) {
    return new DashboardViewModel(deviceRepository, getLiveDeviceStatusUseCase, toggleValveUseCase, waterRepository);
  }
}
