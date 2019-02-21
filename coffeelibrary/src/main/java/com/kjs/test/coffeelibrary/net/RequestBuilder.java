package com.kjs.test.coffeelibrary.net;


import com.kjs.test.coffeelibrary.net.rx.RxObservableListener;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;

/**
 *
 */

public class RequestBuilder<T> {

    private ReqType reqType=ReqType.DISK_CACHE_NO_NETWORK_LIST;
    private Class clazz;
    private String url;
    private boolean useWholeUrl=false;
    private String filePath;
    private String fileName;
    private boolean isUserCommonClass=true;
    private HttpType httpType=HttpType.DEFAULT_GET;
    private RxObservableListener<T> rxObservableListener;
    private Map<String, Object> requestParam;
    private MultipartBody.Part part;

    public enum ReqType {
        //没有缓存
        NO_CACHE_MODEL,
        No_CACHE_LIST,
        //自定义磁盘缓存，没有网络返回磁盘缓存，返回List
        DISK_CACHE_NO_NETWORK_LIST,
        //自定义磁盘缓存，没有网络返回磁盘缓存，返回Model
        DISK_CACHE_NO_NETWORK_MODEL,
    }

    public enum HttpType {
        DEFAULT_GET,
        DEFAULT_POST,
        FIELD_MAP_POST,
        ONE_MULTIPART_POST
    }

    public RequestBuilder(RxObservableListener<T> rxObservableListener) {
        this.rxObservableListener = rxObservableListener;
        requestParam = new HashMap<>();
    }


    public RequestBuilder setTransformClass(Class clazz) {
        this.clazz = clazz;
        return this;
    }

    public Class getTransformClass() {
        return clazz;
    }

    public RequestBuilder setAppendUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public boolean isUseWholeUrl() {
        return useWholeUrl;
    }

    public void setWholeUrl(String url) {
        this.useWholeUrl = true;
        this.url=url;
    }

    public RequestBuilder setFilePathAndFileName(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName=fileName;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public RequestBuilder setParam(String key, Object object) {
        requestParam.put(key, object);
        return this;
    }

    public RequestBuilder setRequestParam(Map<String, Object> requestParam) {
        this.requestParam.putAll(requestParam);
        return this;
    }

    public Map<String, Object> getRequestParam() {
        return requestParam;
    }

    public RequestBuilder setHttpTypeAndReqType(HttpType httpType,ReqType reqType) {
        this.httpType = httpType;
        this.reqType = reqType;
        return this;
    }

    public HttpType getHttpType() {
        return httpType;
    }

    public ReqType getReqType() {
        return reqType;
    }

    public MultipartBody.Part getPart() {
        return part;
    }

    public RequestBuilder setPart(MultipartBody.Part part) {
        this.part = part;
        return this;
    }

    public boolean isUserCommonClass() {
        return isUserCommonClass;
    }

    public RequestBuilder setUserCommonClass(boolean userCommonClass) {
        isUserCommonClass = userCommonClass;
        return this;
    }

    public RxObservableListener<T> getRxObservableListener() {
        return rxObservableListener;
    }

}
