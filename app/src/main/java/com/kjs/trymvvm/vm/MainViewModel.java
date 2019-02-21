package com.kjs.trymvvm.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.kjs.test.coffeelibrary.mvvm.BaseViewModel;
import com.kjs.trymvvm.app.ApiClient;
import com.kjs.trymvvm.app.ApiUrl;
import com.kjs.trymvvm.bean.MainDataBean;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainViewModel extends BaseViewModel {
    private MutableLiveData<MainDataBean> data;
    private Timer timer=new Timer();

    public LiveData<MainDataBean> getData(){
        if(data==null){
            data=new MutableLiveData<>();
            data.setValue(new MainDataBean(123,"大家好！我来自MainViewModel"));
        }
        return data;
    }


    public void loadData(){
        //如果是异步耗时的使用postValue，而不是setValue
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                data.postValue(new MainDataBean(123,"大家好！我是来自线程的文字"));
                //data.getValue().setContent("大家好！我是来自线程的文字");
            }
        },1000);

    }

    @Override
    protected void doDestroy() {

    }
}
