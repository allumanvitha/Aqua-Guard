package com.aquaguard.domain.usecase;

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
public final class GetLiveDeviceStatusUseCase_Factory implements Factory<GetLiveDeviceStatusUseCase> {
  private final Provider<WaterRepository> waterRepositoryProvider;

  public GetLiveDeviceStatusUseCase_Factory(Provider<WaterRepository> waterRepositoryProvider) {
    this.waterRepositoryProvider = waterRepositoryProvider;
  }

  @Override
  public GetLiveDeviceStatusUseCase get() {
    return newInstance(waterRepositoryProvider.get());
  }

  public static GetLiveDeviceStatusUseCase_Factory create(
      Provider<WaterRepository> waterRepositoryProvider) {
    return new GetLiveDeviceStatusUseCase_Factory(waterRepositoryProvider);
  }

  public static GetLiveDeviceStatusUseCase newInstance(WaterRepository waterRepository) {
    return new GetLiveDeviceStatusUseCase(waterRepository);
  }
}
