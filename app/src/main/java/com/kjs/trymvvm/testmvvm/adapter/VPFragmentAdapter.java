package com.kjs.trymvvm.testmvvm.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * 作者：柯嘉少 on 2019/1/4 15:00
 * 邮箱：2449926649@qq.com
 * 说明：
 **/
public class VPFragmentAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private List<Fragment> fragmentList;
    private String[] titleStrs;
    private int[] titleIds;

    public VPFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    public VPFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList, String[] titleStrs) {
        super(fm);
        this.context = context;
        this.fragmentList = fragmentList;
        this.titleStrs = titleStrs;
    }

    public VPFragmentAdapter(Context context, FragmentManager fm, List<Fragment> fragmentList, int[] titleIds) {
        super(fm);
        this.context = context;
        this.fragmentList = fragmentList;
        this.titleIds = titleIds;
    }

    /**
     * 得到每个页面
     */
    @Override
    public Fragment getItem(int arg0) {
        return (fragmentList ==null || fragmentList.size() == 0) ? null: fragmentList.get(arg0);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titleIds != null && context != null && titleIds.length > position) {
            return context.getString(titleIds[position]);
        }
        if (titleStrs != null && titleStrs.length > position) {
            return titleStrs[position];
        }
        return super.getPageTitle(position);
    }

    /**
     * 页面的总个数
     */
    @Override
    public int getCount() {
        return fragmentList == null ? 0 : fragmentList.size();
    }


    @Override
    public int getItemPosition(Object object) {
        //return PagerAdapter.POSITION_NONE;
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        //LogUtils.info("pager", "No." + position + "被删除了");
    }

}

