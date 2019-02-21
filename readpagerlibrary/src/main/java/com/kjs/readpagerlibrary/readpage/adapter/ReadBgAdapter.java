package com.kjs.readpagerlibrary.readpage.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mengmengda.reader.R;
import com.mengmengda.reader.been.setting.ReadBg;

import java.util.List;

/**
 * Created by yangyan
 * on 2018/2/27.
 */

public class ReadBgAdapter extends BaseQuickAdapter<ReadBg> {

	private Context context;

	public ReadBgAdapter(Context mContext, List<ReadBg> data) {
		super(R.layout.item_read_bg, data);
		this.context = mContext;
	}

	@Override
	protected void convert(BaseViewHolder baseViewHolder, ReadBg readBg) {
		ImageView bgIv=baseViewHolder.getView(R.id.bgIv);
		bgIv.setSelected(readBg.isSelect());
		bgIv.setImageResource(readBg.getRes());
	}
}
