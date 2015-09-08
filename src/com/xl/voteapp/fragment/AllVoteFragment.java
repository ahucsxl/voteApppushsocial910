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

package com.xl.voteapp.fragment;

import java.util.ArrayList;
import java.util.List;

import com.xl.voteapp.R;
import com.xl.voteapp.adapter.AllVoteAdapter;
import com.xl.voteapp.base.BaseListFragment;
import com.xl.voteapp.base.ListBaseAdapter;
import com.xl.voteapp.bean.Vote;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Simple Fragment used to display some meaningful content for each page in the
 * sample's {@link android.support.v4.view.ViewPager}.
 */
public class AllVoteFragment extends BaseListFragment<Vote> {

	private static final String KEY_TITLE = "title";
	private static final String KEY_INDICATOR_COLOR = "indicator_color";
	private static final String KEY_DIVIDER_COLOR = "divider_color";
	private static ArrayList<Vote> voteList;

	/**
	 * @return a new instance of {@link AllVoteFragment}, adding the parameters
	 *         into a bundle and setting them as arguments.
	 */
	public static AllVoteFragment newInstance(CharSequence title,
			int indicatorColor, int dividerColor) {
		voteList = new ArrayList<Vote>();
		Bundle bundle = new Bundle();
		bundle.putCharSequence(KEY_TITLE, title);
		bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
		bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);
		AllVoteFragment fragment = new AllVoteFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	/*
	 * @Override public void onScroll(AbsListView view, int firstVisibleItem,
	 * int visibleItemCount, int totalItemCount) { // TODO Auto-generated method
	 * stub
	 * 
	 * }
	 */

	/*
	 * @Override public View onCreateView(LayoutInflater inflater, ViewGroup
	 * container, Bundle savedInstanceState) { return
	 * inflater.inflate(R.layout.pager_item, container, false); }
	 * 
	 * @Override public void onViewCreated(View view, Bundle savedInstanceState)
	 * { super.onViewCreated(view, savedInstanceState);
	 * 
	 * Bundle args = getArguments();
	 * 
	 * if (args != null) { TextView title = (TextView)
	 * view.findViewById(R.id.item_title); title.setText("Title: " +
	 * args.getCharSequence(KEY_TITLE));
	 * 
	 * int indicatorColor = args.getInt(KEY_INDICATOR_COLOR); TextView
	 * indicatorColorView = (TextView)
	 * view.findViewById(R.id.item_indicator_color);
	 * indicatorColorView.setText("Indicator: #" +
	 * Integer.toHexString(indicatorColor));
	 * indicatorColorView.setTextColor(indicatorColor);
	 * 
	 * int dividerColor = args.getInt(KEY_DIVIDER_COLOR); TextView
	 * dividerColorView = (TextView) view.findViewById(R.id.item_divider_color);
	 * dividerColorView.setText("Divider: #" +
	 * Integer.toHexString(dividerColor));
	 * dividerColorView.setTextColor(dividerColor); } }
	 */
	@Override
	protected AllVoteAdapter getListAdapter() {
		return new AllVoteAdapter(getActivity(), R.layout.list_cell_news2,
				voteList);
	}

/*	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.i("tagb", String.valueOf(voteList.size()));
		
		 * News news = mAdapter.getItem(position); if (news != null) {
		 * UIHelper.showNewsRedirect(view.getContext(), news);
		 * 
		 * // 放入已读列表 saveToReadedList(view, NewsList.PREF_READED_NEWS_LIST,
		 * news.getId() + ""); }
		 
		
		Intent intent = new Intent(getActivity(), VotingActivity.class);
		String title = voteList.get(position - 1).getTitle();
		String content=voteList.get(position - 1).getContent();
		intent.putExtra("title", title);
		intent.putExtra("content", content);
		intent.putExtra("uno", "z");
		startActivity(intent);

	}*/

/*	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}*/

	/*
	 * public void getVoteList(){ voteList = new ArrayList<Vote>(); new
	 * Thread(){ public void run(){ try{
	 * voteList=DownloadReceiveInformation.save(); Log.i("downloadr",
	 * String.valueOf(voteList.size())); }catch(Exception e){
	 * e.printStackTrace(); } } }.start(); }
	 */

}
