package com.stoplight.blu.toys.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;
import com.stoplight.blu.toys.R;
import com.stoplight.blu.toys.app.AppManager;
import com.stoplight.blu.toys.ble.BlePlay;
import com.example.blu.toys.ble.agreement.TrafficLightBean;
import com.stoplight.blu.toys.utils.SpUtils;
import com.stoplight.blu.toys.utils.TimeBean;
import com.stoplight.blu.toys.utils.TimeUtil;
import com.stoplight.blu.toys.view.TypefaceTextView;
import com.hjq.toast.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class PlayActivity extends BaseActivity {


	@BindView(R.id.task_time)
	TypefaceTextView typefaceTextView;//倒计时


	private int suspendAndContinue = 1;
	private int mDay = 0;
	private int mHour = 0;
	private int mMin = 0;
	private int mSecond = 00;// 天 ,小时,分钟,秒
	private boolean isRun = true;
	private int totalSecond;


	/**
	 * 不够位数的在前面补0，保留num的长度位数字
	 *
	 * @param code
	 * @return
	 */
	private String autoGenericCode(String code, int num) {
		String result = "";
		// 保留num的位数
		// 0 代表前面补充0
		// num 代表长度为4
		// d 代表参数为正数型
		result = String.format("%0" + num + "d", Integer.parseInt(code) + 1);

		return result;
	}


	public static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);//左补0
//    sb.append(str).append("0");//右补0
				str = sb.toString();
				strLen = str.length();
			}
		}

		return str;
	}

	private Handler timeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			LogUtils.e("定时器在走111", msg.what, mHour, mMin, mSecond);
			if (totalSecond < 1) {
				suspendAndContinue = 0;
			}else{
				if (suspendAndContinue == 1) {
					computeTime();
				}
			}


			String mHourStr = addZeroForNum(mHour + "", 2);
			String mMinStr = addZeroForNum(mMin + "", 2);
			String mSecondStr = addZeroForNum(mSecond + "", 2);
			typefaceTextView.setText(mHourStr + ":" + mMinStr + ":" + mSecondStr);



			sendBleData();


		}
	};

	private TimerManager timerManager = new TimerManager(timeHandler);
	;

	/**
	 * 开启倒计时
	 */
	private void startRun() {


		timerManager.startTimer(0, 1000);

//		new Thread(() -> {
//			while (isRun) {
//				try {
//					Thread.sleep(1000); // sleep 1000ms
//					Message message = Message.obtain();
//					message.what = 1;
//					timeHandler.sendMessage(message);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
	}

	/**
	 * 倒计时计算
	 */
	private void computeTime() {
		LogUtils.e("获取的时分秒", mHour, mMin, mSecond);

		totalSecond--;
		TimeBean timeBean = TimeUtil.getTimeBeanBySecond(Long.valueOf(totalSecond));
		mSecond = timeBean.getmSecond();
		mHour = timeBean.getmHour();
		mMin = timeBean.getmMin();


	}


	@Override
	public int getLayout() {
		return R.layout.activity_play;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		Intent intent = getIntent();
		mHour = intent.getIntExtra("hour", 0);
		mMin = intent.getIntExtra("min", 0);
		mSecond = intent.getIntExtra("second", 0);
		totalSecond = mHour * 3600 + mMin * 60 + mSecond;
		LogUtils.e("获取的时分秒", mHour, mMin, mSecond, totalSecond);
		startRun();
	}

	@Override
	public void initData() {

	}

	public void sendBleData() {
		SpUtils spUtils = SpUtils.getSpUtils(PlayActivity.this);
		int close = spUtils.getSPValue("close", 1);
		int open = spUtils.getSPValue("open", 10);
		int alarm = spUtils.getSPValue("alarm", 1);
		int green = spUtils.getSPValue("green", 1);
		int brightness = spUtils.getSPValue("brightness", 50);
		TrafficLightBean ttt = new TrafficLightBean(2, suspendAndContinue,
				totalSecond, brightness, open, close, green, alarm);
		byte[] bytes12 = ttt.toByte();
		BlePlay.getInstance().sendPause(bytes12, new BleWriteCallback() {
			@Override
			public void onWriteSuccess(int current, int total, byte[] justWrite) {
				LogUtils.e("发送成功：" + suspendAndContinue);
			}

			@Override
			public void onWriteFailure(BleException exception) {

			}
		});
	}

	@OnClick(R.id.back_but)
	public void onViewClicked() {

		//点击返回的时候 不用提示就返回到上个页面即可
		timerManager.cancelTimer();
		AppManager.getInstance().finishActivity();
	}

	@Override
	public void onBackPressed() {
		timerManager.cancelTimer();
		AppManager.getInstance().finishActivity();
	}

	@OnClick({R.id.cancel_but, R.id.pause_but, R.id.resume_but})
	public void onViewClicked(View view) {
		SpUtils spUtils = SpUtils.getSpUtils(PlayActivity.this);
		int close = spUtils.getSPValue("close", 1);
		int open = spUtils.getSPValue("open", 10);
		int alarm = spUtils.getSPValue("alarm", 1);
		int green = spUtils.getSPValue("green", 1);
		int brightness = spUtils.getSPValue("brightness", 50);

		switch (view.getId()) {
			case R.id.cancel_but://取消 关机

				//需要有个提示 直接返回加号页面


				AlertDialog.Builder builder=new AlertDialog.Builder(this);
				builder.setTitle("Tip: Bluetooth connection will be disconnected");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub
						timerManager.cancelTimer();
						TrafficLightBean trafficLightBean = new TrafficLightBean(
								1, 0,
								0, 0, 0, 0, 0, 0);
						byte[] bytes = trafficLightBean.toByte();
						BlePlay.getInstance().sendStard(bytes, new BleWriteCallback() {
							@Override
							public void onWriteSuccess(int current, int total, byte[] justWrite) {
								ToastUtils.show("Power off - send successfully");
								BlePlay.getInstance().closeBle();

								Intent intent=new Intent(PlayActivity.this,AddDeviceActivity.class);

								startActivity(intent);
							}

							@Override
							public void onWriteFailure(BleException exception) {

							}
						});



					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub


					}
				});
				builder.create();
				builder.show();





				break;
			case R.id.pause_but://暂停
//				int time = mHour * 3600 + mMin * 60 + mSecond;
				suspendAndContinue = 0;
//				TrafficLightBean ttt = new TrafficLightBean(2, suspendAndContinue,
//						time, brightness, open, close, green, alarm);
//				byte[] bytes12 = ttt.toByte();
//				BlePlay.getInstance().sendPause(bytes12, new BleWriteCallback() {
//					@Override
//					public void onWriteSuccess(int current, int total, byte[] justWrite) {
//
//						ToastUtils.show("暂停-发送成功");
//
//						//timerManager.cancelTimer();
//					}
//
//					@Override
//					public void onWriteFailure(BleException exception) {
//
//					}
//				});
				break;
			case R.id.resume_but://开始
				suspendAndContinue = 1;
				break;
		}
	}
}