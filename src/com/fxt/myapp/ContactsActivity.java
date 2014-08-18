package com.fxt.myapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ContactsActivity extends Activity {
	LinearLayout mLinLayout;
	ListView mLstViw;
	ArrayList<Map<String, String>> listData;

	static final String NAME = "name";
	static final String NUMBER = "number";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLinLayout = new LinearLayout(this);
		mLinLayout.setOrientation(LinearLayout.VERTICAL);
		mLinLayout.setBackgroundColor(Color.BLACK);

		mLstViw = new ListView(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		mLstViw.setBackgroundColor(Color.BLACK);

		// add the list view to layout
		mLinLayout.addView(mLstViw, params);
		setContentView(mLinLayout);

		listData = new ArrayList<Map<String, String>>();

		// read contacts
		Cursor cur = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		startManagingCursor(cur);

		while (cur.moveToNext()) {
			Map<String, String> mp = new HashMap<String, String>();

			long id = cur.getLong(cur.getColumnIndex("_id"));
			Cursor pcur = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ Long.toString(id), null, null);

			// 处理多个号码的情况
			String phoneNumbers = "";
			while (pcur.moveToNext()) {
				String strPhoneNumber = pcur
						.getString(pcur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				phoneNumbers += strPhoneNumber + ":";
			}
			phoneNumbers += "\n";
			pcur.close();

			String name = cur.getString(cur.getColumnIndex("display_name"));
			mp.put(NAME, name);
			mp.put(NUMBER, phoneNumbers);
			listData.add(mp);
		}
		cur.close();

		// 建立一个适配器去查询数据
		ListAdapter adapter = new SimpleAdapter(this, listData,
				android.R.layout.simple_list_item_2, new String[] { NAME,
						NUMBER }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		mLstViw.setAdapter(adapter);

		mLstViw.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				DisplayToast("选中第  " + Integer.toString(arg2 + 1) + "个");
			}
		});

		mLstViw.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				DisplayToast("滚动到" + Long.toString(arg0.getSelectedItemId())
						+ "行");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

	}

	public void DisplayToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
}
