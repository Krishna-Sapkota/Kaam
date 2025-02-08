package com.project.krishna.kaam.datamodel;

import android.net.Uri;
import android.provider.BaseColumns;

public class TaskContract {

    public static final String AUTHORITY = "com.project.krishna.kaam";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TASK = "task";

    public static final class TaskEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_TASK).build();
        public static final String TABLE_NAME = "task";

        public static final String COLUMN_TASK_ID = "task_id";
        public static final String COLUMN_TASK_NAME = "task_name";
        public static final String COLUMN_TASK_DATE_TIME = "task_datetime";
        public static final String COLUMN_TASK_COMPLETED = "task_completed";


    }
}
