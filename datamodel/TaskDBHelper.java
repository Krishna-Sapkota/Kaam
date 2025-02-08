package com.project.krishna.kaam.datamodel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDBHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "rTask.db";
    private static final int DB_VERSION = 3;

    TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
                TaskContract.TaskEntry.COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                TaskContract.TaskEntry.COLUMN_TASK_NAME + " TEXT NOT NULL ," +
                TaskContract.TaskEntry.COLUMN_TASK_DATE_TIME + " INTEGER ," +
                TaskContract.TaskEntry.COLUMN_TASK_COMPLETED + " INTEGER);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME);
        onCreate(db);

    }
}