package com.sergei.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent ServiceIntent = new Intent(context, AppService.class);
            context.stopService(ServiceIntent);
            context.startForegroundService(ServiceIntent);
        }
    }
}
