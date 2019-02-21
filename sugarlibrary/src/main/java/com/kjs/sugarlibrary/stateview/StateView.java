package com.kjs.sugarlibrary.stateview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kjs.sugarlibrary.R;
import com.kjs.sugarlibrary.listener.NotQuickerClickListener;


/**
 * 页面状态转换
 * 正在加载=》加载完成（正常，暂无数据，断网）
 */

public class StateView extends LinearLayout {
    public static final int STATE_LOADING = 0;
    public static final int STATE_NORMAL = 1;
    public static final int STATE_NO_DATA = 2;
    public static final int STATE_DISCONNECT = 3;

    private LayoutInflater mInflater;
    private ViewGroup.LayoutParams params;
    private int VIEW_POSITION = 0;//布局添加位置

    private LayoutClickListener listener;


    private View mLoadingView;
    private View mNoDataView;
    private View mDisConnectView;

    public StateView(Context context) {
        super(context);
        initView(context);

    }

    public StateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public StateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mInflater = LayoutInflater.from(context);
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setBackgroundColor(getContext().getResources().getColor(R.color.white));
        setSate(STATE_LOADING);//默认正在加载中
    }


    private void setLoadingView() {
        if (mLoadingView == null) {
            mLoadingView = mInflater.inflate(R.layout.library_view_loading, null);
            addView(mLoadingView, VIEW_POSITION, params);
        }

    }

    private void setNormalView() {
        //do nothing

    }

    private void setNoDataView() {
        if (mNoDataView == null) {
            mNoDataView = mInflater.inflate(R.layout.library_view_no_data, null);
            addView(mNoDataView, VIEW_POSITION, params);

            mNoDataView.setOnClickListener(new NotQuickerClickListener() {
                @Override
                public void forbidClick(View view) {
                    listener.onLayoutClick();
                }
            });
        }

    }

    private void setDisconnectView() {
        if (mDisConnectView == null) {
            mDisConnectView = mInflater.inflate(R.layout.library_view_disconnect, null);
            addView(mDisConnectView, VIEW_POSITION, params);

            mDisConnectView.setOnClickListener(new NotQuickerClickListener() {
                @Override
                public void forbidClick(View view) {
                    listener.onLayoutClick();
                }
            });
        }

    }

    public void setSate(int state) {
        if (state == STATE_NORMAL) {
            this.setVisibility(GONE);
            return;
        } else {
            this.setVisibility(VISIBLE);
        }
        switch (state) {
            case STATE_LOADING:
                setLoadingView();
                break;
            case STATE_NO_DATA:
                setNoDataView();
                break;
            case STATE_DISCONNECT:
                setDisconnectView();
                break;
            default:
                break;
        }

    }

    public interface LayoutClickListener {
        void onLayoutClick();
    }

    ;

    public void setListener(LayoutClickListener listener) {
        this.listener = listener;
    }
}
