package com.xl.voteapp.draw;

import java.util.ArrayList;
import java.util.List;

import com.xl.voteapp.R;
import com.xl.voteapp.bean.Item;
import com.xl.voteapp.ui.SlidingTabsColorsFragment;
import com.xl.voteapp.ui.SlidingTabsDrawFragment;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class DrawActivity extends ActionBarActivity {
	public static int optionnum[],i,width,height,num;	 
	public static String []optionname;
	int flag=0;
	public boolean viewisempty=true;
	private String content;
	private String title;
	private ArrayList<Item> optionlist;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.draw);
		Intent intent = getIntent();
		content = intent.getStringExtra("content");
		title = intent.getStringExtra("title");	
		optionlist =  (ArrayList<Item>) getIntent().getSerializableExtra("optionlist"); 
		num=optionlist.size();
		optionnum=new int[num];
		optionname=new String[num];
		for(int i=0;i<optionlist.size();i++)
		{
			optionnum[i]=optionlist.get(i).getI_num();
			optionname[i]=optionlist.get(i).getI_content();
		}
		DisplayMetrics dm = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(dm);	
		width=dm.widthPixels;
		
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsDrawFragment fragment = new SlidingTabsDrawFragment(width-200,optionnum,optionname,num);
            transaction.replace(R.id.chartArea, fragment);
            transaction.commit();
        }
	
	}

}
