package com.xl.voteapp.upload;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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

import com.xl.voteapp.bean.Item;
import com.xl.voteapp.bean.Vote;
import com.xl.voteapp.common.Contanst;
import com.xl.voteapp.util.ErrorCode;
import com.xl.voteapp.util.JsonTools;
import android.widget.Toast;

/*import com.wangjialin.internet.userInformation.post.R;*/

public class UploadPublishVoteInformation {
	public static boolean save(int uno, String vtitle,String vcontent, List<Item> optionlist,String u_name,String u_portrait) throws Exception{
	//	String path = "http://192.168.191.1:8080/votefinalserver150310/PublishVote";		
		String path = Contanst.URLPATH+"PublishVote";
        Vote v=new Vote(0, vtitle, vcontent, uno, 0, null, u_name,u_portrait,optionlist);	
        System.out.println(optionlist);
		return sendPOSTRequest(path, v, "UTF-8");
	}

	/**
	 * 发送POST请求
	 * @param path 请求路径
	 * @param params 请求参数
	 * @param v 
	 * @param optionlist 
	 * @return
	 */
	private static boolean sendPOSTRequest(String path, Vote v, String encoding) throws Exception{
		String str = JsonTools.createJsonString(v);			
		HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);//允许对外传输数据
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");	
		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
		outStream.writeUTF(str);
		outStream.flush();		
		if(conn.getResponseCode() == 200){
			DataInputStream inputStream = new DataInputStream(conn.getInputStream());
		    String DC=inputStream.readUTF();
			if(DC.equals(ErrorCode.PUBLISHVOTE_SUCCESS))		
			return true;
			if(DC.equals(ErrorCode.PUBLISHVOTE_FAIL))
			return false;
		}
		 return false;
	}
}
