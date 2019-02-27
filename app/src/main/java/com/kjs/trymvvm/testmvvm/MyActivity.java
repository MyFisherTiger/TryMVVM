package com.kjs.trymvvm.testmvvm;

import android.os.Message;
import android.support.v4.app.Fragment;


import com.kjs.trymvvm.R;

import com.kjs.trymvvm.testmvvm.adapter.VPFragmentAdapter;
import com.kjs.trymvvm.base.BaseActivity;
import com.kjs.trymvvm.databinding.ActivityMyBinding;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends BaseActivity{
    public ActivityMyBinding binding;
    private VPFragmentAdapter adapter;
    private List<Fragment> fragments=new ArrayList<>();
    private Fragment fragment;

    @Override
    public void handleUiMessage(Message msg) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my;
    }

    @Override
    public void initViewModelBinding() {
        binding=(ActivityMyBinding)mBinding;
        fragments.add(new MyFragment());
        fragments.add(new MyFragment());
        adapter=new VPFragmentAdapter(getSupportFragmentManager(),fragments);
        binding.viewPager.setAdapter(adapter);
    }

    @Override
    public void observeForRefreshUI() {

    }

    @Override
    public void init() {

    }

    /*@Override
    public void requestData() {

    }*/

    @Override
    public void doDestroy() {

    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_my);
        binding.setLifecycleOwner(this);
        initViewModelBinding();
    }

    public void initViewModelBinding(){
        fragments.add(new MyFragment());
        fragments.add(new MyFragment());
        adapter=new VPFragmentAdapter(getSupportFragmentManager(),fragments);
        binding.viewPager.setAdapter(adapter);
    }*/

}
