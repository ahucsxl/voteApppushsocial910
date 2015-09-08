package com.xl.voteapp.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;
import com.xl.voteapp.R;
import com.xl.voteapp.ui.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GuildActivity extends Activity {
	private final int SPLASH_DISPLAY_LENGHT = /*300*/0;
	private SharedPreferences preferences;
	private Editor editor;
	Message m = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guild);
		preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				if (preferences.getBoolean("firststart", true)) {
					editor = preferences.edit();
					editor.putBoolean("firststart", false);
					editor.commit();
					Log.w("loading", "+++ 正在注册信鸽推送:");
					XGPushManager.registerPush(getApplicationContext(), new XGIOperateCallback() {
						@Override
						public void onSuccess(Object data, int flag) {
							Log.w(Constants.LogTag, "+++ register push sucess. token:" + data);
							m.obj = "+++ register push sucess. token:" + data;
							m.sendToTarget();
						}

						@Override
						public void onFail(Object data, int errCode, String msg) {
							Log.w(Constants.LogTag,
									"+++ register push fail. token:" + data + ", errCode:" + errCode + ",msg:" + msg);

							m.obj = "+++ register push fail. token:" + data + ", errCode:" + errCode + ",msg:" + msg;
							m.sendToTarget();
						}
					});

				}
				Intent intent = new Intent();
				intent.setClass(GuildActivity.this, MainActivity.class);
				GuildActivity.this.startActivity(intent);
				GuildActivity.this.finish();

			}
		}, SPLASH_DISPLAY_LENGHT);
	}

}
