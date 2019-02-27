package com.kjs.trymvvm.testmvvm;


import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.kjs.trymvvm.R;
import com.kjs.trymvvm.testmvvm.adapter.RVAdapter;
import com.kjs.trymvvm.base.BaseFragment;
import com.kjs.trymvvm.bean.ItemBean;
import com.kjs.trymvvm.databinding.FragmentMyBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends BaseFragment {
    public FragmentMyBinding binding;
    protected View mView;
    private RVAdapter adapter;
    private List<ItemBean> dataList=new ArrayList<>();
    private ItemBean itemBean;
    private ItemBean itemBean3;

    public MyFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_my;
    }

    @Override
    public void initViewModelBinding(){
        binding=(FragmentMyBinding)mBinding;
    }

    @Override
    public void observeForRefreshUI() {

    }

    @Override
    public void init() {
        itemBean=new ItemBean("1");
        itemBean3=new ItemBean("3");
        dataList.add(itemBean);
        dataList.add(new ItemBean("2"));
        dataList.add(itemBean3);
        dataList.add(new ItemBean("4"));
        adapter=new RVAdapter(dataList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity() ,LinearLayoutManager.VERTICAL,false);
        binding.rvList.setLayoutManager(layoutManager);
        binding.rvList.setAdapter(adapter);

        binding.tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("点击了","---------");
                itemBean.setTips("wobianle");
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void lazyLoadData() {

    }

    @Override
    public void doDestroy() {

    }


}
