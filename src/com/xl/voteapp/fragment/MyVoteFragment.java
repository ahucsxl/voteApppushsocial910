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
import com.xl.voteapp.base.MyVoteBaseListFragment;
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
public class MyVoteFragment extends MyVoteBaseListFragment<Vote> {

	private static final String KEY_TITLE = "title";
	private static final String KEY_INDICATOR_COLOR = "indicator_color";
	private static final String KEY_DIVIDER_COLOR = "divider_color";
	private static ArrayList<Vote> voteList;

	/**
	 * @return a new instance of {@link MyVoteFragment}, adding the parameters
	 *         into a bundle and setting them as arguments.
	 */
	public static MyVoteFragment newInstance(CharSequence title,
			int indicatorColor, int dividerColor) {
		voteList = new ArrayList<Vote>();
		Bundle bundle = new Bundle();
		bundle.putCharSequence(KEY_TITLE, title);
		bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
		bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);
		MyVoteFragment fragment = new MyVoteFragment();
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	protected AllVoteAdapter getListAdapter() {
		return new AllVoteAdapter(getActivity(), R.layout.list_cell_news2,
				voteList);
	}

/*	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}*/


}
