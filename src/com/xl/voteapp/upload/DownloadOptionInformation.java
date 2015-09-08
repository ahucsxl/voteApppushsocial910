package com.xl.voteapp.upload;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xl.voteapp.bean.Item;
import com.xl.voteapp.bean.Vote;
import com.xl.voteapp.common.Contanst;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.util.Log;
import android.widget.Toast;

/*import com.wangjialin.internet.userInformation.post.R;*/

public class DownloadOptionInformation {
	public static  ArrayList<Item> save(String v_title, int v_id) throws Exception{
	//	String path = "http://192.168.191.1:8080/votefinalserver150310/OptionServlet";
		String path = Contanst.URLPATH+"OptionServlet";
		Map<String, String> params = new HashMap<String, String>();			
		params.put("V_id",String.valueOf(v_id));
		params.put("V_title",String.valueOf(v_title));
		return sendPOSTRequest(path, params, "UTF-8");
	}

	/**
	 * 发送POST请求
	 * @param path 请求路径
	 * @param params 请求参数
	 * @return
	 */
	private static ArrayList<Item> sendPOSTRequest(String path, Map<String, String> params, String encoding) throws Exception{
		
		StringBuilder sb = new StringBuilder();
		if(params!=null && !params.isEmpty()){
			for(Map.Entry<String, String> entry : params.entrySet()){
				sb.append(entry.getKey()).append("=");
				sb.append(URLEncoder.encode(entry.getValue(), encoding));
				sb.append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		byte[] data = sb.toString().getBytes();		
		HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);//允许对外传输数据
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", data.length+"");
		OutputStream outStream = conn.getOutputStream();
		outStream.write(data);
		outStream.flush();
		if(conn.getResponseCode() == 200){		
			ArrayList<Item> optionList = new ArrayList<Item>();
			DataInputStream inputStream = new DataInputStream(conn.getInputStream());
			String ins = inputStream.readUTF();		
			Log.d("TAG", ins);
			optionList = getOptionList(ins);
		    return optionList;
		}		
		 return null;
	}
	
	public static ArrayList<Item> getOptionList(String jsonString) {
		ArrayList<Item> olist = new ArrayList<Item>();
		Gson gson = new Gson();
		olist = gson.fromJson(jsonString, new TypeToken<List<Item>>() {
		}.getType()  );
		return olist;
	}
}
