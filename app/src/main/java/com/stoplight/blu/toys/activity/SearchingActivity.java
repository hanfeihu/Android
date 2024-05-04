package com.stoplight.blu.toys.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import com.stoplight.blu.toys.R;
import com.stoplight.blu.toys.app.AppManager;
import com.stoplight.blu.toys.ble.BlePlay;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchingActivity extends BaseActivity {


	@BindView(R.id.iv_beecartoon)
	ImageView ivBeecartoon;
	@BindView(R.id.go_search_ble)
	AppCompatImageView goSearchBle;
	@BindView(R.id.go_search_ble_1)
	AppCompatImageView goSearchBle1;
	@BindView(R.id.go_search_ble_2)
	AppCompatImageView goSearchBle2;

	boolean isBreak;

	@Override
	public int getLayout() {
		return R.layout.activity_searching;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.translate_anim);
		ivBeecartoon.startAnimation(animation);
		goSearchBle.setImageDrawable(this.getResources().getDrawable(R.drawable.search_bg));
		Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.search_anim);
		goSearchBle.startAnimation(animation1);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				Animation animation2 = AnimationUtils.loadAnimation(SearchingActivity.this, R.anim.search_anim);
				goSearchBle1.setImageDrawable(SearchingActivity.this.getResources().getDrawable(R.drawable.search_bg));
				goSearchBle1.startAnimation(animation2);
			}
		}, 700);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				Animation animation3 = AnimationUtils.loadAnimation(SearchingActivity.this, R.anim.search_anim);
				goSearchBle2.setImageDrawable(SearchingActivity.this.getResources().getDrawable(R.drawable.search_bg));
				goSearchBle2.startAnimation(animation3);
			}
		}, 1400);

	}

	@Override
	public void initData() {
		BlePlay.getInstance().scanBle();
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void GoDeviceListPage(String msg) {
		if (isBreak) {
			return;
		}
        goActivity(SelectDeviceActivity.class);
        AppManager.getInstance().finishActivity();

	}

	@OnClick(R.id.back_but)
	public void onViewClicked() {
		isBreak = true;
		BlePlay.getInstance().canceScan();
		AppManager.getInstance().finishActivity();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}