package com.project.krishna.kaam.datamodel;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Krishna on 12/17/17.
 */

public class TaskProvider extends ContentProvider {

    public static final int TASKS = 100;

    public static final int TASK_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASK, TASKS);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASK + "/*", TASK_WITH_ID);
        return uriMatcher;
    }

    TaskDBHelper mTaskDBHelper;


    @Override
    public boolean onCreate() {
        Context context = getContext();
        mTaskDBHelper = new TaskDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase database = mTaskDBHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case TASKS:
                retCursor = database.query(TaskContract.TaskEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TASK_WITH_ID:
                String idExists = uri.getLastPathSegment();
                String select = TaskContract.TaskEntry.COLUMN_TASK_ID + " =?";
                String[] args = {idExists};
                retCursor = database.query(TaskContract.TaskEntry.TABLE_NAME,
                        null,
                        select,
                        args,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase database = mTaskDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case TASKS:
                long id = database.insert(TaskContract.TaskEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase database = mTaskDBHelper.getWritableDatabase();
        int del;
        String idToBeDeleted = uri.getLastPathSegment();
        Log.i("PATH", idToBeDeleted);
        String args[] = {idToBeDeleted};
        String select = TaskContract.TaskEntry.COLUMN_TASK_ID + " =?";
        switch (sUriMatcher.match(uri)) {
            case TASK_WITH_ID:
                del = database.delete(TaskContract.TaskEntry.TABLE_NAME, select, args);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);

        return del;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        final SQLiteDatabase database = mTaskDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        String idToBeUpdated = uri.getLastPathSegment();
        String where = TaskContract.TaskEntry.COLUMN_TASK_ID + "=?";
        String args[] = {idToBeUpdated};
        Uri returnUri;
        Log.i("IDT", "to be updated " + idToBeUpdated);
        int id;
        switch (match) {
            case TASK_WITH_ID:
                id = database.update(TaskContract.TaskEntry.TABLE_NAME, contentValues, where, args);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to update row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return id;
    }
}

