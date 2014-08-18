package com.fxt.myapp;

import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fxt.myapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class RegActivity extends Activity {
	private String userName;
	private String password;

	private EditText view_userName;
	private EditText view_password;
	private CheckBox view_rememberMe;
	private Button view_loginSubmit;
	private Button view_regSubmit;

	private static final int MENU_EXIT = Menu.FIRST - 1;
	private static final int MENU_ABOUT = Menu.FIRST;

	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";

	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";

	private boolean isNetError;

	private ProgressDialog proDialog;
	private long exitTime = 0;
	
	private static final String TAG = "login" ;

	Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			isNetError = msg.getData().getBoolean("isNetError");
			if (proDialog != null) {
				proDialog.dismiss();
			}
			if (isNetError) {
				Toast.makeText(RegActivity.this, "当前网络不可用",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RegActivity.this, "错误的用户名或密码",
						Toast.LENGTH_SHORT).show();

				clearSharePassword();
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);
		findViewsById();
		Log.d(this.toString(), "飞起来吧");
		setListener();
	}

	private void findViewsById() {
		view_userName = (EditText) findViewById(R.id.UserName);
		view_password = (EditText) findViewById(R.id.PassWord);
		view_rememberMe = (CheckBox) findViewById(R.id.Remember_PassWord);
		view_loginSubmit = (Button) findViewById(R.id.Login);
		view_regSubmit = (Button) findViewById(R.id.RegButton);
	}


	/**
	 * 验证登陆
	 */
	private boolean validateLocalLogin(String userName, String password,
			String validateUrl) {

		boolean loginState = false;
		HttpURLConnection conn = null;
		Log.v("login", validateUrl) ;
		try {
			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("username", userName));
			params.add(new BasicNameValuePair("password", password));

			// 对参数编码
			String param = URLEncodedUtils.format(params, "UTF-8");

			// 将URL与参数拼接
			HttpGet getMethod = new HttpGet(validateUrl + "?" + param);

			HttpClient httpClient = new DefaultHttpClient();

			HttpResponse response = httpClient.execute(getMethod); // 发起GET请求

			String loginStateInt = EntityUtils.toString(response.getEntity(),"utf-8") ;
			
			Log.v("loginState", String.valueOf(loginStateInt));
			if (loginStateInt.equals("1")) {
				loginState = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			isNetError = true;
			Log.d(this.toString(), e.getMessage() + "  127 line");
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		if (loginState) {
			if (isRememberMe()) {
				saveSharePreferences(true, true);
			} else {
				saveSharePreferences(true, false);
			}
		} else {
			if (!isNetError) {
				clearSharePassword();
			}
		}
		if (!view_rememberMe.isChecked()) {
			clearSharePassword();
		}

		Log.v("loginState", String.valueOf(loginState));
		return loginState;
	}

	/**
	 * 保存用户名密码
	 */
	private void saveSharePreferences(boolean saveUserName, boolean savePassword) {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		if (saveUserName) {
			Log.d(this.toString(), "saveUserName="
					+ view_userName.getText().toString());
			share.edit()
					.putString(SHARE_LOGIN_USERNAME,
							view_userName.getText().toString()).commit();
		}
		if (savePassword) {
			share.edit()
					.putString(SHARE_LOGIN_PASSWORD,
							view_password.getText().toString()).commit();
		}
		share = null;
	}

	/**
	 * 是否记住密码
	 * 
	 * @return
	 */
	private boolean isRememberMe() {
		if (view_rememberMe.isChecked()) {
			return true;
		}
		return false;
	}

	/**
	 * 监听提交
	 */
	private OnClickListener submitListener = new OnClickListener() {

		public void onClick(View v) {
			proDialog = ProgressDialog.show(RegActivity.this, "请稍候", "",
					true, true);

			Thread loginThread = new Thread(new LoginFailureHandler());
			loginThread.start();
		}
	};
	private OnClickListener loginListener = new OnClickListener() {

		public void onClick(View v) {
			loginActivity() ;
		}
	};
	private void loginActivity(){
		Intent intent = new Intent();
		intent.setClass(RegActivity.this, LoginActivity.class);
		startActivity(intent);
		this.finish() ;
	}
	// .start();
	// }
	// };

	private OnCheckedChangeListener rememberMeListener = new OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (view_rememberMe.isChecked()) {
				Toast.makeText(RegActivity.this, "ischecked",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void setListener() {
		//view_regSubmit.setOnClickListener(submitListener);
		view_loginSubmit.setOnClickListener(loginListener);

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_EXIT, 0, getResources().getText(R.string.MENU_EXIT));
		menu.add(0, MENU_ABOUT, 0, getResources().getText(R.string.MENU_ABOUT));
		return true;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) {
		case MENU_EXIT:
			finish();
			break;
		case MENU_ABOUT:
			alertAbout();
			break;
		}
		return true;
	}

	private void alertAbout() {
		new AlertDialog.Builder(RegActivity.this)
				.setTitle(R.string.MENU_ABOUT)
				.setMessage(R.string.aboutInfo)
				.setPositiveButton(R.string.ok_label,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
							}
						}).show();
	}

	private void clearSharePassword() {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		share.edit().putString(SHARE_LOGIN_PASSWORD, "").commit();
		share = null;
	}

	/**
	 * 登录
	 * 
	 * @author Frank
	 * 
	 */
	class LoginFailureHandler implements Runnable {

		public void run() {
			userName = view_userName.getText().toString();
			password = view_password.getText().toString();
			
			String validateURL = "http://androidtest.sinaapp.com/login.php";
			boolean loginState = validateLocalLogin(userName, password,
					validateURL);
			Log.d(this.toString(), "validateLogin");

			if (loginState) {
				Intent intent = new Intent();
				intent.setClass(RegActivity.this, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("MAP_USERNAME", userName);
				intent.putExtras(bundle);

				startActivity(intent);
				proDialog.dismiss();
			} else {

				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putBoolean("isNetError", isNetError);
				message.setData(bundle);
				loginHandler.sendMessage(message);
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 监测网络类型
	 * @param context
	 * @return
	 */
	public String NetType(Context context) {  
        try {  
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
            NetworkInfo info = cm.getActiveNetworkInfo();  
            String typeName = info.getTypeName().toLowerCase(); // WIFI/MOBILE  
            if (typeName.equalsIgnoreCase("wifi")) {  
            } else {  
                typeName = info.getExtraInfo().toLowerCase();  
                // 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap  
            }  
            return typeName;  
        } catch (Exception e) {  
            return null;  
        }  
    }  	
	public static void log(String msg){
		Log.v(TAG, msg) ;
	}
}