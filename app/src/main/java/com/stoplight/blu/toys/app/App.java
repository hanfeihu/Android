package com.stoplight.blu.toys.app;


import android.app.Application;
import android.util.DisplayMetrics;

import com.clj.fastble.BleManager;
import com.stoplight.blu.toys.utils.SpUtils;
import com.hjq.toast.ToastUtils;

public class App extends Application {


	public static App instance;
	//临时保存 需要登录才能访问的页面 在登录成功后跳转
	//屏幕宽高
	int screenWidth;
	int screenHeight;

	/**
	 * Activity 栈
	 */

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		//1、通过WindowManager获取
		DisplayMetrics dm = new DisplayMetrics();
		// 屏幕宽（像素，如：480px）
		screenWidth = dm.heightPixels;
		// 屏幕高（像素)
		screenHeight = dm.widthPixels;
		//LogUtil.init(BuildConfig.LOG_DEBUG);
		ToastUtils.init(this);


		BleManager.getInstance().init(this);
		BleManager.getInstance()
				.enableLog(true)
				.setReConnectCount(3, 5000)
				.setMaxConnectCount(7)
				.setConnectOverTime(20000)
				.setOperateTimeout(1000);

//
//		BleManager.getInstance()
//				.enableLog(true)
//				.setReConnectCount(3, 5000)
//				.setMaxConnectCount(7)
//				.setSplitWriteNum(50000)
//				.setConnectOverTime(50000).init(this);

		SpUtils spUtils = SpUtils.getSpUtils(this);
		spUtils.putSPValue("close",1);
		spUtils.putSPValue("open",10);
		spUtils.putSPValue("alarm",1);
		spUtils.putSPValue("green",0);
		spUtils.putSPValue("brightness",50);
	}

	public static App getInstance() {
		return instance;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

}
