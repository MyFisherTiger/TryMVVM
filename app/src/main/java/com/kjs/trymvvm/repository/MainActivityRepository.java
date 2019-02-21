package com.kjs.trymvvm.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.kjs.trymvvm.bean.MainDataBean;

//应该使用三级缓存
public class MainActivityRepository {
    private MainDataBean mainDataBean;

    /*public LiveData<MainDataBean> getMainData(){
        final MutableLiveData<MainDataBean> data=new MutableLiveData<>();
        mainDataBean=new MainDataBean(1,"大家好！我是MVVM");
        data.setValue(mainDataBean);
        return data;
    }*/
}
