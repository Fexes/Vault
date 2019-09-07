package com.example.encrypt.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.encrypt.R;


public class NotificationUtil {

    public int NOTIFICATION_ID = 124451;
    NotificationCompat.Builder mBuilder;
    String channelId = "channel-01";
    NotificationManager notificationManager;
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public NotificationUtil(Context context, String message, int total, int current) {
        this.context = context;

        Notification notification = buildNotification(message, total, current);
        notificationManager.notify(NOTIFICATION_ID, notification);


    }

    public void updateNotification(String message, int total, int current) {


        mBuilder.setContentTitle("")
                .setContentTitle("Title")
                .setOngoing(true)
                .setContentText(message)
                .setProgress(total, current, false);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Notification buildNotification(String message, int total, int current) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_LOW;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        mBuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Title")
                .setOngoing(true)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setProgress(total, current, false);


        return mBuilder.build();

    }


    public void cancel() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
