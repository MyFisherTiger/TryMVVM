package com.kjs.test.coffeelibrary.mvvm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.kjs.test.coffeelibrary.net.rx.RxManager;

/**
 * 作者：柯嘉少 on 2019/1/22 13:55
 * 邮箱：2449926649@qq.com
 * 说明：基类ViewModel
 * 注意：由于viewModel里可能存在多个数据源，因此不对getData及loadData进行封装
 **/
public abstract class BaseViewModel extends ViewModel {
    public RxManager rxManager=new RxManager();

    @Override
    protected void onCleared() {
        super.onCleared();
        if(rxManager!=null){
            rxManager.clear();
            rxManager=null;
        }
        doDestroy();

    }

    /**
     * 相关销毁操作，回收内存
     */
    protected abstract void doDestroy();
}
