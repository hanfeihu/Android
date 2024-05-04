package com.stoplight.blu.toys.ble;

import android.bluetooth.BluetoothDevice;

public class Notify {

	public Notify() {
	}

	public Notify(Integer action, BluetoothDevice device) {
		this.action = action;
		this.device = device;
	}

	private Integer action;

	public Integer getAction() {
		return action;
	}

	public void setAction(Integer action) {
		this.action = action;
	}

	public BluetoothDevice getDevice() {
		return device;
	}

	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}

	private BluetoothDevice device;
}
