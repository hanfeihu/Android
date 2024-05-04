package com.stoplight.blu.toys.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.Log;

import com.stoplight.blu.toys.ble.inter.IBlePeripheralCallback;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Roy.lee
 * On 2021/6/27
 * Email: 631934797@qq.com
 * Description:
 */
public class BlePeripheralHelper {

    private static final String TAG = "@@@ ===> " + BlePeripheralHelper.class.getSimpleName();

    // 这里的参数可自行定义
    private static String BLE_NAME = "SmartBox";
    private static final UUID UUID_SERVER = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_CHARREAD = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_CHARWRITE = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private BluetoothGattCharacteristic mCharacteristicRead;

    //BluetoothHelper
    private static BlePeripheralHelper mBlePeripheralHelper;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private static Context mContext;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private BluetoothGattServer mBluetoothGattServer;
    private IBlePeripheralCallback mBlePeripheralCallback;

    private BlePeripheralHelper() {
        initPeripheral(mContext);
    }

    /**
     * 获取BleController实例对象
     * @return
     */
    public synchronized static BlePeripheralHelper getInstance(Context context) {
        mContext = context;
        if (null == mBlePeripheralHelper) {
            mBlePeripheralHelper = new BlePeripheralHelper();

        }
        return mBlePeripheralHelper;
    }

    /**
     * 初始化BLE相关参数
     *
     * @param context
     */
    private void initPeripheral(Context context) {
        // 初始化mBluetoothManager
        mBluetoothManager = getBleManager(mContext);
        // 初始化mBluetoothAdapter
        mBluetoothAdapter = getBluetoothAdapter();

        if (isBleEnabled()){
            mBluetoothLeAdvertiser = getBluetoothLeAdvertiser();
        }else {
            if (setBleEnabled(true)){
                SystemClock.sleep(1000);
                mBluetoothLeAdvertiser = getBluetoothLeAdvertiser();
            }
        }

        if (mBluetoothLeAdvertiser == null){
            Log.e(TAG, "== The device not support peripheral ==");
        }

        // 注册BLE的状态变化广播
        context.registerReceiver(mBluetoothReceiver, makeGattUpdateIntentFilter());

    }

    /**
     * 初始化BLE广播
     */
    public void initGATTServer() {
        initGATTServer(null);
    }



    /**
     * 初始化BLE广播并设置BEL_NAME
     * @param bleName
     */
    @SuppressLint("InlinedApi")
    public void initGATTServer(String bleName)   {


        if (bleName != null && !bleName.isEmpty())
            BLE_NAME = bleName;
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setConnectable(true)
                .setTimeout(0)        //超时时间
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)  //广播模式
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)   //发射功率
                .build();

        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)    //是否在广播中携带设备的名称
                .setIncludeTxPowerLevel(true)  //是否在广播中携带信号强度
                .build();

        AdvertiseData scanResponseData = new AdvertiseData.Builder()
                .addServiceUuid(new ParcelUuid(UUID_SERVER))
                .setIncludeTxPowerLevel(true)
                .build();

        //设置BLE设备的名称
        mBluetoothAdapter.setName(BLE_NAME);

        /**
         * 开启广播的结果callback
         */
        AdvertiseCallback mAdCallback = new AdvertiseCallback() {

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.d(TAG, "BLE advertisement added successfully");

                // TODO 初始化服务
                initServices();
                if (mBlePeripheralCallback != null)
                    mBlePeripheralCallback.onStartAbSuccess(settingsInEffect);

            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e(TAG, "Failed to add BLE advertisement, reason: " + errorCode);
                if (mBlePeripheralCallback != null)
                    mBlePeripheralCallback.onStartAbFailure(errorCode);
            }
        };

        //开启广播
        if (mBluetoothLeAdvertiser != null)
            mBluetoothLeAdvertiser.startAdvertising(settings, advertiseData, scanResponseData, mAdCallback);

    }

    /**
     * 初始化广播服务参数
     */
    private void initServices() {
        mBluetoothGattServer = getBluetoothGattServer(mBluetoothGattServerCallback);
        BluetoothGattService service = new BluetoothGattService(UUID_SERVER, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        //add a read characteristic.
        mCharacteristicRead = new BluetoothGattCharacteristic(UUID_CHARREAD, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
        //add a descriptor
        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(UUID_DESCRIPTOR, BluetoothGattCharacteristic.PERMISSION_WRITE);
        mCharacteristicRead.addDescriptor(descriptor);
        service.addCharacteristic(mCharacteristicRead);

        //add a write characteristic.
        BluetoothGattCharacteristic characteristicWrite = new BluetoothGattCharacteristic(UUID_CHARWRITE,
                BluetoothGattCharacteristic.PROPERTY_WRITE |
                        BluetoothGattCharacteristic.PROPERTY_READ |
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        service.addCharacteristic(characteristicWrite);

        mBluetoothGattServer.addService(service);
        Log.e(TAG, "2. initServices ok");

    }

    /**
     * 获取BluetoothLeAdvertiser
     *
     */
    @SuppressLint("InlinedApi")
    private BluetoothLeAdvertiser getBluetoothLeAdvertiser() {
        return  mBluetoothAdapter == null ? null : mBluetoothAdapter.getBluetoothLeAdvertiser();
    }

    /**
     * 获取BluetoothGattServer
     *
     */
    private BluetoothGattServer getBluetoothGattServer(BluetoothGattServerCallback bluetoothGattServerCallback){
        return mBluetoothManager == null ? null : mBluetoothManager.openGattServer(mContext, bluetoothGattServerCallback);
    }

    /**
     * 获取BluetoothManager
     *
     * @param context
     * @return
     */
    private BluetoothManager getBleManager(Context context) {
        return context == null ? null : (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    /**
     * 获取BluetoothAdapter
     *
     * @return
     */
    private BluetoothAdapter getBluetoothAdapter(){
        return mBluetoothManager == null ? null : mBluetoothManager.getAdapter();
    }

    /**
     *
     * 开启/关闭BLE
     *
     * @param enabled
     * @return
     */
    public boolean setBleEnabled(boolean enabled){

        if (enabled){
            return  mBluetoothAdapter == null ? false : mBluetoothAdapter.enable();
        }else {
            return  mBluetoothAdapter == null ? false : mBluetoothAdapter.disable();
        }

    }

    /**
     * 获取BLE状态
     * @return
     */
    public boolean isBleEnabled(){
        return  mBluetoothAdapter == null ? false : mBluetoothAdapter.isEnabled();
    }


// ========================================================BluetoothGattServerCallback=========================================


    /**
     * 服务事件的回调
     */
    private BluetoothGattServerCallback mBluetoothGattServerCallback = new BluetoothGattServerCallback() {

        /**
         * 1.连接状态发生变化时
         * @param device
         * @param status
         * @param newState
         */
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            Log.w(TAG,"onConnectionStateChange  [ status : " + status + " |  newState : " + newState + "]");

            Log.e(TAG, String.format("1.onConnectionStateChange：device name = %s, address = %s", device.getName(), device.getAddress()));
            Log.e(TAG, String.format("1.onConnectionStateChange：status = %s, newState =%s ", status, newState));
            super.onConnectionStateChange(device, status, newState);
            if (mBlePeripheralCallback != null)
                mBlePeripheralCallback.onConnectionStateChange(device, status, newState);
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
            Log.e(TAG, String.format("onServiceAdded：status = %s", status));
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            Log.e(TAG, String.format("onCharacteristicReadRequest：device name = %s, address = %s", device.getName(), device.getAddress()));
            Log.e(TAG, String.format("onCharacteristicReadRequest：requestId = %s, offset = %s", requestId, offset));

            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
//            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
        }

        /**
         * 3. onCharacteristicWriteRequest,接收具体的字节
         * @param device
         * @param requestId
         * @param characteristic
         * @param preparedWrite
         * @param responseNeeded
         * @param offset
         * @param requestBytes
         */
        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] requestBytes) {
            Log.e(TAG, String.format("3.onCharacteristicWriteRequest：device name = %s, address = %s", device.getName(), device.getAddress()));
            Log.e(TAG, String.format("3.onCharacteristicWriteRequest：requestId = %s, preparedWrite=%s, responseNeeded=%s, offset=%s, value=%s", requestId, preparedWrite, responseNeeded, offset, requestBytes));
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, requestBytes);
            //  TODO 4.处理响应内容
            onResponseToClient( device, requestId, characteristic,requestBytes);
        }

        /**
         * 2.描述被写入时，在这里执行 bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS...  收，触发 onCharacteristicWriteRequest
         * @param device
         * @param requestId
         * @param descriptor
         * @param preparedWrite
         * @param responseNeeded
         * @param offset
         * @param value
         */
        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            Log.e(TAG, String.format("2.onDescriptorWriteRequest：device name = %s, address = %s", device.getName(), device.getAddress()));
            Log.e(TAG, String.format("2.onDescriptorWriteRequest：requestId = %s, preparedWrite = %s, responseNeeded = %s, offset = %s, value = %s,", requestId, preparedWrite, responseNeeded, offset, value));

            // now tell the connected device that this was all successfull
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
        }

        /**
         * 5.特征被读取。当回复响应成功后，客户端会读取然后触发本方法
         * @param device
         * @param requestId
         * @param offset
         * @param descriptor
         */
        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            Log.e(TAG, String.format("onDescriptorReadRequest：device name = %s, address = %s", device.getName(), device.getAddress()));
            Log.e(TAG, String.format("onDescriptorReadRequest：requestId = %s", requestId));
//            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            Log.e(TAG, String.format("5.onNotificationSent：device name = %s, address = %s", device.getName(), device.getAddress()));
            Log.e(TAG, String.format("5.onNotificationSent：status = %s", status));
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
            Log.e(TAG, String.format("onMtuChanged：mtu = %s", mtu));
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
            Log.e(TAG, String.format("onExecuteWrite：requestId = %s", requestId));
        }
    };

    /**
     * 4.处理响应内容
     *
     * @param reqeustBytes
     * @param device
     * @param requestId
     * @param characteristic
     */
    private void onResponseToClient(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, byte[] reqeustBytes) {
        Log.e(TAG, String.format("4.onResponseToClient：device name = %s, address = %s", device.getName(), device.getAddress()));
        Log.e(TAG, String.format("4.onResponseToClient：requestId = %s", requestId));

        if (mBlePeripheralCallback != null){
            // 数据回调
            mBlePeripheralCallback.onReceiveNewBytes(device, requestId, characteristic, reqeustBytes);

        }

    }


    /**
     * 发送数据
     * @param device
     * @param data
     * @return
     */

    public boolean transfer(BluetoothDevice device, byte[] data){
        if (notify(device,mCharacteristicRead,data)){
            if (mBlePeripheralCallback != null)
                mBlePeripheralCallback.onWriteBytesAndStatus(true, data);
            return true;
        }else {
            if (mBlePeripheralCallback != null)
                mBlePeripheralCallback.onWriteBytesAndStatus(false, data);
            return false;
        }
    }


    /**
     * 发送通知给主机
     *
     * @param device         ：发送的目标设备
     * @param characteristic ：用来通知的characteristic
     * @param data           ：通知的内容
     */
    private boolean notify(BluetoothDevice device, BluetoothGattCharacteristic characteristic, byte[] data) {
        if (device != null && characteristic != null && data != null) {
            //设置写操作的类型 WRITE_TYPE_DEFAULT的情况选  底层会自动分包 不用人为分包
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            //把要设置的数据装进characteristic
            characteristic.setValue(data);
            //发送出去
            return mBluetoothGattServer.notifyCharacteristicChanged(device, characteristic, false);
        } else {
            return false;
        }

    }


    /**
     *
     * @return
     */
    public IBlePeripheralCallback getBlePeripheralCallback() {
        return mBlePeripheralCallback;
    }

    public void setBlePeripheralCallback(IBlePeripheralCallback blePeripheralCallback) {
        this.mBlePeripheralCallback = blePeripheralCallback;
    }


    //========================================================BluetoothReceiver======================================================


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        intentFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return intentFilter;
    }

    // 监听BLE状态变化
    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,"=========蓝牙接收处理广播========"+intent.getAction());
            BluetoothDevice device;

            switch (intent.getAction()){
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int bleState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                    switch (bleState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.i(TAG, "...正在关闭蓝牙...");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.i(TAG, "...蓝牙已关闭！");
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.i(TAG, "...正在开启蓝牙...");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.i(TAG, "...蓝牙已开启...");

                            if (mBlePeripheralHelper != null)
                                initPeripheral(mContext);
                            initGATTServer();
                            break;
                    }
                    break;

                case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                    switch (intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1)) {

                        case BluetoothA2dp.STATE_CONNECTING:
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            Log.i(TAG,"device: " + device.getName() +" connecting");
                            break;
                        case BluetoothA2dp.STATE_CONNECTED:
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            Log.i(TAG,"device: " + device.getName() +" connected");

                            break;
                        case BluetoothA2dp.STATE_DISCONNECTING:
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            Log.i(TAG,"device: " + device.getName() +" disconnecting");
                            break;
                        case BluetoothA2dp.STATE_DISCONNECTED:
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            Log.i(TAG,"device: " + device.getName() +" disconnected");
                            break;
                        default:
                            break;
                    }
                    break;

                case BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1);
                    switch (state) {
                        case BluetoothA2dp.STATE_PLAYING:
                            Log.i(TAG,"state: playing.");
                            break;
                        case BluetoothA2dp.STATE_NOT_PLAYING:
                            Log.i(TAG,"state: not playing");
                            break;
                        default:
                            Log.i(TAG,"state: unkown");
                            break;
                    }
                    break;



                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    switch (bondState){
                        case BluetoothDevice.BOND_BONDED:  //配对成功
                            Log.i(TAG,"Device:"+device.getName()+" bonded.");
                            //取消搜索，连接蓝牙设备
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            Log.i(TAG,"Device:"+device.getName()+" bonding.");
                            break;
                        case BluetoothDevice.BOND_NONE:
                            Log.i(TAG,"Device:"+device.getName()+" not bonded.");

                            break;
                        default:
                            break;
                    }
                    break;


                default:
                    break;
            }
        }
    };

}

