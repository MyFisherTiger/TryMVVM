package com.kjs.test.coffeelibrary.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kjs.test.coffeelibrary.base.manager.FragmentUserVisibleController;
import com.kjs.test.coffeelibrary.util.LogUtil;

import java.lang.ref.WeakReference;

/**
 * 作者：柯嘉少 on 2018/12/19 15:44
 * 邮箱：2449926649@qq.com
 * 说明：基类fragment
 **/
public abstract class ABaseFragment extends Fragment implements FragmentUserVisibleController.UserVisibleCallback{

    protected ViewDataBinding mBinding;

    private FragmentUserVisibleController userVisibleController;

    protected View mView;
    private boolean isCreateView;//是否初始化view完成
    private boolean isVisible;//是否处于可见状态
    private boolean isFirstLoad =true;//是否第一次加载
    private boolean isOnResumeLoad=false;//是否每次调用onResume方法时，都尽量懒加载数据
    private String ClazzName=this.getClass().getSimpleName();

    public ABaseFragment() {
        userVisibleController=new FragmentUserVisibleController(this,this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.info(ClazzName+":onAttach");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.info(ClazzName+":onCreateView");
        if(mView==null){
            mView=inflater.inflate(getLayoutResId(),container,false);
        }
        if(mBinding==null){
            mBinding= DataBindingUtil.bind(mView);
            mBinding.setLifecycleOwner(this);
        }

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.info(ClazzName+":onViewCreated");
        initViewModelBinding();
        observeForRefreshUI();
        init();
        if(!isCreateView){
            isCreateView=true;
            checkLazyLoadData();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.info(ClazzName+":onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.info(ClazzName+":onResume");
        checkLazyLoadData();
        //必须在checkLazyLoadData后面,因为checkLazyLoadData里面设置了isFirstLoad=false
        if(isOnResumeLoad){
            isFirstLoad=true;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.info(ClazzName+":onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.info(ClazzName+":onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.info(ClazzName+":onDestroyView");
        doDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.info(ClazzName+":onDestroy");
    }

    private void checkLazyLoadData(){
        LogUtil.info(""+ClazzName+":isFirstLoad="+ isFirstLoad +":isCreateView="+isCreateView+":isVisible="+isVisible);
        if(!isFirstLoad ||!isCreateView||!isVisible){
            return;
        }
        LogUtil.info("加载数据("+ClazzName+")");
        isFirstLoad =false;
        lazyLoadData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser){
            isVisible=true;
            checkLazyLoadData();
        }else {
            isVisible=false;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        userVisibleController.setUserVisibleHint(!hidden);

    }

    @Override
    public void setWaitingShowToUser(boolean waitingShowToUser) {
        userVisibleController.setWaitingShowToUser(waitingShowToUser);
    }

    @Override
    public boolean isWaitingShowToUser() {
        return userVisibleController.isWaitingShowToUser();
    }

    @Override
    public boolean isVisibleToUser() {
        return userVisibleController.isVisibleToUser();
    }

    @Override
    public void callSuperSetUserVisibleHint(boolean isVisibleToUser) {
        userVisibleController.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onVisibleToUserChanged(boolean isVisibleToUser, boolean invokeInResumeOrPause) {

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
    protected void setOnResumeLoad(boolean onResumeLoad) {
        isOnResumeLoad = onResumeLoad;
    }

    public abstract int getLayoutResId();

    public abstract void initViewModelBinding();//初始化dataBinding，ViewModel并绑定数据

    public abstract void observeForRefreshUI();//给viewModel的数据绑定观察者，并设置更新页面的方法

    public abstract void init();//初始化页面布局，监听等

    public abstract void lazyLoadData();//懒加载数据

    public abstract void doDestroy();

}
