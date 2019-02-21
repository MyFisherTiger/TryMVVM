package com.kjs.readpagerlibrary.readpage;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.mengmengda.reader.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yangyan
 * on 2018/4/13.
 */

public class BookReadMorePopuWin extends PopupWindow {

	private View mainView;
	private View.OnClickListener onClickListener;

	@BindView(R.id.backBookRel)
    RelativeLayout backBookRel;
	@BindView(R.id.skipDetailRel)
    RelativeLayout bookDetail;
	@BindView(R.id.refreshBookContent)
    RelativeLayout refreshBookContent;

	public BookReadMorePopuWin(Activity paramActivity, View.OnClickListener onClickListener){
		super(paramActivity);
		this.onClickListener=onClickListener;
		//窗口布局
		mainView = LayoutInflater.from(paramActivity).inflate(R.layout.popup_read_more_menu, null);
		setContentView(mainView);
		ButterKnife.bind(this,mainView);
		backBookRel.setOnClickListener(onClickListener);
		bookDetail.setOnClickListener(onClickListener);
		refreshBookContent.setOnClickListener(onClickListener);

		//设置宽度
		setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
		//设置高度
		setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		//设置背景透明
		setBackgroundDrawable(new ColorDrawable(0));
		setFocusable(true);
	}
}
