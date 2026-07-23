package com.aquaguard.di;

import com.aquaguard.data.local.AquaGuardDatabase;
import com.aquaguard.data.local.dao.AlertDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideAlertDaoFactory implements Factory<AlertDao> {
  private final Provider<AquaGuardDatabase> dbProvider;

  public AppModule_ProvideAlertDaoFactory(Provider<AquaGuardDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AlertDao get() {
    return provideAlertDao(dbProvider.get());
  }

  public static AppModule_ProvideAlertDaoFactory create(Provider<AquaGuardDatabase> dbProvider) {
    return new AppModule_ProvideAlertDaoFactory(dbProvider);
  }

  public static AlertDao provideAlertDao(AquaGuardDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.provideAlertDao(db));
  }
}
