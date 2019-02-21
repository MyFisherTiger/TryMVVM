package com.kjs.readpagerlibrary.readpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.mengmengda.reader.been.BookHistory;
import com.mengmengda.reader.been.BookInfo;
import com.mengmengda.reader.been.BookMenu;
import com.mengmengda.reader.been.setting.ReadBg;
import com.mengmengda.reader.been.setting.ReadFontSize;
import com.mengmengda.reader.listener.ReadActionListener;
import com.mengmengda.reader.logic.BookHistoryUtil;
import com.mengmengda.reader.readpage.animation.ScrollPageAnim;
import com.mengmengda.reader.util.DisplayUtil;
import com.mengmengda.reader.util.FileUtils;
import com.mengmengda.reader.util.IOUtils;
import com.mengmengda.reader.util.ReadSettingManager;
import com.mengmengda.reader.util.StringUtils;
import com.mengmengda.reader.util.UI;
import com.mengmengda.reader.widget.LoadingContentView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * yangyan
 * 内容加载类
 */

public abstract class PageLoader {
	private static final String TAG = "PageLoader";

	//当前页面的状态
	public static final int STATUS_LOADING = 1;  //正在加载
	public static final int STATUS_FINISH = 2;   //加载完成
	public static final int STATUS_ERROR = 3;    //加载错误 (一般是网络加载情况)
	public static final int STATUS_EMPTY = 4;    //空数据
	public static final int STATUS_PARSE = 5;    //正在解析 (一般用于本地数据加载)
	public static final int STATUS_PARSE_ERROR = 6; //本地文件解析错误(暂未被使用)

	public static final String READ_FONT_DEFAULT_NAME = "默认字体";
	public static final String FILE_SUFFIX_TTF = ".ttf";

	static final int DEFAULT_MARGIN_HEIGHT = 25;
	static final int DEFAULT_MARGIN_WIDTH = 20;

	//默认的显示参数配置
	private static final int DEFAULT_TIP_SIZE = 12;
	private static final int EXTRA_TITLE_SIZE = 4;
	//当前章节列表
	protected List<BookMenu> mChapterList;
	//书本对象
	protected BookInfo bookInfo;
	//监听器
	protected OnPageChangeListener mPageChangeListener;

	//页面显示类
	private PageView mPageView;
	//当前显示的页
	private TxtPage mCurPage;
	//上一章的页面列表缓存
	private WeakReference<List<TxtPage>> mWeakPrePageList;
	//当前章节的页面列表
	private List<TxtPage> mCurPageList;
	//下一章的页面列表缓存
	private List<TxtPage> mNextPageList;

	//下一页绘制缓冲区，用户缓解卡顿问题。
	private Bitmap mNextBitmap;

	//	//绘制电池的画笔
//	private Paint mBatteryPaint;
	//绘制提示的画笔
	private Paint mTipPaint;
	//绘制标题的画笔
	private Paint mTitlePaint;
	//绘制背景颜色的画笔(用来擦除需要重绘的部分)
	private Paint mBgPaint;
	//绘制小说内容的画笔
	private TextPaint mTextPaint;
	//阅读器的配置选项
	private ReadSettingManager mSettingManager;
	//被遮盖的页，或者认为被取消显示的页
	private TxtPage mCancelPage;
	/*****************params**************************/
	//当前的状态
	protected int mStatus = STATUS_LOADING;
	//当前章
	protected int mCurChapterPos = 1;
	//书本是否打开
	protected boolean isBookOpen = false;
	//上一章的记录
	private int mLastChapter = 0;
	//书籍绘制区域的宽高
	private int mVisibleWidth;
	private int mVisibleHeight;
	//应用的宽高
	private int mDisplayWidth;
	private int mDisplayHeight;
	//间距
	private int mMarginWidth;
	private int mMarginHeight;
	//字体的颜色
	private int mTextColor;
	//标题的大小
	private int mTitleSize;
	//字体的大小
	private int mTextSize;
	//行间距
	private int mTextInterval;
	//标题的行间距
	private int mTitleInterval;
	//段落距离(基于行间距的额外距离)
	private int mTextPara;
	private int mTitlePara;
	//页面的翻页效果模式
	private int mPageMode;
	//加载器的颜色主题
	private int mBgTheme;
	//当前页面的背景
	private int mPageBg;
	// 在章节开始增加标题画笔
	private int mTitleColor = Color.BLACK;
	//提示画笔颜色
	private int mTipColor = Color.BLACK;
	//当前是否是夜间模式
	private boolean isNightMode;
	//画笔字体
	private Typeface customFont;

	protected Context context;

	protected BookHistory bookHistory;

	protected LoadingContentView loadingContentView;

	protected ReadActionListener readActionListener;
	//背景图片
	protected Bitmap m_book_bg = null;
	//当前vip章节
	protected int currentVip = 0;
	private String percent="0.0%";

	/*****************************init params*******************************/
	public PageLoader(Context context, PageView pageView, LoadingContentView loadingContentView, ReadActionListener readActionListener) {
		mPageView = pageView;
		this.context = context;
		this.loadingContentView = loadingContentView;
		this.readActionListener = readActionListener;

		//初始化数据
		initData();
		//初始化画笔
		initPaint();
		//初始化PageView
		initPageView();
	}

	private void initData() {
		mSettingManager = ReadSettingManager.getInstance();
		mTextSize = ReadFontSize.getFontSizeBySP(context);
		mTitleSize = mTextSize + DisplayUtil.sp2px(context, EXTRA_TITLE_SIZE);
		mPageMode = mSettingManager.getPageMode(context);
		isNightMode = mSettingManager.isNightMode(context);
		mBgTheme = mSettingManager.getReadBgTheme(context);

		if (isNightMode) {
			setBgColor(ReadSettingManager.NIGHT_MODE);
		} else {
			setBgColor(mBgTheme);
		}

		//初始化参数
		mMarginWidth = DisplayUtil.dip2px(context, DEFAULT_MARGIN_WIDTH);
		mMarginHeight = DisplayUtil.dip2px(context, DEFAULT_MARGIN_HEIGHT);
		mTextInterval = ReadLineSpace.getLineSpaceCount(context, mSettingManager.getReadLineSpace(context));
		mTitleInterval = mTitleSize / 2;
		mTextPara = mTextSize + DisplayUtil.dip2px(context, 8); //段落间距由 text 的高度决定。
		mTitlePara = mTitleSize;
	}

	private void initPaint() {
		//画笔字体
		initCustomFont();
		//绘制提示的画笔
		mTipPaint = new Paint();
		mTipPaint.setColor(mTipColor);
		mTipPaint.setTextAlign(Paint.Align.LEFT);//绘制的起始点
		mTipPaint.setTextSize(DisplayUtil.sp2px(context, DEFAULT_TIP_SIZE));//Tip默认的字体大小
		mTipPaint.setAntiAlias(true);
		mTipPaint.setSubpixelText(true);
		mTipPaint.setTypeface(isCustomFont() ? customFont : Typeface.MONOSPACE);

		//绘制页面内容的画笔
		mTextPaint = new TextPaint();
		mTextPaint.setColor(mTextColor);
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTypeface(isCustomFont() ? customFont : Typeface.MONOSPACE);

		//绘制标题的画笔
		mTitlePaint = new TextPaint();
		mTitlePaint.setTextSize(mTitleSize);
		mTitlePaint.setColor(mTitleColor);
		mTitlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
		mTitlePaint.setAntiAlias(true);
		mTitlePaint.setTypeface(isCustomFont() ? customFont : Typeface.MONOSPACE);

		//绘制背景的画笔
		mBgPaint = new Paint();
		mBgPaint.setColor(mPageBg);
	}

	private void initPageView() {
		//配置参数
		mPageView.setPageMode(mPageMode);
		mPageView.setBgColor(mPageBg);
	}

	/****************************** public method***************************/
	//跳转到上一章
	public int skipPreChapter() {
		if (!isBookOpen) {
			return mCurChapterPos;
		}

		//载入上一章。
		if (prevChapter(true)) {
			mCurPage = getCurPage(0);
			mPageView.refreshPage();
		}
		return mCurChapterPos;
	}

	//跳转到下一章
	public int skipNextChapter() {
		if (!isBookOpen) {
			return mCurChapterPos;
		}

		//判断是否达到章节的终止点
		if (nextChapter(true)) {
			mCurPage = getCurPage(0);
			mPageView.refreshPage();
		}
		return mCurChapterPos;
	}

	public void refreshPage(int mStatus) {
		this.mStatus = mStatus;
		if(mCurPage!=null){
			mPageView.refreshPage();
		}
	}

	//跳转到指定章节
	public void skipToChapter(int pos) {
		if (pos > 0 && mChapterList.get(pos - 1).isVip == 1 && mChapterList.get(pos - 1).hasOrder != 1) {
			currentVip = pos;
		} else if (pos > 0) {
			currentVip = 0;
			//绘制当前的状态
			mCurChapterPos = pos;

		} else {
			currentVip = 0;
		}

		if(mSettingManager.getPageMode(context)==PageView.PAGE_MODE_SCROLL){
			isFirstCancel=true;
		}
		//正在加载
		mStatus = STATUS_LOADING;
		//将上一章的缓存设置为null
		mWeakPrePageList = null;
		//将下一章缓存设置为null
		mNextPageList = null;

		mCurPage = getCurPage(0);
		mCancelPage = mCurPage;

		if (mPageChangeListener != null) {
			mPageChangeListener.onChapterChange(mCurChapterPos);
		}
		//为了重新把mCurPage.position重置为0
		if (mChapterList != null && mChapterList.size() > 0 && mCurChapterPos - 1 < mChapterList.size() && mCurChapterPos - 1 >= 0) {
			BookHistoryUtil.saveBookHistory(bookHistory, bookInfo, mCurChapterPos, mChapterList.get(mCurChapterPos - 1).menuName, 0,percent);
		}

		mPageView.refreshPage();
	}

	//跳转到具体的页
	public void skipToPage(int pos) {
		mCurPage = getCurPage(pos);
		mPageView.refreshPage();
	}

	//自动翻到上一章
	public boolean autoPrevPage() {
		return mPageView.autoPrevPage();
	}

	//自动翻到下一章
	public boolean autoNextPage() {
		return mPageView.autoNextPage();
	}

	//更新时间
	public void updateTime() {
		if (mPageView.isPrepare() && !mPageView.isRunning()) {
			if(m_book_bg==null){
				mPageView.drawCurPage(true);
			}else{
				mPageView.drawCurPage(false);
			}
		}
	}

	//设置文字大小
	public void setTextSize(int textSize) {
		if (!isBookOpen) return;

		//设置textSize
		mTextSize = textSize;
		mTextInterval = ReadLineSpace.getLineSpaceCount(context, mSettingManager.getReadLineSpace(context));
		mTextPara = mTextSize + DisplayUtil.dip2px(context, 8);

		mTitleSize = mTextSize + DisplayUtil.sp2px(context, EXTRA_TITLE_SIZE);
		mTitleInterval = mTitleInterval / 2;
		mTitlePara = mTitleSize;

		//设置画笔的字体大小
		mTextPaint.setTextSize(mTextSize);
		//设置标题的字体大小
		mTitlePaint.setTextSize(mTitleSize);
		//存储状态
		ReadFontSize.saveFontSizeBySP(context, mTextSize);
		//取消缓存
		mWeakPrePageList = null;
		mNextPageList = null;
		//如果当前为完成状态。
		if (mStatus == STATUS_FINISH) {
			//重新计算页面
			mCurPageList = loadPageList(mCurChapterPos);

			//防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
			if (mCurPage.position > mCurPageList.size() - 1) {
				mCurPage.position = mCurPageList.size() - 1;
			}
		}
		//重新设置文章指针的位置
		mCurPage = getCurPage(mCurPage.position);
		//绘制
		mPageView.refreshPage();
	}

	//设置行间距
	public void setReadLineSpace() {
		if (!isBookOpen) return;
		mTextInterval = ReadLineSpace.getLineSpaceCount(context, mSettingManager.getReadLineSpace(context));
		//取消缓存
		mWeakPrePageList = null;
		mNextPageList = null;
		//如果当前为完成状态。
		if (mStatus == STATUS_FINISH) {
			//重新计算页面
			mCurPageList = loadPageList(mCurChapterPos);
		}
		//重新设置文章指针的位置
		mCurPage = getCurPage(mCurPage.position);
		//绘制
		mPageView.refreshPage();
	}

	//设置字体
	public void setPaintFont() {
		if (!isBookOpen) return;

		initCustomFont();
		mTextPaint.setTypeface(isCustomFont() ? customFont : Typeface.MONOSPACE);
		mTitlePaint.setTypeface(isCustomFont() ? customFont : Typeface.MONOSPACE);
		mTipPaint.setTypeface(isCustomFont() ? customFont : Typeface.MONOSPACE);

		//取消缓存
		mWeakPrePageList = null;
		mNextPageList = null;
		//如果当前为完成状态。
		if (mStatus == STATUS_FINISH) {
			//重新计算页面
			mCurPageList = loadPageList(mCurChapterPos);
		}
		//重新设置文章指针的位置
		mCurPage = getCurPage(mCurPage.position);
		//绘制
		mPageView.refreshPage();
	}

	//设置夜间模式
	public void setNightMode(boolean nightMode) {
		isNightMode = nightMode;
		mBgTheme = mSettingManager.getReadBgTheme(context);
		if (isNightMode) {
			setBgColor(ReadSettingManager.NIGHT_MODE);
		} else {
			setBgColor(mBgTheme);
		}
		mSettingManager.setNightMode(context, nightMode);
	}

	//绘制背景
	public void setBgColor(int theme) {
		m_book_bg = null;
		if (isNightMode && theme == ReadSettingManager.NIGHT_MODE) {
			mTextColor = ReadBg.NIGHT_TEXT_COLORS;
			mPageBg = ReadBg.NIGHT_BG_COLORS;
			mTitleColor = ReadBg.NIGHT_TITLE_COLORS;
			mTipColor = ReadBg.NIGHT_OTHER_COLORS;
		} else {
			mSettingManager.setReadBackground(context, theme);
			switch (theme) {
				case 3:
				case 4:
				case 5:
					mTextColor = ReadBg.TEXT_COLORS[theme];
					mTipColor = ReadBg.OTHER_COLORS[theme];
					mTitleColor = ReadBg.TITLE_COLORS[theme];
					int bgImgId = ReadBg.BG_COLORS[theme];
					m_book_bg = BitmapFactory.decodeResource(context.getResources(), bgImgId);
					break;
				default:
					mTextColor = ReadBg.TEXT_COLORS[theme];
					mPageBg = ReadBg.BG_COLORS[theme];
					mTipColor = ReadBg.OTHER_COLORS[theme];
					mTitleColor = ReadBg.TITLE_COLORS[theme];
					break;
			}
		}

		if (isBookOpen) {
			//设置参数
			mPageView.setBgColor(mPageBg);
			mTextPaint.setColor(mTextColor);
			mTitlePaint.setColor(mTitleColor);
			mTipPaint.setColor(mTipColor);
			//重绘
			mPageView.refreshPage();
		}
	}

	//翻页动画
	public void setPageMode(int pageMode,boolean isSavePageMode) {
		mPageMode = pageMode;
		mPageView.setPageMode(mPageMode);
		if(isSavePageMode){
			mSettingManager.setPageMode(context, mPageMode);
		}
		//重绘
		mPageView.drawCurPage(false);
	}

	//设置页面切换监听
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mPageChangeListener = listener;
	}

	//获取当前页的状态
	public int getPageStatus() {
		return mStatus;
	}

	//获取当前章节的章节位置
	public int getChapterPos() {
		return mCurChapterPos;
	}

	public void setChapterPos(int chapterPos) {
		mCurChapterPos = chapterPos;
	}

	//获取当前页的页码
	public int getPagePos() {
		if(mCurPage==null){
			return 0;
		}
		return mCurPage.position;
	}

	public List<TxtPage> getCurPageList(){
		if(mCurPageList!=null){
			return mCurPageList;
		}

		return null;
	}


	/****************获取封面设置参数********************/
	//获取正文内容文字
	public int getContentTextColor(){
		return mTextColor;
	}
	//获取图片背景
	public int getReadBgRes() {
		return ReadBg.BG_COLORS[mBgTheme];
	}
	//获取背景颜色
	public int getReadBgColor(){
		return mPageBg;
	}

	public boolean isBackgroundImg() {
		return mBgTheme == 3 || mBgTheme == 4 || mBgTheme == 5;
	}

	//保存阅读记录
	public void saveRecord() {
		//书没打开，就没有记录
		int position = 0;
		if (mCurPage != null) {
			position = mCurPage.position;
		}
		if (mChapterList != null && mChapterList.size() > 0 && mCurChapterPos - 1 < mChapterList.size() && mCurChapterPos - 1 >= 0) {
			BookHistoryUtil.saveBookHistory(bookHistory, bookInfo, mCurChapterPos, mChapterList.get(mCurChapterPos - 1).menuName, position,percent);
		}
	}

	//打开书本，初始化书籍
	public void openBook(BookInfo bookInfo, int pos,boolean isInitPagePos) {
		this.bookInfo = bookInfo;
		bookHistory = BookHistoryUtil.getBookId(bookInfo.bookId);

		if (pos > 0 && mChapterList.get(pos - 1).isVip == 1 && mChapterList.get(pos - 1).hasOrder != 1) {
			if (bookHistory == null) {
				mCurChapterPos = 1;
				bookHistory = new BookHistory();
			} else {
				mCurChapterPos = bookHistory.menuId;
			}
			currentVip = pos;
		} else {
			currentVip = 0;
			if (bookHistory == null) {
				if (pos > 0) {
					mCurChapterPos = pos;
				} else {
					mCurChapterPos = 1;
				}
				bookHistory = new BookHistory();
			} else {
				if (pos > 0) {
					if(isInitPagePos){
						bookHistory.pagePos=0;
					}
					mCurChapterPos = pos;
				} else {
					if(mSettingManager.getPageMode(context)==PageView.PAGE_MODE_SCROLL){
						bookHistory.pagePos=0;
					}
					mCurChapterPos = bookHistory.menuId;
				}

			}
		}
		mLastChapter = mCurChapterPos;
	}


	//打开具体章节
	public void openChapter() {
		if (currentVip > 0 && mChapterList.get(currentVip - 1).isVip == 1 && mChapterList.get(currentVip - 1).hasOrder != 1) {
			readActionListener.requestOrder(currentVip);
			if (getCurPage(0) != null) {
				return;
			}
		}
		mCurPageList = loadPageList(mCurChapterPos);
		if (mCurPageList != null) {
			//进行预加载
			preLoadNextChapter();
			//加载完成
			mStatus = STATUS_FINISH;
			//获取制定页面
			if (!isBookOpen) {
				isBookOpen = true;
				//可能会出现当前页的大小大于记录页的情况。
				int position = bookHistory.pagePos;
				if (position >= mCurPageList.size()) {
					position = mCurPageList.size() - 1;
				}
				mCurPage = getCurPage(position);
				mCancelPage = mCurPage;
				if (mPageChangeListener != null) {
					mPageChangeListener.onChapterChange(mCurChapterPos);
				}
			} else {
				mCurPage = getCurPage(0);
			}

			mPageView.drawCurPage(false);
		} else {
			mStatus = STATUS_ERROR;
		}

	}

	public void chapterError() {
		//加载错误
		mStatus = STATUS_ERROR;
		//显示加载错误
		mPageView.drawCurPage(false);
	}

	//清除记录，并设定是否缓存数据
	public void closeBook() {
		isBookOpen = false;
		mPageView = null;
		mChapterList = null;
		mCurPageList = null;
		mNextPageList = null;
		System.gc();
	}

	/*******************************abstract method***************************************/
	//设置章节
	public abstract void setChapterList(List<BookMenu> bookChapters);

	@Nullable
	public abstract List<TxtPage> loadPageList(int chapter);

	/***********************************default method***********************************************/
	//通过流获取Page的方法
	List<TxtPage> loadPages(BookMenu chapter, BufferedReader br) {
		//生成的页面
		List<TxtPage> pages = new ArrayList<>();
		//使用流的方式加载
		List<String> lines = new ArrayList<>();
		int rHeight = mVisibleHeight; //由于匹配到最后，会多删除行间距，所以在这里多加个行间距
		int titleLinesCount = 0;
		boolean isTitle = true; //不存在没有 Title 的情况，所以默认设置为 true。
		String paragraph = chapter.menuName;//默认展示标题
		try {
			while (isTitle || (paragraph = br.readLine()) != null) {

				//重置段落
				if (!isTitle) {
					paragraph = paragraph.replaceFirst("^\\s+", "　　");
					//如果只有换行符，那么就不执行
					if (paragraph.equals("")) continue;
					paragraph = paragraph + "\n";
				} else {
					//设置 title 的顶部间距
					rHeight -= mTitlePara;
				}

				int wordCount = 0;
				String subStr = null;
				while (paragraph.length() > 0) {
					//当前空间，是否容得下一行文字
					if (isTitle) {
						rHeight -= mTitlePaint.getTextSize();
					} else {
						rHeight -= mTextPaint.getTextSize();
					}

					//一页已经填充满了，创建 TextPage
					if (rHeight < 0) {
						//创建Page
						TxtPage page = new TxtPage();
						page.position = pages.size();
						page.title = chapter.contentLittle;
						page.lines = new ArrayList<>(lines);
						page.titleLines = titleLinesCount;
						pages.add(page);
						//重置Lines
						lines.clear();
						rHeight = mVisibleHeight;
						titleLinesCount = 0;
						continue;
					}

					//测量一行占用的字节数
					if (isTitle) {
						wordCount = mTitlePaint.breakText(paragraph, true, mVisibleWidth, null);
					} else {
						wordCount = mTextPaint.breakText(paragraph, true, mVisibleWidth, null);
					}

					subStr = paragraph.substring(0, isToAddUpLine(paragraph, wordCount));
					if (!subStr.equals("\n")) {
						//将一行字节，存储到lines中
						lines.add(subStr);

						//设置段落间距
						if (isTitle) {
							titleLinesCount += 1;
							rHeight -= mTitleInterval;
						} else {
							rHeight -= mTextInterval;
						}
					}
					//裁剪
					paragraph = paragraph.substring(isToAddUpLine(paragraph, wordCount));
				}

				//增加段落的间距
				if (!isTitle && lines.size() != 0) {
					rHeight = rHeight - mTextPara + mTextInterval;
				}

				if (isTitle) {
					rHeight = rHeight - mTitlePara + mTitleInterval;
					isTitle = false;
				}
			}

			if (lines.size() != 0) {
				//创建Page
				TxtPage page = new TxtPage();
				page.position = pages.size();
				page.title = chapter.contentLittle;
				page.lines = new ArrayList<>(lines);
				page.titleLines = titleLinesCount;
				pages.add(page);
				//重置Lines
				lines.clear();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(br);
		}

		//可能出现内容为空的情况
		if (pages.size() == 0) {
			TxtPage page = new TxtPage();
			page.lines = new ArrayList<>(1);
			pages.add(page);

			mStatus = STATUS_EMPTY;
		}

		//提示章节数量改变了。
		if (mPageChangeListener != null) {
			mPageChangeListener.onPageCountChange(pages.size());
		}
		return pages;
	}

	void onDraw(Bitmap bitmap, boolean isUpdate) {
		drawBackground(mPageView.getBgBitmap(), isUpdate);
		if (!isUpdate) {
			drawContent(bitmap);
		}
		//更新绘制
		mPageView.invalidate();
	}

	/**
	 * 绘画背景图
	 */
	void drawBitmap(Canvas c) {
		Paint myFillPaint = new Paint();
		final int w = m_book_bg.getWidth();
		final int h = m_book_bg.getHeight();
		for (int cw = 0; cw < DisplayUtil.getScreenWidthPixels(context); cw += w) {
			for (int ch = 0; ch < mDisplayHeight; ch += h) {
				c.drawBitmap(m_book_bg, cw, ch, myFillPaint);
			}
		}
	}

	void drawBackground(Bitmap bitmap, boolean isUpdate) {
		Canvas canvas = new Canvas(bitmap);

		//底部时间设置
		float yBottom = mDisplayHeight - (int) mTipPaint.getTextSize() + DisplayUtil.sp2px(context, 6);
		String time = StringUtils.toDate3(System.currentTimeMillis());
		int timeWidth = (int) mTipPaint.measureText(time) + mMarginWidth;

		if (!isUpdate) {
			/****绘制背景****/
			if (m_book_bg == null)
				canvas.drawColor(mPageBg);
			else
				drawBitmap(canvas);

			if (mStatus == STATUS_FINISH) {
				/*****顶部提示：书名、当前章节序号********/
				//需要注意的是:绘制text的y的起始点是text的基准线的位置，而不是从text的头部的位置
				float tipTop = (int) mTipPaint.getTextSize() + DisplayUtil.sp2px(context, 6);
				if (bookInfo != null && !TextUtils.isEmpty(bookInfo.bookName)) {
					canvas.drawText(bookInfo.bookName
							, mMarginWidth, tipTop, mTipPaint);
				}

				if(mChapterList!=null){
					String tip = mCurChapterPos + "/" + mChapterList.size();
					int visibleRight = mDisplayWidth - (int) mTipPaint.measureText(tip) - mMarginWidth;
					canvas.drawText(tip, visibleRight, tipTop, mTipPaint);
				}



				/******章节名********/
				// 防止章节名过长遮挡时间和阅读进度，截取前13字，加省略号
				if (mChapterList != null && mChapterList.size() != 0) {
					String menuName = StringUtils.ellipsis(mChapterList.get(mCurChapterPos - 1).menuName, 16);
					int nNameWidth = (int) mTipPaint.measureText(menuName);
					int nPercentWidth = (int) mTipPaint.measureText("999.9%") + mMarginWidth;
					int titleWidth = timeWidth + (mDisplayWidth - nPercentWidth - timeWidth - nNameWidth) / 2;
					canvas.drawText(menuName, titleWidth, yBottom, mTipPaint); // 显示章节名

					/******绘制百分比********/
					if(mCurPage!=null&&mCurPageList!=null){
						DecimalFormat df = new DecimalFormat("#0.0");
						float fPercent = (float) ((mCurPage.position + 1) * 1.0 /mCurPageList.size());
						percent= df.format(fPercent * 100) + "%";
						canvas.drawText(percent, mDisplayWidth - nPercentWidth,
								yBottom, mTipPaint);
					}

				}
			}
		} else {
			//擦除区域
			mBgPaint.setColor(mPageBg);
			canvas.drawRect(mMarginWidth, mDisplayHeight - mMarginHeight + DisplayUtil.dip2px(context, 2), timeWidth, mDisplayHeight, mBgPaint);
		}

		/******绘制当前时间********/
		canvas.drawText(time, mMarginWidth, yBottom, mTipPaint);

	}

	void drawContent(Bitmap bitmap) {
		Canvas canvas = new Canvas(bitmap);
		float top=0;
		String str;

		//设置总距离
		int interval = mTextInterval + (int) mTextPaint.getTextSize();
		int para = mTextPara + (int) mTextPaint.getTextSize();
		int titleInterval = mTitleInterval + (int) mTitlePaint.getTextSize();
		int titlePara = mTitlePara + (int) mTextPaint.getTextSize();

		if (mPageMode == PageView.PAGE_MODE_SCROLL) {
			top = -(mTextPaint.getFontMetrics().top);
		} else {
			top = (int) mTipPaint.getTextSize() + mTitlePaint.getTextSize() + DisplayUtil.sp2px(context, 8);
		}
		if (mPageMode == PageView.PAGE_MODE_SCROLL) {
			if (m_book_bg == null)
				canvas.drawColor(mPageBg);
			else
				drawBitmap(canvas);
		}
		/******绘制内容****/
		if (mStatus != STATUS_FINISH) {
			//绘制字体
			switch (mStatus) {
				//加载中
				case STATUS_LOADING:
					UI.visible(loadingContentView);
					break;
				//加载错误
				case STATUS_ERROR:
					UI.visible(loadingContentView);
					break;
				//文章内容为空
				case STATUS_EMPTY:
					break;
				//正在排版
				case STATUS_PARSE:
					break;
				//文件解析错误
				case STATUS_PARSE_ERROR:
					break;
			}
		} else {

			UI.invisible(loadingContentView);

			if(mCurPage!=null){
				//对标题进行绘制
				for (int i = 0; i < mCurPage.titleLines; ++i) {
					str = mCurPage.lines.get(i);

					//设置顶部间距
					if (i == 0) {
						top += mTitlePara;
					}

					//进行绘制
					canvas.drawText(str, mMarginWidth, top, mTitlePaint);

					//设置尾部间距
					if (i == mCurPage.titleLines - 1) {
						top += titlePara;
					} else {
						//行间距
						top += titleInterval;
					}
				}
				//对内容进行绘制
				for (int i = mCurPage.titleLines; i < mCurPage.lines.size(); ++i) {
					str = mCurPage.lines.get(i);

					canvas.drawText(str, mMarginWidth, top, mTextPaint);
					if (str.endsWith("\n")) {
						top += para;
					} else {
						top += interval;
					}
				}
			}

		}
	}

	void setDisplaySize(int w, int h) {
		//获取PageView的宽高
		mDisplayWidth = w;
		mDisplayHeight = h;

		//获取内容显示位置的大小
		mVisibleWidth = mDisplayWidth - mMarginWidth * 2;
		mVisibleHeight = mDisplayHeight - mMarginHeight * 2;

		//创建用来缓冲的 Bitmap
		mNextBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

		//如果章节已显示，那么就重新计算页面
		if (mStatus == STATUS_FINISH) {
			mCurPageList = loadPageList(mCurChapterPos);
			preLoadNextChapter();
			//重新设置文章指针的位置
			if(mCurPage==null){
				mCurPage = getCurPage(0);
			}else{
				mCurPage = getCurPage(mCurPage.position);
			}
		}

		mPageView.drawCurPage(false);
	}

	//翻阅上一页
	boolean prev() {
		if (!checkStatus()) return false;

		//判断是否达到章节的起始点
		TxtPage prevPage = getPrevPage();
		if (prevPage == null) {
			//载入上一章。

			if (!prevChapter(false)) {
				if(mSettingManager.getPageMode(context)!=PageView.PAGE_MODE_SCROLL){
					if (mChapterList.get(mCurChapterPos - 1).isVip == 1 && mChapterList.get(mCurChapterPos - 1).hasOrder != 1) {
						mCancelPage = mCurPage;
						mPageView.drawNextPage();
						return true;
					}
				}
				return false;
			} else {
				mCancelPage = mCurPage;
				mCurPage = getPrevLastPage();
				mPageView.drawNextPage();
				return true;
			}
		}

		mCancelPage = mCurPage;
		mCurPage = prevPage;

		mPageView.drawNextPage();
		return true;
	}
	//加载上一章
	boolean prevChapter(boolean showTip) {

		//判断是否上一章节为空
		if (mCurChapterPos - 1 < 1) {
			if(mSettingManager.getPageMode(context)==PageView.PAGE_MODE_SCROLL){
				ScrollPageAnim.isScroll=false;
			}
			readActionListener.hasNoPrePage();
			return false;
		}

		if (mCurChapterPos - 2 > 0 && mChapterList.get(mCurChapterPos - 2).isVip == 1 && mChapterList.get(mCurChapterPos - 2).hasOrder != 1) {
			if(mSettingManager.getPageMode(context)==PageView.PAGE_MODE_SCROLL){
				ScrollPageAnim.isScroll=false;
				mPageView.postDelayed(new Runnable() {
					@Override
					public void run() {
						readActionListener.requestOrder(mCurChapterPos - 1);
					}
				},500);
			}else{
				readActionListener.requestOrder(mCurChapterPos - 1);
			}

			return false;
		}

		if(mSettingManager.getPageMode(context)==PageView.PAGE_MODE_SCROLL){
			ScrollPageAnim.isScroll=true;
		}


		//加载上一章数据
		int prevChapter = mCurChapterPos - 1;
		//当前章变成下一章
		mNextPageList = mCurPageList;

		if (loadingContentView.getVisibility() == View.VISIBLE) {
			mWeakPrePageList = null;
		}

		//判断上一章缓存是否存在，如果存在则从缓存中获取数据。
		if (mWeakPrePageList != null && mWeakPrePageList.get() != null) {
			mCurPageList = mWeakPrePageList.get();
			mWeakPrePageList = null;
		}
		//如果不存在则加载数据
		else {
			mCurPageList = loadPageList(prevChapter);
		}

		if (mCurPageList != null) {
			mStatus = STATUS_FINISH;
			mLastChapter = mCurChapterPos;
			mCurChapterPos = prevChapter;
			if (showTip) {
				Toast.makeText(context, mChapterList.get(mCurChapterPos - 1).menuName, Toast.LENGTH_SHORT).show();
			}
		}
		//如果当前章不存在，则表示在加载中
		else {
			//重置position的位置，防止正在加载的时候退出时候存储的位置为上一章的页码
			mCurPage.position = 0;
			chapterError();
		}

		if (mPageChangeListener != null) {
			mPageChangeListener.onChapterChange(mCurChapterPos);
		}

		return true;
	}

	//翻阅下一页
	boolean next() {
		if (!checkStatus()) return false;
		//判断是否到最后一页了
		TxtPage nextPage = getNextPage();
		if (nextPage == null) {
			if (!nextChapter(false)) {
				if(mSettingManager.getPageMode(context)!=PageView.PAGE_MODE_SCROLL){
					if (mCurChapterPos < mChapterList.size() && mChapterList.get(mCurChapterPos).isVip == 1 && mChapterList.get(mCurChapterPos).hasOrder != 1) {
						mCancelPage = mCurPage;
						mPageView.drawNextPage();
						return true;
					}
				}
				return false;
			} else {
				mCancelPage = mCurPage;
				mCurPage = getCurPage(0);
				mPageView.drawNextPage();
				return true;
			}
		}

		mCancelPage = mCurPage;
		mCurPage = nextPage;
		mPageView.drawNextPage();

		return true;
	}

	boolean nextChapter(boolean showTip) {
		//加载一章
		if (mCurChapterPos + 1 > mChapterList.size()) {
			readActionListener.hasNoNextPage();
			if(mSettingManager.getPageMode(context)==PageView.PAGE_MODE_SCROLL){
				ScrollPageAnim.isScroll=false;
			}
			return false;
		}

		if (mCurChapterPos < mChapterList.size() && mChapterList.get(mCurChapterPos).isVip == 1 && mChapterList.get(mCurChapterPos).hasOrder != 1) {
			if(mSettingManager.getPageMode(context)==PageView.PAGE_MODE_SCROLL){
				ScrollPageAnim.isScroll=false;
				mPageView.postDelayed(new Runnable() {
					@Override
					public void run() {
						readActionListener.requestOrder(mCurChapterPos + 1);
					}
				},500);
			}else{
				readActionListener.requestOrder(mCurChapterPos + 1);
			}

			return false;
		}

		//如果存在下一章，则存储当前Page列表为上一章
		if (mCurPageList != null) {
			mWeakPrePageList = new WeakReference<List<TxtPage>>(new ArrayList<>(mCurPageList));
		}

		int nextChapter = mCurChapterPos + 1;
		//如果存在下一章预加载章节。
		if (mNextPageList != null) {
			mCurPageList = mNextPageList;
			mNextPageList = null;
		} else {
			//这个PageList可能为 null，可能会造成问题。
			mCurPageList = loadPageList(nextChapter);
		}

		//如果存在当前章，预加载下一章
		if (mCurPageList != null) {
			mLastChapter = mCurChapterPos;
			mCurChapterPos = nextChapter;
			mStatus = STATUS_FINISH;
			preLoadNextChapter();
			if (showTip) {
				Toast.makeText(context, mChapterList.get(mCurChapterPos - 1).menuName, Toast.LENGTH_SHORT).show();
			}
		}
		//如果当前章不存在，则表示在加载中
		else {
			chapterError();
		}

		if (mPageChangeListener != null) {
			mPageChangeListener.onChapterChange(mCurChapterPos);
		}

		return true;
	}

	//预加载下一章
	private void preLoadNextChapter() {
		//判断是否存在下一章
		if (mChapterList==null||mCurChapterPos + 1 >= mChapterList.size()) {
			return;
		}
		//判断下一章的文件是否存在
		int nextChapter = mCurChapterPos + 1;
		mNextPageList = loadPageList(nextChapter);
	}

	private boolean isFirstCancel=true;
	private boolean isRefreshScroll=false;
	void scrollPageCancel() {
		if(isFirstCancel){
			isFirstCancel=false;

			if(ScrollPageAnim.flag==1){
				if(isRefreshScroll){
					isRefreshScroll=false;
					mCurPageList=loadPageList(mCurChapterPos);
					mCurPage=mCurPageList.get(mCurPageList.size()-1);
					skipToPage(mCurPageList.size()-1);
				}
			}else{
				mCurPage = mCancelPage;
			}
		}else{
			//加载到下一章取消了
			if (mCurPage.position == 0 && mCurChapterPos > mLastChapter) {
				cancelPrevChapter();
			} else if (mCurPageList == null ||
					(mCurPage.position == mCurPageList.size() - 1 && mCurChapterPos < mLastChapter)) {
				cancelNextChapter();
			}

			mCurPage = mCancelPage;
		}

	}

	public void refreshFirstScroll(){
		if(isFirstCancel){
			mCurPage = mCancelPage;
			isRefreshScroll=true;
		}
	}

	boolean cancelNextChapter() {
		//如果存在下一章，则存储当前Page列表为上一章
		if (mCurPageList != null) {
			mWeakPrePageList = new WeakReference<List<TxtPage>>(new ArrayList<>(mCurPageList));
		}

		int nextChapter = mCurChapterPos + 1;
		//如果存在下一章预加载章节。
		if (mNextPageList != null) {
			mCurPageList = mNextPageList;
			mNextPageList = null;
		} else {
			//这个PageList可能为 null，可能会造成问题。
			mCurPageList = loadPageList(nextChapter);
		}

		mLastChapter = mCurChapterPos;
		mCurChapterPos = nextChapter;

		//如果存在当前章，预加载下一章
		if (mCurPageList != null) {
			mStatus = STATUS_FINISH;
			preLoadNextChapter();
		}
		//如果当前章不存在，则表示在加载中
		else {
			chapterError();
		}

		if (mPageChangeListener != null) {
			mPageChangeListener.onChapterChange(mCurChapterPos);
		}

		return true;
	}

	//加载上一章
	boolean cancelPrevChapter() {
		//加载上一章数据
		int prevChapter = mCurChapterPos - 1;
		//当前章变成下一章
		mNextPageList = mCurPageList;

		//判断上一章缓存是否存在，如果存在则从缓存中获取数据。
		if (mWeakPrePageList != null && mWeakPrePageList.get() != null) {
			mCurPageList = mWeakPrePageList.get();
			mWeakPrePageList = null;
		}
		//如果不存在则加载数据
		else {
			mCurPageList = loadPageList(prevChapter);
		}

		mLastChapter = mCurChapterPos;
		mCurChapterPos = prevChapter;

		if (mCurPageList != null) {
			mStatus = STATUS_FINISH;
		}
		//如果当前章不存在，则表示在加载中
		else {
			//重置position的位置，防止正在加载的时候退出时候存储的位置为上一章的页码
			mCurPage.position = 0;
			chapterError();
		}

		if (mPageChangeListener != null) {
			mPageChangeListener.onChapterChange(mCurChapterPos);
		}

		return true;
	}


	/**
	 * @return:获取初始显示的页面
	 */
	TxtPage getCurPage(int pos) {
		if (mPageChangeListener != null) {
			if (mCurPageList == null||pos>=mCurPageList.size()) {
				return null;
			}
			mPageChangeListener.onPageChange(mCurPageList.size(), pos);
		}

		if(pos>=mCurPageList.size()){
			return null;
		}
		return mCurPageList.get(pos);
	}


	/**************************************private method********************************************/

	/**
	 * @return:获取上一个页面
	 */
	private TxtPage getPrevPage() {
		if (mCurPage == null) {
			return null;
		}
		int pos = mCurPage.position - 1;
		if (pos < 0) {
			return null;
		}
		if (mCurPageList == null) {
			return null;
		}

		if (pos >= mCurPageList.size()) {
			return null;
		}

		if (mPageChangeListener != null) {
			mPageChangeListener.onPageChange(mCurPageList.size(), pos);
		}
		return mCurPageList.get(pos);
	}

	/**
	 * @return:获取下一的页面
	 */
	private TxtPage getNextPage() {
		if (mCurPage == null) {
			return null;
		}
		int pos = mCurPage.position + 1;
		if (mCurPageList == null) {
			return null;
		}
		if (pos >= mCurPageList.size()) {
			return null;
		}
		if (mPageChangeListener != null) {
			mPageChangeListener.onPageChange(mCurPageList.size(), pos);
		}
		return mCurPageList.get(pos);
	}

	/**
	 * @return:获取上一个章节的最后一页
	 */
	private TxtPage getPrevLastPage() {
		int pos = mCurPageList.size() - 1;
		return mCurPageList.get(pos);
	}

	/**
	 * 检测当前状态是否能够进行加载章节数据
	 *
	 * @return
	 */
	private boolean checkStatus() {
		if (mStatus == STATUS_LOADING) {
//			Toast.makeText(context,"正在加载中，请稍等",Toast.LENGTH_SHORT).show();
			return false;
		} else if (mStatus == STATUS_ERROR) {
			//点击重试
			mStatus = STATUS_LOADING;
			mPageView.drawCurPage(false);
			return false;
		}
		//由于解析失败，让其退出
		return true;
	}


	/*****************************************段落标点设置*****************************************/
	/**
	 * 判断下一行的开始是否包括句首不能出现的标点符号
	 *
	 * @param content
	 * @param size
	 * @return
	 */
	public int isToAddUpLine(String content, int size) {
		size = getSubstringSize(content, size);
		return size;
	}

	/**
	 * 根据定义的标点符号来判断每行的长度大小
	 *
	 * @param content
	 * @param size
	 * @return
	 */
	private int getSubstringSize(String content, int size) {
		if (content.length() > size) {
			char singleChar = content.charAt(size);
			switch (singleChar) {
				case PunctuationType.CHCOMMA:
				case PunctuationType.ENCOMMA:
				case PunctuationType.CHCOLON:
				case PunctuationType.ENCOLON:
				case PunctuationType.CHEND:
				case PunctuationType.ENEND:
				case PunctuationType.CHTAN:
				case PunctuationType.ENTAN:
				case PunctuationType.CHQUESTIONMARK:
				case PunctuationType.CHQUOTE:
				case PunctuationType.ENQUOTE:
				case PunctuationType.CHSINGLEQUOTE:
				case PunctuationType.ENQUESTIONMARK:
				case PunctuationType.CHPAUSE:
				case PunctuationType.CHPARENTHES:
				case PunctuationType.ENPARENTHES:
				case PunctuationType.ENELLIP:
				case PunctuationType.CHBRACKET:
				case PunctuationType.ENBRACKET:
				case PunctuationType.CHCURLYBRACE:
					size++;
					break;
			}

			if (content.length() > size) {
				char nextChar = content.charAt(size);
				if (nextChar == '\n') {
					size++;
				}
			}
		}
		return size;
	}

	//得到画笔字体
	private void initCustomFont() {
		customFont = null;
		StringBuilder builder = new StringBuilder();
		String fontName = mSettingManager.getReadFont(context);
		builder.append("SP_STR_READ_FONT_NAME:").append(fontName);
		if (!fontName.equals(READ_FONT_DEFAULT_NAME)) {
			File fontFile = new File(context.getExternalFilesDir("font"), fontName + FILE_SUFFIX_TTF);
			String fontFilePath = fontFile.getAbsolutePath();
			builder.append(",fontFilePath:").append(fontFilePath);
			if (FileUtils.checkFileExists(fontFilePath)) {
				builder.append(",Exists");
				customFont = Typeface.createFromFile(fontFilePath);
			}
		}
	}

	private boolean isCustomFont() {
		return customFont != null;
	}

	/**
	 * 获取书籍章节
	 */
	public List<BookMenu> getChapterList() {
		return mChapterList;
	}


	/*****************************************interface*****************************************/

	public interface OnPageChangeListener {
		void onChapterChange(int pos);

		//请求加载回调
		void onLoadChapter(List<BookMenu> chapters, int pos);

		//当目录加载完成的回调(必须要在创建的时候，就要存在了)
		void onCategoryFinish(List<BookMenu> chapters);

		//页码改变
		void onPageCountChange(int count);

		//页面改变
		void onPageChange(int total, int pos);
	}
}
