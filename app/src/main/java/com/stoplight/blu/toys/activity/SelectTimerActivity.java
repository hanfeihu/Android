package com.stoplight.blu.toys.activity;

import android.os.Bundle;
import android.widget.Switch;

import com.blankj.utilcode.util.LogUtils;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;
import com.stoplight.blu.toys.R;
import com.stoplight.blu.toys.app.AppManager;
import com.stoplight.blu.toys.ble.BlePlay;
import com.example.blu.toys.ble.agreement.TrafficLightBean;
import com.stoplight.blu.toys.utils.SpUtils;
import com.stoplight.blu.toys.view.TypefaceTextView;

import butterknife.BindView;
import butterknife.OnClick;

//设置
public class SelectTimerActivity extends BaseActivity {


	@BindView(R.id.back_but)
	TypefaceTextView backBut;
	@BindView(R.id.will)
	TypefaceTextView will;
	@BindView(R.id.alarm_switch)
	Switch alarmSwitch;
	@BindView(R.id.music_switch)
	Switch musicSwitch;
	@BindView(R.id.green_switch)
	Switch greenSwitch;
//	@BindView(R.id.brightness_bar)
//	SeekBar brightnessBar;

	@Override
	public int getLayout() {
		return R.layout.activity_select_timer;
	}


	private void  sendBle(){
		SpUtils spUtils = SpUtils.getSpUtils(SelectTimerActivity.this);
		int close = spUtils.getSPValue("close", 1);
		int open = spUtils.getSPValue("open", 10);
		int alarm = spUtils.getSPValue("alarm", 1);
		int green = spUtils.getSPValue("green", 1);
		int brightness = spUtils.getSPValue("brightness", 50);
		TrafficLightBean ttt = new TrafficLightBean(2, 0,
				0, brightness, open, close, green, alarm);

		byte[] bytes12 = ttt.toByte();
		BlePlay.getInstance().sendPause(bytes12, new BleWriteCallback() {
			@Override
			public void onWriteSuccess(int current, int total, byte[] justWrite) {
				LogUtils.e("发送成功：" );
			}

			@Override
			public void onWriteFailure(BleException exception) {

			}
		});
	}

	@Override
	public void init(Bundle savedInstanceState) {

		SpUtils spUtils = SpUtils.getSpUtils(this);

		spUtils.getSPValue("close",1);
		int open=	spUtils.getSPValue("open",1);
		int alarm = spUtils.getSPValue("alarm", 1);
		int green = spUtils.getSPValue("green", 1);
		int brightness = spUtils.getSPValue("brightness", 50);

		//数据回显
		alarmSwitch.setChecked(alarm == 1 ? true : false);
		greenSwitch.setChecked(green == 1 ? true : false);

		musicSwitch.setChecked(open == 1 ? true : false);
//		brightnessBar.setProgress(brightness);


		alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				spUtils.putSPValue("alarm", 1);
			} else {
				spUtils.putSPValue("alarm", 0);
			}
			sendBle();
		});

		musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			//startActivity(new Intent(SelectTimerActivity.this, MusicActivity.class));

			if (isChecked) {
				spUtils.putSPValue("open", 1);
			} else {
				spUtils.putSPValue("open", 0);
			}
			sendBle();
		});

		greenSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				spUtils.putSPValue("green", 1);
			} else {
				spUtils.putSPValue("green", 0);
			}
			sendBle();
		});

//		brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//			@Override
//			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//				spUtils.putSPValue("brightness", progress);
//				sendBle();
//			}
//
//			@Override
//			public void onStartTrackingTouch(SeekBar seekBar) {
//
//			}
//
//			@Override
//			public void onStopTrackingTouch(SeekBar seekBar) {
//
//			}
//		});


	}

	@Override
	public void initData() {
		LogUtils.e("---初始化");

	}


	@OnClick(R.id.back_but)
	public void onViewClicked() {



		AppManager.getInstance().finishActivity();
	}
}