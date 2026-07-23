package com.aquaguard.presentation.ai;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AIAssistantViewModel_Factory implements Factory<AIAssistantViewModel> {
  @Override
  public AIAssistantViewModel get() {
    return newInstance();
  }

  public static AIAssistantViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AIAssistantViewModel newInstance() {
    return new AIAssistantViewModel();
  }

  private static final class InstanceHolder {
    private static final AIAssistantViewModel_Factory INSTANCE = new AIAssistantViewModel_Factory();
  }
}
