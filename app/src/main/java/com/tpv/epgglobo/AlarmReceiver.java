package com.tpv.epgglobo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("alarm_receiver", "Alarm activated!");
        if (intent == null) {
            return;
        }

        String message = intent.getStringExtra("EXTRA_PROGRAM_MESSAGE");
        String title = intent.getStringExtra("EXTRA_PROGRAM_TITLE");

        if (message != null && title != null) {
            Notification notification = new NotificationCompat.Builder(context, "EPGGlobo")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText(message)
                    .setContentTitle(title)
                    .build();
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(message.hashCode(), notification);
        }
    }
}
