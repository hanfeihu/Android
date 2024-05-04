package com.stoplight.blu.toys.ble;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.AdvertiseSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.stoplight.blu.toys.ble.inter.IBlePeripheralCallback;
import com.stoplight.blu.toys.ble.receiver.BluetoothOpenAndClose;
import com.stoplight.blu.toys.utils.BlePeripheralHelper;

public class MyService extends Service {
    IntentFilter connectedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

    public MyService() {


        System.out.println("--------------------MyService");
    }


       public BlePeripheralHelper mBlePeripheralHelper ;

    private BluetoothOpenAndClose bluetoothOpenAndClose;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        mBlePeripheralHelper= BlePeripheralHelper.getInstance(this);
         connectedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
         bluetoothOpenAndClose=new BluetoothOpenAndClose();
        registerReceiver(bluetoothOpenAndClose,connectedFilter);
        System.out.println("--------------------onCreate");

        startBle();
        super.onCreate();

    }

    @Override
    public ComponentName startService(Intent service) {
        startBle();
        System.out.println("--------------------startService");
        return super.startService(service);
    }



    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        System.out.println("--------------------onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
       // unregisterReceiver(bluetoothOpenAndClose);
      //  registerReceiver(bluetoothOpenAndClose,connectedFilter);

        mBlePeripheralHelper= BlePeripheralHelper.getInstance(this);


        System.out.println("--------------------onDestroy");
        Intent localIntent = new Intent(this, MyService.class);
        this.startService(localIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("--------------------intent");

        return null;

    }


    public  void startBle(){






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
    }
}
