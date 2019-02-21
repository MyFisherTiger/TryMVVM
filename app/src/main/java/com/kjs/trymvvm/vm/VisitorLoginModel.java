package com.kjs.trymvvm.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.kjs.test.coffeelibrary.mvvm.BaseViewModel;
import com.kjs.test.coffeelibrary.net.RequestBuilder;
import com.kjs.test.coffeelibrary.net.RequestManager;
import com.kjs.test.coffeelibrary.net.rx.RxObservableListener;
import com.kjs.trymvvm.app.ApiClient;
import com.kjs.trymvvm.app.ApiUrl;
import com.kjs.trymvvm.app.AppConfig;
import com.kjs.trymvvm.bean.response.VisitorLoginRt;
import com.kjs.trymvvm.util.DeviceUtil;

/**
 * Created by Administrator on 2019/1/30.
 */

public class VisitorLoginModel extends BaseViewModel{
    private MutableLiveData<VisitorLoginRt> data;

    public LiveData<VisitorLoginRt> getData(){
        if(data==null){
            data=new MutableLiveData<>();
            //data.setValue(new VisitorLoginRt());
        }
        return data;
    }

    public void loadData(){
        RequestBuilder<VisitorLoginRt> requestBuilder=new RequestBuilder<>(new RxObservableListener<VisitorLoginRt>() {
            @Override
            public void onNext(VisitorLoginRt result) {
                result.setErrorMsg("大家好，我改变了");
                data.postValue(result);

            }


        });

        requestBuilder
                .setAppendUrl(ApiUrl.VISITOR_LOGIN)
                .setRequestParam(ApiClient.getRequiredBaseParam())
                .setParam(ApiClient.UUID,DeviceUtil.getInstance().getUUID())
                .setHttpTypeAndReqType(RequestBuilder.HttpType.DEFAULT_GET, RequestBuilder.ReqType.DISK_CACHE_NO_NETWORK_MODEL)
                .setFilePathAndFileName(AppConfig.Net_Visitor_Login_Path,AppConfig.Net_File_Name)
                .setUserCommonClass(false)
                .setTransformClass(VisitorLoginRt.class);

        rxManager.addObserver(RequestManager.getInstance().httpRequest(requestBuilder));

    }


    @Override
    protected void doDestroy() {

    }
}
