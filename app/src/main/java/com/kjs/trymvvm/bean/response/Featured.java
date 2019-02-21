package com.kjs.trymvvm.bean.response;

import java.io.Serializable;

/**
 * 作者：柯嘉少 on 2019/2/18
 * 邮箱：2449926649@.qq.com
 * 说明：
 */

public class Featured implements Serializable{
    /***
     * 广告图点击跳转类型
     * */
    public static final String TYPE_ONE = "0";
    public static final String TYPE_TWO = "1";
    public static final String TYPE_THREE = "2";
    public static final String TYPE_FOURTH = "3";

    /**
     * 广告图片地址
     **/
    public String webface;

    /**
     * 待用参数
     **/
    public int recommendId;

    /**
     * 广告链接地址
     **/
    public String url;

    /**
     * 广告图片地址本地缓存
     **/
    public String webfacelocal;

    /**
     *广告图片显示的截止时间
     * */
    public String endTime;

    /**
     * 广告图片的类型
     *
     * */
    public String type;

    /***
     * 广告图片所代表的书的Id
     * **/
    public String bookId;

    /**
     * 洛米科技返回的链接
     * countUrl  外显闪屏回调地址
     * clickUrl    点击闪屏回调地址
     */

    public String countUrl;
    public String clickUrl;
}
