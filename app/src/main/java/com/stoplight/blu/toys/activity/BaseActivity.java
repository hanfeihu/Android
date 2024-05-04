package com.stoplight.blu.toys.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.stoplight.blu.toys.app.AppManager;
import com.stoplight.blu.toys.utils.Utils;
import com.stoplight.blu.toys.view.WrapContentGridLayoutManager;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseActivity extends AppCompatActivity {

	private Unbinder mBinder;

	protected static final int REQUEST_CODE_PICK_IMAGE = 0x1111;//相册
	protected static final int PERMISSION_REQUEST = 1001;

	private List<String> permissionsList = new ArrayList<>();
	private String[] permissions = new String[]{
			Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		AppManager.getInstance().addActivity(this);
		checkPermission();
		mBinder = ButterKnife.bind(this);
		init(savedInstanceState);
		initData();
	}

	private void checkPermission() {
		XXPermissions.with(this) // 申请安装包权限
				.permission( Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_FINE_LOCATION) // 申请多个权限
				.request(new OnPermission() {
							 @Override
							 public void hasPermission(List<String> granted, boolean all) {
								 if (!all) {
									 checkPermission();
								 }
							 }

							 @Override
							 public void noPermission(List<String> denied, boolean never) {
								 if (never) {
									 XXPermissions.startPermissionActivity(BaseActivity.this, denied);
								 } else {
									 checkPermission();
								 }

							 }
						 }

				);
	}

	protected LinearLayoutManager getLinearLayout() {
		LinearLayoutManager layoutManager = new WrapContentGridLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		return layoutManager;
	}


	public abstract int getLayout();

	public abstract void init(Bundle savedInstanceState);

	public abstract void initData();

	protected String getStringResourcesId(int resourcesId) {
		return Utils.getString(this, resourcesId);
	}

	public <T> void goActivity(Class<T> activity) {
		startActivity(new Intent(this, activity));
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBinder.unbind();
	}
}
