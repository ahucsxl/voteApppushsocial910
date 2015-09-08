package com.xl.voteapp.ui;

import java.util.ArrayList;
import java.util.List;

import com.xl.voteapp.R;
import com.xl.voteapp.base.AppContext;
import com.xl.voteapp.bean.Item;
import com.xl.voteapp.draw.DrawActivity;
import com.xl.voteapp.ui.empty.EmptyLayout;
import com.xl.voteapp.upload.UploadVotingInformation;
import com.xl.voteapp.util.TDevice;
import com.xl.voteapp.util.UIHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class VotingActivity extends Activity {
	private ProgressDialog mLoginProgressDialog;
	private RadioGroup radioGroup = null;
	TextView etModifyTitle = null;
	TextView etModifyContent = null;
	private Button vote_result;
	//String uno = null;
	String visitor = null; // 访问者ID
	String rid = null; // 日记的ID
	String cid = null;
	String a = null;
	String title;
	String content;
	ArrayList<Item> optionlist;
	List<String[]> ticketnum;
	private LinearLayout mLayout;
	private Button addvote;
	private String v_id;
    private String u_no;
    protected EmptyLayout mErrorLayout;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.voting);
        mErrorLayout = (EmptyLayout) findViewById(R.id.error_layout_voting);
        if (!TDevice.hasInternet()) {
			mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
			return;
		}
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
		Intent intent = getIntent();
		content = intent.getStringExtra("content");
		title = intent.getStringExtra("v_title");
		v_id=intent.getStringExtra("v_id");		
		u_no=intent.getStringExtra("u_no");	
		optionlist =  (ArrayList<Item>) getIntent().getSerializableExtra("optionlist");  		
		etModifyTitle = (TextView) findViewById(R.id.zhuti); // 获得标题EditText
		etModifyTitle.setText(title);
		etModifyContent = (TextView) findViewById(R.id.xiangxi); // 获得内容EditText
		etModifyContent.setText(content);
		radioGroup = (RadioGroup) findViewById(R.id.select_radiogroup);
		mLayout = (LinearLayout) findViewById(R.id.optionlist);
		
		for (int i = 0; i < optionlist.size(); i++) {
			RadioButton  newRadio =new RadioButton(getApplicationContext());  
            newRadio.setText(optionlist.get(i).getI_content()); 
            newRadio.setId(i);
            newRadio.setTextAppearance(getApplicationContext(), R.style.option_item_text_parent);
            newRadio.setTextSize(16);
            newRadio.setBackgroundResource(R.drawable.list_item_background);
            newRadio.setTextColor(Color.BLACK);
            RadioGroup.LayoutParams lp=new  RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);   
            lp.setMargins(0, 10, 0, 0);;
            newRadio.setLayoutParams(lp);
            radioGroup.addView(newRadio, radioGroup.getChildCount());
		}
		
		addvote = (Button) findViewById(R.id.addvote);
		addvote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < optionlist.size(); i++)
					if (((RadioButton) findViewById(i)).isChecked()) {
						cid = String.valueOf(optionlist.get(i).getI_id());
					}
			if(!AppContext.login){
					Toast.makeText(VotingActivity.this, "请先登录再进行投票", Toast.LENGTH_SHORT).show();					
				}
			 else if(cid==null)
            	 Toast.makeText(VotingActivity.this, "请先选择选项再进行投票", Toast.LENGTH_SHORT).show();			
             else addVote();
			}
		});
		 
		vote_result = (Button) findViewById(R.id.vote_result);
		vote_result.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UIHelper.showDrawActivity(VotingActivity.this,title, content, optionlist);				
			}
		});

	}

	public void addVote() {
		if(mLoginProgressDialog == null) {
    		mLoginProgressDialog = new ProgressDialog(VotingActivity.this);
    		mLoginProgressDialog.setCancelable(true);
    		mLoginProgressDialog.setCanceledOnTouchOutside(false);
    		mLoginProgressDialog.setMessage("正在加载投票信息...");
    	}
    	if(mLoginProgressDialog != null) {
			mLoginProgressDialog.show();
		}
		new Thread() {
			public void run() {
				Looper.prepare();
				try {
					int result = 2;
					result = UploadVotingInformation.save(cid,v_id,u_no);
					if (result==1) {
						mLoginProgressDialog.dismiss();	
						Toast.makeText(VotingActivity.this, "投票成功！",							
								Toast.LENGTH_SHORT).show();
						
						for (int i = 0; i < optionlist.size(); i++)
							if (((RadioButton) findViewById(i)).isChecked()) {
								optionlist.get(i).setI_num(optionlist.get(i).getI_num()+1);
							}						
						Looper.loop();
					} else if(result==2){
						mLoginProgressDialog.dismiss();	
						Toast.makeText(VotingActivity.this, "投票失败，请重试！",
								Toast.LENGTH_LONG).show();
						Looper.loop();
					} else if(result==3){
						mLoginProgressDialog.dismiss();	
						Toast.makeText(VotingActivity.this, "您已经投过票了！不能重复投票",
								Toast.LENGTH_LONG).show();
						Looper.loop();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Looper.myLooper().quit();
			}
		}.start();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
