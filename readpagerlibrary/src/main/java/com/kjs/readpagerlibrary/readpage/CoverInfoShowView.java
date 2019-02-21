package com.kjs.readpagerlibrary.readpage;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mengmengda.reader.R;
import com.mengmengda.reader.been.BookInfo;
import com.mengmengda.reader.util.DisplayUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yangyan
 * on 2018/5/2.
 */

public class CoverInfoShowView {

	@BindView(R.id.rl_BookInfoPanel)
    RelativeLayout rl_BookInfoPanel;
	@BindView(R.id.tv_BookName)
    TextView tv_BookName;
	@BindView(R.id.tv_BookInfoAuthor)
    TextView tv_BookInfoAuthor;
	@BindView(R.id.tv_BookInfoWordCount)
    TextView tv_BookInfoWordCount;
	@BindView(R.id.iv_BookInfoWebFace)
    ImageView iv_BookInfoWebFace;

	@BindView(R.id.tv_BookInfoCopyright1)
    TextView tv_BookInfoCopyright1;
	@BindView(R.id.tv_BookInfoCopyright2)
    TextView tv_BookInfoCopyright2;

	private Context context;
	private View mainView;
	private int mScreenWidth;
	private onHideCoverInfoListener onHideCoverInfoListener;

	public CoverInfoShowView(Context context, View mainView){
		this.context=context;
		this.mainView=mainView;
		ButterKnife.bind(this,this.mainView);
		mScreenWidth = DisplayUtil.getScreenWidthPixels(context);

		rl_BookInfoPanel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideBookInfo();
			}
		});
	}

	public void setBookInfoColor(int textColor, int backColor) {
		rl_BookInfoPanel.setBackgroundColor(backColor);
		tv_BookName.setTextColor(textColor);
		tv_BookInfoAuthor.setTextColor(textColor);
		tv_BookInfoWordCount.setTextColor(textColor);
		tv_BookInfoCopyright1.setTextColor(textColor);
		tv_BookInfoCopyright2.setTextColor(textColor);
	}

	public void setBookInfoColorByImg(int textColor, int backImgResId) {
		rl_BookInfoPanel.setBackgroundResource(backImgResId);
		tv_BookName.setTextColor(textColor);
		tv_BookInfoAuthor.setTextColor(textColor);
		tv_BookInfoWordCount.setTextColor(textColor);
		tv_BookInfoCopyright1.setTextColor(textColor);
		tv_BookInfoCopyright2.setTextColor(textColor);
	}


	public void hideBookInfo() {
		ValueAnimator animator = ValueAnimator.ofFloat(0,-mScreenWidth);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (float) animation.getAnimatedValue();
				rl_BookInfoPanel.setTranslationX(value);
				if(value==-mScreenWidth){
					onHideCoverInfoListener.onHideCoverInfo();
					hideLayout();
				}
			}
		});
		animator.setDuration(500);
		animator.start();
	}

	public void hideLayout(){
		rl_BookInfoPanel.setVisibility(View.GONE);
	}

	public void showBookInfo(BookInfo bookInfo) {
		rl_BookInfoPanel.setVisibility(View.VISIBLE);
		rl_BookInfoPanel.setTranslationX(0);
		Glide.with(context).load(bookInfo.webface).placeholder(R.drawable.book_default).into(iv_BookInfoWebFace);
		tv_BookName.setText(bookInfo.bookName);
		tv_BookInfoAuthor.setText(context.getString(R.string.read_BookInfoAuthorF, bookInfo.author));
		tv_BookInfoWordCount.setText(context.getString(R.string.read_BookInfoWordCountF, bookInfo.wordsum+""));
	}

	public boolean isShowing(){
		if(rl_BookInfoPanel.getVisibility()== View.VISIBLE){
			return true;
		}
		return false;
	}

	public void setOnHideCoverInfoListener(onHideCoverInfoListener onHideCoverInfoListener){
		this.onHideCoverInfoListener=onHideCoverInfoListener;
	}

	public interface onHideCoverInfoListener{
		void onHideCoverInfo();
	}

}
