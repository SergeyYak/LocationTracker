package com.sergei.tracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import com.sergei.tracker.R;

import org.jetbrains.annotations.NotNull;

public class AppService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        this.startForegroundNotification();
        final Context context = getApplicationContext();
        boolean[] appReadyStatus = new AppConfig(context).CheckIfAppReadyForLaunch();
        for (boolean appPoint:appReadyStatus) {
            if(!appPoint) stopSelf();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                LocationTaskListener locListener = new LocationTaskListener(context);
                locListener.StartListener();
                Looper.loop();
            }
        }).start();
        return START_STICKY;
    }

    void startForegroundNotification() {
        Notification notificationBuild = new NotificationCompat.Builder(this,
                "default")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(AppConfig.Values.EmptyString)
                .build();
        initChannels(this, notificationBuild);
        startForeground(1, notificationBuild);
    }

    void initChannels(@NotNull Context context, Notification notificationBuild) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "ServiceNotification",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null, null);
        channel.setShowBadge(false);
        channel.setDescription("ServiceNotificationChannel");
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(1, notificationBuild);
    }
}