package com.clj.fastble.bluetooth;


import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.utils.BleLruHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleBluetoothController {

    private BleLruHashMap<String, BleBluetooth> bleLruHashMap;
    private HashMap<String, BleBluetooth> bleTempHashMap;

    public MultipleBluetoothController() {
        bleLruHashMap = new BleLruHashMap<>(BleManager.getInstance().getMaxConnectCount());
        bleTempHashMap = new HashMap<>();
    }

    public BleBluetooth buildConnectingBle(BleDevice bleDevice) {
        BleBluetooth bleBluetooth = new BleBluetooth(bleDevice);
        if (!bleTempHashMap.containsKey(bleBluetooth.getDeviceKey())) {
            bleTempHashMap.put(bleBluetooth.getDeviceKey(), bleBluetooth);
        }
        return bleBluetooth;
    }

    public void removeConnectingBle(BleBluetooth bleBluetooth) {
        if (bleBluetooth == null) {
            return;
        }
        if (bleTempHashMap.containsKey(bleBluetooth.getDeviceKey())) {
            bleTempHashMap.remove(bleBluetooth.getDeviceKey());
        }
    }

    public void addBleBluetooth(BleBluetooth bleBluetooth) {
        if (bleBluetooth == null) {
            return;
        }
        if (!bleLruHashMap.containsKey(bleBluetooth.getDeviceKey())) {
            bleLruHashMap.put(bleBluetooth.getDeviceKey(), bleBluetooth);
        }
    }

    public void removeBleBluetooth(BleBluetooth bleBluetooth) {
        if (bleBluetooth == null) {
            return;
        }
        if (bleLruHashMap.containsKey(bleBluetooth.getDeviceKey())) {
            bleLruHashMap.remove(bleBluetooth.getDeviceKey());
        }
    }

    public boolean isContainDevice(BleDevice bleDevice) {
        return bleDevice != null && bleLruHashMap.containsKey(bleDevice.getKey());
    }

    public boolean isContainDevice(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice != null && bleLruHashMap.containsKey(bluetoothDevice.getName() + bluetoothDevice.getAddress());
    }

    public BleBluetooth getBleBluetooth(BleDevice bleDevice) {
        if (bleDevice != null) {
            if (bleLruHashMap.containsKey(bleDevice.getKey())) {
                return bleLruHashMap.get(bleDevice.getKey());
            }
        }
        return null;
    }

    public void disconnect(BleDevice bleDevice) {
        if (isContainDevice(bleDevice)) {
            getBleBluetooth(bleDevice).disconnect();
        }
    }


    public void disconnectAllDevice() {
        Log.i("mkk", "断连接大活动===");
        for (Map.Entry<String, BleBluetooth> stringBleBluetoothEntry : bleLruHashMap.entrySet()) {
            stringBleBluetoothEntry.getValue().disconnect();
        }
        bleLruHashMap.clear();
    }

    public void destroy() {
        Log.i("mkk", "清除大活动===");
        for (Map.Entry<String, BleBluetooth> stringBleBluetoothEntry : bleLruHashMap.entrySet()) {
            stringBleBluetoothEntry.getValue().destroy();
        }
        bleLruHashMap.clear();
        for (Map.Entry<String, BleBluetooth> stringBleBluetoothEntry : bleTempHashMap.entrySet()) {
            stringBleBluetoothEntry.getValue().destroy();
        }
        bleTempHashMap.clear();
    }

    public List<BleBluetooth> getBleBluetoothList() {
        List<BleBluetooth> bleBluetoothList = new ArrayList<>(bleLruHashMap.values());
        Collections.sort(bleBluetoothList, new Comparator<BleBluetooth>() {
            @Override
            public int compare(BleBluetooth lhs, BleBluetooth rhs) {
                return lhs.getDeviceKey().compareToIgnoreCase(rhs.getDeviceKey());
            }
        });
        return bleBluetoothList;
    }

    public List<BleDevice> getDeviceList() {
        refreshConnectedDevice();
        List<BleDevice> deviceList = new ArrayList<>();
        for (BleBluetooth BleBluetooth : getBleBluetoothList()) {
            if (BleBluetooth != null) {
                deviceList.add(BleBluetooth.getDevice());
            }
        }
        return deviceList;
    }

    public void refreshConnectedDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<BleBluetooth> bluetoothList = getBleBluetoothList();
            for (int i = 0; bluetoothList != null && i < bluetoothList.size(); i++) {
                BleBluetooth bleBluetooth = bluetoothList.get(i);
                if (!BleManager.getInstance().isConnected(bleBluetooth.getDevice())) {
                    removeBleBluetooth(bleBluetooth);
                }
            }
        }
    }


}
