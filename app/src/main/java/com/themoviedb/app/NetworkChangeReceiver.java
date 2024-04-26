package com.themoviedb.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (connectivityManager.getActiveNetworkInfo() != null
                    && connectivityManager.getActiveNetworkInfo().isConnected()) {
                Toast.makeText(context, "Terhubung ke jaringan", Toast.LENGTH_SHORT).show();
                ((MainActivity) context).checkAndFetchData();
            } else {
                Toast.makeText(context, "Tidak terhubung ke jaringan", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
