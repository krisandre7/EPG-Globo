package com.tpv.epgglobo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.tpv.epgglobo.model.Program;

public class AlarmScheduler implements AlarmSchedulerInterface {

    private final Context context;
    private final AlarmManager alarmManager;

    public AlarmScheduler(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void schedule(Program program) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("EXTRA_PROGRAM_MESSAGE", String.format(
                "Venha assistir seu programa, %s!", program.getName()
        ));
        intent.putExtra("EXTRA_PROGRAM_TITLE", String.format(
                "O programa %s começou!", program.getName()
        ));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                        program.getStartTime().getTime(),
                    PendingIntent.getBroadcast(
                        context,
                        program.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    )
                );

                Log.i("alarm", "Alarm scheduled successfully!");
            } else {
                Log.i("alarm", "Alarm couldn't be scheduled!");
            }
        }
    }

    @Override
    public void cancel(Program item) {
        alarmManager.cancel(
                PendingIntent.getBroadcast(
                        context,
                        item.hashCode(),
                        new Intent(context, AlarmReceiver.class),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                )
        );
    }
}

