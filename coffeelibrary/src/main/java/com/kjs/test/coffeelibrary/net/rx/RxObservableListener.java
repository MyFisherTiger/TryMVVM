package com.kjs.test.coffeelibrary.net.rx;

import android.text.TextUtils;

import com.kjs.test.coffeelibrary.mvp.BaseView;
import com.kjs.test.coffeelibrary.net.NetWorkCodeException;

public abstract class RxObservableListener<T> implements ObservableListener<T>{

	protected RxObservableListener(){

	}

	@Override
	public void onComplete() {

	}

	@Override
	public void onError(NetWorkCodeException.ResponseThrowable e) {

	}
}
