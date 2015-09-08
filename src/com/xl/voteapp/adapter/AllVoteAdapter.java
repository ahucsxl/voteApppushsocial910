package com.xl.voteapp.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.xl.voteapp.R;
import com.xl.voteapp.base.AppContext;
import com.xl.voteapp.base.ListBaseAdapter;
import com.xl.voteapp.base.RequestQueueSingleton;
import com.xl.voteapp.bean.Vote;
import com.xl.voteapp.widget.CircleImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AllVoteAdapter extends ArrayAdapter<Vote> {
	private int resourceId;
	 private Context context;
	// List<Vote> votes;

	public AllVoteAdapter(Context context, int resource, List<Vote> votes) {
		super(context, resource, votes);
		resourceId = resource;
		this.context=context;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Vote vote = getItem(position);

		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		if (vote.getU_portrait() != null) {
			String faceUrl = vote.getU_portrait();
			
			ImageLoader imageLoader = RequestQueueSingleton.getInstance(context).getImageLoader();
			imageLoader.get(faceUrl,
					ImageLoader.getImageListener(vh.face, R.drawable.widget_dface_loading, R.drawable.mini_avatar));
		}
		else vh.face.setImageResource(R.drawable.mini_avatar);
		vh.face.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		vh.title.setText(vote.getV_title());
		vh.title.setTextColor(parent.getContext().getResources().getColor(R.color.main_black));
		vh.description.setText(vote.getV_content().trim());
		vh.tv_time.setText(vote.getPubtime());
		vh.source.setText(vote.getU_name());
		vh.comment_count.setText(vote.getV_tnum() + "");

		return convertView;
	}

	static class ViewHolder {
		TextView title;
		TextView description;
		TextView source;
		TextView comment_count;
		TextView tv_time;
		public CircleImageView face;// 用户头像

		public ViewHolder(View view) {
			tv_time = (TextView) view.findViewById(R.id.tv_time);
			title = (TextView) view.findViewById(R.id.tv_title);
			description = (TextView) view.findViewById(R.id.tv_description);
			source = (TextView) view.findViewById(R.id.tv_source);
			comment_count = (TextView) view.findViewById(R.id.tv_comment_count);
			face = (CircleImageView) view.findViewById(R.id.menu_userface);

		}
	}
}
