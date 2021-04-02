package com.subba.clipboardmanager.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.subba.clipboardmanager.Services.ClipboardMonitorService;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent stopService = new Intent(context, ClipboardMonitorService.class);
        intent.setAction(ClipboardMonitorService.ACTION_STOP_FOREGROUND_SERVICE);
        context.startService(stopService);
    }

}
