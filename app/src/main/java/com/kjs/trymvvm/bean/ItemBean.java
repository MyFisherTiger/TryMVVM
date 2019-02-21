package com.kjs.trymvvm.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.kjs.trymvvm.BR;

/**
 * 作者：柯嘉少 on 2019/1/4 17:51
 * 邮箱：2449926649@qq.com
 * 说明：
 **/
public class ItemBean extends BaseObservable {
    public ItemBean(String tips) {
        this.tips = tips;
    }

    @Bindable
    public String tips;

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
        notifyPropertyChanged(BR.tips);
    }
}
