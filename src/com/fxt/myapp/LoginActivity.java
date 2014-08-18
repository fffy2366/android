package com.fxt.myapp;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.fxt.myapp.utils.DatabaseUtil;
import com.fxt.myapp.utils.HttpClientUtil;

public class LoginActivity extends Activity {
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
	
	private Context ctx ;

	Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			isNetError = msg.getData().getBoolean("isNetError");
			if (proDialog != null) {
				proDialog.dismiss();
			}
			if (isNetError) {
				Toast.makeText(LoginActivity.this, "当前网络不可用",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(LoginActivity.this, "错误的用户名或密码",
						Toast.LENGTH_SHORT).show();
				clearSharePassword();
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		findViewsById();
		initView(false);
//		System.out.println("飞一会");

		Log.d(this.toString(), "飞起来吧");
		//创建数据库
		DatabaseUtil dbu = new DatabaseUtil(getBaseContext()) ;
		dbu.open() ;
		dbu.createStudent("name", "grade") ;
		dbu.createStudent("frank", "20") ;
		dbu.close() ;
		setListener();
	}

	private void findViewsById() {
		view_userName = (EditText) findViewById(R.id.UserName);
		view_password = (EditText) findViewById(R.id.PassWord);
		view_rememberMe = (CheckBox) findViewById(R.id.Remember_PassWord);
		view_loginSubmit = (Button) findViewById(R.id.Login);
		view_regSubmit = (Button) findViewById(R.id.Login2);
	}
	
	private void initView(boolean isRememberMe) {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		String userName = share.getString(SHARE_LOGIN_USERNAME, "");
		String password = share.getString(SHARE_LOGIN_PASSWORD, "");
		Log.d(this.toString(), "userName=" + userName + " password=" + password);
		if (!"".equals(userName)) {
			view_userName.setText(userName);
		}
		if (!"".equals(password)) {
			view_password.setText(password);
			view_rememberMe.setChecked(true);
		}

		if (view_password.getText().toString().length() > 0) {
			// view_loginSubmit.requestFocus();
			// view_password.requestFocus();
		}
		share = null;
	}

	/**
	 * 验证登陆
	 */
	private boolean validateLocalLogin(String userName, String password,
			String validateUrl) {

		boolean loginState = false;
		Log.v("login", validateUrl) ;
		try {
			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("username", userName));
			params.add(new BasicNameValuePair("password", password));			
			String loginStateInt = HttpClientUtil.get(validateUrl, params) ;
			Log.v("loginState", String.valueOf(loginStateInt));
			if (loginStateInt.equals("1")) {
				loginState = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			isNetError = true;
			Log.d(this.toString(), e.getMessage() + "  127 line");
		} finally {
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
			proDialog = ProgressDialog.show(LoginActivity.this, "请稍候", "",
					true, true);

			Thread loginThread = new Thread(new LoginFailureHandler());
			loginThread.start();
		}
	};

	/**
	 * 监听注册按钮
	 */
	private OnClickListener regListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			regActivity();
		}
	};
	/**
	 * 跳转注册页面
	 */
	private void regActivity(){
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, RegActivity.class);
		startActivity(intent);		
		this.finish() ;
	}
	private OnCheckedChangeListener rememberMeListener = new OnCheckedChangeListener() {

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (view_rememberMe.isChecked()) {
				Toast.makeText(LoginActivity.this, "ischecked",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void setListener() {
		view_loginSubmit.setOnClickListener(submitListener);
		view_regSubmit.setOnClickListener(regListener);

		view_rememberMe.setOnCheckedChangeListener(rememberMeListener);		
	}
	/**
	 * 菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_EXIT, 0, getResources().getText(R.string.MENU_EXIT));
		menu.add(0, MENU_ABOUT, 0, getResources().getText(R.string.MENU_ABOUT));
		return true;
	}
	@Override
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
		new AlertDialog.Builder(LoginActivity.this)
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
				intent.setClass(LoginActivity.this, MainActivity.class);
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
	
	/**
	 * 再按一次退出
	 */
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
 	 * debug
 	 * @param msg
 	 */
	public static void log(String msg){
		Log.v(TAG, msg) ;
	}
}