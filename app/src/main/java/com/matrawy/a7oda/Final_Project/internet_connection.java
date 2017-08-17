package com.matrawy.a7oda.Final_Project;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by 7oda on 10/25/2016.
 */

public class internet_connection {  //A class to check internet connection
    public static Context Mycontext;
    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) Mycontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

