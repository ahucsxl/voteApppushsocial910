package com.xl.voteapp.ui;

import java.util.ArrayList;
import java.util.List;

import com.xl.voteapp.R;
import com.xl.voteapp.bean.Item;
import com.xl.voteapp.upload.UploadPublishVoteInformation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class PublishVoteActivity extends Activity {

	private LinearLayout mLayout, mLayout2;
	private ScrollView mScrollView;
	private final Handler mHandler = new Handler();
	ProgressDialog pd = null;
	int uno;
	String opnum = null;
	int optionnum = 1;
    String u_name;
    String u_portrait;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		uno = Integer.valueOf(intent.getStringExtra("uno"));
		u_name = intent.getStringExtra("u_name");
		u_portrait = intent.getStringExtra("u_portrait");
		setContentView(R.layout.publish_vote);

		mScrollView = (ScrollView) this.findViewById(R.id.ScrollView01);
		mLayout = (LinearLayout) findViewById(R.id.LIL);
		mLayout2 = (LinearLayout) findViewById(R.id.LIL2);

		final EditText title = new EditText(getApplicationContext());
		final EditText content = new EditText(getApplicationContext());

		final EditText et1 = new EditText(getApplicationContext());
		final Button add = new Button(getApplicationContext());

		title.setId(0);
		content.setId(1);
	//	content.setText("测试内容");
		content.setLines(4);
		add.setId(3);
		et1.setId(4);
	//	et1.setText("测试选项");

		title.setWidth(500);
		content.setWidth(500);
		et1.setWidth(500);

		LinearLayout title1 = new LinearLayout(getApplicationContext());
		LinearLayout content1 = new LinearLayout(getApplicationContext());

		LinearLayout ll1 = new LinearLayout(getApplicationContext());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.topMargin = 10;

		title1.setLayoutParams(lp);
		content1.setLayoutParams(lp);
		ll1.setLayoutParams(lp);

		title1.setOrientation(LinearLayout.HORIZONTAL);
		title1.setGravity(Gravity.CENTER_HORIZONTAL);
		content1.setOrientation(LinearLayout.HORIZONTAL);
		content1.setGravity(Gravity.CENTER_HORIZONTAL);

		ll1.setOrientation(LinearLayout.HORIZONTAL);
		ll1.setGravity(Gravity.CENTER_HORIZONTAL);
		title.setHint("主题:                      ");
		title.setHintTextColor(Color.BLACK);
		title.setTextColor(Color.BLACK);
		content.setHint("详细描述:                      ");
		content.setHintTextColor(Color.BLACK);
		content.setTextColor(Color.rgb(0, 0, 0));
		et1.setHint("选项:1                      ");
		et1.setTextColor(Color.rgb(0, 0, 0));
		et1.setHintTextColor(Color.BLACK);
		add.setBackgroundResource(R.drawable.add_icon_selector);

		title.setTextSize(15);
		content.setTextSize(15);
		et1.setTextSize(15);

		title.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		content.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		et1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		title1.addView(title);
		content1.addView(content);

		ll1.addView(et1);
		ll1.addView(add);
		mLayout2.addView(title1);
		mLayout2.addView(content1);
		mLayout2.addView(ll1);
		Button btnDiary = (Button) findViewById(R.id.btnfabu); // 获得发布日志按钮
		btnDiary.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 pd = ProgressDialog.show(PublishVoteActivity.this, "请稍候",
				 "正在发起投票...",true,true);
				publishVote();
			}
		});

		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setVisibility(Button.INVISIBLE);
				LinearLayout ll = new LinearLayout(getApplicationContext());
				optionnum++;
				EditText et = new EditText(getApplicationContext());
				Button add1 = new Button(getApplicationContext());
				et.setId(optionnum + 3);
				add1.setBackgroundResource(R.drawable.add_icon_selector);
				add1.setOnClickListener(this);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.topMargin = 10;
				ll.setLayoutParams(lp);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				ll.setGravity(Gravity.CENTER_HORIZONTAL);
				et.setHint("选项:" + optionnum + "                    ");
				et.setHintTextColor(Color.BLACK);
				et.setWidth(500);
				et.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				et.setTextSize(15);
				et.setTextColor(Color.rgb(0, 0, 0));
				ll.addView(et);
				ll.addView(add1);
				mLayout2.addView(ll);
				mHandler.post(mScrollToBottom);

			}
		});

	}

	private Runnable mScrollToBottom = new Runnable() {
		@Override
		public void run() {
			// 线性布局的高度减去屏幕高度
			int off = mLayout.getMeasuredHeight() - mScrollView.getHeight();
			if (off > 0) { // 如果屏幕不能完全显示部件，则滚屏
				mScrollView.scrollTo(0, off);
			}
		}
	};

	// 方法：连接服务器，发表投票
	public void publishVote() {
		new Thread() {
			public void run() {
				Looper.prepare();
				EditText etTitle = (EditText) findViewById(0); 
				String title = etTitle.getEditableText().toString().trim(); 
				String content = ((EditText) findViewById(1)).getEditableText()
						.toString().trim();
				String et1 = ((EditText) findViewById(4)).getEditableText()
						.toString().trim();
				List<Item> optionlist=new ArrayList<Item>();
				Item o;
				for (int i = 0; i < optionnum; i++){					
					String option_ontent = ((EditText) findViewById(i + 4))
							.getEditableText().toString().trim();
					o=new Item();
					o.setI_content(option_ontent);
					o.setI_num(0);
					o.setU_no(uno);					
					o.setV_id(0);
					
					
					optionlist.add(o);
				}		
				boolean flag = true;
				if (title.equals("") || content.equals("") || et1.equals("") ||(!flag)) { // 如果标题或内容为空
					pd.dismiss();
					Toast.makeText(PublishVoteActivity.this, "请将内容填写完整",
							Toast.LENGTH_LONG).show();
					Looper.loop();
					return;
				}
				try {
					opnum = String.valueOf(optionnum);
					boolean result = false;
					result = UploadPublishVoteInformation.save(uno, title,
							content, optionlist,u_name,u_portrait);
					
					/*for(int i=0;i<200;i++)
						result = UploadPublishVoteInformation.save(uno, title+" "+i,
								content, optionlist,u_name);*/
					
					 pd.dismiss();
					if (result) {
						Toast.makeText(PublishVoteActivity.this, "发布成功！",
								Toast.LENGTH_LONG).show();
						finish();
						Looper.loop();
					} else {
						Toast.makeText(PublishVoteActivity.this, "发布失败！请重试！",
								Toast.LENGTH_LONG).show();
						Looper.loop();
						return;
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