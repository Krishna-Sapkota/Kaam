package com.project.krishna.kaam.navigations;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.krishna.kaam.R;
import com.project.krishna.kaam.datamodel.Task;
import com.project.krishna.kaam.datamodel.TaskContract;
import com.project.krishna.kaam.util.DateConversion;
import com.project.krishna.kaam.widget.WidgetUpdateService;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class TodayTask extends AllTask
{



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        view=inflater.inflate(R.layout.fragment_all_task,container,false);
        super.onCreateView(inflater,container,savedInstanceState);
        return view;
    }


public List<Task> builtTaskList(Cursor cursor) {
        List<Task> taskList=new ArrayList<>();
        List<Task> todayList=new ArrayList<>();
        cursor.moveToFirst();
        boolean todayTask=false;

        while(!cursor.isAfterLast()){
            Task task=new Task();
            boolean completed=false;
            int idIndex=cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_ID);
            int id=cursor.getInt(idIndex);
            int taskIndex=cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_NAME);
            String tName=cursor.getString(taskIndex);
            int dateTimeIndex=cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DATE_TIME);
            long date=cursor.getLong(dateTimeIndex);
            int cColoumn=cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_COMPLETED);
            int com=cursor.getInt(cColoumn);
            if(com!=0) completed=true;
            task.setId(id);
            task.setTaskName(tName);
            task.setDate(date);
            task.setCompleted(completed);

            if (DateConversion.isToday(new DateTime(task.getDate())) && !task.isCompleted())
                    {
                        taskList.add(task);
                    }


            cursor.moveToNext();
        }


        WidgetUpdateService.startBakingService(getContext(),(ArrayList<Task>)taskList);

        return  taskList;
    }

}

