package com.stoplight.blu.toys.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CollectionUtils;
import com.clj.fastble.data.BleDevice;
import com.stoplight.blu.toys.R;
import com.stoplight.blu.toys.adapter.BleDeviceAdapter;
import com.stoplight.blu.toys.app.AppManager;
import com.stoplight.blu.toys.ble.BlePlay;
import com.stoplight.blu.toys.ble.MyService;
import com.stoplight.blu.toys.ble.inter.IBlePeripheralCallback;
import com.stoplight.blu.toys.utils.BlePeripheralHelper;
import com.stoplight.blu.toys.view.TypefaceTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Logger;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectDeviceActivity extends BaseActivity {

	@BindView(R.id.recyclerView)
	RecyclerView mRecyclerView;
	@BindView(R.id.device_not_found)
	TypefaceTextView mDeviceNotFound;

	BleDeviceAdapter bleDeviceAdapter;
	private List<BleDevice> bleDevices;

	@Override
	public int getLayout() {
		return R.layout.activity_seleclt_device;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		if (bleDevices == null) {
			bleDevices = new ArrayList<>();
		}
		mRecyclerView.setLayoutManager(getLinearLayout());
		bleDeviceAdapter = new BleDeviceAdapter(R.layout.ble_devices_item, bleDevices);
		mRecyclerView.setAdapter(bleDeviceAdapter);
	}

	@Override
	public void initData() {
		bleDeviceAdapter.setOnItemClickListener((adapter, view, position) -> {
		BleDevice bleDevice = (BleDevice) adapter.getItem(position);



			BlePlay.getInstance().connectedBleDevice(bleDevice);
			System.out.println(bleDevice.getShowName());

			//Intent intent = new Intent(SelectDeviceActivity.this, MyService.class);
			//startService(intent);




		});
		showBleDevices();
	}


	public void showBleDevices() {
		List<BleDevice> bles = BlePlay.getInstance().getScanResultList();
		if (CollectionUtils.isEmpty(bles)) {
			mDeviceNotFound.setVisibility(View.VISIBLE);
			return;
		}

		bleDevices.clear();
		for (int i = 0; i < bles.size(); i++) {
			BleDevice bleDevice = bles.get(i);
			String deviceName = bleDevice.getName();
			if (!TextUtils.isEmpty(deviceName) && deviceName.contains("Traffic")) {
				bleDevices.add(bleDevice);
			}
		}
		if (CollectionUtils.isEmpty(bleDevices)) {
			mDeviceNotFound.setVisibility(View.VISIBLE);
		}
		if (bleDeviceAdapter != null) {
			bleDeviceAdapter.notifyDataSetChanged();
		}
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void connDevices(BleDevice ble) {
		goActivity(SetTimeActivity.class);
		AppManager.getInstance().finishActivity();
	}


	@OnClick(R.id.back_but)
	public void onViewClicked() {
		AppManager.getInstance().finishActivity();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

}