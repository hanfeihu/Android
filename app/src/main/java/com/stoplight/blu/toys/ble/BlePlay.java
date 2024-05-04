package com.stoplight.blu.toys.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.stoplight.blu.toys.app.App;
import com.stoplight.blu.toys.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.UUID;


@SuppressWarnings("all")
public class BlePlay {


//	String UUID_SERVICE = "00001812-0000-1000-8000-00805f9b34fb";
//	String UUID_WRITE = "0000180f-0000-1000-8000-00805f9b34fb";
//
//	String UUID_READ = "0000ae02-0000-1000-8000-00805f9b34fb";
//
//
//	UUID[] serviceIds = {UUID.fromString("49535343-fe7d-4ae5-8fa9-9fafd205e455"),
//			UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"),
//			UUID.fromString("00001812-0000-1000-8000-00805f9b34fb")};


	String UUID_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
	String UUID_WRITE = "0000ff4-0000-1000-8000-00805f9b34fb";
	String UUID_READ = "0000ffe5-0000-1000-8000-00805f9b34fb";


	private static BlePlay instance;

	private SpUtils mSpUtils;

	public List<BleDevice> getScanResultList() {
		return scanResultList;
	}

	public void setScanResultList(List<BleDevice> scanResultList) {
		this.scanResultList = scanResultList;
	}

	private List<BleDevice> scanResultList;

	public static BlePlay getInstance() {
		if (instance == null) {
			synchronized (BlePlay.class) {
				if (instance == null) {
					instance = new BlePlay();
				}
			}
		}
		return instance;
	}

	private BleDevice bleDevice;


	private BlePlay() {
		mSpUtils = SpUtils.getSpUtils(App.getInstance());
	}


	public BleDevice getBleDevice() {
		return bleDevice;
	}

	public void setBleDevice(BleDevice bleDevice) {
		this.bleDevice = bleDevice;
	}


	/**
	 * 扫描设备 30000
	 */
	public void scanBle() {
		BleManager bleManager = BleManager.getInstance();
		BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder().setAutoConnect(false)
				.setScanTimeOut(5000).build();
		bleManager.initScanRule(scanRuleConfig);
		BleManager.getInstance().scan(new BleScanCallback() {
			@Override
			public void onScanStarted(boolean success) {
				LogUtils.e("开始扫描");
			}

			@Override
			public void onScanning(BleDevice bleDevice) {
				LogUtils.e("获取设备");

			}

			@Override
			public void onScanFinished(List<BleDevice> scanResultList) {
				LogUtils.e("扫描介素获取设备列表");
				LogUtils.e("--------哈哈哈哈---");
				setScanResultList(scanResultList);
				EventBus.getDefault().post("OK");
			}
		});
	}


	/**
	 * @param mac 地址
	 */
	public void connectedCac(String mac) {

	}


	/**
	 * @param name 名字
	 */
	public void connectedName(String name) {

	}

	/**
	 * @param bleDevice 设备
	 */
	public void connectedBleDevice(BleDevice bleDevice) {
		BleManager.getInstance().connect(bleDevice, new BleGattCallback() {

			//开始链接
			@Override
			public void onStartConnect() {

			}

			//链接失败
			@Override
			public void onConnectFail(BleDevice bleDevice, BleException exception) {

			}

			//链接成功
			@Override
			public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
				setBleDevice(bleDevice);
				List<BluetoothGattService> serviceList = gatt.getServices();

				for (BluetoothGattService service : serviceList) {

					UUID uuid_service = service.getUuid();

					if (UUID_SERVICE.equals(uuid_service)){}


					LogUtils.e("uuid_service----" + uuid_service);

					List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
					for (BluetoothGattCharacteristic characteristic : characteristicList) {
						UUID uuid_chara = characteristic.getUuid();


						int properties = characteristic.getProperties();

						LogUtils.e("uuid_chara----" + uuid_service);
					}


				}

				EventBus.getDefault().post(bleDevice);
			}

			//关闭链接
			@Override
			public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

			}
		});

	}




/*	public boolean pair(BluetoothDevice device) {
		String TAG="pair";
		// Stops the discovery and then creates the pairing.
		if (bluetooth.isDiscovering()) {
			Log.d(TAG, "Bluetooth cancelling discovery.");
			bluetooth.cancelDiscovery();
		}
		Log.d(TAG, "Bluetooth bonding with device: " + device.getName());
		boolean outcome = device.createBond();
		Log.d(TAG, "Bounding outcome : " + outcome);

		// If the outcome is true, we are bounding with this device.
		if (outcome == true) {
			this.boundingDevice = device;
		}
		return outcome;
	}*/


	/**
	 * 暂停
	 */
	public void sendCance(byte[] data,BleWriteCallback callback) {
		BleManager.getInstance().write(
				bleDevice,
				UUID_SERVICE,
				UUID_WRITE,
				data,
				new BleWriteCallback() {
					@Override
					public void onWriteSuccess(int current, int total, byte[] justWrite) {
						callback.onWriteSuccess(current,total,justWrite);
					}

					@Override
					public void onWriteFailure(BleException exception) {
						callback.onWriteFailure(exception);
					}
				});

	}


	/**
	 * 开始/继续
	 */
	public void sendStard(byte[] data,BleWriteCallback callback) {
		BleManager.getInstance().write(bleDevice, UUID_SERVICE, UUID_WRITE, data, new BleWriteCallback() {
			@Override
			public void onWriteSuccess(int current, int total, byte[] justWrite) {
				callback.onWriteSuccess(current,total,justWrite);

			}

			@Override
			public void onWriteFailure(BleException exception) {
				callback.onWriteFailure(exception);
			}
		});

	}


	/**
	 * 暂停
	 */
	public void sendPause(byte[] data,BleWriteCallback callback) {
		BleManager.getInstance().write(
				bleDevice,
				UUID_SERVICE,
				UUID_WRITE,
				data,
				new BleWriteCallback() {
					@Override
					public void onWriteSuccess(int current, int total, byte[] justWrite) {
						callback.onWriteSuccess(current,total,justWrite);
					}

					@Override
					public void onWriteFailure(BleException exception) {
						callback.onWriteFailure(exception);
					}
				});


	}


	/**
	 * 取消扫描
	 */
	public void canceScan() {
		BleManager.getInstance().cancelScan();
	}


	/**
	 * 蓝牙是否连接
	 *
	 * @rturn
	 */
	public boolean isConnected() {
		return BleManager.getInstance().isConnected(bleDevice);
	}

	/**
	 * 断开蓝牙链接
	 */
	public void closeBle() {
		BleManager.getInstance().disconnect(bleDevice);
	}

	/**
	 * 是否打开蓝牙
	 *
	 * @return
	 */
	public boolean isBleOpen() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		return adapter == null ? false : true;
	}


}
