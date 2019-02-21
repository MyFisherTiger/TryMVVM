package com.kjs.test.coffeelibrary.repo.manager;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

/**
 * 作者：柯嘉少 on 2019/1/2 10:21
 * 邮箱：2449926649@qq.com
 * 说明：提供文件系统存储根路径
 *
 *
 */

public class FilePathProvider {

    /**
     * 应用内部存储空间(/data/data/包名/files)
     *
     * 应用被卸载的时候，目录下的文件会被删除
     *
     * （数据文件私有）文件存储到这个路径下，不需要申请权限。
     *
     * @param context
     * @return
     */
    public static String getFilesDirPath(Context context){
        String rootPath="";
        try {
            rootPath=context.getFilesDir().getPath();
        }catch (Exception e){
            e.printStackTrace();
        }
        return rootPath;
    }


    /**
     * 应用外部存储空间路径(/storage/emulated/0)
     *
     *  卸载不会被删除
     *
     * （数据文件非私有，可以被手机的系统程序访问（如MP3格式的文件，会被手机系统检索出来），同样，该目录下的文件，所有的APP程序也都是可以访问的）
     *  注意：
     *  1.外部存储空间可能处于不可访问状态，或者已经被移除状态，或者存储空间损坏无法访问等问题。可以通过getExternalStorageState()这个方法来判断外部存储空间的状态。
     *  2.在该目录下读写文件，需要获取读写权限
     * @param appName  该应用的名称
     * @return
     */
    public static String getExternalStorageDirectoryPath(String appName){
        String rootPath="";
        try {
            rootPath=Environment.getExternalStorageDirectory().getPath()+"/"+appName+"/";
        }catch (Exception e){
            e.printStackTrace();
        }
        return rootPath;
    }
}
