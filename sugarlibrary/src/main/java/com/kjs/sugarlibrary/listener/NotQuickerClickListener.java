package com.kjs.sugarlibrary.listener;

import android.view.View;

/**
 * 作者：柯嘉少 on 2018/12/4 14:02
 * 邮箱：2449926649@qq.com
 * 说明：防止快速点击，产生多事件的事件监听器
 *
 * 建议所有的OnClickListener都使用此监听代替
 **/
public abstract class NotQuickerClickListener implements View.OnClickListener{

    private long currentTime=0;
    private long lastTime=0;
    private int MIN_Time=1000;//默认一秒内防止重复点击

    /**
     * 构造方法
     */
    public NotQuickerClickListener() {
    }

    /**
     * 构造方法
     * @param MIN_Time 设置在此时间内，防止重复点击，单位毫秒，1000=1秒
     */
    public NotQuickerClickListener(int MIN_Time) {
        this.MIN_Time = MIN_Time;
    }

    public abstract void forbidClick(View view);

    @Override
    public void onClick(View v) {
        currentTime=System.currentTimeMillis();
        if(currentTime-lastTime>MIN_Time){
            lastTime=currentTime;
            forbidClick(v);
        }
    }
}
