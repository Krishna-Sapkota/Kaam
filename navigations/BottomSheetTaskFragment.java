package com.project.krishna.kaam.navigations;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.krishna.kaam.R;
import com.project.krishna.kaam.alram.AlramUtil;
import com.project.krishna.kaam.alram.ReminderBroadcastReceiver;
import com.project.krishna.kaam.datamodel.Task;
import com.project.krishna.kaam.datamodel.TaskContract;
import com.project.krishna.kaam.util.DateConversion;

import org.joda.time.DateTime;

public class BottomSheetTaskFragment extends BottomSheetDialogFragment {
    ImageView mMark;
    TextView taskName;
    LinearLayout mCompleted, mShare;
    Task selectedTask;
    MyDialogCloseListener closeListener;
    boolean dismissed = true;

    public BottomSheetTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface MyDialogCloseListener {
        public void handleDialogClose(DialogInterface dialog, boolean dismissed);
    }

    public void DismissListener(MyDialogCloseListener closeListener) {
        this.closeListener = closeListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_bottom_sheet, container, false);
        mMark = view.findViewById(R.id.mark);
        mCompleted = view.findViewById(R.id.mark_completed);
        mShare = view.findViewById(R.id.share);

        taskName = view.findViewById(R.id.taskname);
        taskName.setText(selectedTask.getTaskName());
        if (selectedTask.isCompleted()) {
            mMark.setVisibility(View.VISIBLE);
        }

        mCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissed = false;
                if (selectedTask.isCompleted()) {
                    Log.i("VIS", "Not completed");
                    mMark.setVisibility(View.INVISIBLE);

                    ContentValues modifiedValue = new ContentValues();
                    modifiedValue.put(TaskContract.TaskEntry.COLUMN_TASK_COMPLETED, 0);
                    getContext().getContentResolver().update(TaskContract.TaskEntry.CONTENT_URI.
                                    buildUpon().appendPath(String.valueOf(selectedTask.getId())).build(),
                            modifiedValue, null, null);
                    selectedTask.setCompleted(false);
                    AlramUtil.setAlram(getContext(), selectedTask.getDate(), selectedTask.getId(), selectedTask.getTaskName());

                    dismiss();
                } else if (!selectedTask.isCompleted()) {
                    Log.i("VIS", "completed" + selectedTask.isCompleted());
                    mMark.setVisibility(View.VISIBLE);
                    ContentValues modifiedValue = new ContentValues();
                    modifiedValue.put(TaskContract.TaskEntry.COLUMN_TASK_COMPLETED, 1);
                    getContext().getContentResolver().update(TaskContract.TaskEntry.CONTENT_URI.
                                    buildUpon().appendPath(String.valueOf(selectedTask.getId())).build(),
                            modifiedValue, null, null);
                    selectedTask.setCompleted(true);
                    AlramUtil.cancelAlram(getContext(), selectedTask.getId(), selectedTask.getTaskName());

                    dismiss();

                }

            }
        });
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contentToShare = selectedTask.getTaskName() + "\n" +
                        DateConversion.fromMiliToDate(new DateTime(selectedTask.getDate())) + "\n"
                        + DateConversion.toAMPM(new DateTime(selectedTask.getDate()));
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, contentToShare);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share this reminder"));
                dismiss();
            }
        });


        return view;

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (closeListener != null) {
            closeListener.handleDialogClose(null, dismissed);
        }

    }

    public void setSelectedTask(Task selectedTask) {
        this.selectedTask = selectedTask;
    }


}