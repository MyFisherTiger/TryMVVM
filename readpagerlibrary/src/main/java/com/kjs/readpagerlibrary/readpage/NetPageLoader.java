package com.kjs.readpagerlibrary.readpage;

import android.content.Context;
import android.support.annotation.Nullable;

import com.mengmengda.reader.been.BookInfo;
import com.mengmengda.reader.been.BookMenu;
import com.mengmengda.reader.common.AppConfig;
import com.mengmengda.reader.listener.ReadActionListener;
import com.mengmengda.reader.util.FileUtils;
import com.mengmengda.reader.widget.LoadingContentView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by yangyan on 17-5-29.
 * 网络页面加载器
 */

public class NetPageLoader extends PageLoader {
	private static final String TAG = "PageFactory";

	public NetPageLoader(Context context, PageView pageView, LoadingContentView loadingContentView, ReadActionListener readActionListener) {
		super(context, pageView,loadingContentView,readActionListener);
	}

	//初始化书籍
	@Override
	public void openBook(BookInfo bookInfo,int pos,boolean isInitPagePos) {
		super.openBook(bookInfo,pos,isInitPagePos);
		isBookOpen = false;
		if (bookInfo.menuInfoList == null) return;
		mChapterList = bookInfo.menuInfoList;
		//设置目录回调
		if (mPageChangeListener != null) {
			mPageChangeListener.onCategoryFinish(mChapterList);
		}
		//提示加载下面的章节
		loadCurrentChapter();
	}

	@Override
	public void saveRecord() {
		super.saveRecord();
	}

	@Nullable
	@Override
	public List<TxtPage> loadPageList(int chapter) {
		if (mChapterList == null) {
			throw new IllegalArgumentException("chapter list must not null");
		}

		//获取要加载的文件
		BookMenu txtChapter = mChapterList.get(chapter - 1);
		String menuFilePath = String.format(Locale.getDefault(), "%s/bookcon/%s/%s.t", AppConfig.SDPATH, bookInfo.bookId+"", txtChapter.menuId+"");
		//防止旧版本缓存出现问题，等版本稳定可以考虑去掉
		String content=FileUtils.ReadTxtFile(menuFilePath);
		if(content.contains("\\r\\n")){
			String contentStr=content.replaceAll("\\\\r\\\\n", "\n");
			String strMenuPath = AppConfig.SDPATH + "bookcon/" +  bookInfo.bookId + "/";
			FileUtils.WriterTxtFile(strMenuPath, txtChapter.menuId+"" + ".t", contentStr, false);
		}

		File file = new File(menuFilePath);
		if (!file.exists()) {
			return null;
		}

		Reader reader = null;
		try {
			reader = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(reader);
		return loadPages(txtChapter, br);
	}

	//装载上一章节的内容
	@Override
	boolean prevChapter(boolean showTip) {

		boolean hasPrev = super.prevChapter(showTip);
		if (!hasPrev) return false;

		if (mStatus == STATUS_FINISH) {
			loadCurrentChapter();
			return true;
		} else if (mStatus == STATUS_LOADING) {
			loadCurrentChapter();
			return false;
		}
		return false;
	}

	//装载下一章节的内容
	@Override
	boolean nextChapter(boolean showTip) {
		boolean hasNext = super.nextChapter(showTip);
		if (!hasNext) return false;
		if (mStatus == STATUS_FINISH) {
			loadNextChapter();
			return true;
		} else if (mStatus == STATUS_LOADING) {
			loadCurrentChapter();
			return false;
		}
		return false;
	}

	//跳转到指定章节
	public void skipToChapter(int pos) {
		super.skipToChapter(pos);

		//提示章节改变，需要下载
		loadCurrentChapter();
	}

	private void loadPrevChapter() {
		//提示加载上一章
		if (mPageChangeListener != null) {
			//提示加载前面3个章节（不包括当前章节）
			int current = mCurChapterPos;
			int prev = current - 3;
			if (prev < 0) {
				prev = 0;
			}
			mPageChangeListener.onLoadChapter(mChapterList.subList(prev, current), mCurChapterPos);
		}
	}

	private void loadCurrentChapter() {
		if (mPageChangeListener != null) {
			List<BookMenu> bookChapters = new ArrayList<>(5);
			//提示加载当前章节和前面两章和后面两章
			int current = mCurChapterPos - 1;
			if (mChapterList != null && mChapterList.size() > 0) {
				bookChapters.add(mChapterList.get(current));
				//如果当前已经是最后一章，那么就没有必要加载后面章节
				if (current != mChapterList.size()) {
					int begin = current + 1;
					int next = begin + 2;
					if (begin < mChapterList.size()) {
						if (next <= mChapterList.size()) {
							bookChapters.addAll(mChapterList.subList(begin, next));
						} else {
							bookChapters.addAll(mChapterList.subList(begin, mChapterList.size()));
						}
					}
				}

				//如果当前已经是第一章，那么就没有必要加载前面章节
				if (current != 0) {
					int prev = current - 2;
					if (prev < 0) {
						prev = 0;
					}
					bookChapters.addAll(mChapterList.subList(prev, current));
				}
			}

			mPageChangeListener.onLoadChapter(bookChapters, mCurChapterPos);
		}
	}

	private void loadNextChapter() {
		//提示加载下一章
		if (mPageChangeListener != null) {
			//提示加载当前章节和后面3个章节
			int current = mCurChapterPos + 1;
			int next = mCurChapterPos + 3;
			if(current>mChapterList.size()){
				current=mChapterList.size();
			}
			if (next > mChapterList.size()) {
				next = mChapterList.size();
			}
			mPageChangeListener.onLoadChapter(mChapterList.subList(current, next), mCurChapterPos);
		}
	}

	@Override
	public void setChapterList(List<BookMenu> bookChapters) {
		if (bookChapters == null) return;

		mChapterList = bookChapters;

		if (mPageChangeListener != null) {
			mPageChangeListener.onCategoryFinish(mChapterList);
		}
	}
}

