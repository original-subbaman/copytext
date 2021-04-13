package com.subba.clipboardmanager.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.subba.clipboardmanager.Activities.App;
import com.subba.clipboardmanager.Activities.MainActivity;
import com.subba.clipboardmanager.R;
import com.subba.clipboardmanager.Room.Entity.ClipboardItem;
import com.subba.clipboardmanager.Utility.NotificationReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClipboardMonitorService extends Service {

    private static final String TAG = "ClipboardMonitorService";
    private ClipboardManager mClipboardManager;
    private static final String NOTIFICATION_TITLE_SERVICE_START = "CopyPaste is running in the background";
    private static final String NOTIFICATION_CONTENT_SERVICE_START = "Copy any text and have it saved to the app automatically.";
    private static final String NOTIFICATION_TITLE_SERVICE_STOP = "CopyPaste has stopped working";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    private NotificationManagerCompat mNotificationManager;
    private ClipboardItem previousItem = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null){
            String action = intent.getAction();
            Log.d(TAG, "onStartCommand: " + action);
            switch(action){
                case ACTION_START_FOREGROUND_SERVICE:
                    Log.d(TAG, "onStartCommand: foreground started");
                    mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
                    createNotificationWithMessage(NOTIFICATION_TITLE_SERVICE_START, NOTIFICATION_CONTENT_SERVICE_START);
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    Log.d(TAG, "onStartCommand: foreground stopped");
                    stopForeground(true);
                    stopSelf();
                    break;
                default:
                    Log.d(TAG, "default");
            }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mClipboardManager != null){
            mClipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
            createNotificationWithMessage(NOTIFICATION_TITLE_SERVICE_STOP, NOTIFICATION_TITLE_SERVICE_STOP);
        }
    }

    private void createNotificationWithMessage(String title, String description) {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent stopIntent = new Intent(this, ClipboardMonitorService.class);
        stopIntent.setAction(ACTION_STOP_FOREGROUND_SERVICE);
        PendingIntent btPendingIntent = PendingIntent.getService(this, 0, stopIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(contentIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop Service", btPendingIntent)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        startForeground(1, builder.build());

    }

    public String getCurrentTime(){
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
            currentTime = simpleDateFormat.format(calendar.getTime());
        }else{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
            currentTime = simpleDateFormat.format(new Date());
        }

        return currentTime;
    }


    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    String copiedText = mClipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                    ClipboardItem clipboardItem = new ClipboardItem(copiedText, getCurrentTime());

                    if(previousItem == null){
                        previousItem = clipboardItem;
                    }else if(previousItem.getText().equals(clipboardItem.getText())){
                        return;
                    }

                    MainActivity.viewModel.insert(clipboardItem);
                    previousItem = clipboardItem;
                }
            };
}
