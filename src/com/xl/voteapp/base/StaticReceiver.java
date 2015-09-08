package com.xl.voteapp.base;

import com.xl.voteapp.R;
import com.xl.voteapp.ui.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class StaticReceiver extends BroadcastReceiver {
	/** Notification管理 */
	public NotificationManager mNotificationManager;
	/** Notification构造器 */
	NotificationCompat.Builder mBuilder;
	/** Notification的ID */
	int notifyId = 100;
	private Notification notification;

	@Override
	public void onReceive(Context context, Intent intent) {

        mNotificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("测试标题")
		.setContentText("测试内容")
		.setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的
		.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
		.setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
		.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消  
		.setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
		.setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
		.setSmallIcon(R.drawable.ic_launcher);
        mNotificationManager.notify(notifyId, mBuilder.build());
        

	}}

