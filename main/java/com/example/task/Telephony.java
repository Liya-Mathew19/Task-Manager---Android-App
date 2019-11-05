package com.example.task;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.UUID;

public class Telephony {
    Context CurrentContext;
    boolean AllPermissionFlag;

    public Telephony(Context cntxt) {
        CurrentContext = cntxt;
        AllPermissionFlag = false;
    }

    public boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) CurrentContext.getSystemService(CurrentContext.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }

    public String GetDeviceUniqueId() {

        String DeviceUniqueId = "";
        String StoreIdentifier = "", WSUrl = "";
        TelephonyManager TelephoneManager1;
        TelephoneManager1 = (TelephonyManager) CurrentContext.getSystemService(CurrentContext.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(CurrentContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

        }
        DeviceUniqueId = TelephoneManager1.getDeviceId();

        if(DeviceUniqueId == null || DeviceUniqueId.length() == 0 ) {

            SharedPreferences sharedPrefs1 = PreferenceManager.getDefaultSharedPreferences(CurrentContext);
            DeviceUniqueId = sharedPrefs1.getString("StoredDevId","");
            StoreIdentifier = sharedPrefs1.getString("StoreIdf","");
            WSUrl = sharedPrefs1.getString("WSUrl","");

            Log.d("MSAPP", "Taking From SharedPreferences");
            //Toast.makeText(CurrentContext,"Taking From SharedPreferences", Toast.LENGTH_SHORT).show();

        }

        if(DeviceUniqueId == null || DeviceUniqueId.length() == 0 ) {
            String strUUID  = String.valueOf( UUID.randomUUID());
            strUUID = strUUID.replace("-", "");
            DeviceUniqueId = strUUID.substring(0,15).toUpperCase();

            Log.d("MSAPP", "Taking From UUID");
        }

        Log.d("MSAPP", "DeviceUniqueId : " + DeviceUniqueId);
        Log.d("MSAPP", "StoreIdentifier : " + StoreIdentifier);
        Log.d("MSAPP", "WSUrl : " + WSUrl);

        return DeviceUniqueId;
    }
    //	}
}
