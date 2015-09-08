package com.xl.voteapp.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.xl.voteapp.R;
import com.xl.voteapp.base.AppContext;
import com.xl.voteapp.base.BaseActivity;
import com.xl.voteapp.base.RequestQueueSingleton;
import com.xl.voteapp.bean.RecData;
import com.xl.voteapp.bean.User;
import com.xl.voteapp.common.BroadcastController;
import com.xl.voteapp.common.Contanst;
import com.xl.voteapp.common.CyptoUtils;
import com.xl.voteapp.ui.empty.EmptyLayout;
import com.xl.voteapp.util.GsonRequest;
import com.xl.voteapp.util.StringUtils;
import com.xl.voteapp.util.TDevice;
import com.xl.voteapp.util.UIHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends /* Base */Activity implements OnClickListener, OnEditorActionListener {

	@InjectView(R.id.login_btn_login)
	Button mLogin;

	ActivityHelper mHelper = new ActivityHelper(this);
	// private AppContext mAppContext;
	private AutoCompleteTextView mAccountEditText;
	private EditText mPasswordEditText;
	private ProgressDialog mLoginProgressDialog;

	// private Button mLogin;
	private InputMethodManager imm;
	private TextWatcher textWatcher;
	private Context mContext;
	private final static String APP_CONFIG = "config";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// mAppContext = getGitApplication();
		initView();
	}

	private void initView() {
		ButterKnife.inject(this);
		mAccountEditText = (AutoCompleteTextView) findViewById(R.id.login_account);
		mPasswordEditText = (EditText) findViewById(R.id.login_password);
		// mLogin = (Button) findViewById(R.id.login_btn_login);
		mLogin.setOnClickListener(this);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		textWatcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				// 若密码和帐号都为空，则登录按钮不可操作
				String account = mAccountEditText.getText().toString();
				String pwd = mPasswordEditText.getText().toString();
				if (StringUtils.isEmpty(account) || StringUtils.isEmpty(pwd)) {
					mLogin.setEnabled(false);
				} else {
					mLogin.setEnabled(true);
				}
			}
		};
		// 添加文本变化监听事件
		mAccountEditText.addTextChangedListener(textWatcher);
		mPasswordEditText.addTextChangedListener(textWatcher);
		mPasswordEditText.setOnEditorActionListener(this);

		/*
		 * String account = CyptoUtils.decode(Contanst.ACCOUNT_EMAIL,
		 * mAppContext.getProperty(Contanst.ACCOUNT_EMAIL));
		 * mAccountEditText.setText(account); String pwd =
		 * CyptoUtils.decode(Contanst.ACCOUNT_PWD,
		 * mAppContext.getProperty(Contanst.ACCOUNT_PWD));
		 * mPasswordEditText.setText(pwd);
		 */
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// 在输入法里点击了“完成”，则去登录
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			checkLogin();
			// 将输入法隐藏
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
			return true;
		}
		return false;
	}

	/**
	 * 检查登录
	 */
	private void checkLogin() {
		if (!TDevice.hasInternet()) {
			Toast.makeText(this, "无网络连接", Toast.LENGTH_LONG).show();
			return;
		}
		int email = Integer.valueOf(mAccountEditText.getText().toString());
		String passwd = mPasswordEditText.getText().toString();
		// 检查用户输入的参数
		if (StringUtils.isEmpty(String.valueOf(email))) {
			Toast.makeText(this, "用户名为空", Toast.LENGTH_LONG).show();
			return;
		}
		/*
		 * if(!StringUtils.isEmail(email)) { Toast.makeText(this,
		 * "email不合法",Toast.LENGTH_LONG).show(); return; }
		 */
		if (StringUtils.isEmpty(passwd)) {
			Toast.makeText(this, "密码为空", Toast.LENGTH_LONG).show();
			return;
		}
		// 保存用户名和密码
		// mAppContext.saveAccountInfo(CyptoUtils.encode(Contanst.ACCOUNT_EMAIL,
		// email), CyptoUtils.encode(Contanst.ACCOUNT_PWD, passwd));
		login(email, passwd);
	}

	// 方法：连接服务器进行登录
	public void login(final int uno, final String passwd) {
		if (mLoginProgressDialog == null) {
			mLoginProgressDialog = new ProgressDialog(this);
			mLoginProgressDialog.setCancelable(true);
			mLoginProgressDialog.setCanceledOnTouchOutside(false);
			mLoginProgressDialog.setMessage("正在登陆...");
		}
		if (mLoginProgressDialog != null) {
			mLoginProgressDialog.show();
		}
		GsonRequest<User> gsonObjRequest;
		String url = Contanst.URLPATH + "Login";
		Map<String, String> appendHeader = new HashMap<String, String>();
		appendHeader.put("u_no", String.valueOf(uno));
		appendHeader.put("pageNo", CyptoUtils.encode("voteApppush", passwd));
		AppContext.getInstance().addSessionCookie(appendHeader);
		gsonObjRequest = new GsonRequest<User>(Request.Method.POST, url, User.class, null,
				new Response.Listener<User>() {
					@Override
					public void onResponse(User response) {
						try {
							User user = response;
							if (user.getU_no()!=0) {
								saveLoginInfo(user);
								AppContext.login = true;
								mLoginProgressDialog.dismiss();
								Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
								// 发送用户登录成功的广播
								BroadcastController.sendUserChangeBroadcase(getActivity());
								finish();
							} else {
								AppContext.login = false;
								mLoginProgressDialog.dismiss();
								Toast.makeText(LoginActivity.this, "帐号密码错误", Toast.LENGTH_LONG).show();
								return;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if (error instanceof NetworkError) {
						} else if (error instanceof ServerError) {
						} else if (error instanceof AuthFailureError) {
						} else if (error instanceof ParseError) {
						} else if (error instanceof NoConnectionError) {
						} else if (error instanceof TimeoutError) {
						}
					}
				}, appendHeader);
		RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(gsonObjRequest);

	}

	protected Context getActivity() {

		return mHelper.getActivity();
	}

	@Override
	public void onClick(View v) {
		imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
		checkLogin();
	}

	/**
	 * 保存登录用户的信息到property
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
			{
				setProperty(Contanst.PROP_KEY_UNO, String.valueOf(user.getU_no()));
				setProperty(Contanst.PROP_KEY_UNAME, String.valueOf(user.getU_name()));
				setProperty(Contanst.PROP_KEY_UPWD, CyptoUtils.encode("voteApppush", user.getU_pwd()));
				setProperty(Contanst.ROP_KEY_UEMAIL, String.valueOf(user.getU_email()));
				setProperty(Contanst.ROP_KEY_UTEL, String.valueOf(user.getU_tel()));
			}
		});

	}

	public void setProperties(Properties ps) {
		set(ps);
	}

	public Properties get() {
		FileInputStream fis = null;
		Properties props = new Properties();
		try {
			// 读取files目录下的config
			// fis = activity.openFileInput(APP_CONFIG);
			// 读取app_config目录下的config
			File dirConf = getApplication().getDir(APP_CONFIG, Context.MODE_PRIVATE);
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

	private void setProps(Properties p) {
		FileOutputStream fos = null;
		try {
			// 把config建在files目录下
			// fos = activity.openFileOutput(APP_CONFIG, Context.MODE_PRIVATE);
			// 把config建在(自定义)app_config的目录下
			File dirConf = getApplication().getDir(APP_CONFIG, Context.MODE_PRIVATE);
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

	public void set(Properties ps) {
		Properties props = get();
		props.putAll(ps);
		setProps(props);
	}

}
