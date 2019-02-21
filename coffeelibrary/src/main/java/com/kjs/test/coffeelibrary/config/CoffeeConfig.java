package com.kjs.test.coffeelibrary.config;

import android.content.Context;

/**
 * 作者：柯嘉少 on 2018/12/20 14:24
 * 邮箱：2449926649@qq.com
 * 说明：
 **/
public class CoffeeConfig {
    /**必传参数**/
    public static Context CONTEXT;//设置Context
    public static String BaseUrl;//基Url
    public static Class MClASS;//设置公共的类


    /**非必传参数**/
    public static long MAX_MEMORY_SIZE=100 * 1024 * 1024;//okHttp与retrofit最大缓存容量（单位B），默认100M
    public static int Time_Out=10;//设置网络连接超时时间（单位秒），默认10秒
    public static String  URL_CACHE;//okHttp与retrofit的磁盘缓存路径（File），必须设置了，该缓存机制才会生效,（默认为空，机制不生效）
    public static long MAX_CACHE_SECONDS=10;//okHttp与retrofit的缓存存留时间，最大4周，默认10秒

}
