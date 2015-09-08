package com.xl.voteapp.ui;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.xl.voteapp.R;
import com.xl.voteapp.base.AppContext;
import com.xl.voteapp.bean.User;
import com.xl.voteapp.common.BroadcastController;
import com.xl.voteapp.common.Contanst;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 用户信息详情页面
 * @created 2014-07-01
 * @author 火蚁(http://my.oschina.net/LittleDY)
 *
 */

public class MySelfInfoActivity extends Activity implements View.OnClickListener {


	private User mUser;
	private TextView mUserName;
	private TextView mUserEmail;
	private TextView mUserTel;
	private TextView mUserPassword;
	private Button Loginout;
	
	private final static String APP_CONFIG = "config";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myselfinfo_detail);
		initView();
		initData();
	}
	
	private void initView() {
		mUserName = (TextView) findViewById(R.id.user_info_name);
		mUserEmail = (TextView) findViewById(R.id.user_info_email);
		mUserTel = (TextView) findViewById(R.id.user_info_tel);
		Loginout = (Button) findViewById(R.id.user_info_logout);
	//	mEditFace.setOnClickListener(this);		
		Loginout.setOnClickListener(this);
	}
	
	private void initData() {
		mUser = getLoginInfo();
		if (mUser != null) {
			mUserName.setText(mUser.getU_name());
			mUserEmail.setText(mUser.getU_email());
			mUserTel.setText(mUser.getU_tel());
		}
	}
	
	/**
	 * 获取登录信息
	 * 
	 * @return
	 */
	public User getLoginInfo() {
		User user = new User();
		user.setU_name(getProperty(Contanst.PROP_KEY_UNAME));
		user.setU_pwd(getProperty(Contanst.PROP_KEY_UPWD));
		user.setU_email(getProperty(Contanst.ROP_KEY_UEMAIL));
		user.setU_tel(getProperty(Contanst.ROP_KEY_UTEL));

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
			File dirConf = getApplication().getDir(APP_CONFIG, Context.MODE_PRIVATE);
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

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
	/*	// 编辑用户头像
		case R.id.user_info_editer:
			CharSequence[] items = { getString(R.string.img_from_album),
					getString(R.string.img_from_camera) };
			imageChooseItem(items);
			break;*/
		case R.id.user_info_logout:
			loginout();
			
			break;
		// 其他
		default:
			break;
		}
	}
	
	private void loginout() {
    //	ApiClient.cleanToken();
		// 清除已登录用户的信息
		AppContext.getInstance().deleteSessionCookie();
		    cleanLoginInfo();
		    AppContext.login= false;
            BroadcastController.sendUserChangeBroadcase(MySelfInfoActivity.this);
			this.finish();
	}
	
	/**
	 * 清除登录信息，用户的私有token也一并清除
	 */
	private void cleanLoginInfo() {
		removeProperty(Contanst.PROP_KEY_UNO,Contanst.PROP_KEY_UNAME,Contanst.PROP_KEY_UPWD,Contanst.ROP_KEY_UEMAIL,Contanst.ROP_KEY_UTEL,Contanst.ROP_KEY_PORTRAIT);
	}
	
	public void removeProperty(String... key) {
		remove(key);
	}
	
	public void remove(String... key) {
		Properties props = get();
		for (String k : key)
			props.remove(k);
		setProps(props);
	}
	
	private void setProps(Properties p) {
		FileOutputStream fos = null;
		try {
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
	private void imageChooseItem(CharSequence[] items) {/*
		AlertDialog imageDialog = new AlertDialog.Builder(this)
		.setTitle("上传头像").setIcon(android.R.drawable.btn_star)
		.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// 相册选图
				if (item == 0) {
					startImagePick();
				}
				// 手机拍照
				else if (item == 1) {
					startActionCamera();
				}
			}
		}).create();

		imageDialog.show();
	*/}
	
	/**
	 * 选择图片裁剪
	 * 
	 * @param output
	 */
	private void startImagePick() {/*
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "选择图片"),
				ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
	*/}
	
	/**
	 * 相机拍照
	 * 
	 * @param output
	 */
	private void startActionCamera() {/*
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, this.getCameraTempFile());
		startActivityForResult(intent,
				ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
	*/}
	
	// 拍照保存的绝对路径
	/*private Uri getCameraTempFile() {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			File savedir = new File(FILE_SAVEPATH);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
		} else {
			UIHelper.ToastMessage(getGitApplication(), "无法保存上传的头像，请检查SD卡是否挂载");
			return null;
		}
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		// 照片命名
		String cropFileName = "osc_camera_" + timeStamp + ".jpg";
		// 裁剪头像的绝对路径
		protraitPath = FILE_SAVEPATH + cropFileName;
		protraitFile = new File(protraitPath);
		cropUri = Uri.fromFile(protraitFile);
		this.origUri = this.cropUri;
		return this.cropUri;
	}*/
	
	// 裁剪头像的绝对路径
	/*private Uri getUploadTempFile(Uri uri) {
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			File savedir = new File(FILE_SAVEPATH);
			if (!savedir.exists()) {
				savedir.mkdirs();
			}
		} else {
			UIHelper.ToastMessage(getGitApplication(), "无法保存上传的头像，请检查SD卡是否挂载");
			return null;
		}
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

		// 如果是标准Uri
		if (StringUtils.isEmpty(thePath)) {
			thePath = ImageUtils.getAbsoluteImagePath(MySelfInfoActivity.this, uri);
		}
		String ext = FileUtils.getFileFormat(thePath);
		ext = StringUtils.isEmpty(ext) ? "jpg" : ext;
		// 照片命名
		String cropFileName = "osc_crop_" + timeStamp + "." + ext;
		// 裁剪头像的绝对路径
		protraitPath = FILE_SAVEPATH + cropFileName;
		protraitFile = new File(protraitPath);

		cropUri = Uri.fromFile(protraitFile);
		return this.cropUri;
	}*/
	
	/**
	 * 拍照后裁剪
	 * 
	 * @param data
	 *            原始图片
	 * @param output
	 *            裁剪后图片
	 */
	private void startActionCrop(Uri data) {/*
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		intent.putExtra("output", this.getUploadTempFile(data));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// 输出图片大小
		intent.putExtra("outputY", CROP);
		intent.putExtra("scale", true);// 去黑边
		intent.putExtra("scaleUpIfNeeded", true);// 去黑边
		startActivityForResult(intent,
				ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
	*/}
	
	/**
	 * 上传新照片
	 */
	private void uploadNewPhoto() {/*
		
		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage("正在上传头像...");
		
		new AsyncTask<Void, Void, Message>() {
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.show();
			}

			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				dialog.dismiss();
				if (msg.what == 1) {
					UpLoadFile file = (UpLoadFile) msg.obj;
					if (file == null) {
						Log.i("Test", "返回的结果为空");
					}
					//UIHelper.ToastMessage(mAppContext, file.getMsg());
					mUserFace.setImageBitmap(protraitBitmap);
				} else {
					UIHelper.ToastMessage(getGitApplication(), "上次头像失败");
				}
			}

			@Override
			protected Message doInBackground(Void... params) {
				Message msg = new Message();
				// 获取头像缩略图
				if (!StringUtils.isEmpty(protraitPath) && protraitFile.exists()) {
					protraitBitmap = ImageUtils.loadImgThumbnail(protraitPath,
							200, 200);
				} else {
					UIHelper.ToastMessage(getActivity(), "图像不存在");
				}
				if (protraitBitmap != null && protraitFile != null) {
					try {
						UpLoadFile file = mAppContext.upLoad(protraitFile);
						msg.what = 1;
						msg.obj = file;
					} catch (AppException e) {
						dialog.dismiss();
						msg.what = -1;
						msg.obj = e;
					}
				}
				return msg;
			}
			
		}.execute();
	*/}
	
	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {/*
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
			startActionCrop(origUri);// 拍照后裁剪
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
			startActionCrop(data.getData());// 选图后裁剪
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
			uploadNewPhoto();// 上传新照片
			break;
		}
	*/}
}
