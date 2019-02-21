package com.kjs.readpagerlibrary.readpage;

import java.util.List;

/**
 * yangyan
 * 记录每页的参数
 */

public class TxtPage {
    public int position;
    public String title;
    public int titleLines; //当前 lines 中为 title 的行数。
    public List<String> lines;
}
