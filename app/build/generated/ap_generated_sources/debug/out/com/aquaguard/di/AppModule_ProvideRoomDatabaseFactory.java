package com.aquaguard.di;

import android.content.Context;
import com.aquaguard.data.local.AquaGuardDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideRoomDatabaseFactory implements Factory<AquaGuardDatabase> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideRoomDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AquaGuardDatabase get() {
    return provideRoomDatabase(contextProvider.get());
  }

  public static AppModule_ProvideRoomDatabaseFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideRoomDatabaseFactory(contextProvider);
  }

  public static AquaGuardDatabase provideRoomDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.provideRoomDatabase(context));
  }
}
