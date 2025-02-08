package com.project.krishna.kaam.util;

import android.content.Context;
import android.database.Cursor;

import com.project.krishna.kaam.datamodel.TaskContract;


public class DataSQL {


    public static int getNextAutoIncrement(Context context) {

        String[] projection = {TaskContract.TaskEntry.COLUMN_TASK_ID};


        Cursor cursor = context.getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI, projection, null, null, null);
        int autoIncrement = 0;
        cursor.moveToFirst();
        int indexSeq = 0;
        while (!cursor.isAfterLast()) {
            indexSeq = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_ID);
            autoIncrement = cursor.getInt(indexSeq);
            cursor.moveToNext();

        }

        cursor.close();

        return autoIncrement + 1;
    }
}
