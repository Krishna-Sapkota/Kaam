package com.project.krishna.kaam.navigations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.project.krishna.kaam.AnalyticsApplication;
import com.project.krishna.kaam.EditTask;
import com.project.krishna.kaam.HideShowScrollListener;
import com.project.krishna.kaam.R;
import com.project.krishna.kaam.alram.AlramUtil;
import com.project.krishna.kaam.datamodel.AllTaskAdapter;
import com.project.krishna.kaam.datamodel.Task;
import com.project.krishna.kaam.datamodel.TaskContract;
import com.project.krishna.kaam.util.DateConversion;
import com.project.krishna.kaam.widget.WidgetUpdateService;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.helper.ItemTouchHelper.Callback.getDefaultUIUtil;

public class CompletedTask extends AllTask {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_all_task, container, false);
        super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }


    private List<Task> builtTaskList(Cursor cursor) {
        List<Task> taskList = new ArrayList<>();
        List<Task> todayList = new ArrayList<>();
        cursor.moveToFirst();
        boolean todayTask = false;

        while (!cursor.isAfterLast()) {
            Task task = new Task();
            boolean completed = false;
            int idIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_ID);
            int id = cursor.getInt(idIndex);
            int taskIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_NAME);
            String tName = cursor.getString(taskIndex);
            int dateTimeIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DATE_TIME);
            long date = cursor.getLong(dateTimeIndex);
            int cColoumn = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_COMPLETED);
            int com = cursor.getInt(cColoumn);
            if (com != 0) completed = true;
            task.setId(id);
            task.setTaskName(tName);
            task.setDate(date);
            task.setCompleted(completed);

            if (DateConversion.isToday(new DateTime(task.getDate())) && !task.isCompleted()) {
                todayList.add(task);
            }


            if (task.isCompleted()) {
                taskList.add(task);
            }


            cursor.moveToNext();
        }

        //cursor.close();
        WidgetUpdateService.startBakingService(getContext(), (ArrayList<Task>) todayList);

        return taskList;
    }


}

