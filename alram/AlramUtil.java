package com.project.krishna.kaam.alram;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.project.krishna.kaam.util.DateConversion;

import org.joda.time.DateTime;

import static com.project.krishna.kaam.util.DataSQL.getNextAutoIncrement;

public class AlramUtil {
    private static final String TASK_ID ="task_id";
    private static final String ALRAM_TIME ="alram";

    public static void setAlram(Context context, long alram, int taskId, String taskName) {

        // registerReceiver(new ReminderBroadcastReceiver(),new IntentFilter("com.project.krishna.kaam"));
        Intent intent = new Intent(context,ReminderBroadcastReceiver.class);
        intent.putExtra(ReminderBroadcastReceiver.REMINDER_NAME,taskName);
        intent.putExtra(TASK_ID,taskId);
        intent.putExtra(ALRAM_TIME,alram);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        DateTime today=DateTime.now();

        if(!(new DateTime(alram).isBefore(today))) {
            am.set(AlarmManager.RTC_WAKEUP,
                    alram, pendingIntent);
        }
    }
    public static void cancelAlram(Context context,int taskId,String taskName){
        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra(ReminderBroadcastReceiver.REMINDER_NAME,taskName);
        intent.putExtra(TASK_ID,taskId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        am.cancel(pendingIntent);
    }
}
