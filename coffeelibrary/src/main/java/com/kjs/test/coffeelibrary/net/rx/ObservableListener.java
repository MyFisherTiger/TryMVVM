package com.kjs.test.coffeelibrary.net.rx;


import com.kjs.test.coffeelibrary.net.NetWorkCodeException;

public interface ObservableListener<T> {
	void onNext(T result);
	void onComplete();
	void onError(NetWorkCodeException.ResponseThrowable e);
}
