package com.kjs.test.coffeelibrary.net;

import android.text.TextUtils;

import com.kjs.test.coffeelibrary.BuildConfig;
import com.kjs.test.coffeelibrary.config.CoffeeConfig;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 *
 */

public class RetrofitManager {

    private static Retrofit mRetrofit;
    private static Retrofit retrofit;

    /**
     * 建议尽量使用这个
     * 每次调用，如果已经存在，不会重新创建
     * @return
     */
    private static Retrofit getRetrofit() {

        if (mRetrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(CoffeeConfig.Time_Out, TimeUnit.SECONDS);
            //如果不是在正式包，添加拦截 打印响应json
            if (BuildConfig.LOG_SHOW) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(logging);
            }

            if (!TextUtils.isEmpty(CoffeeConfig.URL_CACHE) && CoffeeConfig.CONTEXT != null) {
                //设置缓存
                File httpCacheDirectory = new File(CoffeeConfig.URL_CACHE);
                builder.cache(new Cache(httpCacheDirectory, CoffeeConfig.MAX_MEMORY_SIZE));
                builder.addInterceptor(RequestManager.getInterceptor());
            }
            OkHttpClient okHttpClient = builder.build();
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(CoffeeConfig.BaseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
        }

        return mRetrofit;
    }

    /**
     * 使用全新的url，不包含基url
     * 每次调用都会重新创建
     * @return
     */
    private static Retrofit getNewRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(CoffeeConfig.Time_Out, TimeUnit.SECONDS);
        //如果不是在正式包，添加拦截 打印响应json
        if (BuildConfig.LOG_SHOW) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }

        if (!TextUtils.isEmpty(CoffeeConfig.URL_CACHE) && CoffeeConfig.CONTEXT != null) {
            //设置缓存
            File httpCacheDirectory = new File(CoffeeConfig.URL_CACHE);
            builder.cache(new Cache(httpCacheDirectory, CoffeeConfig.MAX_MEMORY_SIZE));
            builder.addInterceptor(RequestManager.getInterceptor());
        }
        OkHttpClient okHttpClient = builder.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(CoffeeConfig.BaseUrl)//如果是完整url会自动覆盖基url
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit;
    }

    /**
     * 接口定义类,获取到可以缓存的retrofit
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T getApiService(Class<T> tClass,boolean useWholeUrl) {
        if(useWholeUrl){
            return getNewRetrofit().create(tClass);
        }else {
            return getRetrofit().create(tClass);
        }
    }
}
