package com.stoplight.blu.toys.ble.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.AdvertiseSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blankj.utilcode.util.LogUtils;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.stoplight.blu.toys.ble.MyService;
import com.stoplight.blu.toys.ble.inter.IBlePeripheralCallback;
import com.stoplight.blu.toys.utils.BlePeripheralHelper;

import java.util.List;


/**
 * 作者: 程冲
 * 时间: 2018/3/26.
 */

public class BluetoothOpenAndClose extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {

		String action = intent.getAction();
		if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
					BluetoothAdapter.ERROR);
			switch (state) {
				case BluetoothAdapter.STATE_OFF:
					LogUtils.e("蓝牙断开");
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					LogUtils.d("手机蓝牙正在关闭");
					break;
				case BluetoothAdapter.STATE_ON:
					LogUtils.d("手机蓝牙开启");
					//myService.startBle();
					BlePeripheralHelper mBlePeripheralHelper = BlePeripheralHelper.getInstance(context);


					if (mBlePeripheralHelper.isBleEnabled()){
						// 设置回调
						System.out.println("--------------------startBle");
						mBlePeripheralHelper.setBlePeripheralCallback(new IBlePeripheralCallback() {
							//连接状态回调
							@Override
							public void onConnectionStateChange(BluetoothDevice bluetoothDevice, int i, int i1) {
								System.out.println(bluetoothDevice);
							}

							//开启广播成功回调
							@Override
							public void onStartAbSuccess(AdvertiseSettings advertiseSettings) {
								System.out.println(advertiseSettings);
							}

							//开启广失败的功回调
							@Override
							public void onStartAbFailure(int i) {
								System.out.println(i);
							}
							//收到BLE数据回调
							@Override
							public void onReceiveNewBytes(BluetoothDevice bluetoothDevice, int i, BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bytes) {
								System.out.println(bluetoothDevice);
							}

							//发送BLE数据回调
							@Override
							public void onWriteBytesAndStatus(boolean b, byte[] bytes) {
								System.out.println(bytes);
							}
						});


						// 初始化广播
						mBlePeripheralHelper.initGATTServer();
					}


					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					LogUtils.d("STATE_TURNING_ON 手机蓝牙正在开启");
					break;
			}
		}
	}

	public BluetoothOpenAndClose (){

	}

}
