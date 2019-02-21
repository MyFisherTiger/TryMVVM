package com.kjs.trymvvm.app;

import android.app.Activity;

import com.kjs.test.coffeelibrary.util.FilePathUtil;

/**
 * Created by Administrator on 2019/2/1.
 */

public class AppConfig {
    /**
     * SharePreference存储的键的值
     *
     * 注意：SharePreference的所用键都应该写到这里
     * */
    public static final String  DefaultSPFileName =""+AppContext.getInstance().getAndroidIMEI();

    //0：未知，1：男生，2：女生
    public static final int SEX_UNKNOWN = 0;
    public static final int SEX_BOY = 1;
    public static final int SEX_GIRL = 2;

    //app安装后首次打开时间
    public static final String First_START = "imeiTime";


    //msg
    public static final int MSG_START_INDEX = 0x30;//跳转到Index页面


    //sharePreference键
    public static final String BOOK_COLLECT_SEX_INT="BOOK_COLLECT_SEX_INT";//我的书架默认推荐图书 1:男生 2：女生







    /**
     *  File文件存储路径配置表
     *  注意：手机sdcard，内置存储的路径应该写到这里
     * */
    //(随应用卸载而清掉)
    public static final String External_File_Path =FilePathUtil.with(ReaderApplication.getApplication().getBaseContext()).EXTERNAL_FILES_DIR;

    //永久外部存储路径（不会随应用卸载而清掉）
    public static final String Storage_File_Path =FilePathUtil.with(ReaderApplication.getApplication().getBaseContext()).EXTERNAL_STORAGE_DIR;

    //网络接口返回数据存储的文件名
    public static final String Net_File_Name ="result.json";

    //网络接口返回数据存储路径，与接口url相关联
    public static final String Net_Visitor_Login_Path=AppConfig.External_File_Path+"/" +ApiUrl.VISITOR_LOGIN;
}
