package com.stoplight.blu.toys.activity;

import android.os.Handler;
import android.os.Message;

import com.blankj.utilcode.util.LogUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by MQL on 2016/8/12.
 */
public class TimerManager {

	private Timer timer;
	private TimerTask timerTask;
	private Handler handler;
	private int startCount = 0;
	private int countCount = 0;

	public TimerManager(final Handler handler) {
		this.handler = handler;
		this.timer = new Timer();
		this.timerTask = new TimerTask() {
			@Override
			public void run() {
				if (handler != null) {
				//	LogUtils.e("定时器在走");

					Message msg = new Message();
					handler.sendMessage(msg);
				}
			}
		};
	}

	/*
		启动定时器, 在延时delay的时间后执行第一次，以后每隔period时间，执行一次
		delay:第一次执行延时的时间
		period:循环执行周期
	 */
	public void startTimer(long delay, long period) {

		try {
			startCount++;
			timer.schedule(timerTask, delay, period);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
		取消定时器
	 */
	public void cancelTimer() {
		timer.cancel();
		timerTask.cancel();
		countCount++;
	}
}

