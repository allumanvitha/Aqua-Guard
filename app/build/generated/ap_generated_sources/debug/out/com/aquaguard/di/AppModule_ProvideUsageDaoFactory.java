package com.aquaguard.di;

import com.aquaguard.data.local.AquaGuardDatabase;
import com.aquaguard.data.local.dao.UsageDao;
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
public final class AppModule_ProvideUsageDaoFactory implements Factory<UsageDao> {
  private final Provider<AquaGuardDatabase> dbProvider;

  public AppModule_ProvideUsageDaoFactory(Provider<AquaGuardDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public UsageDao get() {
    return provideUsageDao(dbProvider.get());
  }

  public static AppModule_ProvideUsageDaoFactory create(Provider<AquaGuardDatabase> dbProvider) {
    return new AppModule_ProvideUsageDaoFactory(dbProvider);
  }

  public static UsageDao provideUsageDao(AquaGuardDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.provideUsageDao(db));
  }
}
