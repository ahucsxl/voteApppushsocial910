package com.xl.voteapp.upload;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.xl.voteapp.base.AppContext;
import com.xl.voteapp.common.Contanst;
import com.xl.voteapp.util.ErrorCode;

import android.R.integer;
import android.widget.Toast;

public class UploadRegisterUserInformation {
	public static int save(String name, String pwd,String email,String tel, String thirduid,String portrait) throws Exception{
	//	String path = "http://192.168.191.1:8080/votefinalserver150310/Register";
		String path = Contanst.URLPATH+"Register";
		Map<String, String> params = new HashMap<String, String>();
		AppContext.getInstance().addSessionCookie(params);
		params.put("UserName", name);
		params.put("Password", pwd);
		params.put("Email", email);
		params.put("Tel", tel);
		params.put("Uthirduid", thirduid);
		params.put("Portrait", portrait);
		return sendPOSTRequest(path, params, "UTF-8");
	}

	/**
	 * 发送POST请求
	 * @param path 请求路径
	 * @param params 请求参数
	 * @return
	 */
	private static int sendPOSTRequest(String path, Map<String, String> params, String encoding) throws Exception{
		//  title=liming&length=30
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
		conn.setRequestProperty("Cookie", params.get("Cookie"));
		OutputStream outStream = conn.getOutputStream();
		outStream.write(data);
		outStream.flush();
		if(conn.getResponseCode() == 200){
			DataInputStream inputStream = new DataInputStream(conn.getInputStream());
		    String DC=inputStream.readUTF();
			if(DC.equals(ErrorCode.REGISTER_FAIL))
			return 0;
			else
				return Integer.valueOf(DC);
		}
		
		
		
		 return 0;
	}
}
