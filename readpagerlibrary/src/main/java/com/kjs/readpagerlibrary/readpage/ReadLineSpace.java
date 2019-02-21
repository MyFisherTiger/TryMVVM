package com.kjs.readpagerlibrary.readpage;

import android.content.Context;

import com.mengmengda.reader.util.DisplayUtil;

/**
 * 行距设置
 * Created by yangyan on 2018/2/28.
 */

public class ReadLineSpace {
    private static final int[] LINE_SPACES_FOR_480 = new int[]{10, 14, 18, 22};
    private static final int[] LINE_SPACES_FOR_720 = new int[]{16, 22, 28, 34};
    private static final int[] LINE_SPACES_FOR_1080 = new int[]{24, 32, 42, 54};

    /** 最大行距 */
    public static final int LINE_SPACE_MAX_COUNT = 3;
    public static final int LINE_SPACE_SECOND_COUNT = 2;
    public static final int LINE_SPACE_THIRD_COUNT = 1;
    /** 最小行距 */
    public static final int LINE_SPACE_MIN_COUNT = 0;

    /** 获取行距数值 */
    public static int getLineSpaceCount(Context context, int lineSpaceType) {
        int widthPixels = DisplayUtil.getScreenWidthPixels(context);
        int lineSpaceCount;

        //根据屏幕宽度，使用不同的行距方案
        if (widthPixels <= DisplayUtil.SCREEN_WIDTH_PIXELS_480) {
            lineSpaceCount = LINE_SPACES_FOR_480[lineSpaceType];
        } else if (widthPixels <= DisplayUtil.SCREEN_WIDTH_PIXELS_720) {
            lineSpaceCount = LINE_SPACES_FOR_720[lineSpaceType];
        } else {
            lineSpaceCount = LINE_SPACES_FOR_1080[lineSpaceType];
        }

        return lineSpaceCount;
    }
}
