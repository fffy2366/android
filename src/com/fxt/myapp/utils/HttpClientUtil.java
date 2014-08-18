package com.fxt.myapp.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * @author Frank
 * @date 2013-07-29
 * @email fengxuting@gmail.com
 *
 */
public class HttpClientUtil {
	private static String TAG = "httpclient";

	public static String get(String baseUrl,List<BasicNameValuePair> params) {
		// 先将参数放入List，再对参数进行URL编码
//		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
//		params.add(new BasicNameValuePair("param1", "中国"));
//		params.add(new BasicNameValuePair("param2", "value2"));

		// 对参数编码
		String param = URLEncodedUtils.format(params, "UTF-8");

		// 将URL与参数拼接
		HttpGet getMethod = new HttpGet(baseUrl + "?" + param);

		HttpClient httpClient = new DefaultHttpClient();

		try {
			HttpResponse response = httpClient.execute(getMethod); // 发起GET请求

			Log.i(TAG, "resCode = " + response.getStatusLine().getStatusCode()); // 获取响应码
			String result = EntityUtils.toString(response.getEntity(),"utf-8") ; //entity中的内容只能读取一次
			Log.i(TAG,
					"result = "
							+ result); // 获取响应内容
			return  result ;
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		
	}

	public static String post(String baseUrl, List<BasicNameValuePair> params) {
		// 和GET方式一样，先将参数放入List
		/*
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("param1", "Post方法"));
		params.add(new BasicNameValuePair("param2", "第二个参数"));
		*/
		try {
			HttpPost postMethod = new HttpPost(baseUrl);
			postMethod.setEntity(new UrlEncodedFormEntity(params, "utf-8")); // 将参数填入POST
																				// Entity中

			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(postMethod); // 执行POST方法
			Log.i(TAG, "resCode = " + response.getStatusLine().getStatusCode()); // 获取响应码
			String result = EntityUtils.toString(response.getEntity(),"utf-8") ; //entity中的内容只能读取一次
			Log.i(TAG,
					"result = "
							+ result); // 获取响应内容
			return  result ;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public static void main(String args[]){
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("username", args[0]));
		params.add(new BasicNameValuePair("password", args[1]));		
		String validateURL = "http://androidtest.sinaapp.com/login.php";
		String ret = HttpClientUtil.get(validateURL, params) ;
		System.out.println(ret);
	}
}
