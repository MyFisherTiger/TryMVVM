package com.kjs.test.coffeelibrary.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kjs.test.coffeelibrary.base.manager.AppManager;
import com.kjs.test.coffeelibrary.base.manager.PermissionManager;
import com.kjs.test.coffeelibrary.util.LogUtil;

import java.lang.ref.WeakReference;

/**
 * 作者：柯嘉少 on 2018/12/19 10:05
 * 邮箱：2449926649@qq.com
 * 说明：基类
 **/
public abstract class ABaseActivity extends AppCompatActivity {

    protected Handler mUiHandler = new UiHandler(this);

    protected ViewDataBinding mBinding;

    //private boolean isOnResumeLoad = true;
    //权限
    protected PermissionManager permissionManager;
    protected OnPermissionRequestListener permissionRequestListener;

    protected String ClazzName = this.getClass().getSimpleName();

    public interface OnPermissionRequestListener {
        void onSuccess();//请求权限成功时回调

        void onFailure();//请求权限失败且不再提醒时回调
    };

    private static class UiHandler extends Handler {
        private final WeakReference<ABaseActivity> mActivityReference;

        public UiHandler(ABaseActivity activity) {
            mActivityReference = new WeakReference<ABaseActivity>(activity);
        }

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (mActivityReference.get() != null) {
                mActivityReference.get().handleUiMessage(msg);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.info(ClazzName + ":onCreate");
        AppManager.getAppManager().addActivity(this);
        permissionManager = PermissionManager.with(this);
        if (getLayoutId() != 0) {
            mBinding=DataBindingUtil.setContentView(this,getLayoutId());
            mBinding.setLifecycleOwner(this);
            initViewModelBinding();
            observeForRefreshUI();
            init();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.info(ClazzName + ":onStart");
    }

    /**
     * 若要每次可见时都加载数据，重写该方法设为在super.onResume()后加上setOnResumeLoad(true)；
     */
    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.info(ClazzName + ":onResume");
        //如果是首次加载页面加载数据
        /*if (isOnResumeLoad) {
            requestData();
            isOnResumeLoad = false;
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.info(ClazzName + ":onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.info(ClazzName + ":onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.info(ClazzName + ":onDestroy");
        doDestroy();
        AppManager.getAppManager().removeActivity(this);
    }

    @Override
    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        LogUtil.info(ClazzName + ":setIntent");
        setIntent(newIntent);
    }

    /**
     * 请求权限
     *
     * @param permissions                 要请求权限（String[]）
     * @param onPermissionRequestListener
     */
    public void askPermission(String[] permissions, OnPermissionRequestListener onPermissionRequestListener) {
        permissionManager.setNecessaryPermissions(permissions);
        permissionRequestListener = onPermissionRequestListener;
        if (permissionManager.isLackPermission()) {
            permissionManager.requestPermissions();
        } else {
            permissionRequestListener.onSuccess();
        }
    }


    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int canShowPermissionCode = permissionManager.getShouldShowRequestPermissionsCode();

        if (requestCode == PermissionManager.PERMISSION_REQUEST_CODE) {//PERMISSION_REQUEST_CODE为请求权限的请求值
            //有必须权限选择了禁止
            if (canShowPermissionCode == PermissionManager.EXIST_NECESSARY_PERMISSIONS_PROHIBTED) {
                permissionManager.requestPermissions();
            } //有必须权限选择了禁止不提醒
            else if (canShowPermissionCode == PermissionManager.EXIST_NECESSARY_PERMISSIONS_PROHIBTED_NOT_REMIND) {
                permissionRequestListener.onFailure();
            } else {
                permissionRequestListener.onSuccess();
            }
        }


    }


    /**
     *获取ViewModel
     * @param clazz 类类型，该类型需要继承自ViewModel
     * @return
     */
    protected ViewModel getViewModel(Class clazz){
        ViewModel viewModel= ViewModelProviders.of(this).get(clazz);
        return viewModel;
    }

    /**
     * 设置是否每次调用onResume方法时，都尽量懒加载数据
     * @param onResumeLoad
     */
    /*protected void setOnResumeLoad(boolean onResumeLoad) {
        isOnResumeLoad = onResumeLoad;
    }*/

    public abstract void handleUiMessage(Message msg);

    public abstract int getLayoutId();

    /**
     * 初始化viewModel，将DataBinding绑定viewModel的数据
     */
    public abstract void initViewModelBinding();

    /**
     * 给viewModel的数据绑定观察者，并设置更新页面的方法
     */
    public abstract void observeForRefreshUI();

    /**
     * 进行布局页面的初始化样式，绑定监听等
     */
    public abstract void init();

    //public abstract void requestData();

    public abstract void doDestroy();

}
