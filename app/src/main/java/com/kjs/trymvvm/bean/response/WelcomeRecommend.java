package com.kjs.trymvvm.bean.response;

/**
 * welcome闪屏推荐
 * Created by yangyan
 * on 2018/4/12.
 */

public class WelcomeRecommend {

	/***
	 * 广告图点击跳转类型
	 * */
	public static final String TYPE_ONE = "0";
	public static final String TYPE_TWO = "1";
	public static final String TYPE_THREE = "2";
	public static final String TYPE_FOURTH = "3";

	private String webface;
	private int recommendId;
	private String type;
	private String endTime;
	private String bookId;
	private String linkUrl;

	/**
	 * 广告图片地址本地缓存
	 **/
	private String webfacelocal;

	public String getWebface() {
		return webface;
	}

	public void setWebface(String webface) {
		this.webface = webface;
	}

	public int getRecommendId() {
		return recommendId;
	}

	public void setRecommendId(int recommendId) {
		this.recommendId = recommendId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}


	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getWebfacelocal() {
		return webfacelocal;
	}

	public void setWebfacelocal(String webfacelocal) {
		this.webfacelocal = webfacelocal;
	}
}
