package com.partyanimal.clockwidget;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Implementation of App Widget functionality.
 */
public class PartyClockWidget extends AppWidgetProvider {

    private PendingIntent service = null;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
      //super.onUpdate(context, appWidgetManager, appWidgetIds);
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Calendar time = Calendar.getInstance();
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.HOUR, 0);

        final Intent intent = new Intent(context, PartyClock.class);

        if (service == null) {          
            service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        alarmManager.setRepeating(AlarmManager.RTC, time.getTime().getTime(),  813 /* ms */, service);
    }

    @Override
    public void onDisabled(Context context) {
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(service);
    }
}