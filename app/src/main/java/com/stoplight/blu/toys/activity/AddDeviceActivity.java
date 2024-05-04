package com.stoplight.blu.toys.activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatImageView;

import com.stoplight.blu.toys.R;
import com.stoplight.blu.toys.app.AppManager;
import butterknife.BindView;
import butterknife.OnClick;

public class AddDeviceActivity extends BaseActivity {

	@BindView(R.id.go_search_ble)
	AppCompatImageView goSearchBle;
	//退出时的时间
	private long mExitTime;

	@Override
	public int getLayout() {
		return R.layout.activity_add_device;
	}

	@Override
	public void init(Bundle savedInstanceState) {

	}

	@Override
	public void initData() {

	}


	//对返回键进行监听
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	//退出方法
	private void exit() {
		if ((System.currentTimeMillis() - mExitTime) > 2000) {
			Toast.makeText(AddDeviceActivity.this, "Press again to exit the app", Toast.LENGTH_SHORT).show();
			mExitTime = System.currentTimeMillis();
		} else {
			//用户退出处理
			AppManager.getInstance().exitApp();
		}
	}

	@OnClick(R.id.go_search_ble)
	public void onViewClicked() {
		goActivity(SearchingActivity.class);
	}
}