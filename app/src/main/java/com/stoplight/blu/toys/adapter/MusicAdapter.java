package com.stoplight.blu.toys.adapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.stoplight.blu.toys.R;
import com.stoplight.blu.toys.utils.SpUtils;

import java.util.List;

public class MusicAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {


	private String musicType;

	public MusicAdapter(int layoutResId, List<Integer> data, String musicType) {
		super(layoutResId, data);
		this.musicType = musicType;
	}

	@Override
	protected void convert(BaseViewHolder helper, Integer item) {
		SpUtils spUtils = SpUtils.getSpUtils(mContext);
		int spValue = spUtils.getSPValue(musicType, -1);
		if (spValue == item) {
			helper.setTextColor(R.id.dv_name, mContext.getResources().getColor(R.color.backer_back));
		}
		helper.setText(R.id.dv_name, item + "");
	}
}