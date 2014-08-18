package com.fxt.myapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {
	/**
	 * º‡≤‚Õ¯¬Á¿‡–Õ
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
}
