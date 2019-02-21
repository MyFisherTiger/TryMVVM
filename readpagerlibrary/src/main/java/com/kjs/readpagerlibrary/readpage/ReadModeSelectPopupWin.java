package com.kjs.readpagerlibrary.readpage;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.mengmengda.reader.R;
import com.mengmengda.reader.util.ReadSettingManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 阅读模式选择弹出框
 * Created by yangyan
 * on 2018/2/12.
 */

public class ReadModeSelectPopupWin extends PopupWindow {

	@BindView(R.id.read_mode_scroll)
    RelativeLayout read_mode_scroll;
	@BindView(R.id.read_mode_left_right)
    RelativeLayout read_mode_left_right;
	@BindView(R.id.read_mode_fangzhen)
    RelativeLayout read_mode_fangzhen;

	private Activity activity;
	private PageLoader mPageLoader;
	private View mainView;
	private int mode;

	public ReadModeSelectPopupWin(Activity activity, PageLoader mPageLoader) {
		this.activity = activity;
		this.mPageLoader = mPageLoader;
		mainView = LayoutInflater.from(activity).inflate(R.layout.dialog_read_mode, null);
		setContentView(mainView);
		ButterKnife.bind(this, mainView);
		//设置宽度
		setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
		//设置高度
		setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
		//设置背景透明
		setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mainView.setFocusable(true);
		mainView.setFocusableInTouchMode(true);

		mainView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		init();
	}

	private void init() {
		mode = ReadSettingManager.getInstance().getPageMode(activity);
		setModeSelect(false);
	}

	@OnClick({R.id.read_mode_scroll, R.id.read_mode_left_right, R.id.read_mode_fangzhen})
	public void onMenuClick(View view) {
		switch (view.getId()) {
			case R.id.read_mode_scroll:
				mode=PageView.PAGE_MODE_SCROLL;
				setModeSelect(true);
				break;
			case R.id.read_mode_left_right:
				mode=PageView.PAGE_MODE_COVER;
				setModeSelect(true);
				break;
			case R.id.read_mode_fangzhen:
				mode=PageView.PAGE_MODE_SIMULATION;
				setModeSelect(true);
				break;
		}
	}

	private void setModeSelect(boolean isDismiss){
		switch (mode) {
			case PageView.PAGE_MODE_SIMULATION:
				read_mode_scroll.setSelected(false);
				read_mode_left_right.setSelected(false);
				read_mode_fangzhen.setSelected(true);
				break;
			case PageView.PAGE_MODE_COVER:
				read_mode_scroll.setSelected(false);
				read_mode_left_right.setSelected(true);
				read_mode_fangzhen.setSelected(false);
				break;
			case PageView.PAGE_MODE_SCROLL:
				read_mode_scroll.setSelected(true);
				read_mode_left_right.setSelected(false);
				read_mode_fangzhen.setSelected(false);
				break;
		}

		if(isDismiss){
			mainView.postDelayed(new Runnable() {
				@Override
				public void run() {
					dismiss();
				}
			},500);
		}

	}

	@Override
	public void dismiss() {
		super.dismiss();
		mPageLoader.setPageMode(mode, true);
		ReadSettingManager.getInstance().setModeSelectFirst(activity,false);

	}

	public void show(View view) {
		showAtLocation(view, Gravity.CENTER, 0, 0);
		update();
	}
}
