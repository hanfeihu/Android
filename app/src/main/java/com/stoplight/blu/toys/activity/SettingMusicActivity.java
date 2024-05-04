package com.stoplight.blu.toys.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.stoplight.blu.toys.R;
import com.stoplight.blu.toys.adapter.MusicAdapter;
import com.stoplight.blu.toys.app.AppManager;
import com.stoplight.blu.toys.utils.SpUtils;
import com.stoplight.blu.toys.view.TypefaceTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingMusicActivity extends BaseActivity {


	@BindView(R.id.recyclerView)
	RecyclerView mRecyclerView;

	MusicAdapter musicAdapter;
	@BindView(R.id.back_but)
	TypefaceTextView backBut;

	private List<Integer> integerList;
	private SpUtils spUtils;

	@Override
	public int getLayout() {
		return R.layout.activity_setting_music;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		spUtils = SpUtils.getSpUtils(SettingMusicActivity.this);
		Intent intent = getIntent();
		String musicType = intent.getStringExtra("musicType");
		integerList = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			integerList.add(i);
		}
		mRecyclerView.setLayoutManager(getLinearLayout());
		musicAdapter = new MusicAdapter(R.layout.ble_devices_item, integerList, musicType);
		mRecyclerView.setAdapter(musicAdapter);
		musicAdapter.setOnItemClickListener((adapter, view, position) -> {
			Integer integer = integerList.get(position);
			if (musicType.equals("close")) {
				spUtils.putSPValue("close", integer);
			} else {
				spUtils.putSPValue("open", integer);
			}
			AppManager.getInstance().finishActivity();
		});
	}

	@Override
	public void initData() {

	}

	@OnClick(R.id.back_but)
	public void onViewClicked() {
		AppManager.getInstance().finishActivity();
	}
}