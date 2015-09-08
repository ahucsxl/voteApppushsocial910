package com.xl.voteapp.util;

import java.io.File;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.List;
import java.util.UUID;

import com.xl.voteapp.base.BaseApplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TDevice {

    // 手机网络类型
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static boolean GTE_HC;
    public static boolean GTE_ICS;
    public static boolean PRE_HC;
    private static Boolean _hasBigScreen = null;
    private static Boolean _hasCamera = null;
    private static Boolean _isTablet = null;
    private static Integer _loadFactor = null;

    private static int _pageSize = -1;
    public static float displayDensity = 0.0F;

    static {
	GTE_ICS = Build.VERSION.SDK_INT >= 14;
	GTE_HC = Build.VERSION.SDK_INT >= 11;
	PRE_HC = Build.VERSION.SDK_INT >= 11 ? false : true;
    }

    public TDevice() {
    }


    public static int[] getRealScreenSize(Activity activity) {
	int[] size = new int[2];
	int screenWidth = 0, screenHeight = 0;
	WindowManager w = activity.getWindowManager();
	Display d = w.getDefaultDisplay();
	DisplayMetrics metrics = new DisplayMetrics();
	d.getMetrics(metrics);
	// since SDK_INT = 1;
	screenWidth = metrics.widthPixels;
	screenHeight = metrics.heightPixels;
	// includes window decorations (statusbar bar/menu bar)
	if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
	    try {
		screenWidth = (Integer) Display.class.getMethod("getRawWidth")
			.invoke(d);
		screenHeight = (Integer) Display.class
			.getMethod("getRawHeight").invoke(d);
	    } catch (Exception ignored) {
	    }
	// includes window decorations (statusbar bar/menu bar)
	if (Build.VERSION.SDK_INT >= 17)
	    try {
		Point realSize = new Point();
		Display.class.getMethod("getRealSize", Point.class).invoke(d,
			realSize);
		screenWidth = realSize.x;
		screenHeight = realSize.y;
	    } catch (Exception ignored) {
	    }
	size[0] = screenWidth;
	size[1] = screenHeight;
	return size;
    }



    public static boolean hasHardwareMenuKey(Context context) {
	boolean flag = false;
	if (PRE_HC)
	    flag = true;
	else if (GTE_ICS) {
	    flag = ViewConfiguration.get(context).hasPermanentMenuKey();
	} else
	    flag = false;
	return flag;
    }

    public static boolean hasInternet() {
	boolean flag;
	if (((ConnectivityManager) BaseApplication.context().getSystemService(
		"connectivity")).getActiveNetworkInfo() != null)
	    flag = true;
	else
	    flag = false;
	return flag;
    }

    public static boolean gotoGoogleMarket(Activity activity, String pck) {
	try {
	    Intent intent = new Intent();
	    intent.setPackage("com.android.vending");
	    intent.setAction(Intent.ACTION_VIEW);
	    intent.setData(Uri.parse("market://details?id=" + pck));
	    activity.startActivity(intent);
	    return true;
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
    }


    public static void hideAnimatedView(View view) {
	if (PRE_HC && view != null)
	    view.setPadding(view.getWidth(), 0, 0, 0);
    }




    public static String percent(double p1, double p2) {
	String str;
	double p3 = p1 / p2;
	NumberFormat nf = NumberFormat.getPercentInstance();
	nf.setMinimumFractionDigits(2);
	str = nf.format(p3);
	return str;
    }

    public static String percent2(double p1, double p2) {
	String str;
	double p3 = p1 / p2;
	NumberFormat nf = NumberFormat.getPercentInstance();
	nf.setMinimumFractionDigits(0);
	str = nf.format(p3);
	return str;
    }


    public static boolean isHaveMarket(Context context) {
	Intent intent = new Intent();
	intent.setAction("android.intent.action.MAIN");
	intent.addCategory("android.intent.category.APP_MARKET");
	PackageManager pm = context.getPackageManager();
	List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
	return infos.size() > 0;
    }


    public static void setFullScreen(Activity activity) {
	WindowManager.LayoutParams params = activity.getWindow()
		.getAttributes();
	params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
	activity.getWindow().setAttributes(params);
	activity.getWindow().addFlags(
		WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void cancelFullScreen(Activity activity) {
	WindowManager.LayoutParams params = activity.getWindow()
		.getAttributes();
	params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
	activity.getWindow().setAttributes(params);
	activity.getWindow().clearFlags(
		WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void installAPK(Context context, File file) {
	if (file == null || !file.exists())
	    return;
	Intent intent = new Intent();
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	intent.setAction(Intent.ACTION_VIEW);
	intent.setDataAndType(Uri.fromFile(file),
		"application/vnd.android.package-archive");
	context.startActivity(intent);
    }

    public static Intent getInstallApkIntent(File file) {
	Intent intent = new Intent();
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	intent.setAction(Intent.ACTION_VIEW);
	intent.setDataAndType(Uri.fromFile(file),
		"application/vnd.android.package-archive");
	return intent;
    }

    public static void openDial(Context context, String number) {
	Uri uri = Uri.parse("tel:" + number);
	Intent it = new Intent(Intent.ACTION_DIAL, uri);
	context.startActivity(it);
    }

    public static void openSMS(Context context, String smsBody, String tel) {
	Uri uri = Uri.parse("smsto:" + tel);
	Intent it = new Intent(Intent.ACTION_SENDTO, uri);
	it.putExtra("sms_body", smsBody);
	context.startActivity(it);
    }

    public static void openDail(Context context) {
	Intent intent = new Intent(Intent.ACTION_DIAL);
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	context.startActivity(intent);
    }

    public static void openSendMsg(Context context) {
	Uri uri = Uri.parse("smsto:");
	Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	context.startActivity(intent);
    }

    public static void openCamera(Context context) {
	Intent intent = new Intent(); // 调用照相机
	intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
	intent.setFlags(0x34c40000);
	context.startActivity(intent);
    }

    public static String getPhoneType() {
	return android.os.Build.MODEL;
    }


    public static boolean openAppActivity(Context context, String packageName,
	    String activityName) {
	Intent intent = new Intent(Intent.ACTION_MAIN);
	intent.addCategory(Intent.CATEGORY_LAUNCHER);
	ComponentName cn = new ComponentName(packageName, activityName);
	intent.setComponent(cn);
	try {
	    context.startActivity(intent);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }



    /**
     * 发送邮件
     * 
     * @param context
     * @param subject
     *            主题
     * @param content
     *            内容
     * @param emails
     *            邮件地址
     */
    public static void sendEmail(Context context, String subject,
	    String content, String... emails) {
	try {
	    Intent intent = new Intent(Intent.ACTION_SEND);
	    // 模拟器
	    // intent.setType("text/plain");
	    intent.setType("message/rfc822"); // 真机
	    intent.putExtra(android.content.Intent.EXTRA_EMAIL, emails);
	    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
	    intent.putExtra(Intent.EXTRA_TEXT, content);
	    context.startActivity(intent);
	} catch (ActivityNotFoundException e) {
	    e.printStackTrace();
	}
    }



    public static boolean hasStatusBar(Activity activity) {
	WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
	if ((attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * 调用系统安装了的应用分享
     * 
     * @param context
     * @param title
     * @param url
     */
    public static void showSystemShareOption(Activity context,
	    final String title, final String url) {
	Intent intent = new Intent(Intent.ACTION_SEND);
	intent.setType("text/plain");
	intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
	intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
	context.startActivity(Intent.createChooser(intent, "选择分享"));
    }

    /**
     * 获取当前网络类型
     * 
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public static int getNetworkType() {
	int netType = 0;
/*	ConnectivityManager connectivityManager = (ConnectivityManager) AppContext
		.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
	if (networkInfo == null) {
	    return netType;
	}
	int nType = networkInfo.getType();
	if (nType == ConnectivityManager.TYPE_MOBILE) {
	    String extraInfo = networkInfo.getExtraInfo();
	    if (!StringUtils.isEmpty(extraInfo)) {
		if (extraInfo.toLowerCase().equals("cmnet")) {
		    netType = NETTYPE_CMNET;
		} else {
		    netType = NETTYPE_CMWAP;
		}
	    }
	} else if (nType == ConnectivityManager.TYPE_WIFI) {
	    netType = NETTYPE_WIFI;
	}*/
	return netType;
    }
    
    public static int getVersionCode() {
    	int versionCode = 0;
    	try {
    	    versionCode = BaseApplication
    		    .context()
    		    .getPackageManager()
    		    .getPackageInfo(BaseApplication.context().getPackageName(),
    			    0).versionCode;
    	} catch (PackageManager.NameNotFoundException ex) {
    	    versionCode = 0;
    	}
    	return versionCode;
        }
}
