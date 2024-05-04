package com.stoplight.blu.toys.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.LogUtils;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;
import com.stoplight.blu.toys.R;
import com.stoplight.blu.toys.ble.BlePlay;
import com.example.blu.toys.ble.agreement.TrafficLightBean;
import com.stoplight.blu.toys.utils.SpUtils;
import com.stoplight.blu.toys.utils.TimeBean;
import com.stoplight.blu.toys.utils.TimeUtil;
import com.stoplight.blu.toys.view.TypefaceTextView;
import com.stoplight.blu.toys.view.WheelView;
import com.hjq.toast.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class SetTimeActivity extends BaseActivity {


	@BindView(R.id.wheel_hour) //时
			WheelView wheelHour;
	@BindView(R.id.wheel_minute)//分
			WheelView wheelMinute;
	@BindView(R.id.wheel_second)//秒
			WheelView wheelSecond;
	@BindView(R.id.wheel_am_pm)
	WheelView mAmPm;

	@BindView(R.id.wheel_hour_dw)
	WheelView wheelHour_dw;
	@BindView(R.id.wheel_minute_dw)
	WheelView wheelMinute_dw;
	@BindView(R.id.wheel_second_dw)
	WheelView wheelSecond_dw;
	@BindView(R.id.back_but)
	TypefaceTextView backBut;
	@BindView(R.id.center)
	LinearLayout center;
	@BindView(R.id.start_but)
	RelativeLayout startBut;
	@BindView(R.id.will)
	TypefaceTextView will;
	@BindView(R.id.settings)
	ImageView settings;
	@BindView(R.id.shop)
	ImageView shop;
	@BindView(R.id.rate)
	ImageView mImageView03;


	private ArrayList<String> hourList = new ArrayList<>();
	private ArrayList<String> sixList = new ArrayList<>();
	private ArrayList<String> selectTime = new ArrayList<>();
	private ArrayList<String> amPmList = new ArrayList<>();


	String selectHour;
	String selectMin;
	String selectSec;


	int hour = 0;
	int min = 0;
	int second = 0;


	@Override
	public int getLayout() {
		return R.layout.activity_set_time;
	}

	private TimeBean getBottomTime() {
		TimeBean timeBean = new TimeBean();


		String hour = wheelHour_dw.getSeletedItem();
		String minute = wheelMinute_dw.getSeletedItem();
		String second = wheelSecond_dw.getSeletedItem();


		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
		calendar.add(Calendar.MINUTE, Integer.valueOf(minute));
		calendar.add(Calendar.SECOND, Integer.valueOf(second));



		//设置上面的时间
		wheelHour.setSeletion(calendar.get(Calendar.HOUR));
		wheelMinute.setSeletion(calendar.get(Calendar.MINUTE));
		wheelSecond.setSeletion(calendar.get(Calendar.SECOND));
		mAmPm.setSeletion(calendar.get(Calendar.AM_PM));

		return timeBean;
	}

	public int getNowTotalSecond() {
		Calendar cal = Calendar.getInstance();

		int hour1 = cal.get(Calendar.HOUR_OF_DAY);//小时
		int minute2 = cal.get(Calendar.MINUTE);//分
		int second3 = cal.get(Calendar.SECOND);//秒





		return hour1 * 3600 + minute2 * 60 + second3;
	}

	private TimeBean getTopTime() {

		//滚动的上面
		TimeBean timeBean = new TimeBean();

		String hour = wheelHour.getSeletedItem();
		String minute = wheelMinute.getSeletedItem();
		String second = wheelSecond.getSeletedItem();
		String pm = mAmPm.getSeletedItem();

		timeBean.setmSecond(Integer.valueOf(second));
		timeBean.setmMin(Integer.valueOf(minute));
		timeBean.setmHour(Integer.valueOf(hour));
		timeBean.setPm(pm);
		timeBean.toTime();




		if (0 > timeBean.getSelectTopTotalSecond24H()) {
			wheelHour_dw.setSeletion(15);
			wheelMinute_dw.setSeletion(59);
			wheelSecond_dw.setSeletion(59);
			return timeBean;
		}

		TimeBean	showTime=null;
			try {
				Date date=new Date();
				//转换成时间格式12小时制
				SimpleDateFormat df_12=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
				//转换成时间格式24小时制
				SimpleDateFormat yyyMMdd=new SimpleDateFormat("yyyy-MM-dd");

				Date date1=     df_12.parse(yyyMMdd.format(date)+" "+hour+":"+minute+":"+second+" "+pm);

				System.out.println("日期:"+yyyMMdd.format(date));
				System.out.println("12小时制时间::"+df_12.format(date));

				 showTime=     TimeUtil.getTimeBeanBySecond((date1.getTime()-new Date().getTime())/1000);

				System.out.println(showTime.getmHour());
				System.out.println(showTime.getmMin());
				System.out.println(showTime.getmSecond());

			}catch (Exception e){
				e.printStackTrace();
			}





		//设置下面的时间
		showTime.toTime();
		//设置下面时间
		wheelHour_dw.setSeletion(showTime.getmHour());
		wheelMinute_dw.setSeletion(showTime.getmMin());
		wheelSecond_dw.setSeletion(showTime.getmSecond());
		return timeBean;
	}


	@Override
	public void init(Bundle savedInstanceState) {

		wheelHour.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
//				if (StringUtils.isEmpty(item)) {
//					//不能动
//					int indexHour = hourList.indexOf(selectHour);
//					wheelHour.setSeletion(indexHour);
//					return;
//				}
//				setDwTime();
				LogUtils.e("滚动了6");
				getTopTime();
			}
		});


		wheelMinute.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
//				if (StringUtils.isEmpty(item)) {
//					int indexMinute = sixList.indexOf(selectMin);
//					wheelMinute.setSeletion(indexMinute);
//					return;
//				}
				LogUtils.e("滚动了5");
//				setDwTime();
				getTopTime();

			}
		});


		//秒的选择
		wheelSecond.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
//				if (StringUtils.isEmpty(item)) {
//					int indexSecond = sixList.indexOf(selectSec);
//					wheelSecond.setSeletion(indexSecond);
//					return;
//				}
				LogUtils.e("滚动了4");
//				setDwTime();
				getTopTime();

			}
		});

		//amPm
		mAmPm.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
				//选择早上下午

				LogUtils.e("滚动了00");
				getTopTime();
			}
		});


		wheelHour_dw.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
//				setUpTime();
				LogUtils.e("滚动了3");
				getBottomTime();
			}
		});


		wheelMinute_dw.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
//				setUpTime();
				LogUtils.e("滚动了2");
				getBottomTime();
			}
		});


		wheelSecond_dw.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
			@Override
			public void onSelected(int selectedIndex, String item) {
				LogUtils.e("滚动了1");
//				setUpTime();
				getBottomTime();
			}
		});

	}

	@Override
	public void initData() {

		amPmList.add("AM");
		amPmList.add("PM");




		Calendar calendar=	Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		int second =calendar.get(Calendar.SECOND);
		int pm= 	calendar.get(Calendar.AM_PM);





		hourList.clear();
		sixList.clear();



		for (int i = 0; i < 13; i++) {
				hourList.add(TimeUtil.autoGenericCode(i+"",2));
		}

		for (int i = 0; i < 60; i++) {
				sixList.add(TimeUtil.autoGenericCode(i+"",2));
		}

		for (int i = 0; i < 16; i++) {
			selectTime.add(TimeUtil.autoGenericCode(i+"",2));
		}

		///时////
		wheelHour.setOffset(3);
		wheelHour.setItems(hourList);
		selectHour =  TimeUtil.autoGenericCode(hour+"",2);
		int indexHour = hourList.indexOf(selectHour);
		wheelHour.setSeletion(indexHour);

		///分////
		wheelMinute.setOffset(3);// 对话框中当前项上面和下面的项数
		wheelMinute.setItems(sixList);// 设置数据源

		selectMin = TimeUtil.autoGenericCode(minute+"",2);

		int indexMinute = sixList.indexOf(selectMin);
		wheelMinute.setSeletion(indexMinute);// 默认选中第三项

		///秒////
		wheelSecond.setOffset(3);
		wheelSecond.setItems(sixList);// 设置数据源
		selectSec = TimeUtil.autoGenericCode(second+"",2);

		int indexSecond = sixList.indexOf(selectSec);
		wheelSecond.setSeletion(indexSecond);

		//amPM
		mAmPm.setOffset(1);
		mAmPm.setItems(amPmList);
		mAmPm.setSeletion(pm);

		/////////下////////
		///时////
		wheelHour_dw.setOffset(3);
		wheelHour_dw.setItems(selectTime);
		wheelHour_dw.setSeletion(0);

		///分////
		wheelMinute_dw.setOffset(3);// 对话框中当前项上面和下面的项数
		wheelMinute_dw.setItems(sixList);// 设置数据源
		wheelMinute_dw.setSeletion(0);// 默认选中第三项

		///秒////
		wheelSecond_dw.setOffset(3);
		wheelSecond_dw.setItems(sixList);
		wheelSecond_dw.setSeletion(0);

	}


	@Override
	public void onBackPressed() {



		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("Tip: Bluetooth connection will be disconnected");

		builder.setPositiveButton("determine", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub
				BlePlay.getInstance().closeBle();

				Intent intent=new Intent(SetTimeActivity.this,AddDeviceActivity.class);

				startActivity(intent);


			}
		});
		builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub


			}
		});
		builder.create();
		builder.show();


//		BlePlay.getInstance().closeBle();
//		AppManager.getInstance().finishActivity();
	}


	@OnClick({R.id.back_but, R.id.start_but, R.id.settings, R.id.shop, R.id.rate})
	public void onViewClicked(View view) {
		Intent intent = null;
		Uri uri = null;
		switch (view.getId()) {
			case R.id.settings:
				intent = new Intent(SetTimeActivity.this, SelectTimerActivity.class);
				startActivity(intent);
				break;
			case R.id.shop:
			case R.id.rate:
				uri = Uri.parse("http://stoplightgolight.com/product/stoplight-golight/");
				intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				break;
			case R.id.back_but:
				AlertDialog.Builder builder=new AlertDialog.Builder(this);
				builder.setTitle("Tip: Bluetooth connection will be disconnected");

				builder.setPositiveButton("determine", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub
						BlePlay.getInstance().closeBle();

						Intent intent=new Intent(SetTimeActivity.this,AddDeviceActivity.class);

						startActivity(intent);


					}
				});
				builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub


					}
				});
				builder.create();
				builder.show();
				break;
			case R.id.start_but:
				boolean bleOpen = BlePlay.getInstance().isBleOpen();
				if (!bleOpen) {
					ToastUtils.show("Please turn on Bluetooth");
					return;
				}
				String selectHour_dw = wheelHour_dw.getSeletedItem();
				String selectMin_dw = wheelMinute_dw.getSeletedItem();
				String selectSec_dw = wheelSecond_dw.getSeletedItem();

				second = Integer.valueOf(selectSec_dw);
				hour = Integer.valueOf(selectHour_dw);
				min = Integer.valueOf(selectMin_dw);

				int time = Integer.valueOf(selectHour_dw) * 3600 + Integer.valueOf(selectMin_dw) * 60 + Integer.valueOf(selectSec_dw);
				if (time == 0) {
					ToastUtils.show("Please select the time");
					return;
				}

				LogUtils.e("滚动了");


				//转换成秒 0000000000000000

				SpUtils spUtils = SpUtils.getSpUtils(SetTimeActivity.this);
				int close = spUtils.getSPValue("close", 1);
				int open = spUtils.getSPValue("open", 10);
				int alarm = spUtils.getSPValue("alarm", 1);
				int green = spUtils.getSPValue("green", 1);
				int brightness = spUtils.getSPValue("brightness", 50);

				TrafficLightBean trafficLightBean = new TrafficLightBean(
						2, 1,
						time, brightness, open, close, green, alarm);

				byte[] bytes = trafficLightBean.toByte();
				BlePlay.getInstance().sendStard(bytes, new BleWriteCallback() {
					@Override
					public void onWriteSuccess(int current, int total, byte[] justWrite) {
						Intent intent = new Intent(SetTimeActivity.this, PlayActivity.class);
						intent.putExtra("hour", hour);
						intent.putExtra("min", min);
						intent.putExtra("second", second);
						startActivity(intent);
					}

					@Override
					public void onWriteFailure(BleException exception) {

					}
				});
				break;
		}
	}
}