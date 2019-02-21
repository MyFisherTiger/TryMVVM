package com.kjs.readpagerlibrary.readpage;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mengmengda.reader.R;
import com.mengmengda.reader.activity.APPSettingActivity;
import com.mengmengda.reader.activity.BookReadActivity;
import com.mengmengda.reader.activity.FontSettingActivity;
import com.mengmengda.reader.been.setting.*;
import com.mengmengda.reader.readpage.adapter.ReadBgAdapter;
import com.mengmengda.reader.util.LightUtils;
import com.mengmengda.reader.util.ReadSettingManager;
import com.mengmengda.reader.util.StateListDrawableUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 阅读页设置弹出框
 * Created by yangyan
 * on 2018/2/12.
 */

public class ReadSettingDialog extends PopupWindow {

	@BindView(R.id.read_setting_ll_menu)
    RelativeLayout read_setting_ll_menu;
	@BindView(R.id.seekBar_Light)
    SeekBar seekBar_Light;
	@BindView(R.id.tv_LightSystem)
    TextView tv_LightSystem;
	@BindView(R.id.tv_FontSize)
    TextView tv_FontSize;
	@BindView(R.id.read_setting_rv_bg)
    RecyclerView mRvBg;
	//行距大小
	@BindView(R.id.ib_lineSpaceMax)
	public ImageButton ib_lineSpaceMax;
	@BindView(R.id.ib_lineSpaceSecond)
	public ImageButton ib_lineSpaceSecond;
	@BindView(R.id.ib_lineSpaceThird)
	public ImageButton ib_lineSpaceThird;
	@BindView(R.id.ib_lineSpaceMin)
	public ImageButton ib_lineSpaceMin;
	//设置
	/** 当前字体名称 */
	@BindView(R.id.tv_CurFontName)
    TextView tv_CurFontName;

	private static final int MIN_LIGHT = 1;
	private static final int MAX_LIGHT = 10;
	private View mainView;
	private PageLoader mPageLoader;
	private Activity mActivity;
	private ReadSettingManager mSettingManager;
	private ReadBgAdapter mReadBgAdapter;
	private int mTextSize;
	private int mReadBgTheme;
	private int progress;
	//背景图片
	private List<ReadBg> mBgs = new ArrayList<>();
	private int[] bgSelect = {R.drawable.selector_read_bg_01, R.drawable.selector_read_bg_02, R.drawable.selector_read_bg_03,
			R.drawable.selector_read_bg_04, R.drawable.selector_read_bg_05,
			R.drawable.selector_read_bg_06, R.drawable.selector_read_bg_07, R.drawable.selector_read_bg_08};

	public ReadSettingDialog(Activity activity, PageLoader mPageLoader) {
		mActivity = activity;
		this.mPageLoader = mPageLoader;
		mainView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_read_setting, null);
		setContentView(mainView);
		ButterKnife.bind(this, mainView);
		//设置宽度
		setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
		//设置高度
		setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		//设置背景透明
		setBackgroundDrawable(null);
		mainView.setFocusable(true);
		mainView.setFocusableInTouchMode(true);

		read_setting_ll_menu.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dismiss();
				return false;
			}
		});

		setAnimationStyle(R.style.anim_slide_bottom_in_out);
		initData();
		initWidget();
		initClick();
	}

	private void initData() {
		mSettingManager = ReadSettingManager.getInstance();
		mTextSize = ReadFontSize.getFontSizeBySP(mActivity);
		mReadBgTheme = mSettingManager.getReadBgTheme(mActivity);

		// 设置普通、选中状态的图片
		ib_lineSpaceMax.setImageDrawable(new StateListDrawableUtil()
				.addSelectedState(mActivity, R.drawable.read_setting_line_space_max_select)
				.addNormalState(mActivity, R.drawable.read_setting_line_space_max)
				.create()
		);
		ib_lineSpaceSecond.setImageDrawable(new StateListDrawableUtil()
				.addSelectedState(mActivity, R.drawable.read_setting_line_space_second_select)
				.addNormalState(mActivity, R.drawable.read_setting_line_space_second)
				.create()
		);
		ib_lineSpaceThird.setImageDrawable(new StateListDrawableUtil()
				.addSelectedState(mActivity, R.drawable.read_setting_line_space_third_select)
				.addNormalState(mActivity, R.drawable.read_setting_line_space_third)
				.create()
		);
		ib_lineSpaceMin.setImageDrawable(new StateListDrawableUtil()
				.addSelectedState(mActivity, R.drawable.read_setting_line_space_min_select)
				.addNormalState(mActivity, R.drawable.read_setting_line_space_min)
				.create()
		);

		int lineSpaceType = mSettingManager.getReadLineSpace(mActivity);
		refreshLineSpaceTypeUI(lineSpaceType);


		// 获取当前字体名称
		String fontName = mSettingManager.getReadFont(mActivity);
		tv_CurFontName.setText(fontName);
	}

	private void initWidget() {
		tv_FontSize.setText(mTextSize + "");
		//RecyclerView
		setUpAdapter();
	}

	private void setUpAdapter() {
		for (int i = 0; i < bgSelect.length; i++) {
			ReadBg readBg = new ReadBg();
			readBg.setRes(bgSelect[i]);
			if (i == mReadBgTheme) {
				readBg.setSelect(true);
			}
			mBgs.add(readBg);
		}

		mReadBgAdapter = new ReadBgAdapter(mActivity, mBgs);
		mRvBg.setLayoutManager(new GridLayoutManager(mActivity, 8));
		mRvBg.setAdapter(mReadBgAdapter);
	}

	private void initClick() {

		//0.1~1.0
		seekBar_Light.setProgress(1);
		seekBar_Light.setMax(MAX_LIGHT - MIN_LIGHT);
		//亮度进度条监听
		seekBar_Light.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int value = progress + MIN_LIGHT;

				if (fromUser || !mSettingManager.isBrightnessAuto(mActivity)) {
					//调整亮度并保存到本地
					lightChangeAndSave(value);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		double systemLight = LightUtils.getActivityWindowLight(mActivity);
		double localLight = LightUtils.getLocalLight(mActivity, systemLight);
		int lightProgress = (int) (localLight * 10);
		seekBar_Light.setProgress(lightProgress - MIN_LIGHT);

		// 设置选中与普通状态的文字颜色
		int[][] states = new int[][]{new int[]{android.R.attr.state_selected}, new int[0]};
		int[] colors = new int[]{ContextCompat.getColor(mActivity, R.color.readSetting_Select), ContextCompat.getColor(mActivity, R.color.white)};
		ColorStateList colorStateList = new ColorStateList(states, colors);
		tv_LightSystem.setTextColor(colorStateList);
		if (mSettingManager.isBrightnessAuto(mActivity)) {
			//选中系统亮度，则点击下界面的按钮，触发后面的逻辑
			//默认是没选中的状态
			tv_LightSystem.setSelected(true);
			//开启系统亮度则不可自定义亮度
			seekBar_Light.setEnabled(false);
		}

		mReadBgAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
			@Override
			public void onItemClick(View view, int pos) {
				if (mSettingManager.isNightMode(mActivity)) {
					mSettingManager.setNightMode(mActivity,false);
					((BookReadActivity)mActivity).toggleNightMode();
				}
				notifyAdapter(pos);
				mPageLoader.setBgColor(pos);
			}
		});

	}

	private void lightChangeAndSave(int value) {
		double light = value * 0.1;
		LightUtils.setCurrentActivityLight(mActivity, light);
		LightUtils.saveLocalLight(mActivity, light);
	}

	//事件监听
	@OnClick({R.id.tv_LightSystem, R.id.read_setting_iv_brightness_minus, R.id.read_setting_iv_brightness_plus,R.id.tv_CurFontName,R.id.tv_SettingMore})
	public void onMenuClick(View view) {
		switch (view.getId()) {
			case R.id.tv_LightSystem:
				tv_LightSystem.setSelected(!tv_LightSystem.isSelected());
				boolean isSelected = tv_LightSystem.isSelected();
				//开启系统亮度则不可自定义亮度
				seekBar_Light.setEnabled(!isSelected);
				if (isSelected) {
					//开启系统亮度
					LightUtils.openSystemLight(mActivity);
				} else {
					LightUtils.closeSystemLight(mActivity);
					lightChangeAndSave(seekBar_Light.getProgress() + 1);
				}
				ReadSettingManager.getInstance().setAutoBrightness(mActivity, isSelected);
				break;
			//监听降低亮度按钮
			case R.id.read_setting_iv_brightness_minus:
				if (ReadSettingManager.getInstance().isBrightnessAuto(mActivity)) {
					ReadSettingManager.getInstance().setAutoBrightness(mActivity, false);
					tv_LightSystem.setSelected(false);
					seekBar_Light.setEnabled(true);
				}
				progress = seekBar_Light.getProgress() - 1;
				if (progress < 0) return;
				seekBar_Light.setProgress(progress);
				break;
			//监听提高亮度按钮
			case R.id.read_setting_iv_brightness_plus:
				if (mSettingManager.isBrightnessAuto(mActivity)) {
					mSettingManager.setAutoBrightness(mActivity, false);
					tv_LightSystem.setSelected(false);
					seekBar_Light.setEnabled(true);
				}
				progress = seekBar_Light.getProgress() + 1;
				if (progress > seekBar_Light.getMax()) return;
				seekBar_Light.setProgress(progress);
				break;
			case R.id.tv_CurFontName:
				Intent fontIntent = new Intent(mActivity, FontSettingActivity.class);
				mActivity.startActivityForResult(fontIntent, BookReadActivity.REQUEST_SETTING);
				dismiss();
				break;
			case R.id.tv_SettingMore:
				Intent intent = new Intent(mActivity, APPSettingActivity.class);
				mActivity.startActivityForResult(intent, BookReadActivity.REQUEST_SETTING);
				dismiss();
				break;
		}
	}

	@OnClick({R.id.read_setting_tv_font_minus,R.id.read_setting_tv_font_plus})
	public void onClickFontSizeBar(View view) {
		if (view instanceof TextView) {
			int fontSize = ReadFontSize.getFontSizeBySP(mActivity);
			switch (view.getId()) {
				case R.id.read_setting_tv_font_minus:
					fontSize = fontSize -1;
					break;
				case R.id.read_setting_tv_font_plus:
					fontSize = fontSize + 1;
					break;
			}
			int formatFontSize = ReadFontSize.formatFontSize(fontSize);
			if (fontSize == formatFontSize) {
				tv_FontSize.setText(mActivity.getString(R.string.readSetting_FontSizeFormat, formatFontSize));
				mPageLoader.setTextSize(fontSize);
			}
		}
	}

	@OnClick({R.id.ib_lineSpaceMax, R.id.ib_lineSpaceSecond, R.id.ib_lineSpaceThird, R.id.ib_lineSpaceMin})
	public void onClickLineSpace(View view) {
		if (view instanceof ImageButton) {
			ImageButton imageButton = (ImageButton) view;
			if (!imageButton.isSelected()) {
				int lineSpaceType =mSettingManager.getReadLineSpace(mActivity);
				switch (view.getId()) {
					case R.id.ib_lineSpaceMax:
						lineSpaceType = ReadLineSpace.LINE_SPACE_MAX_COUNT;
						break;
					case R.id.ib_lineSpaceSecond:
						lineSpaceType = ReadLineSpace.LINE_SPACE_SECOND_COUNT;
						break;
					case R.id.ib_lineSpaceThird:
						lineSpaceType = ReadLineSpace.LINE_SPACE_THIRD_COUNT;
						break;
					case R.id.ib_lineSpaceMin:
						lineSpaceType = ReadLineSpace.LINE_SPACE_MIN_COUNT;
						break;
				}
				refreshLineSpaceTypeUI(lineSpaceType);
				mSettingManager.setReadLineSpace(mActivity,lineSpaceType);
				mPageLoader.setReadLineSpace();
			}
		}
	}

	private void refreshLineSpaceTypeUI(int lineSpaceType) {

		ib_lineSpaceMax.setSelected(lineSpaceType == ReadLineSpace.LINE_SPACE_MAX_COUNT);
		ib_lineSpaceSecond.setSelected(lineSpaceType == ReadLineSpace.LINE_SPACE_SECOND_COUNT);
		ib_lineSpaceThird.setSelected(lineSpaceType ==ReadLineSpace.LINE_SPACE_THIRD_COUNT);
		ib_lineSpaceMin.setSelected(lineSpaceType == ReadLineSpace.LINE_SPACE_MIN_COUNT);
	}

	//更新阅读背景
	private void notifyAdapter(int pos) {
		for (int i = 0; i < mBgs.size(); i++) {
			if (i == pos) {
				mBgs.get(i).setSelect(true);
			} else {
				mBgs.get(i).setSelect(false);
			}
		}
		mReadBgAdapter.notifyDataSetChanged();
	}


	public void show(View view){
		// 获取当前字体名称
		String fontName = mSettingManager.getReadFont(mActivity);
		tv_CurFontName.setText(fontName);
		showAsDropDown(view);
		update();
	}
}
