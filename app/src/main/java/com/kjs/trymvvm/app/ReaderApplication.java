package com.kjs.trymvvm.app;

import android.app.Application;

import com.kjs.test.coffeelibrary.config.CoffeeConfig;
import com.kjs.trymvvm.bean.response.Result;

/**
 * Created by Administrator on 2019/1/24.
 */

public class ReaderApplication extends Application{
    public static ReaderApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
        config();
    }

    public static ReaderApplication getApplication(){
        return application;
    }

    public void config(){
        CoffeeConfig.CONTEXT=this;
        CoffeeConfig.BaseUrl=ApiUrl.BASE_URL;
        CoffeeConfig.MClASS=Result.class;
    }

}
