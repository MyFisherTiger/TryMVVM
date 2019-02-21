package com.kjs.trymvvm.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import com.kjs.test.coffeelibrary.util.LogUtil;


/**
 * 应用上下文
 *
 * @author yangyan
 */
public class AppContext {
    private static AppContext appContext;

    /**
     * 默认渠道值
     */
    private static final int DEF_CHANNEL_VALUE = 10000;
    /**
     * 默认注册渠道值
     */
    private static final String DEF_REGISTER_CHANNEL_ID = "10000";

    private String mChannelValue;

    public AppContext() {
    }

    public static AppContext getInstance() {
        if (null == appContext) {
            appContext = new AppContext();
        }
        return appContext;
    }

    /**
     * 获取手机唯一识别码
     *
     * @return
     */
    public String getAndroidId(int bookid) {
        String android_id = Secure.getString(ReaderApplication.getApplication().getContentResolver(), Secure.ANDROID_ID);

        if (android_id == null || android_id == "") {
            android_id = android.os.Build.VERSION.RELEASE + android.os.Build.MODEL + bookid;
        }
        if (TextUtils.isEmpty(android_id)) {
            android_id = "";
        }
        return android_id;
    }

    /**
     * 获取手机的IMEI
     *
     * @return
     */
    public String getAndroidIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) ReaderApplication.getApplication().getSystemService(
                Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission")
        String imei = telephonyManager.getDeviceId();

        if (!TextUtils.isEmpty(imei)) {
            return imei;
        }
        return "this_phone_has_no_imei";
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public String GetMobileVersion() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取系统的版本号
     *
     * @return
     */
    public String GetAndroidVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = ReaderApplication.getApplication().getPackageManager()
                    .getPackageInfo(ReaderApplication.getApplication().getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null) {
            info = new PackageInfo();
        }
        return info;
    }

    /**
     * 获取渠道值
     */
    public String getChannelValue() {
        if (TextUtils.isEmpty(mChannelValue)) {
            ApplicationInfo appInfo = null;
            try {
                appInfo = ReaderApplication.getApplication().getPackageManager().getApplicationInfo(ReaderApplication.getApplication().getPackageName(), PackageManager.GET_META_DATA);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }

            int channelValue = -1;
            if (appInfo != null && appInfo.metaData != null) {
                Object umengChannelObj = appInfo.metaData.get("UMENG_CHANNEL");
                LogUtil.info("[umengChannelObj:%s]" + umengChannelObj);
                if (umengChannelObj != null) {
                    LogUtil.info("umengChannelObj.getClass():%s" + umengChannelObj.getClass());

                    if (umengChannelObj instanceof String) {
                        String umengChannelStr = (String) umengChannelObj;
                        try {
                            //出错则为0
                            channelValue = Integer.parseInt(umengChannelStr);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else if (umengChannelObj instanceof Integer) {
                        channelValue = (int) umengChannelObj;
                    }
                }
            }

            if (channelValue == -1) {
                //错误为-1，则认为是默认值
                channelValue = DEF_CHANNEL_VALUE;
            }
            mChannelValue = channelValue + "";
            LogUtil.info("mChannelValue:%s" + mChannelValue);
        }

        return mChannelValue;
    }


    /**
     * 获取版本号
     *
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();//获取包管理器
        try {
            //通过当前的包名获取包的信息
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);//获取包对象信息
            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取坂本明
     *
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            //第二个参数代表额外的信息，例如获取当前应用中的所有的Activity
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES
            );
            ActivityInfo[] activities = packageInfo.activities;
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
