package com.kjs.test.coffeelibrary.net;

import android.text.TextUtils;


import com.kjs.test.coffeelibrary.config.CoffeeConfig;
import com.kjs.test.coffeelibrary.net.gson.GsonUtils;
import com.kjs.test.coffeelibrary.net.rx.RxSchedulers;
import com.kjs.test.coffeelibrary.net.rx.RxSubscriber;
import com.kjs.test.coffeelibrary.repo.manager.FileManager;
import com.kjs.test.coffeelibrary.util.NetworkUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 请求管理
 *
 */

public class RequestManager {

    private static RequestManager manager;//考虑同步问题

    private RequestManager() {
    }

    public static RequestManager getInstance() {
        if(manager==null){
            manager=  new RequestManager();
        }
        return manager;
    }

    private <T> DisposableObserver<ResponseBody> request(RequestBuilder<T> builder) {

        if (builder.getReqType() == RequestBuilder.ReqType.NO_CACHE_MODEL) {
            Observable<ResponseBody> observable = getRetrofit(builder);
            return loadOnlyNetWorkModel(builder, observable);
        } else if (builder.getReqType() == RequestBuilder.ReqType.No_CACHE_LIST) {
            Observable<ResponseBody> observable = getRetrofit(builder);
            return loadOnlyNetWorkList(builder, observable);
        } else if (builder.getReqType() == RequestBuilder.ReqType.DISK_CACHE_NO_NETWORK_LIST) {
            Observable<ResponseBody> observable = getRetrofit(builder);
            return loadNoNetWorkWithCacheResultList(builder, observable);
        } else if (builder.getReqType() == RequestBuilder.ReqType.DISK_CACHE_NO_NETWORK_MODEL) {
            Observable<ResponseBody> observable = getRetrofit(builder);
            return loadNoNetWorkWithCacheModel(builder, observable);
        }
        return null;
    }

    private <T> Observable<ResponseBody> getRetrofit(RequestBuilder<T> builder) {
        if (builder.getHttpType() == RequestBuilder.HttpType.DEFAULT_GET) {
            return RetrofitManager.getApiService(RequestService.class,builder.isUseWholeUrl()).getObservableWithQueryMap(builder.getUrl(), builder.getRequestParam());
        } else if (builder.getHttpType() == RequestBuilder.HttpType.DEFAULT_POST) {
            return RetrofitManager.getApiService(RequestService.class,builder.isUseWholeUrl()).getObservableWithQueryMapByPost(builder.getUrl(), builder.getRequestParam());
        } else if (builder.getHttpType() == RequestBuilder.HttpType.FIELD_MAP_POST) {
            return RetrofitManager.getApiService(RequestService.class,builder.isUseWholeUrl()).getObservableWithFieldMap(builder.getUrl(), builder.getRequestParam());
        } else if (builder.getHttpType() == RequestBuilder.HttpType.ONE_MULTIPART_POST) {
            return RetrofitManager.getApiService(RequestService.class,builder.isUseWholeUrl()).getObservableWithImage(builder.getUrl(), builder.getRequestParam(), builder.getPart());
        }
        return null;
    }


    /**
     * ============================自定义的本地json缓存，两种策略===========================================
     */

    /**
     * 只通过网络返回数据，返回list
     */
    private <T> DisposableObserver<ResponseBody> loadOnlyNetWorkList(final RequestBuilder<T> builder, Observable<ResponseBody> observable) {
        DisposableObserver<ResponseBody> observer = observable.compose(RxSchedulers.<ResponseBody>io_main())
                .subscribeWith(new RxSubscriber<ResponseBody>() {
                    @Override
                    public void _onNext(ResponseBody t) {
                        try {
                            T a;
                            if (CoffeeConfig.MClASS == null || !builder.isUserCommonClass()) {
                                a = GsonUtils.fromJsonNoCommonClass(t.string(), builder.getTransformClass());
                            } else {
                                a = GsonUtils.fromJsonArray(t.string(), builder.getTransformClass());
                            }
                            builder.getRxObservableListener().onNext(a);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void _onError(NetWorkCodeException.ResponseThrowable e) {
                        builder.getRxObservableListener().onError(e);
                    }

                    @Override
                    public void _onComplete() {
                        builder.getRxObservableListener().onComplete();
                    }
                });
        return observer;
    }


    /**
     * 只通过网络返回数据，返回Model
     */
    private <T> DisposableObserver<ResponseBody> loadOnlyNetWorkModel(final RequestBuilder<T> builder, Observable<ResponseBody> observable) {
        DisposableObserver<ResponseBody> observer = observable.compose(RxSchedulers.<ResponseBody>io_main())
                .subscribeWith(new RxSubscriber<ResponseBody>() {
                    @Override
                    public void _onNext(ResponseBody t) {
                        try {
                            T a;
                            if (CoffeeConfig.MClASS == null || !builder.isUserCommonClass()) {
                                a = GsonUtils.fromJsonNoCommonClass(t.string(), builder.getTransformClass());
                            } else {
                                a = GsonUtils.fromJsonObject(t.string(), builder.getTransformClass());
                            }
                            builder.getRxObservableListener().onNext(a);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void _onError(NetWorkCodeException.ResponseThrowable e) {
                        builder.getRxObservableListener().onError(e);
                    }

                    @Override
                    public void _onComplete() {
                        builder.getRxObservableListener().onComplete();
                    }
                });
        return observer;
    }


    /**
     * 有网络使用网络数据
     * 没有网络再请求磁盘缓存
     * 返回List
     */
    private <T> DisposableObserver<ResponseBody> loadNoNetWorkWithCacheResultList(final RequestBuilder<T> builder, Observable<ResponseBody> observable) {
        final String[] json = new String[1];
        DisposableObserver<ResponseBody> observer = observable
                .doOnNext(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        json[0] = responseBody.string();
                        FileManager.WriterTxtFile(builder.getFilePath(), builder.getFileName(), json[0], false);
                    }
                })
                .compose(RxSchedulers.<ResponseBody>io_main())
                .subscribeWith(new RxSubscriber<ResponseBody>() {
                    @Override
                    public void _onNext(ResponseBody t) {
                        try {
                            T a;
                            if (CoffeeConfig.MClASS == null || !builder.isUserCommonClass()) {
                                a = GsonUtils.fromJsonNoCommonClass(t.string(), builder.getTransformClass());
                            } else {
                                a = GsonUtils.fromJsonArray(t.string(), builder.getTransformClass());
                            }
                            builder.getRxObservableListener().onNext(a);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void _onError(NetWorkCodeException.ResponseThrowable e) {
                        if (!NetworkUtil.isNetworkConnected(CoffeeConfig.CONTEXT)) {
                            String json = FileManager.ReadTxtFile(builder.getFilePath() + "/" + builder.getFileName());
                            if (!TextUtils.isEmpty(json) && !json.equals("")) {
                                T a;
                                if (CoffeeConfig.MClASS == null) {
                                    a = GsonUtils.fromJsonNoCommonClass(json, builder.getTransformClass());
                                } else if (!builder.isUserCommonClass()) {
                                    a = GsonUtils.fromJsonNoCommonClass(json, builder.getTransformClass());
                                } else {
                                    a = GsonUtils.fromJsonArray(json, builder.getTransformClass());
                                }
                                builder.getRxObservableListener().onNext(a);

                            } else {
                                builder.getRxObservableListener().onError(e);
                            }
                        } else {
                            builder.getRxObservableListener().onError(e);
                        }

                    }

                    @Override
                    public void _onComplete() {
                        builder.getRxObservableListener().onComplete();
                    }
                });

        return observer;
    }

    /**
     * 有网络使用网络数据
     * 没有网络再请求磁盘缓存
     * 返回Model
     */
    private <T> DisposableObserver<ResponseBody> loadNoNetWorkWithCacheModel(final RequestBuilder<T> builder, Observable<ResponseBody> observable) {

        final String[] json = new String[1];

        DisposableObserver<ResponseBody> observer = observable.doOnNext(new Consumer<ResponseBody>() {
            @Override
            public void accept(ResponseBody t) throws Exception {
                json[0] = t.string();
                FileManager.WriterTxtFile(builder.getFilePath(), builder.getFileName(), json[0], false);
            }
        }).compose(RxSchedulers.<ResponseBody>io_main()).subscribeWith(new RxSubscriber<ResponseBody>() {
            @Override
            public void _onNext(ResponseBody t) {
                T a;
                if (CoffeeConfig.MClASS == null||!builder.isUserCommonClass()) {
                    a = GsonUtils.fromJsonNoCommonClass(json[0], builder.getTransformClass());
                } else {
                    a = GsonUtils.fromJsonObject(json[0], builder.getTransformClass());
                }
                builder.getRxObservableListener().onNext(a);
            }

            @Override
            public void _onError(NetWorkCodeException.ResponseThrowable e) {
                if (!NetworkUtil.isNetworkConnected(CoffeeConfig.CONTEXT)) {
                    String json = FileManager.ReadTxtFile(builder.getFilePath() + "/" + builder.getFileName());
                    if (!TextUtils.isEmpty(json) && !json.equals("")) {
                        T a;
                        if (CoffeeConfig.MClASS == null||!builder.isUserCommonClass()) {
                            a = GsonUtils.fromJsonNoCommonClass(json, builder.getTransformClass());
                        } else {
                            a = GsonUtils.fromJsonObject(json, builder.getTransformClass());
                        }
                        builder.getRxObservableListener().onNext(a);
                    }else {
                        builder.getRxObservableListener().onError(e);
                    }
                }else {
                    builder.getRxObservableListener().onError(e);
                }

            }

            @Override
            public void _onComplete() {
                builder.getRxObservableListener().onComplete();
            }
        });

        return observer;
    }


    /**
     * ===============================Retrofit+OkHttp的缓存机制=========================================
     */

    public static Interceptor getInterceptor() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                CacheControl.Builder cacheBuilder = new CacheControl.Builder();
                cacheBuilder.maxAge(0, TimeUnit.SECONDS);
                cacheBuilder.maxStale(365, TimeUnit.DAYS);
                CacheControl cacheControl = cacheBuilder.build();

                Request request = chain.request();
                if (!NetworkUtil.isNetworkConnected(CoffeeConfig.CONTEXT)) {
                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }
                Response originalResponse = chain.proceed(request);
                if (NetworkUtil.isNetworkConnected(CoffeeConfig.CONTEXT)) {
                    int maxAge = 0; // read from cache
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public ,max-age=" + maxAge)
                            .build();
                } else {
                    long maxStale = CoffeeConfig.MAX_CACHE_SECONDS; // tolerate 4-weeks stale
                    return originalResponse.newBuilder()
                            .removeHeader("Pragma")
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                }
            }
        };

        return interceptor;
    }


    public  <T> DisposableObserver<ResponseBody> httpRequest(RequestBuilder<T> requestBuilder) {
        return request(requestBuilder);
    }
}
