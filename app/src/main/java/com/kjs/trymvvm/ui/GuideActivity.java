package com.kjs.trymvvm.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.kjs.test.coffeelibrary.repo.manager.SPManager;
import com.kjs.test.coffeelibrary.util.NotQuickerClickListener;
import com.kjs.trymvvm.R;
import com.kjs.trymvvm.WelcomeActivity;
import com.kjs.trymvvm.app.AppConfig;
import com.kjs.trymvvm.base.BaseActivity;
import com.kjs.trymvvm.databinding.ActivityGuideBinding;
import com.kjs.trymvvm.ui.main.IndexActivity;

public class GuideActivity extends BaseActivity{
    ActivityGuideBinding binding;

    @Override
    public void handleUiMessage(Message msg) {


    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    public void initViewModelBinding() {
        binding = (ActivityGuideBinding) mBinding;

    }

    @Override
    public void observeForRefreshUI() {

    }

    @Override
    public void init() {
        initListener();
    }

    private void initListener(){
        binding.ivBoy.setOnClickListener(new NotQuickerClickListener() {
            @Override
            public void forbidClick(View view) {
                SPManager.getInstance().saveByFileNameAndKeyWithSP(SPManager.defaultFileName, AppConfig.BOOK_COLLECT_SEX_INT, AppConfig.SEX_BOY);
                startActivity(new Intent(GuideActivity.this, IndexActivity.class));
            }
        });

        binding.ivGirl.setOnClickListener(new NotQuickerClickListener() {
            @Override
            public void forbidClick(View view) {
                SPManager.getInstance().saveByFileNameAndKeyWithSP(SPManager.defaultFileName, AppConfig.BOOK_COLLECT_SEX_INT, AppConfig.SEX_GIRL);
                startActivity(new Intent(GuideActivity.this, IndexActivity.class));
            }
        });

        binding.loginPanel.setOnClickListener(new NotQuickerClickListener() {
            @Override
            public void forbidClick(View view) {

            }
        });
    }

    @Override
    public void doDestroy() {

    }
}
