package com.subba.clipboardmanager.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.subba.clipboardmanager.Activities.App;
import com.subba.clipboardmanager.Activities.MainActivity;
import com.subba.clipboardmanager.R;
import com.subba.clipboardmanager.Room.ClipboardItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class ClipboardMonitorService extends Service {

    private static final String TAG = "ClipboardMonitorService";
    private ClipboardManager mClipboardManager;
    private static final String NOTIFICATION_TITLE_SERVICE_START = "CopyPaste is running in the background";
    private static final String NOTIFICATION_CONTENT_SERVICE_START = "Copy any text and have it saved to the app automatically.";
    private static final String NOTIFICATION_TITLE_SERVICE_STOP = "CopyPaste has stopped working";
    private static final String NOTIFICATION_CONTENT_SERVICE_STOP = "To restart the service open the app once more.";
    private NotificationManagerCompat mNotificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationWithMessage(NOTIFICATION_TITLE_SERVICE_START, NOTIFICATION_CONTENT_SERVICE_START);
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        startForeground(1, builder.build());

    }

    public String getCurrentTime(){
        String currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
            currentTime = simpleDateFormat.format(calendar.getTime());
        }else{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
            currentTime = simpleDateFormat.format(new Date());
        }

        return currentTime;
    }


    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    String copiedText = mClipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                    MainActivity.viewModel.insert(new ClipboardItem(copiedText, getCurrentTime()));
                }
            };
}
