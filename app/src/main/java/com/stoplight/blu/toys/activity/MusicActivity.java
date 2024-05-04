package com.stoplight.blu.toys.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.stoplight.blu.toys.R;
import com.stoplight.blu.toys.app.AppManager;
import com.stoplight.blu.toys.view.TypefaceTextView;

import butterknife.BindView;
import butterknife.OnClick;

public class MusicActivity extends BaseActivity {

	@BindView(R.id.open_music)
	TypefaceTextView openMusic;
	@BindView(R.id.close_music)
	TypefaceTextView closeMusic;
	@BindView(R.id.back_but)
	TypefaceTextView backBut;

	@Override
	public int getLayout() {
		return R.layout.activity_music;
	}

	@Override
	public void init(Bundle savedInstanceState) {

	}

	@Override
	public void initData() {

	}


	@OnClick({R.id.open_music, R.id.close_music})
	public void onViewClicked(View view) {
		Intent intent = new Intent(MusicActivity.this, SettingMusicActivity.class);
		switch (view.getId()) {
			case R.id.open_music:
				intent.putExtra("musicType", "open");
				break;
			case R.id.close_music:
				intent.putExtra("musicType", "close");
				break;
		}
		startActivity(intent);
	}


	@OnClick(R.id.back_but)
	public void onViewClicked() {
		AppManager.getInstance().finishActivity();
	}
}