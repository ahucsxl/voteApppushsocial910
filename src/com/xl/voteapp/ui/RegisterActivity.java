package com.xl.voteapp.ui;
import com.xl.voteapp.R;
import com.xl.voteapp.base.BaseActivity;
import com.xl.voteapp.common.Contanst;
import com.xl.voteapp.common.CyptoUtils;
import com.xl.voteapp.upload.UploadRegisterUserInformation;
import com.xl.voteapp.util.StringUtils;
import com.xl.voteapp.util.TDevice;
import com.xl.voteapp.util.UIHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.TextView.OnEditorActionListener;


public class RegisterActivity extends /*Base*/Activity 
	implements OnClickListener/*, OnEditorActionListener*/ {
	

	private EditText mAccountEditText;
	private EditText mPasswordEditText;
	private EditText mPasswordverEditText;
	private EditText mEmailEditText;
	private EditText mTelEditText;
	private ProgressDialog mRegisterProgressDialog;
	private Button mRegister;
	private InputMethodManager imm;
	private TextWatcher textWatcher;
	private TextView regresult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
	}

	private void initView() {
		mAccountEditText = (EditText) findViewById(R.id.register_name);
		mPasswordEditText = (EditText) findViewById(R.id.register_password);
		mPasswordverEditText = (EditText) findViewById(R.id.register_passwordver);
		mEmailEditText = (EditText) findViewById(R.id.register_email);
		mTelEditText = (EditText) findViewById(R.id.register_tel);
		mRegister = (Button) findViewById(R.id.register_btn);
		regresult = (TextView) findViewById(R.id.regresult);
		
		mRegister.setOnClickListener(this);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		textWatcher = new TextWatcher() {	
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				
				// 若密码和帐号都为空，则登录按钮不可操作
				String account = mAccountEditText.getText().toString();
				String pwd = mPasswordEditText.getText().toString();
				String pwdver = mPasswordverEditText.getText().toString();
				String email = mEmailEditText.getText().toString();
				String tel = mTelEditText.getText().toString();
				if (StringUtils.isEmpty(account) || StringUtils.isEmpty(pwd) || StringUtils.isEmpty(pwdver) || StringUtils.isEmpty(email) || StringUtils.isEmpty(tel)) {
					mRegister.setEnabled(false);
				} else {
					mRegister.setEnabled(true);
				}
			}
		};
		// 添加文本变化监听事件
		mAccountEditText.addTextChangedListener(textWatcher);
		mPasswordEditText.addTextChangedListener(textWatcher);
		mPasswordverEditText.addTextChangedListener(textWatcher);
		mEmailEditText.addTextChangedListener(textWatcher);
		mTelEditText.addTextChangedListener(textWatcher);
		
///		mPasswordEditText.setOnEditorActionListener(this);
		
/*		String account = CyptoUtils.decode(Contanst.ACCOUNT_EMAIL, mAppContext.getProperty(Contanst.ACCOUNT_EMAIL));
		mAccountEditText.setText(account);
		String pwd = CyptoUtils.decode(Contanst.ACCOUNT_PWD, mAppContext.getProperty(Contanst.ACCOUNT_PWD));
		mPasswordEditText.setText(pwd);*/
	}
	
/*	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		//在输入法里点击了“完成”，则去登录
		if(actionId == EditorInfo.IME_ACTION_DONE) {
			checkLogin();
			//将输入法隐藏
			InputMethodManager imm = (InputMethodManager)getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
			return true;
		}
		return false;
	}*/
	
	/**
	 * 检查注册
	 */
	private void checkRegister() {
		String name = mAccountEditText.getText().toString();
		String passwd = mPasswordEditText.getText().toString();	
		String passwdver = mPasswordverEditText.getText().toString();	
		String email = mEmailEditText.getText().toString();	
		String tel = mTelEditText.getText().toString();	
		if (!TDevice.hasInternet()) {
			Toast.makeText(this, "无网络连接", Toast.LENGTH_LONG).show();
			return;
		}
		//检查用户输入的参数
		if(StringUtils.isEmpty(name)){
			Toast.makeText(this, "用户名为空",Toast.LENGTH_LONG).show();
			return;
		}
		if(StringUtils.isEmpty(passwd)){
			Toast.makeText(this, "密码为空",Toast.LENGTH_LONG).show();			
			return;
		}		
		if(StringUtils.isEmpty(passwdver)){
			Toast.makeText(this, "密码确认为空",Toast.LENGTH_LONG).show();			
			return;
		}
		if(StringUtils.isEmpty(email)){
			Toast.makeText(this, "email为空",Toast.LENGTH_LONG).show();			
			return;
		}
		if(!StringUtils.isEmail(email)) {
			Toast.makeText(this, "email不合法",Toast.LENGTH_LONG).show();			
			return;
		}
		if(StringUtils.isEmpty(tel)){
			Toast.makeText(this, "电话号码为空",Toast.LENGTH_LONG).show();			
			return;
		}
		if(!passwd.equals(passwdver)){				//判断两次输入的密码是否一致
			Toast.makeText(RegisterActivity.this, "两次输入的密码不一致！", Toast.LENGTH_LONG).show();
			return;
		}
		// 保存用户名和密码
//		mAppContext.saveAccountInfo(CyptoUtils.encode(Contanst.ACCOUNT_EMAIL, email), CyptoUtils.encode(Contanst.ACCOUNT_PWD, passwd));		
		register(name,passwd, passwdver,email,tel);

	}
	
	// 登录验证
	private void register(final String account, final String passwd, String passwdver, final String email, final String tel) {
		if(mRegisterProgressDialog == null) {
			mRegisterProgressDialog = new ProgressDialog(this);
			mRegisterProgressDialog.setCancelable(true);
			mRegisterProgressDialog.setCanceledOnTouchOutside(false);
			mRegisterProgressDialog.setMessage("正在注册。。。");
    	}
		
		if(mRegisterProgressDialog != null) {
			mRegisterProgressDialog.show();
		}
		
		new Thread(){
			public void run(){
				Looper.prepare();																		
				try{										
					int result ;		    		
		    		result = UploadRegisterUserInformation.save(account, passwd, email,  tel,"myapp","");
		    		if(result!=0){
					//	finish();
		    			
		    			mRegisterProgressDialog.dismiss();
		    	//		
		    			regresult.setText("注册成功，您的登录账号为："+result);
		    			
					//	Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_LONG).show();
						Looper.loop();
				    }else{
		    			mRegisterProgressDialog.dismiss();
				    	Toast.makeText(RegisterActivity.this, "注册失败！请重试！", Toast.LENGTH_LONG).show();					
						Looper.loop();
						return;						
			    	}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
/*	
		//异步登录
    	new AsyncTask<Void, Void, Message>() {
			@Override
			protected Message doInBackground(Void... params) {
				Message msg =new Message();
				try {
	                User user = mAppContext.loginVerify(account, passwd);
	                msg.what = 1;
	                msg.obj = user;
	            } catch (Exception e) {
			    	msg.what = -1;
			    	msg.obj = e;
			    	if(mLoginProgressDialog != null) {
						mLoginProgressDialog.dismiss();
					}
	            }
				return msg;
			}
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				if(mLoginProgressDialog != null) {
					mLoginProgressDialog.show();
				}
			}
			
			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				//如果程序已经关闭，则不再执行以下处理
				if(isFinishing()) {
					return;
				}
				if(mLoginProgressDialog != null) {
					mLoginProgressDialog.dismiss();
				}
				Context context = LoginActivity.this;
				if(msg.what == 1){
					User user = (User)msg.obj;
					if(user != null){
						//提示登陆成功
						UIHelper.ToastMessage(context, R.string.msg_login_success);
						//返回标识，成功登录
						setResult(RESULT_OK);
						// 发送用户登录成功的广播
						BroadcastController.sendUserChangeBroadcase(getActivity());
						finish();
					}
				} else if(msg.what == 0){
					UIHelper.ToastMessage(context, getString(
							R.string.msg_login_fail) + msg.obj);
				} else if(msg.what == -1){
					if (msg.obj instanceof AppException) {
						AppException e = ((AppException)msg.obj);
						if (e.getCode() == 401) {
							UIHelper.ToastMessage(context, R.string.msg_login_error);
						} else {
							((AppException)msg.obj).makeToast(context);
						}
					} else {
						UIHelper.ToastMessage(context, R.string.msg_login_error);
					}
				}
			}
		}.execute();*/
	}

	@Override
	public void onClick(View v) {
		imm.hideSoftInputFromWindow(mPasswordEditText.getWindowToken(), 0);
		checkRegister();
	}
}
