package com.kjs.trymvvm.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 作者：柯嘉少 on 2019/2/20
 * 邮箱：2449926649@.qq.com
 * 说明：可以设置不能滑动的viewpager
 */

public class ReaderViewPager extends ViewPager {

    private boolean scrollable = true;

    public ReaderViewPager(Context context) {
        super(context);
    }

    public ReaderViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setScrollable(boolean enable) {
        scrollable = enable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (scrollable) {
            //System.out.println("允许滑动");
            return super.onInterceptTouchEvent(event);
        } else {

            // System.out.println("不准滑动XXXXXXXXXXX");
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (scrollable) {
            return super.onTouchEvent(ev);
        }else{
            return false;
        }
    }
}

