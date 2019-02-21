package com.kjs.trymvvm.bean.response;

/**
 * Created by Administrator on 2019/1/31.
 */

public class Result<T> {
    public boolean success;
    public String errorMsg;
    public T content;
}
