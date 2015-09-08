package com.xl.voteapp.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.xl.voteapp.R;
import com.xl.voteapp.base.AppContext;
import com.xl.voteapp.base.RequestQueueSingleton;
import com.xl.voteapp.bean.User;
import com.xl.voteapp.common.BroadcastController;
import com.xl.voteapp.common.Contanst;
import com.xl.voteapp.common.CyptoUtils;
import com.xl.voteapp.upload.UploadRegisterUserInformation;
import com.xl.voteapp.util.StringUtils;
import com.xl.voteapp.util.UIHelper;
import com.xl.voteapp.widget.CircleImageView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 侧滑菜单界面
 * 
 */
public class NavigationDrawerFragment extends Fragment implements OnClickListener {

	// private DrawerLayout mDrawerLayout;
	private View mDrawerListView;
	private View mFragmentContainerView;
	private int mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private CircleImageView mUser_info_userface;
	private LinearLayout mMenu_user_register;
	private LinearLayout mMenu_user_login_native;
	private LinearLayout mMenu_user_login_qq;
	private LinearLayout mMenu_user_login_blog;
	private LinearLayout mMenu_user_vote;
	private LinearLayout mMenu_setting;
	private LinearLayout mMenu_exit;
	private TextView mUser_info_username;
	private Context mContext;
	private final static String APP_CONFIG = "config";
	private int uno;
    private String u_name;
    private User user;
	// 整个平台的Controller,负责管理整个SDK的配置、操作等处理
	private UMSocialService mController = UMServiceFactory.getUMSocialService(Contanst.DESCRIPTOR);

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		setupUserView(AppContext.login);
	}

	private void initView(View view) {
		mMenu_user_login_native = (LinearLayout) view.findViewById(R.id.menu_item_login_native);
		mMenu_user_login_qq = (LinearLayout) view.findViewById(R.id.menu_item_login_qq);
		mMenu_user_login_blog = (LinearLayout) view.findViewById(R.id.menu_item_login_blog);
		mMenu_user_register = (LinearLayout) view.findViewById(R.id.menu_item_register);
		mMenu_user_vote = (LinearLayout) view.findViewById(R.id.menu_item_vote);
	//	mMenu_setting = (LinearLayout) view.findViewById(R.id.menu_item_setting);
		mMenu_exit = (LinearLayout) view.findViewById(R.id.menu_item_exit);
		mUser_info_userface = (CircleImageView) view.findViewById(R.id.menu_userface);
		mMenu_user_login_native.setOnClickListener(this);
		mMenu_user_register.setOnClickListener(this);
		mMenu_user_vote.setOnClickListener(this);
		mMenu_user_login_qq.setOnClickListener(this);
		mMenu_user_login_blog.setOnClickListener(this);
	//	mMenu_setting.setOnClickListener(this);
		mMenu_exit.setOnClickListener(this);
		mUser_info_username = (TextView) view.findViewById(R.id.menu_item_login_name);
		

	}

	private BroadcastReceiver mUserChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 接收到变化后，更新用户资料
			setupUserView(true);
		}
	};

	private void setupUserView(final boolean reflash) {
		// 判断是否已经登录，如果已登录则显示用户的头像与信息
		if (!AppContext.login) {
			mUser_info_username.setText("投票系统账号登录");
			mUser_info_userface.setImageResource(R.drawable.mini_avatar);
			return;
		}

		new AsyncTask<Void, Void, User>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();				
			}

			@Override
			protected User doInBackground(Void... params) {
				user = getLoginInfo();
				return user;
			}

			@Override
			protected void onPostExecute(User user) {
				if (user == null || isDetached()) {		
					return;
				}
				// 加载用户头像

				if (user.get_portrait() != null) {
					String faceUrl = user.get_portrait();
					
					ImageLoader imageLoader = new ImageLoader(AppContext.mQueue, new ImageCache() {
						@Override
						public void putBitmap(String url, Bitmap bitmap) {
						}

						@Override
						public Bitmap getBitmap(String url) {
							return null;
						}
					});
					
					imageLoader.get(faceUrl, ImageLoader.getImageListener(mUser_info_userface,
							R.drawable.widget_dface_loading, R.drawable.mini_avatar));
					
					
				/*	ImageRequest imageRequest = new ImageRequest(faceUrl, new Response.Listener<Bitmap>() {
						@Override
						public void onResponse(Bitmap response) {
							mUser_info_userface.setImageBitmap(response);
						}
					}, 0, 0, Config.RGB_565, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							mUser_info_userface.setImageResource(R.drawable.mini_avatar);
						}
					});*/				
				//	RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(imageRequest);
				}
				mUser_info_username.setText(user.getU_name());
			}
		}.execute();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		BroadcastController.registerUserChangeReceiver(activity, mUserChangeReceiver);

	}

	@Override
	public void onDetach() {
		super.onDetach();
		BroadcastController.unregisterReceiver(getActivity(), mUserChangeReceiver);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mDrawerListView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
		initView(mDrawerListView);
		// 配置需要分享的相关平台
		configPlatforms();
		return mDrawerListView;
	}

	/**
	 * 配置分享平台参数
	 */
	private void configPlatforms() {
		// 添加新浪sso授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加腾讯微博SSO授权
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		// 添加人人网SSO授权
		RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(getActivity(), "201874",
				"28401c0964f04a72a14c812d6132fcef", "3bf66e42db1e4fa9829b955cc300b737");
		mController.getConfig().setSsoHandler(renrenSsoHandler);

		// 添加QQ、QZone平台
		addQQQZonePlatform();

		// 添加微信、微信朋友圈平台
		addWXPlatform();

	}

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	private void addWXPlatform() {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wx967daebe835fbeac";
		String appSecret = "5bb696d9ccd75a38c8a0bfe0675559b3";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(getActivity(), appId, appSecret);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(), appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	/**
	 * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
	 *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
	 *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
	 *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
	 * @return
	 */
	private void addQQQZonePlatform() {
		String appId = "100424468";
		String appKey = "c7394704798a158208a74ab60104f0ba";
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(), appId, appKey);
		qqSsoHandler.setTargetUrl("http://www.umeng.com");
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), appId, appKey);
		qZoneSsoHandler.addToSocialSDK();
	}

	/**
	 * 授权。如果授权成功，则获取用户信息
	 * 
	 * @param platform
	 */
	private void login(final SHARE_MEDIA platform) {
		mController.doOauthVerify(getActivity(), platform, new UMAuthListener() {

			@Override
			public void onStart(SHARE_MEDIA platform) {
				// Toast.makeText(getActivity(), "授权开始",
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError(SocializeException e, SHARE_MEDIA platform) {
				Toast.makeText(getActivity(), "授权失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(Bundle value, SHARE_MEDIA platform) {
				// 获取uid
				String thirduid = value.getString("uid");
				if (!TextUtils.isEmpty(thirduid)) {
				//	Log.i("info", thirduid);
					// uid不为空，获取用户信息
					getUserInfo(platform,thirduid);
				} else {
					Toast.makeText(getActivity(), "授权失败...", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onCancel(SHARE_MEDIA platform) {
				Toast.makeText(getActivity(), "授权取消", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 获取用户信息
	 * 
	 * @param platform
	 * @param uid 
	 */
	private void getUserInfo(SHARE_MEDIA platform, final String thirduid) {
		mController.getPlatformInfo(getActivity(), platform, new UMDataListener() {

			@Override
			public void onStart() {

			}

			@Override
			public void onComplete(int status, Map<String, Object> info) {
				// String showText = "";
				// if (status == StatusCode.ST_CODE_SUCCESSED) {
				// showText = "用户名：" +
				// info.get("screen_name").toString();
				// Log.d("#########", "##########" + info.toString());
				// } else {
				// showText = "获取用户信息失败";
				// }
				
				if (info != null) {
				    user = new User();	
				    user.set_portrait(String.valueOf(info.get("profile_image_url")));
				    user.setU_thirduid(thirduid);
					user.setU_name(String.valueOf(info.get("screen_name")));
					register(user.getU_name(),thirduid,user.get_portrait());
													
				}
			}
		});
	}

	protected void register(final String u_name, final String thirduid, final String portrait) {
		new Thread(){
			public void run(){
				Looper.prepare();																		
				try{										
					int result = UploadRegisterUserInformation.save(u_name, "", "",  "",thirduid,portrait);
		    		if(result!=0){
		    			user.setU_no(result);
		    			saveLoginInfo(user);
		    			AppContext.login=true;
		    			BroadcastController.sendUserChangeBroadcase(getActivity());
		    			
		    			
		    			
				//		finish();
		    	//		mRegisterProgressDialog.dismiss();
				//		Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_LONG).show();
						Looper.loop();
				    }else{
		    	//		mRegisterProgressDialog.dismiss();
			//	    	Toast.makeText(RegisterActivity.this, "注册失败！请重试！", Toast.LENGTH_LONG).show();					
						Looper.loop();
						return;						
			    	}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
		
	}

	/**
	 * 保存登录用户的信息
	 * 
	 * @param user
	 */
	@SuppressWarnings("serial")
	private void saveLoginInfo(final User user) {
		if (null == user) {
			return;
		}
		// 保存用户的信息
		setProperties(new Properties() {
			{   setProperty(Contanst.PROP_KEY_UNO, String.valueOf(user.getU_no())/*String.valueOf(user.getU_tel())*/);
				setProperty(Contanst.PROP_KEY_UNAME, String.valueOf(user.getU_name()));
				setProperty(Contanst.PROP_KEY_UPWD, ""/*CyptoUtils.encode("voteApppush", user.getU_pwd())*/);
				setProperty(Contanst.ROP_KEY_UEMAIL, ""/*String.valueOf(user.getU_email())*/);
				setProperty(Contanst.ROP_KEY_UTEL, ""/*String.valueOf(user.getU_tel())*/);
				setProperty(Contanst.ROP_KEY_PORTRAIT, String.valueOf(user.get_portrait()));
			}
		});

	}

	public void setProperties(Properties ps) {
		set(ps);
	}

	public void set(Properties ps) {
		Properties props = get();
		props.putAll(ps);
		setProps(props);
	}

	private void setProps(Properties p) {
		FileOutputStream fos = null;
		try {
			// 把config建在files目录下
			// fos = activity.openFileOutput(APP_CONFIG, Context.MODE_PRIVATE);
			// 把config建在(自定义)app_config的目录下
			File dirConf = getActivity().getApplication().getDir(APP_CONFIG, Context.MODE_PRIVATE);
			File conf = new File(dirConf, APP_CONFIG);
			fos = new FileOutputStream(conf);
			p.store(fos, null);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {

				fos.close();
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * 点击事件
	 * 
	 * 
	 */
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.menu_item_login_native:
			if (!AppContext.login)
				UIHelper.showLoginActivity(getActivity());
			else
				UIHelper.showMySelfInfoActivity(getActivity());
			break;
		case R.id.menu_item_register:
			UIHelper.showRegisterActivity(getActivity());
			break;
		case R.id.menu_item_login_qq:
			login(SHARE_MEDIA.QQ);
			break;
		case R.id.menu_item_login_blog:
			login(SHARE_MEDIA.SINA);
			break;
		case R.id.menu_item_vote:
			if (!AppContext.login)
				Toast.makeText(getActivity(), "请先登录再发起投票", Toast.LENGTH_SHORT).show();
			else		    
				UIHelper.showPublishVoteActivity(getActivity(), user.getU_no(),user.getU_name(), user.get_portrait());
			break;
		/*case R.id.menu_item_setting:
			break;*/
		case R.id.menu_item_exit:
			getActivity().finish();
			break;
		default:
			break;

		}
		/*
		 * mDrawerLayout.postDelayed(new Runnable() {
		 * 
		 * @Override public void run() { mDrawerLayout.closeDrawers(); } },
		 * 800);
		 */
	}

	/**
	 * 获取登录信息
	 * 
	 * @return
	 */
	public User getLoginInfo() {
		User user = new User();
		user.setU_no(Integer.valueOf(getProperty(Contanst.PROP_KEY_UNO)));
		user.setU_name(getProperty(Contanst.PROP_KEY_UNAME));
		user.setU_pwd(getProperty(Contanst.PROP_KEY_UPWD));
		user.setU_email(getProperty(Contanst.ROP_KEY_UEMAIL));
		user.setU_tel(getProperty(Contanst.ROP_KEY_UTEL));
		user.set_portrait(getProperty(Contanst.ROP_KEY_PORTRAIT));
		return user;
	}

	public String getProperty(String key) {
		String res = get(key);
		return res;
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
			File dirConf = getActivity().getApplication().getDir(APP_CONFIG, Context.MODE_PRIVATE);
			fis = new FileInputStream(dirConf.getPath() + File.separator + APP_CONFIG);

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
}
