package com.kjs.trymvvm.app;

import android.text.TextUtils;

import com.kjs.test.coffeelibrary.repo.manager.FilePathProvider;
import com.kjs.test.coffeelibrary.repo.manager.SPManager;
import com.kjs.test.coffeelibrary.util.LogUtil;
import com.kjs.trymvvm.util.EncryptUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 */

public class ApiClient {

    public final static String PASS = "pass"; // 参数加密结果
    public final static String IMEI_TIME = "imeiTime";//app安装后首次打开时间

    public static final String UUID = "UUID";//设备唯一识别码加强版
    public final static String MID = "mid"; // 手机唯一识别码
    public final static String PID = "pid"; // 整型 手机渠道号id,用来区分推荐的渠道，如360, 百度
    public final static String IMEI = "imei"; //手机IMEI号
    public final static String M_VER = "m_ver"; //手机版本号
    public final static String S_VER = "s_ver";//手机系统版本
    public final static String TIMESTAMP = "timestamp"; // 请求时间戳
    public final static String VERSIONNAME = "versionName"; // 当前版本名称
    public final static String VERSIONCODE = "versionCode"; // 当前版本id
    public final static String MD5KEY = "9kus"; // 加密key
    public static final String IS_VISTOR = "isVistor";//是否游客
    public static final String SEX = "sex";
    //public static final String spFileName =""+AppContext.getInstance().getAndroidIMEI();


    /**
     * /**
     * 添加必传参数
     * 用于提交方式为GET的接口
     * mid  唯一识别码
     * pid  渠道号
     * imei 手机imei码
     * m_ver 手机型号
     * s_ver 系统版本号
     * versionName   包的版本名称
     * versionCode   包的版本号
     * pass 参数验证码  (pid+imei+versionCode+timestamp+"9kus")
     *
     * @return params
     */
    public static Map<String, Object> getRequiredBaseParam() {
        Map<String, Object> params = new HashMap<>();
        AppContext appContext = AppContext.getInstance();
        int versionCode = appContext.getPackageInfo().versionCode;
        String mid = appContext.getAndroidId(0);
        String imei = appContext.getAndroidIMEI();
        String m_ver = appContext.GetMobileVersion();
        String pid = appContext.getChannelValue();
        String s_ver = appContext.GetAndroidVersion();
        String versionName = appContext.getPackageInfo().versionName;

        String systemTime = String.valueOf(System.currentTimeMillis());
        String timestamp;
        if (systemTime.length() < 10) {
            timestamp = systemTime;
        } else {
            timestamp = systemTime.substring(0, 10);
        }

        String imeiTime;
        String time = SPManager.getInstance().queryByFileNameAndKeyWithSP(AppConfig.DefaultSPFileName,AppConfig.First_START, String.class, "");
        if (TextUtils.isEmpty(imeiTime = time)) {
            imeiTime = timestamp;
            SPManager.getInstance().saveByFileNameAndKeyWithSP(AppConfig.DefaultSPFileName,AppConfig.First_START, imeiTime);
        }
        LogUtil.info("imeiTime-->" + imeiTime);
        //偏好选择，用于统计
        int attr = 1;
        int isVisitor=1;
        try {

            String md5pass = getPass(pid, imei, versionCode, timestamp);
            params.put(PASS, md5pass);
            params.put(PID, pid);
            params.put(MID, mid);
            params.put(IMEI, imei);
            params.put(TIMESTAMP, timestamp);
            params.put(IMEI_TIME, imeiTime);
            params.put(VERSIONCODE, versionCode);
            params.put(VERSIONNAME, versionName);
            params.put(IS_VISTOR,isVisitor);
            params.put(M_VER, URLEncoder.encode(m_ver, "utf-8"));
            params.put(S_VER, URLEncoder.encode(s_ver, "utf-8"));
            params.put(SEX,attr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return params;
    }

    private static String getPass(String pid, String imei, int versionCode, String timestamp) {
        return (new EncryptUtil()).getMD5Str(pid + imei + versionCode + timestamp + MD5KEY);
    }

    /**
     * 支持与基础域名url拼接的链接组合
     *
     * @param reqUrl
     * @return
     */
    public static String makeUrl(String reqUrl) {
        return String.format(Locale.getDefault(), "%s%s", ApiUrl.BASE_URL, reqUrl);
    }

    /**
     * 支持全新的完整url
     *
     * @param p_url
     * @param params
     * @return
     */
    public static String makeNewURL(String p_url, Map<String, Object> params) {
        StringBuilder url = new StringBuilder(p_url);
        if (url.indexOf("?") < 0)
            url.append('?');

        for (String name : params.keySet()) {
            url.append('&');
            url.append(name);
            url.append('=');
            url.append(String.valueOf(params.get(name)));
        }

        return url.toString().replace("?&", "?");
    }

}
