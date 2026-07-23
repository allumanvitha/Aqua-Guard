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
public final class ToggleValveUseCase_Factory implements Factory<ToggleValveUseCase> {
  private final Provider<WaterRepository> waterRepositoryProvider;

  public ToggleValveUseCase_Factory(Provider<WaterRepository> waterRepositoryProvider) {
    this.waterRepositoryProvider = waterRepositoryProvider;
  }

  @Override
  public ToggleValveUseCase get() {
    return newInstance(waterRepositoryProvider.get());
  }

  public static ToggleValveUseCase_Factory create(
      Provider<WaterRepository> waterRepositoryProvider) {
    return new ToggleValveUseCase_Factory(waterRepositoryProvider);
  }

  public static ToggleValveUseCase newInstance(WaterRepository waterRepository) {
    return new ToggleValveUseCase(waterRepository);
  }
}
