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

import android.widget.Toast;

/*import com.wangjialin.internet.userInformation.post.R;*/

public class UploadVotingInformation {
	public static int save(String i_id, String v_id, String u_no) throws Exception{
		String path = Contanst.URLPATH+"AddVote";
		Map<String, String> params = new HashMap<String, String>();
		AppContext.getInstance().addSessionCookie(params);
		params.put("I_id", i_id);
		params.put("V_id", v_id);
		params.put("U_no", u_no);
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
			if(DC.equals(ErrorCode.VOTING_SUCCESS))		
			return 1;
			else if(DC.equals(ErrorCode.VOTING_FAIL))
			return 2;
			else if(DC.equals(ErrorCode.VOTING_FAIL_DUPLICATE))
			return 3;
		}
		 return 2;
	}
}
