package com.stoplight.blu.toys.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.EditText;

public class Utils {

	public static void copy(Context context, String data) {
		// 获取系统剪贴板
		ClipboardManager clipboard = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
		// 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）,其他的还有
		// newHtmlText、
		// newIntent、
		// newUri、
		// newRawUri
		ClipData clipData = ClipData.newPlainText(null, data);
		// 把数据集设置（复制）到剪贴板
		clipboard.setPrimaryClip(clipData);
	}

	public static void paste(Context context, EditText view) {
		// 获取系统剪贴板
		ClipboardManager clipboard = (ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
		// 获取剪贴板的剪贴数据集
		ClipData clipData = clipboard.getPrimaryClip();
		if (clipData != null && clipData.getItemCount() > 0) {
			// 从数据集中获取（粘贴）第一条文本数据
			CharSequence text = clipData.getItemAt(0).getText();
			view.setText(text);
		}
	}


	public static String getString(Context context, int resourcesId) {
		return context.getApplicationContext().getResources().getString(resourcesId);
	}
}
