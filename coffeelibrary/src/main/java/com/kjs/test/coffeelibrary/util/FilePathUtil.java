package com.kjs.test.coffeelibrary.util;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

/**
 * 作者：柯嘉少 on 2019/2/18
 * 邮箱：2449926649@.qq.com
 * 说明：android文件存储路径工具类
 *
 */

public class FilePathUtil {
    public static Context mContext;
    public static FilePathUtil filePathUtil;

    /**
     * 目录存在app的内部存储上,app卸载后,数据会被清空
     * 注意：无须申请权限，可以存放用户敏感数据，当内置存储的空间不足时将被系统自动清除
     * 在6.0以下版本中为/data/data/<package_name>/cache，6.0以上版本中为/data/user/0/<package_name>/cache
     *
     **/
    public final String CACHE_DIR;//= Build.VERSION.SDK_INT>Build.VERSION_CODES.M? mContext.getCacheDir().getPath():mContext.getCacheDir().getPath()+"/";

    /**
     * 目录存在app的内部存储上,app卸载后,数据会被清空
     * 注意：无须申请权限，可以存放用户敏感数据，系统不会自动清除
     * 在6.0以下版本中为/data/data/<package_name>/files，6.0以上版本中为/data/user/0/<package_name>/files
     *
     **/
    public final String FILES_DIR;//= Build.VERSION.SDK_INT>Build.VERSION_CODES.M? mContext.getFilesDir().getPath():mContext.getFilesDir().getPath()+"/";



    /**
     * 目录存在外部SD卡上的,app卸载后,数据会被清空
     * 注意：当API<19(android4.4)时，需要申请权限；当API>=19时，无需申请权限；不要存放用户敏感数据,当存储的空间不足时将被系统自动清除
     * <sdcard_path>/Android/data/<package_name>/cache
    **/
    public final String EXTERNAL_CACHE_DIR;//= mContext.getExternalCacheDir() .getPath();

    /**
     * 目录存在外部SD卡上的,app卸载后,数据会被清空
     * 注意：当API<19(android4.4)时，需要申请权限；当API>=19时，无需申请权限；不要存放用户敏感数据,系统不会自动清除
     * <sdcard_path>/Android/data/<package_name>/files
     **/
    public final String EXTERNAL_FILES_DIR;//= mContext.getExternalFilesDir(null) .getPath();


    /**
     * 存储空间在外置存储中,app卸载后,数据不会被清空
     * 注意：须申请权限；不要存放用户敏感数据,系统不会自动清除,建议路径拼接上rootName（这里已经拼接上）
     * <sdcard_path>就是/storage/emulated/0
     **/
    public String EXTERNAL_STORAGE_DIR;//= Environment.getExternalStorageDirectory().getPath()+mContext.getPackageName()+"/";


    private FilePathUtil(Context context) {
        this.mContext = context;
        CACHE_DIR= (Build.VERSION.SDK_INT>Build.VERSION_CODES.M? mContext.getCacheDir().getPath():mContext.getCacheDir().getPath());
        FILES_DIR= (Build.VERSION.SDK_INT>Build.VERSION_CODES.M? mContext.getFilesDir().getPath()+"/":mContext.getFilesDir().getPath());
        EXTERNAL_CACHE_DIR= mContext.getExternalCacheDir() .getPath();
        EXTERNAL_FILES_DIR= mContext.getExternalFilesDir(null) .getPath();
        EXTERNAL_STORAGE_DIR= Environment.getExternalStorageDirectory().getPath()+"/"+mContext.getPackageName();
    }

    public static FilePathUtil with(Context context){
        if(filePathUtil==null){
            filePathUtil=new FilePathUtil(context);
        }
        return filePathUtil;
    }


}
