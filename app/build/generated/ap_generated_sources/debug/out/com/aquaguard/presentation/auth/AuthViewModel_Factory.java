package com.aquaguard.presentation.auth;

import com.aquaguard.data.local.PreferencesManager;
import com.aquaguard.domain.repository.AuthRepository;
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public AuthViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(authRepositoryProvider.get(), preferencesManagerProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new AuthViewModel_Factory(authRepositoryProvider, preferencesManagerProvider);
  }

  public static AuthViewModel newInstance(AuthRepository authRepository,
      PreferencesManager preferencesManager) {
    return new AuthViewModel(authRepository, preferencesManager);
  }
}
