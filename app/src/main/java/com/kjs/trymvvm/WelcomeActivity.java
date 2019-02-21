package com.kjs.trymvvm;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.kjs.test.coffeelibrary.base.manager.AppManager;
import com.kjs.test.coffeelibrary.base.manager.PermissionManager;
import com.kjs.test.coffeelibrary.net.gson.GsonUtils;
import com.kjs.test.coffeelibrary.repo.manager.FileManager;
import com.kjs.test.coffeelibrary.repo.manager.SPManager;
import com.kjs.test.coffeelibrary.util.LogUtil;
import com.kjs.trymvvm.app.AppConfig;
import com.kjs.trymvvm.base.BaseActivity;
import com.kjs.trymvvm.bean.response.Featured;
import com.kjs.trymvvm.bean.response.VisitorLoginRt;
import com.kjs.trymvvm.databinding.ActivityWelcomeBinding;
import com.kjs.trymvvm.ui.GuideActivity;
import com.kjs.trymvvm.ui.main.IndexActivity;
import com.kjs.trymvvm.util.DeviceUtil;
import com.kjs.trymvvm.vm.VisitorLoginModel;

/**
 * Created by Administrator on 2019/1/30.
 */

public class WelcomeActivity extends BaseActivity {
    private ActivityWelcomeBinding binding;
    private VisitorLoginModel visitorLoginModel;

    private Featured featured;

    //android6.0及以上系统，项目需要动态申请的权限，缺失了会影响应用使用
    private static final String[] NEED_PERMISSION = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE

    };

    @Override
    public void handleUiMessage(Message msg) {
        switch (msg.what) {
            case AppConfig.MSG_START_INDEX:
                int bookCollectSex = SPManager.getInstance().queryByFileNameAndKeyWithSP(SPManager.defaultFileName,AppConfig.BOOK_COLLECT_SEX_INT,Integer.class,0);// (WelcomeActivity.this, AppConfig.BOOK_COLLECT_SEX_INT, 0);
                Class clazz = IndexActivity.class;
                if (bookCollectSex == 0) {
                    clazz = GuideActivity.class;
                }
                Intent mainIntent = new Intent(WelcomeActivity.this, clazz);
                WelcomeActivity.this.startActivity(mainIntent);
                WelcomeActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    public void initViewModelBinding() {
        binding = (ActivityWelcomeBinding) mBinding;
        visitorLoginModel=(VisitorLoginModel)getViewModel(VisitorLoginModel.class);

    }

    @Override
    public void observeForRefreshUI() {
        visitorLoginModel.getData().observe(this, new Observer<VisitorLoginRt>() {
            @Override
            public void onChanged(@Nullable VisitorLoginRt visitorLoginRt) {
                LogUtil.error("gaibiale~~~~~~~~~~~~~"+visitorLoginRt.toString());
            }
        });

    }

    @Override
    public void init() {
        askPermission(NEED_PERMISSION, new OnPermissionRequestListener() {
            @Override
            public void onSuccess() {
                afterGrant();
            }

            @Override
            public void onFailure() {
                new AlertDialog
                        .Builder(WelcomeActivity.this)
                        .setTitle("提示")
                        .setMessage("请到系统设置中开启权限")
                        .setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                PermissionManager.startAppSettings(WelcomeActivity.this);
                                AppManager.getAppManager().App_Exit();

                            }
                        })
                        .setNegativeButton("退出应用", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                AppManager.getAppManager().App_Exit();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }
    /**
     * 获取权限后执行
     * 1.静态权限后（6.0以前）
     * 2.动态获取权限后（6.0以后）
     */
    private void afterGrant() {
        DeviceUtil.getInstance().getMobInfo(this);
        LogUtil.error("safas:::::::"+AppConfig.External_File_Path);
        visitorLoginModel.loadData();
        showFeatured();
    }

    private void showFeatured() {
        String jsonString =FileManager.ReadTxtFile(AppConfig.Net_Visitor_Login_Path +"/"+AppConfig.Net_File_Name);
        LogUtil.error("保存的广告："+jsonString);
        if(TextUtils.isEmpty(jsonString)){
            mUiHandler.sendEmptyMessageDelayed(AppConfig.MSG_START_INDEX,2000);
            return;
        }

        featured= GsonUtils.fromJsonNoCommonClass(jsonString,Featured.class);



    }

    @Override
    public void doDestroy() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppManager.getAppManager().App_Exit();
    }
}
