package com.stoplight.blu.toys.ble.inter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.AdvertiseSettings;



/**
 * Created by Roy.lee
 * On 2021/6/27
 * Email: 631934797@qq.com
 * Description:
 */
public interface IBlePeripheralCallback {

    /**
     * Connection status callback
     *
     * @param device
     * @param status
     * @param newState
     */
    void onConnectionStateChange(BluetoothDevice device, int status, int newState);

    /**
     *  Advertise success callback
     *
     * @param settingsInEffect
     */
    void onStartAbSuccess(AdvertiseSettings settingsInEffect);

    /**
     *  Advertise failure callback
     *
     * @param errorCode
     */
    void onStartAbFailure(int errorCode);

    /**
     *  Receive new data callback
     *
     * @param device
     * @param requestId
     * @param characteristic
     */
    void onReceiveNewBytes(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, byte[] reqeustBytes);

    /**
     * Send data status callback
     * @param status
     * @param bytes
     */
    void onWriteBytesAndStatus(boolean status, byte[] bytes);

}
