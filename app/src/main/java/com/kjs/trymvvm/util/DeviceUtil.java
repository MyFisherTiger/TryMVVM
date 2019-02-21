package com.kjs.trymvvm.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.kjs.test.coffeelibrary.repo.manager.SPManager;

import java.util.UUID;

/**
 * 作者：柯嘉少 on 2019/1/31 16:25
 * 邮箱：2449926649@qq.com
 * 说明：
 **/
public class DeviceUtil {
    public static DeviceUtil instance;

    //
    private static String fileName = "Default";
    private static String keyUUID = "UUID";
    private static String keyIME = "mIME";
    private static String keySerialNum = "SerialNum";

    //手机硬件信息
    private static String mUUID = "";
    private static String mIME = "";
    private static String mSIMSerialNum = "";

    private DeviceUtil() {
    }

    public static DeviceUtil getInstance(){
        if(instance==null){
            synchronized (DeviceUtil.class){
                instance=new DeviceUtil();
            }
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    public void getMobInfo(Context mActivity) {
        TelephonyManager tm = (TelephonyManager) mActivity.getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice, tmSerial;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        String androidId = "" + Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uuid = deviceUuid.toString();
        //LogUtils.error("获取的IME与序列号",""+tmDevice+"序列号："+tmSerial);
        setIME(tmDevice);
        setSIMSerialNum(tmSerial);
        setUUID(uuid);

    }

    /**
     * 获取设备的唯一标识，deviceId的进化版UUID
     *
     * @return
     */
    public String getUUID() {
        if (TextUtils.isEmpty(mUUID)) {
            mUUID =SPManager.getInstance().queryByFileNameAndKeyWithSP(fileName,keyUUID,String.class, "");
        }
        return mUUID;
    }

    public void setUUID(String uuid) {
        mUUID = uuid;
        SPManager.getInstance().saveByFileNameAndKeyWithSP(fileName,keyIME,mUUID);
    }

    /**
     * 获取手机IME码
     *
     * @return
     */
    public static String getIME() {
        if (TextUtils.isEmpty(mIME)) {
            mIME =SPManager.getInstance().queryByFileNameAndKeyWithSP(fileName,keyIME,String.class, "");
        }
        return mIME;
    }

    /**
     * 设置手机IME码
     *
     * @param IME
     */
    public void setIME(String IME) {
        mIME = IME;
        SPManager.getInstance().saveByFileNameAndKeyWithSP(fileName,keyIME,IME);
    }

    /**
     * 获取SIM序列号
     *
     * @return
     */
    public String getSIMSerialNum() {
        if (TextUtils.isEmpty(mSIMSerialNum)) {
            mSIMSerialNum =SPManager.getInstance().queryByFileNameAndKeyWithSP(fileName,keySerialNum,String.class, "");
        }
        return mSIMSerialNum;
    }

    /**
     * 设置SIM序列号
     *
     * @param SIMSerialNum
     */
    public void setSIMSerialNum(String SIMSerialNum) {
        mSIMSerialNum = SIMSerialNum;
        SPManager.getInstance().saveByFileNameAndKeyWithSP(fileName,keySerialNum, mIME);
    }


}
