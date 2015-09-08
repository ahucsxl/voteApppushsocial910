package com.xl.voteapp.base;


import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.xl.voteapp.R;
import com.xl.voteapp.bean.User;
import com.xl.voteapp.common.Contanst;
import com.xl.voteapp.util.CyptoUtils;
import com.xl.voteapp.util.StringUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * 
 * @author 火蚁 (http://my.oschina.net/LittleDY)
 * @version 1.0
 * @created 2014-04-22
 */
public class AppContext extends BaseApplication {

    public static final int PAGE_SIZE = 20;// 默认分页大小
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "name";
    private SharedPreferences _preferences;
    private RequestQueue _requestQueue;
    public static RequestQueue mQueue;

    private static AppContext instance;

 //   private int loginUid;

    public static boolean login=false;
    private final static String APP_CONFIG = "config";
    @Override
    public void onCreate() {
        super.onCreate();
        
/*        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle("测试标题")
		.setContentText("测试内容")
	//	.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
//		.setNumber(number)//显示数量
		.setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的
		.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
		.setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
//		.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消  
		.setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
		.setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
		//Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
		.setSmallIcon(R.drawable.ic_launcher);*/
        
        instance = this;
        _preferences = PreferenceManager.getDefaultSharedPreferences(this);
     //  _requestQueue = Volley.newRequestQueue(this);

        
        
        
       mQueue = RequestQueueSingleton.getInstance(getApplicationContext()).
			    getRequestQueue();
    //    Log.i("lognin", String.valueOf(login));
        instance = this;
        init();
        initLogin();
        // Thread.setDefaultUncaughtExceptionHandler(AppException
        // .getAppExceptionHandler(this));
       //    UIHelper.sendBroadcastForNotice(this);
    }

    private void init() {/*
        // 初始化网络请求
        AsyncHttpClient client = new AsyncHttpClient();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        ApiHttpClient.setHttpClient(client);
        ApiHttpClient.setCookie(ApiHttpClient.getCookie(this));

        // Log控制器
        KJLoger.openDebutLog(true);
        TLog.DEBUG = BuildConfig.DEBUG;

        // Bitmap缓存地址
        BitmapConfig.CACHEPATH = "OSChina/imagecache";
    */}

    private void initLogin() {
        User user = getLoginUser();
     //   Log.i("lognin", String.valueOf(login));
        if (null != user&& user.getU_name()!=null) {
            login = true;
         //   Log.i("lognin", String.valueOf(login));
        //    loginUid = user.getId();
            
        } else {
            this.cleanLoginInfo();
        }
    }

    /**
     * 获得当前app运行的AppContext
     * 
     * @return
     */
    public static AppContext getInstance() {
        return instance;
    }

    /**
     * Checks the response headers for session cookie and saves it
     * if it finds it.
     * @param headers Response Headers.
     */
    public final void checkSessionCookie(Map<String, String> headers) {
        if (headers.containsKey(SET_COOKIE_KEY)
                && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
                String cookie = headers.get(SET_COOKIE_KEY);
                if (cookie.length() > 0) {
                    String[] splitCookie = cookie.split(";");
                    String[] splitSessionId = splitCookie[0].split("=");
                    cookie = splitSessionId[1];
                    Editor prefEditor = _preferences.edit();
                    prefEditor.putString(SESSION_COOKIE, cookie);
                    prefEditor.commit();
              //      Log.i("TAG", _preferences.getString(SESSION_COOKIE, ""));
                }
            }
    }

    /**
     * Adds session cookie to headers if exists.
     * @param headers
     */
    public final void addSessionCookie(Map<String, String> headers) {
        String sessionId = _preferences.getString(SESSION_COOKIE, "");
        if (sessionId.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(SESSION_COOKIE);
            builder.append("=");
            builder.append(sessionId);
            if (headers.containsKey(COOKIE_KEY)) {
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
        //    Log.i("TAG", builder.toString());
            headers.put(COOKIE_KEY, builder.toString());
        }
    }
    
    public final void deleteSessionCookie()
    {
    	Editor editor = _preferences.edit();  
    	editor.clear();  
    	editor.commit(); 
    }
    
    
    

   /*  public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

   public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    *//**
     * 获取cookie时传AppConfig.CONF_COOKIE
     * 
     * @param key
     * @return
     */
/*    public String getProperty(String key) {
        String res = AppConfig.getAppConfig(this).get(key);
        return res;
    }*/

 /*   public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }*/

    /**
     * 获取App唯一标识
     * 
     * @return
     *//*
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    *//**
     * 获取App安装包信息
     * 
     * @return
     *//*
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }*/

    /**
     * 保存登录信息
     * 
     * @param username
     * @param pwd
     */
    @SuppressWarnings("serial")
    public void saveUserInfo(final User user) {/*
        this.loginUid = user.getId();
        this.login = true;
        setProperties(new Properties() {
            {
                setProperty("user.uid", String.valueOf(user.getId()));
                setProperty("user.name", user.getName());
                setProperty("user.face", user.getPortrait());// 用户头像-文件名
                setProperty("user.account", user.getAccount());
                setProperty("user.pwd",
                        CyptoUtils.encode("oschinaApp", user.getPwd()));
                setProperty("user.location", user.getLocation());
                setProperty("user.followers",
                        String.valueOf(user.getFollowers()));
                setProperty("user.fans", String.valueOf(user.getFans()));
                setProperty("user.score", String.valueOf(user.getScore()));
                setProperty("user.favoritecount",
                        String.valueOf(user.getFavoritecount()));
                setProperty("user.gender", String.valueOf(user.getGender()));
                setProperty("user.isRememberMe",
                        String.valueOf(user.isRememberMe()));// 是否记住我的信息
            }
        });
    */}

    /**
     * 更新用户信息
     * 
     * @param user
     */
    @SuppressWarnings("serial")
    public void updateUserInfo(final User user) {/*
        setProperties(new Properties() {
            {
                setProperty("user.name", user.getName());
                setProperty("user.face", user.getPortrait());// 用户头像-文件名
                setProperty("user.followers",
                        String.valueOf(user.getFollowers()));
                setProperty("user.fans", String.valueOf(user.getFans()));
                setProperty("user.score", String.valueOf(user.getScore()));
                setProperty("user.favoritecount",
                        String.valueOf(user.getFavoritecount()));
                setProperty("user.gender", String.valueOf(user.getGender()));
            }
        });
    */}

    /**
     * 获得登录用户的信息
     * 
     * @return
     */
    public User getLoginUser() {
        User user = new User();
        user.setU_name(get(Contanst.PROP_KEY_UNAME));
        user.setU_pwd(CyptoUtils.decode("voteApppush",get(Contanst.PROP_KEY_UPWD)));
        user.setU_email(get(Contanst.ROP_KEY_UEMAIL));
        user.setU_tel(get(Contanst.ROP_KEY_UTEL));
        
        
        return user;
 
    }
    
    public String get(String key) {
        Properties props = get();
        return (props != null) ? props.getProperty(key) : null;
    }
    
    public Properties get() {
		FileInputStream fis = null;
		Properties props = new Properties();
		try {
			// 读取files目录下的config
			// fis = activity.openFileInput(APP_CONFIG);

			// 读取app_config目录下的config
			File dirConf = this.getDir(APP_CONFIG, Context.MODE_PRIVATE);
			fis = new FileInputStream(dirConf.getPath() + File.separator
					+ APP_CONFIG);

			props.load(fis);
		} catch (Exception e) {
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return props;
	}
    /**
     * 清除登录信息
     */
    public void cleanLoginInfo() {/*
        this.loginUid = 0;
        this.login = false;
        removeProperty("user.uid", "user.name", "user.face", "user.location",
                "user.followers", "user.fans", "user.score",
                "user.isRememberMe", "user.gender", "user.favoritecount");
    */}

/*    public int getLoginUid() {
        return loginUid;
    }*/

    public boolean isLogin() {
        return login;
    }

    /**
     * 用户注销
     */
    public void Logout() {/*
        cleanLoginInfo();
        ApiHttpClient.cleanCookie();
        this.cleanCookie();
        this.login = false;
        this.loginUid = 0;

        Intent intent = new Intent(Constants.INTENT_ACTION_LOGOUT);
        sendBroadcast(intent);
    */}

    /**
     * 清除保存的缓存
     */
/*    public void cleanCookie() {
        removeProperty(AppConfig.CONF_COOKIE);
    }*/

    /**
     * 清除app缓存
     */
/*    public void clearAppCache() {
        DataCleanManager.cleanDatabases(this);
        // 清除数据缓存
        DataCleanManager.cleanInternalCache(this);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            DataCleanManager.cleanCustomCache(MethodsCompat
                    .getExternalCacheDir(this));
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
        new KJBitmap().cleanCache();
    }*/

  /*  public static void setLoadImage(boolean flag) {
        set(KEY_LOAD_IMAGE, flag);
    }*/

    /**
     * 判断当前版本是否兼容目标版本的方法
     * 
     * @param VersionCode
     * @return
     */
 /*   public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    public static String getTweetDraft() {
        return getPreferences().getString(
                KEY_TWEET_DRAFT + getInstance().getLoginUid(), "");
    }

    public static void setTweetDraft(String draft) {
        set(KEY_TWEET_DRAFT + getInstance().getLoginUid(), draft);
    }

    public static String getNoteDraft() {
        return getPreferences().getString(
                AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), "");
    }

    public static void setNoteDraft(String draft) {
        set(AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), draft);
    }

    public static boolean isFristStart() {
        return getPreferences().getBoolean(KEY_FRITST_START, true);
    }

    public static void setFristStart(boolean frist) {
        set(KEY_FRITST_START, frist);
    }*/
}
