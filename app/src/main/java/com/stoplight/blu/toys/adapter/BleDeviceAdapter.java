package com.stoplight.blu.toys.adapter;


import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


import com.clj.fastble.data.BleDevice;
import com.stoplight.blu.toys.R;

import java.util.List;

public class BleDeviceAdapter extends BaseQuickAdapter<BleDevice, BaseViewHolder> {


	public BleDeviceAdapter(int layoutResId, List<BleDevice> data) {
		super(layoutResId, data);
	}

	@Override
	protected void convert(BaseViewHolder helper, BleDevice item) {
		String deviceName = item.getName();
		if ((!TextUtils.isEmpty(deviceName) && deviceName.contains("Traffic"))||!TextUtils.isEmpty(deviceName) && deviceName.contains("Traffic")) {
			helper.setText(R.id.dv_name, item.getShowName() + "");
		}
	}
}