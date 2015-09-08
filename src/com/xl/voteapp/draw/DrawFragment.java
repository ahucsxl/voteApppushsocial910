/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xl.voteapp.draw;

import com.xl.bplcharts.view.Myviewbar;
import com.xl.bplcharts.view.Myviewcircle;
import com.xl.bplcharts.view.Myviewline;
import com.xl.voteapp.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Simple Fragment used to display some meaningful content for each page in the
 * sample's {@link android.support.v4.view.ViewPager}.
 */
public class DrawFragment extends Fragment {

	private static final String KEY_TITLE = "title";
	private static final String KEY_INDICATOR_COLOR = "indicator_color";
	private static final String KEY_DIVIDER_COLOR = "divider_color";
	private static final String PView = "view";

	private Myviewcircle myviewcircle;
	private Myviewbar myviewbar;
	private Myviewline myviewline;
	private FrameLayout layout_chart;
	public boolean viewisempty = true;
	private ListView mListView;

	public static int B[], i, width, height;
	public static String[] commentname;
	public static String View;
	/**
	 * @param view
	 * @param voteNameList
	 * @param width2
	 * @param num
	 * @param voteNumList
	 * @return a new instance of {@link DrawFragment}, adding the parameters
	 *         into a bundle and setting them as arguments.
	 */
	public static DrawFragment newInstance(CharSequence title,
			int indicatorColor, int dividerColor, String view,
			int[] voteNumList, int num, int width2, String[] voteNameList) {
		B = voteNumList;
		i = num;
		width = width2;
		commentname = voteNameList;
		View=view;
		Bundle bundle = new Bundle();
		bundle.putCharSequence(KEY_TITLE, title);
		bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
		bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);
	//	bundle.putInt(PView, view);
		DrawFragment fragment = new DrawFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.drawfragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		layout_chart = (FrameLayout) view.findViewById(R.id.chart);
		Bundle args = getArguments();
		if(args.getCharSequence(KEY_TITLE).equals("柱状图")){
		myviewbar = new Myviewbar(getActivity(), B, commentname, width, i);
		layout_chart.addView(myviewbar);
		}
		else if(args.getCharSequence(KEY_TITLE).equals("折线图")){
			myviewline = new Myviewline(getActivity(), B, commentname, width, i);
			layout_chart.addView(myviewline);
		}
		else if(args.getCharSequence(KEY_TITLE).equals("饼图")){
			myviewcircle = new Myviewcircle(getActivity(), B, commentname, width, i);
			layout_chart.addView(myviewcircle);
		}
			
	}
}
