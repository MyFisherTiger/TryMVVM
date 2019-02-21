package com.kjs.test.coffeelibrary.base;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.kjs.test.coffeelibrary.R;
import com.kjs.test.coffeelibrary.util.NotQuickerClickListener;

/**
 * 作者：柯嘉少 on 2018/12/19 15:45
 * 邮箱：2449926649@qq.com
 * 说明：
 **/
public abstract class BaseFragmentDialog extends DialogFragment {
    protected Context mContext;
    protected View mView;
    protected OnViewClickListener listener;

    private boolean isFullScreen = true;
    private int mTheme = R.style.fragment_dialog_default_screen;//使用自定义的默认主题
    private int mGravity = Gravity.NO_GRAVITY;
    private int mWidth=WindowManager.LayoutParams.MATCH_PARENT;
    private int mHeight=WindowManager.LayoutParams.MATCH_PARENT;

    public interface OnViewClickListener{
        public void onViewClick(View view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initDialog();
        if (isFullScreen) {
            mTheme = R.style.fragment_dialog_full_screen;
        }

        mView = getActivity().getLayoutInflater().inflate(getLayoutId(),null);
        ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        mView.setOnClickListener(new NotQuickerClickListener() {
            @Override
            public void forbidClick(View view) {
                if(listener!=null){
                    listener.onViewClick(view);
                }
            }
        });

        Dialog dialog = new Dialog(mContext, mTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(mView,params);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(mWidth, mHeight);
        window.setGravity(mGravity);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    /**
     * 显示dialog
     * <p>
     * add的时候是不显示的
     *
     * @param manager
     */
    public void show(FragmentManager manager) {
        if (!isAdded()&&getActivity()!=null&&!getActivity().isFinishing()) {
            super.show(manager, this.getClass().getSimpleName());
        }
    }

    public void dismiss(){
        if (getActivity()!=null&&!getActivity().isFinishing()&&getDialog().isShowing()) {
            super.dismissAllowingStateLoss();
        }
    }

    /**
     * 设置dialog为全屏模式
     *
     * @param fullScreen true全屏模式，false非全屏模式
     */
    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }


    /**
     * 设置dialog的主题风格
     *
     * @param mTheme
     */
    public void setTheme(int mTheme) {
        this.mTheme = mTheme;
    }

    /**
     * 设置dialog宽度
     * @param mWidth
     */
    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    /**
     * 设置dialog长度
     * @param mHeight
     */
    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    /**
     * 设置点击dialog的监听器
     * @param listener
     */
    public void setListener(OnViewClickListener listener) {
        this.listener = listener;
    }

    /**
     * 设置位置Gravity
     * @param mGravity
     */
    public void setGravity(int mGravity){
        this.mGravity=mGravity;
    }

    /**
     * 给dialog设置布局
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始配置dialog
     *
     * 例如：设置dialog是否为全屏，dialog的长宽等
     */
    protected abstract void initDialog();

    /**
     * 初始化dialog
     *
     * 初始化页面布局，数据，监听事件等
     * 注意：必须配置完了dialog即initDialog()后才能执行
     */
    protected abstract void init();

}
