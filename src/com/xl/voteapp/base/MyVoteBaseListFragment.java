package com.xl.voteapp.base;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import butterknife.ButterKnife;
import butterknife.InjectView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.umeng.socialize.utils.Log;
import com.xl.voteapp.R;
import com.xl.voteapp.adapter.AllVoteAdapter;
import com.xl.voteapp.base.BaseListFragment.MsgClickReceiver;
import com.xl.voteapp.base.BaseListFragment.MsgReceiver;
import com.xl.voteapp.bean.Entity;
import com.xl.voteapp.bean.Item;
import com.xl.voteapp.bean.RecData;
import com.xl.voteapp.bean.User;
import com.xl.voteapp.bean.Vote;
import com.xl.voteapp.cache.CacheManager;
import com.xl.voteapp.common.BroadcastController;
import com.xl.voteapp.common.Contanst;
import com.xl.voteapp.ui.empty.EmptyLayout;
import com.xl.voteapp.upload.DownloadOptionInformation;
import com.xl.voteapp.util.DateUtil;
import com.xl.voteapp.util.GsonRequest;
import com.xl.voteapp.util.TDevice;
import com.xl.voteapp.util.UIHelper;

@SuppressLint("NewApi")
public abstract class MyVoteBaseListFragment<T extends Entity> extends BaseFragment
		implements SwipeRefreshLayout.OnRefreshListener, OnItemClickListener, OnScrollListener {

	String messagefull = "false";
	String loading = "false";
	boolean haveCacheData = false;
	public static final int MESSAGE_STATE_ERROR = -1;
	public static final int MESSAGE_STATE_EMPTY = 0;
	public static final int MESSAGE_STATE_MORE = 1;
	public static final int MESSAGE_STATE_FULL = 2;
	// 没有状态
	public static final int LISTVIEW_ACTION_NONE = -1;
	// 更新状态，不显示toast
	public static final int LISTVIEW_ACTION_UPDATE = 0;
	// 初始化时，加载缓存状态
	public static final int LISTVIEW_ACTION_INIT = 1;
	// 刷新状态，显示toast
	public static final int LISTVIEW_ACTION_REFRESH = 2;
	// 下拉到底部时，获取下一页的状态
	public static final int LISTVIEW_ACTION_SCROLL = 3;
	// 当前页面已加载的数据总和
	private int mSumData;
	// 当前加载状态
	private int mState = STATE_NONE;
	// UI状态
	private int mListViewAction = LISTVIEW_ACTION_NONE;

	// 当前数据状态，如果是已经全部加载，则不再执行滚动到底部就加载的情况
	private int mMessageState = MESSAGE_STATE_MORE;
	private boolean isPauseLife = true;

	static final int STATE_NONE = -1;
	static final int STATE_LOADING = 0;
	static final int STATE_LOADED = 1;

	@InjectView(R.id.error_layout)
	protected EmptyLayout mErrorLayout;

	public static final String BUNDLE_KEY_CATALOG = "BUNDLE_KEY_CATALOG";
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	protected ListView mListView;

	private View mFooterView;
	private View mFooterProgressBar;
	private TextView mFooterTextView;

	protected ArrayAdapter<T> mAdapter;
	List<Vote> voteList = new ArrayList<Vote>();;
	ArrayList<Item> optionList;
	private User user;
	private int uno;
	private final static String APP_CONFIG = "config";
	private static final int REFRESH_COMPLETE = 0X110;
	protected int mStoreEmptyState = -1;
	protected int mCurrentPage = 1;
	protected int mCatalog = 1;
	private MsgReceiver updateListViewReceiver;
	private MsgClickReceiver updateListViewClickReceiver;
	
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case REFRESH_COMPLETE:
				requestData(true);
				//getVoteList(1, LISTVIEW_ACTION_REFRESH);
				mAdapter.notifyDataSetChanged();
				mSwipeRefreshLayout.setRefreshing(false);
				break;
			case 0:
				loading = "false";
				RecData recdata = (RecData) msg.obj;
				List<Vote> result = recdata.getListvote();
				messagefull = recdata.getStateFull();
				if (messagefull.equals("true")) {
					setFooterFullState();
				}

				if (msg.arg2 == 1) {
					voteList = result;
					mAdapter = (ArrayAdapter<T>) new AllVoteAdapter(getActivity(), R.layout.list_cell_news2, voteList);
					mListView.setAdapter(mAdapter);
					mSumData = result.size();
					mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
				} else {
					mSumData += result.size();
					voteList.addAll(result);
				}
				mAdapter.notifyDataSetChanged();
				break;
			case 1:
				UIHelper.showVoteActivity(getActivity(), voteList.get(msg.arg1).getV_id(),
						voteList.get(msg.arg1).getV_title(), voteList.get(msg.arg1).getV_content(), uno, optionList);
				break;
			case 2:
				mErrorLayout.setErrorType(EmptyLayout.NODATA);
				break;

			}
		};
	};

	private BroadcastReceiver mUserChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 接收到变化后，更新用户资料
			
			setupUserView(true);
		}
	};

	private void setupUserView(final boolean reflash) {

		if (!AppContext.login) {
			if (!TDevice.hasInternet()) {
				mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
				return;
			}

			user = null;
			voteList = null;
			mErrorLayout.setErrorType(EmptyLayout.NODATA);
			return;
		}
		new AsyncTask<Void, Void, User>() {
			@Override
			protected User doInBackground(Void... params) {
				user = getLoginInfo();
				uno=user.getU_no();
				return user;
			}

			@Override
			protected void onPostExecute(User user) {
				//requestData(true);
				getVoteList(1, LISTVIEW_ACTION_INIT);
				/*
				 * if (user == null || isDetached()) { return; }
				 * mUser_info_username.setText(user.getU_name());
				 */
			}
		}.execute();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		BroadcastController.registerUserChangeReceiver(activity, mUserChangeReceiver);

	}

	@Override
	public void onDetach() {
		super.onDetach();
		BroadcastController.unregisterReceiver(getActivity(), mUserChangeReceiver);
		getActivity().unregisterReceiver(updateListViewReceiver);
		getActivity().unregisterReceiver(updateListViewClickReceiver);
	}

	/**
	 * 获取登录信息
	 * 
	 * @return
	 */
	public User getLoginInfo() {
		User user = new User();
		user.setU_no(Integer.valueOf(getProperty(Contanst.PROP_KEY_UNO)));
		user.setU_name(getProperty(Contanst.PROP_KEY_UNAME));
		user.setU_pwd(getProperty(Contanst.PROP_KEY_UPWD));
		user.setU_email(getProperty(Contanst.ROP_KEY_UEMAIL));
		user.setU_tel(getProperty(Contanst.ROP_KEY_UTEL));
		user.set_portrait(getProperty(Contanst.ROP_KEY_PORTRAIT));
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

			File dirConf = getActivity().getApplication().getDir(APP_CONFIG, Context.MODE_PRIVATE);
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

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_pull_refresh_listview;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mFooterView = inflater.inflate(R.layout.list_cell_footer, null);
		mFooterProgressBar = mFooterView.findViewById(R.id.progressbar);
		mFooterTextView = (TextView) mFooterView.findViewById(R.id.listview_foot_more);
		View view = inflater.inflate(getLayoutId(), container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.inject(this, view);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
		mListView = (ListView) view.findViewById(R.id.listview);
		mListView.setOnScrollListener(this);
		mListView.setOnItemClickListener(this);
		mListView.addFooterView(mFooterView);
		initView(view);
		setupUserView(AppContext.login);
	}

	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mCatalog = args.getInt(BUNDLE_KEY_CATALOG, 0);
			// 0.注册数据更新监听器
			updateListViewReceiver = new MsgReceiver();
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("com.qq.xgdemo.activity.UPDATE_LISTVIEW");
			getActivity().registerReceiver(updateListViewReceiver, intentFilter);
			// 1.注册数据点击监听器
			updateListViewClickReceiver = new MsgClickReceiver();
			IntentFilter intentFilterclick = new IntentFilter();
			intentFilterclick.addAction("com.qq.xgdemo.activity.UPDATE_LISTVIEW_CLICK");
			getActivity().registerReceiver(updateListViewClickReceiver, intentFilterclick);
		}
	}

	@Override
	public void initView(View view) {
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.swiperefresh_color1, R.color.swiperefresh_color2,
				R.color.swiperefresh_color3, R.color.swiperefresh_color4);
		mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TDevice.hasInternet()) {
					mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
					return;
				}
				mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
				getVoteList(1, LISTVIEW_ACTION_INIT);
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	protected abstract ArrayAdapter<T> getListAdapter();

	// 下拉刷新数据
	@Override
	public void onRefresh() {
		mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);

	}

	protected boolean requestDataIfViewCreated() {
		return true;
	}

	protected String getCacheKeyPrefix() {
		String type="MyVote";
		return type;
	}

	public void beforeLoading(int action) {
		// 开始加载
		mState = STATE_LOADING;
		if (action == LISTVIEW_ACTION_REFRESH) {
			// setSwipeRefreshLoadingState();
		} else if (action == LISTVIEW_ACTION_SCROLL) {
			setFooterLoadingState();
		}	
	}

	   private String getCacheKey() {
	        return new StringBuilder(getCacheKeyPrefix()).append("_")
	                .append(mCurrentPage).toString();
	    }
	   
	   
		 /***
	     * 判断是否需要读取缓存的数据
	     * 
	     * @author 火蚁 2015-2-10 下午2:41:02
	     * 
	     * @return boolean
	     * @param refresh
	     * @return
	     */
	    protected boolean isReadCacheData(boolean refresh) {
	        String key = getCacheKey();
	        if (!TDevice.hasInternet()) {
	            return true;
	        }
	        // 第一页若不是主动刷新，缓存存在，优先取缓存的
	        if (CacheManager.isExistDataCache(getActivity(), key) && !refresh
	                && mCurrentPage == 1) {
	            return true;
	        }
	        // 其他页数的，缓存存在以及还没有失效，优先取缓存的
	        if (CacheManager.isExistDataCache(getActivity(), key)
	                && !CacheManager.isCacheDataFailure(getActivity(), key)
	                && mCurrentPage != 1) {
	            return true;
	        }

	        return false;
	    }

	    private class SaveCacheTask extends AsyncTask<Void, Void, RecData> {
	        private final WeakReference<Context> mContext;
	        private final Serializable seri;
	        private final String key;

	        private SaveCacheTask(Context context, Serializable seri, String key) {
	            mContext = new WeakReference<Context>(context);
	            this.seri = seri;
	            this.key = key;
	        }

	        @Override
	        protected RecData doInBackground(Void... params) {
	            CacheManager.saveObject(mContext.get(), seri, key);
	            return null;
	        }
	    }
	    
	    private class CacheTask extends AsyncTask<String, Void, Void> {
	        private final WeakReference<Context> mContext;

	        private CacheTask(Context context) {
	            mContext = new WeakReference<Context>(context);
	        }

	        @Override
	        protected Void doInBackground(String... params) {
	        	
				return null;
	          /*  Serializable seri = CacheManager.readObject(mContext.get(),
	                    params[0]);
	            if (seri == null) {
	                return null;
	            } else {
	                return readList(seri);
	            }*/
	        }

	        @Override
	        protected void onPostExecute(Void list) {
	            /*super.onPostExecute(list);
	            if (list != null) {
	                executeOnLoadDataSuccess(list.getList());
	            } else {
	                executeOnLoadDataError(null);
	            }
	            executeOnLoadFinish();*/
	        }
	    }
	    
	public void getVoteList(final int page, final int action) {
		beforeLoading(action);
		boolean refresh = true;
		if (action == LISTVIEW_ACTION_INIT) {
			refresh = false;
		}

		if (user != null) {
			GsonRequest<RecData> gsonObjRequest;
			final String url = Contanst.URLPATH + "ReceiveVote";
			Map<String, String> appendHeader = new HashMap<String, String>();
			appendHeader.put("u_no", String.valueOf(user.getU_no()));
			appendHeader.put("pageNo", String.valueOf(page));
			AppContext.getInstance().addSessionCookie(appendHeader);
			gsonObjRequest = new GsonRequest<RecData>(Request.Method.POST,
					url, RecData.class, null, new Response.Listener<RecData>() {
						@Override
						public void onResponse(RecData response) {
							try {
								RecData recdata = response;
								if (recdata == null) {
									mHandler.sendEmptyMessage(2);
								} else {
									new SaveCacheTask(getActivity(), recdata, getCacheKey()).execute();
									Message m = new Message();
									m.obj = recdata;
									m.arg1 = action;
									m.arg2 = page;
									m.what = 0;
									mHandler.sendMessage(m);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							loading="fasle";
							RecData recdata = (RecData) CacheManager.readObject(getActivity(),getCacheKey());
							if (recdata == null) {
								if (!TDevice.hasInternet()) {
									setFooterNoInternet();
						        }
								//mHandler.sendEmptyMessage(2);
							} else {
								haveCacheData = true;
								Message m = new Message();
								m.obj = recdata;
								m.arg1 = action;
								m.arg2 = page;
								m.what = 0;
								mHandler.sendMessage(m);
							}
							/*try {
								String jsonString=new String(RequestQueueSingleton.getInstance(getActivity()).getRequestQueue().getCache().get(url).data,HttpHeaderParser.parseCharset(RequestQueueSingleton.getInstance(getActivity()).getRequestQueue().getCache().get(url).responseHeaders));
								Log.d("tagg", jsonString);
								Gson mGson = new Gson();
								RecData recdata=mGson.fromJson(jsonString, RecData.class);
								if (recdata == null) {
									mHandler.sendEmptyMessage(2);
								} else {
									Message m = new Message();
									m.obj = recdata;
									m.arg1 = action;
									m.arg2 = page;
									m.what = 0;
									mHandler.sendMessage(m);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							// Handle your error types accordingly.For Timeout &
							// No connection error, you can show 'retry' button.
							// For AuthFailure, you can re login with user
							// credentials.
							// For ClientError, 400 & 401, Errors happening on
							// client side when sending api request.
							// In this case you can check how client is forming
							// the api and debug accordingly.
							// For ServerError 5xx, you can do retry or handle
							// accordingly.
							if (error instanceof NetworkError) {Log.i("my", "NetworkError ");
							} else if (error instanceof ServerError) {Log.i("my", "ServerError ");
							} else if (error instanceof AuthFailureError) {Log.i("my", "AuthFailureError ");
							} else if (error instanceof ParseError) {Log.i("my", "ParseError ");
							} else if (error instanceof NoConnectionError) {Log.i("my", "NoConnectionError ");
							} else if (error instanceof TimeoutError) {
							}
						}
					}, appendHeader);
			RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(gsonObjRequest);
		} else
			mHandler.sendEmptyMessage(2);
	}

	public void getOptionList(final int v_id, final String v_title, final int position) {
		optionList = new ArrayList<Item>();
		new Thread() {
			public void run() {
				try {
					optionList = DownloadOptionInformation.save(v_title, v_id);
					Message m = new Message();
					m.arg1 = position;
					m.what = 1;
					mHandler.sendMessage(m);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (!TDevice.hasInternet()) {
			UIHelper.showVoteActivity(getActivity(), 0, null, null, 0, optionList);
			return;
		}
		if (position < voteList.size())
			getOptionList(voteList.get(position).getV_id(), voteList.get(position).getV_title(), position);
	}

	// 是否需要自动刷新
	protected boolean needAutoRefresh() {
		return true;
	}

	/***
	 * 获取列表数据
	 * 
	 * 
	 * @author 火蚁 2015-2-9 下午3:16:12
	 * 
	 * @return void
	 * @param refresh
	 */
	protected void requestData(boolean refresh) {
		 String key = getCacheKey();
		 if (isReadCacheData(refresh)) {
		 readCacheData(key);
		 } else {
		// 取新的数据
		sendRequestData();
		 }
	}
	private void readCacheData(String cacheKey) {
		RecData recdata = (RecData) CacheManager.readObject(getActivity(),getCacheKey());
		android.util.Log.i("loadingg", recdata.toString());
		if (recdata == null) {
			if (!TDevice.hasInternet()) {
				if (!haveCacheData)
					mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
	        }
			//mHandler.sendEmptyMessage(2);
		} else {
			android.util.Log.i("loadingg", "abc"+recdata.toString());
			Message m = new Message();
			m.obj = recdata;
			//m.arg1 = action;
			m.arg2 = 1;
			m.what = 0;
			mHandler.sendMessage(m);
		}
       // cancelReadCacheTask();
       // mCacheTask = new CacheTask(getActivity()).execute(cacheKey);
    }

	/***
	 * 自动刷新的时间
	 * 
	 * 默认：自动刷新的时间为半天时间
	 * 
	 * @author 火蚁 2015-2-9 下午5:55:11
	 * 
	 * @return long
	 * @return
	 */
	protected long getAutoRefreshTime() {
		return 12 * 60 * 60;
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
		 * if (onTimeRefresh()) { onRefresh(); }
		 */
	}

	protected void sendRequestData() {
		getVoteList(1, LISTVIEW_ACTION_INIT);
	}

	/**
	 * 是否需要隐藏listview，显示无数据状态
	 * 
	 * @author 火蚁 2015-1-27 下午6:18:59
	 * 
	 */
	protected boolean needShowEmptyNoData() {
		return true;
	}

	protected boolean compareTo(List<? extends Entity> data, Entity enity) {
		int s = data.size();
		if (enity != null) {
			for (int i = 0; i < s; i++) {
				if (enity.getId() == data.get(i).getId()) {
					return true;
				}
			}
		}
		return false;
	}

	protected void onRefreshNetworkSuccess() {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mAdapter == null || mAdapter.getCount() == 0) {
			return;
		}
		if (messagefull.equals("true")|| loading.equals("true"))
			return;
		// 数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件
		/*
		 * if (mMessageState == MESSAGE_STATE_FULL || mMessageState ==
		 * MESSAGE_STATE_EMPTY || mState == STATE_LOADING) { Log.i("loading",
		 * String.valueOf("3")); return; }
		 */
		// 判断是否滚动到底部

		boolean scrollEnd = false;
		try {
			if (view.getPositionForView(mFooterView) == view.getLastVisiblePosition()) {
				scrollEnd = true;
			}
		} catch (Exception e) {
			scrollEnd = false;
		}
		if (scrollEnd) {
			onLoadNextPage();
		}
	}

	/** 加载下一页 */
	protected void onLoadNextPage() {
		// 当前pageIndex

		int pageIndex = mSumData / 20 + 1;
		mCurrentPage=pageIndex;
		loadList(pageIndex, LISTVIEW_ACTION_SCROLL);
	}

	/**
	 * 加载数据
	 * 
	 * @param page
	 *            页码
	 * @param action
	 *            加载的触发事件
	 */
	void loadList(int page, int action) {
		mListViewAction = action;
		getVoteList(page, action);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	}

	// ** 设置顶部正在加载的状态 *//*
	void setSwipeRefreshLoadingState() {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(true);
			// 防止多次重复刷新
			mSwipeRefreshLayout.setEnabled(false);
		}
	}

	/** 设置顶部加载完毕的状态 */
	void setSwipeRefreshLoadedState() {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(false);
			mSwipeRefreshLayout.setEnabled(true);
		}

	}

	/** 设置底部有错误的状态 */
	void setFooterErrorState() {
		if (mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.load_error);
		}
	}

	/** 设置底部有更多数据的状态 */
	void setFooterHasMoreState() {
		if (mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.load_more);
		}
	}

	/** 设置底部已加载全部的状态 */
	void setFooterFullState() {
		if (mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.load_full);
		}
	}

	/** 设置底部无数据的状态 */
	void setFooterNoMoreState() {
		if (mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.load_empty);
		}
	}

	/** 设置底部加载中的状态 */
	void setFooterLoadingState() {
		if (mFooterView != null) {
			mFooterProgressBar.setVisibility(View.VISIBLE);
			mFooterTextView.setText(R.string.load_ing);
			loading = "true";
		}
	}
	
	/** 设置底部无网络连接的状态 */
	void setFooterNoInternet() {
		if (mFooterView != null) {
			mFooterProgressBar.setVisibility(View.GONE);
			mFooterTextView.setText(R.string.tip_network_error);

		}
	}
	
	public class MsgReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 0);

		}
	}

	public class MsgClickReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
	/*		final String vtitle = intent.getStringExtra("vtitle");
			Log.i("tagg", voteList.size()+"a");
			new Thread() {
				public void run() {
			for(int i=0;i<voteList.size();i++)
				if(voteList.get(i).getV_title().equals(vtitle))				
					{
					Log.i("tagg", voteList.get(i).getV_title());
					Message m = new Message();
				m.arg1 = i;
				m.what = 1;
				mHandler.sendMessage(m);}
				}
			}.start();*/
	/*			
			UIHelper.showVoteActivity(getActivity(), voteList.get(i).getV_id(),
					voteList.get(i).getV_title(), voteList.get(i).getV_content(), uno,optionList);*/
		}
	}

}
