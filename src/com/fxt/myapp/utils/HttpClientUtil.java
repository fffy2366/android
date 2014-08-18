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
		// �Ƚ���������List���ٶԲ�������URL����
//		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
//		params.add(new BasicNameValuePair("param1", "�й�"));
//		params.add(new BasicNameValuePair("param2", "value2"));

		// �Բ�������
		String param = URLEncodedUtils.format(params, "UTF-8");

		// ��URL�����ƴ��
		HttpGet getMethod = new HttpGet(baseUrl + "?" + param);

		HttpClient httpClient = new DefaultHttpClient();

		try {
			HttpResponse response = httpClient.execute(getMethod); // ����GET����

			Log.i(TAG, "resCode = " + response.getStatusLine().getStatusCode()); // ��ȡ��Ӧ��
			String result = EntityUtils.toString(response.getEntity(),"utf-8") ; //entity�е�����ֻ�ܶ�ȡһ��
			Log.i(TAG,
					"result = "
							+ result); // ��ȡ��Ӧ����
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
		// ��GET��ʽһ�����Ƚ���������List
		/*
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
		params = new LinkedList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("param1", "Post����"));
		params.add(new BasicNameValuePair("param2", "�ڶ�������"));
		*/
		try {
			HttpPost postMethod = new HttpPost(baseUrl);
			postMethod.setEntity(new UrlEncodedFormEntity(params, "utf-8")); // ����������POST
																				// Entity��

			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(postMethod); // ִ��POST����
			Log.i(TAG, "resCode = " + response.getStatusLine().getStatusCode()); // ��ȡ��Ӧ��
			String result = EntityUtils.toString(response.getEntity(),"utf-8") ; //entity�е�����ֻ�ܶ�ȡһ��
			Log.i(TAG,
					"result = "
							+ result); // ��ȡ��Ӧ����
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
