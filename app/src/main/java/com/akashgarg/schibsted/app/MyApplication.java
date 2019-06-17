package com.akashgarg.schibsted.app;


import android.app.Application;
import com.akashgarg.schibsted.di.component.ApiComponent;
import com.akashgarg.schibsted.di.component.DaggerApiComponent;
import com.akashgarg.schibsted.di.module.ApiModule;
import com.akashgarg.schibsted.di.module.AppModule;
import com.akashgarg.schibsted.utils.Urls;

public class MyApplication extends Application {
    private ApiComponent mApiComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mApiComponent = DaggerApiComponent.builder()
                .appModule(new AppModule(this))
                .apiModule(new ApiModule(Urls.BASE_URL))
                .build();
    }

    public ApiComponent getNetComponent() {
        return mApiComponent;
    }
}
