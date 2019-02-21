package com.kjs.trymvvm.ui.testmvvm;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.kjs.trymvvm.R;
import com.kjs.trymvvm.base.BaseActivity;
import com.kjs.trymvvm.bean.MainDataBean;
import com.kjs.trymvvm.databinding.ActivityMainBinding;
import com.kjs.trymvvm.vm.MainViewModel;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding mainBinding;
    private MainViewModel mainViewModel;

    @Override
    public void handleUiMessage(Message msg) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViewModelBinding() {
        mainBinding=(ActivityMainBinding)mBinding;
        mainViewModel = (MainViewModel)getViewModel(MainViewModel.class);
        //绑定数据
        mainBinding.setVm(mainViewModel.getData().getValue());
        initListener();
    }

    @Override
    public void observeForRefreshUI() {
        mainViewModel.getData().observe(this, new Observer<MainDataBean>() {
            @Override
            public void onChanged(@Nullable MainDataBean mainDataBean) {
                Log.e("改变了","---------");
                mainBinding.setVm(mainDataBean);
            }
        });
    }

    @Override
    public void init() {
        mainViewModel.loadData();
    }

    @Override
    public void doDestroy() {

    }

    public void initListener(){
        mainBinding.btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("点击了","---------");
                mainViewModel.loadData();
            }
        });

        mainBinding.btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MyActivity.class));
            }
        });
    }



    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        mainBinding.setLifecycleOwner(this);
        initViewModelBinding();
        initListener();

    }

    public void initViewModelBinding(){
        mainViewModel= ViewModelProviders.of(this).get(MainViewModel.class);
        bean=mainViewModel.getData().getValue();
        mainBinding.setVm(bean);
        refreshUI();
    }

    public void initListener(){
        mainBinding.btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("点击了","---------");
                mainViewModel.loadData();
            }
        });

        mainBinding.btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MyActivity.class));
            }
        });
    }

    public void refreshUI(){
        mainViewModel.getData().observe(this, new Observer<MainDataBean>() {
            @Override
            public void onChanged(@Nullable MainDataBean mainDataBean) {
                Log.e("改变了","---------");
                mainBinding.setVm(mainDataBean);
            }
        });
    }*/
}
