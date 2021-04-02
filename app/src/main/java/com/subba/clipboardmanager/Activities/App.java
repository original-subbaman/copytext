package com.subba.clipboardmanager.Activities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.subba.clipboardmanager.R;

public class App extends Application {
    public static final String CHANNEL_ID = "MONITOR_SERVICE_STARTED";
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    public static Context getContext(){
        return mContext;
    }

    public static void setContext(Context context){
        mContext = context;
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
