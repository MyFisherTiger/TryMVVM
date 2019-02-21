package com.kjs.test.coffeelibrary.repo.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.kjs.test.coffeelibrary.config.CoffeeConfig;

/**
 * 作者：柯嘉少 on 2019/1/2 10:20
 * 邮箱：2449926649@qq.com
 * 说明：SharedPreference存储管理
 *
 * 一般与用户账户关联的数据，建议使用用户id与名称相关联的spFileName进行存储
 * 通用的数据，则不需要关联
 **/
public class SPManager {
    private static SPManager manager;
    private static Context mContext;
    public static final String defaultFileName="DefaultFileName";

    private SPManager(){}

    public static SPManager getInstance(){
        if(manager==null){
            synchronized (SPManager.class){
                if(manager==null){
                    manager=new SPManager();
                    mContext= CoffeeConfig.CONTEXT;
                }
            }
        }
        return manager;
    }

    /**
     * 保存String到sp
     *
     * @param context
     * @param spFileName    sp的名称
     * @param key     存的字段名称
     * @param content 内容
     * @return
     */
    private boolean putString(Context context, String spFileName, String key, String content) {
        if (content != null && spFileName != null && context != null) {
            return getSharedPreferences(context, spFileName)
                    .edit()
                    .putString(key, content)
                    .commit();
        }
        return false;
    }

    /**
     * 保存int到sp
     *
     * @param context
     * @param spFileName
     * @param key     存的字段名称
     * @param content 内容
     * @return
     */
    private boolean putInt(Context context, String spFileName, String key, int content) {
        if (spFileName != null && context != null) {
            return getSharedPreferences(context, spFileName).edit().putInt(key, content).commit();
        }
        return false;
    }

    private boolean putLong(Context context, String spFileName, String key, long value) {
        return notNull(context, spFileName) && getSharedPreferences(context, spFileName).edit().putLong(key, value).commit();
    }

    /**
     * 保存boolean值
     *
     * @param context
     * @param spFileName
     * @param key
     * @param content
     * @return
     */
    private boolean putBoolean(Context context, String spFileName, String key, boolean content) {
        if (context != null) {
            return getSharedPreferences(context, spFileName)
                    .edit()
                    .putBoolean(key, content)
                    .commit();
        }
        return false;
    }

    /**
     * 获取SP中的String内容
     *
     * @return 失败返回默认值defaultValue
     */
    private String getString(Context context, String spFileName, String key, String defaultValue) {
        String result = defaultValue;
        if (context != null && !TextUtils.isEmpty(spFileName) && !TextUtils.isEmpty(key)) {
            result = getSharedPreferences(context, spFileName).getString(key, defaultValue);
        }
        return result;
    }


    /**
     * @param context
     * @param spFileName
     * @param key
     * @param defaultValue
     * @return
     */
    private int getInt(Context context, String spFileName, String key, int defaultValue) {
        if (notNull(context, spFileName)) {
            return getSharedPreferences(context, spFileName).getInt(key, defaultValue);
        }
        return defaultValue;
    }

    /**
     * @param context
     * @param spFileName
     * @param key
     * @param defaultValue
     * @return
     */
    private long getLong(Context context, String spFileName, String key, long defaultValue) {
        if (notNull(context, spFileName)) {
            return getSharedPreferences(context, spFileName).getLong(key, defaultValue);
        }
        return defaultValue;
    }


    /**
     * 获取boolean值
     *
     * @param context
     * @param spFileName
     * @param key
     * @param defaultValue
     * @return
     */
    private boolean getBoolean(Context context, String spFileName, String key,boolean defaultValue) {
        if (context != null && spFileName != null) {
            return getSharedPreferences(context, spFileName).getBoolean(key, defaultValue);
        }
        return false;
    }



    private boolean notNull(Context context, String spFileName) {
        return !TextUtils.isEmpty(spFileName) && context != null;
    }

    private SharedPreferences getSharedPreferences(Context context, String spFileName) {
        return context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
    }

    /**
     * @param fileName
     * @param key
     * @param object
     */
    public void saveByFileNameAndKeyWithSP(String fileName, String key, Object object) {
        if (object instanceof Integer){
            putInt(mContext,fileName,key.toString(),(int)object);
        }else if (object instanceof String){
            putString(mContext,fileName,key.toString(),object.toString());
        }else if (object instanceof Boolean){
            putBoolean(mContext,fileName,key.toString(),(boolean) object);
        }else if(object instanceof Long){
            putLong(mContext,fileName,key.toString(),(long)object);
        }
    }


    /**
     * @param fileName
     * @param key
     * @param clazz
     * @param defaultValue
     * @param <T>
     * @return
     */
    public <T> T queryByFileNameAndKeyWithSP(String fileName, String key, Class<T> clazz,Object defaultValue) {
        if (clazz == Integer.class){
            return (T)Integer.valueOf(getInt(mContext,fileName,key.toString(),(int)defaultValue));
        }else if(clazz == String.class){
            return (T)String.valueOf(getString(mContext,fileName,key.toString(),defaultValue.toString()));
        }else if (clazz == Boolean.class){
            return (T)Boolean.valueOf(getBoolean(mContext,fileName,key.toString(),(boolean)defaultValue));
        }else if(clazz==Long.class){
            return (T)Long.valueOf(getLong(mContext,fileName,key.toString(),(long) defaultValue));
        }
        return null;
    }


}
