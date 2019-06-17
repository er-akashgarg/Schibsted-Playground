package com.akashgarg.schibsted.di.component;

import com.akashgarg.schibsted.di.module.ApiModule;
import com.akashgarg.schibsted.di.module.AppModule;
import com.akashgarg.schibsted.ui.activity.MainActivity;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface ApiComponent {
    void inject(MainActivity activity);
}