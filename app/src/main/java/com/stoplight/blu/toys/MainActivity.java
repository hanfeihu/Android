package com.stoplight.blu.toys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.stoplight.blu.toys.R;
import com.stoplight.blu.toys.activity.AddDeviceActivity;
import com.stoplight.blu.toys.activity.BaseActivity;
import com.stoplight.blu.toys.app.AppManager;
import com.stoplight.blu.toys.ble.MyService;

public class MainActivity extends BaseActivity {


	@Override
	public int getLayout() {
		return R.layout.activity_main;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		new Handler().postDelayed(() -> {



			Intent intent = new Intent(this, MyService.class); this.startService(intent);

			goActivity(AddDeviceActivity.class);
			AppManager.getInstance().finishActivity();
		}, 1000);    //延时1s执行
	}


	@Override
	public void initData() {

	}
}