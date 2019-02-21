package com.kjs.trymvvm.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.kjs.trymvvm.BR;

public class MainDataBean extends BaseObservable {
    @Bindable
    public int userId;
    @Bindable
    public String content;

    public MainDataBean(int userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        notifyPropertyChanged(BR.content);
    }
}
