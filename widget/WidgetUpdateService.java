package com.project.krishna.kaam.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.project.krishna.kaam.datamodel.Task;

import java.util.ArrayList;

/**
 * Created by Krishna on 1/16/18.
 */

public class WidgetUpdateService extends IntentService {


    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    public static void startBakingService(Context context, ArrayList<Task> today) {
        Intent intent = new Intent(context, WidgetUpdateService.class);

        intent.putParcelableArrayListExtra("Today", today);


        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {

            ArrayList<Task> today = intent.getParcelableArrayListExtra("Today");
            handleActionUpdateBakingWidgets(today);

        }
    }


    private void handleActionUpdateBakingWidgets(ArrayList<Task> today) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE2");
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE2");

        intent.putParcelableArrayListExtra("Today", today);
        Log.i("BRD", "Broadcast sent");
        LocalBroadcastManager.getInstance(this).registerReceiver(new KaamWidget(), new IntentFilter("android.appwidget.action.APPWIDGET_UPDATE2"));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

}