package com.project.krishna.kaam;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.project.krishna.kaam.alram.AlramUtil;
import com.project.krishna.kaam.datamodel.Task;
import com.project.krishna.kaam.datamodel.TaskContract;
import com.project.krishna.kaam.navigations.AllTask;
import com.project.krishna.kaam.util.DateConversion;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class EditTask extends AppCompatActivity {
    EditText mTaskName, mDate, mTime;
    Button mDone, mCancel;
    DateTime changedDate;
    LocalTime changedTime;
    Task taskToBeEdited;
    Toolbar toolbar;
    private boolean taskModified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        mTaskName = findViewById(R.id.et_task_name);
        mDate = findViewById(R.id.et_date_field);
        mTime = findViewById(R.id.et_time_field);
        mDone = findViewById(R.id.btn_ok);
        mCancel = findViewById(R.id.btn_cancel);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        taskToBeEdited = getIntent().getParcelableExtra(AllTask.TASK_TO_BE_EDITED);
        mTaskName.setText(taskToBeEdited.getTaskName());
        mDate.setText(DateConversion.fromMiliToDate(new DateTime(taskToBeEdited.getDate())));
        mTime.setText(DateConversion.toAMPM(new DateTime(taskToBeEdited.getDate())));
        changedDate = new DateTime(taskToBeEdited.getDate());
        DateTime dt = new DateTime(taskToBeEdited.getDate());
        Log.i("CLASS", "here " + taskToBeEdited.getId());

        changedTime = new LocalTime(dt.getHourOfDay(), dt.getMinuteOfHour());
        mDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                LocalDate mcurrentDate = new LocalDate();
                int mYear = mcurrentDate.getYear();
                int mMonth = mcurrentDate.getMonthOfYear();
                int mDay = mcurrentDate.getDayOfMonth();
                DatePickerDialog mDatePicker = new DatePickerDialog(EditTask.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mDate.setText("");
                        Log.d("DEBUG", "year: " + year + " month: " + month + " day: " + day);
                        DateTime dt = new DateTime(year, month + 1, day, 0, 0);
                        changedDate = dt;
                        mDate.append(String.valueOf(dt.getYear()) + " ");
                        mDate.append(dt.monthOfYear().getAsText() + " ");
                        mDate.append(String.valueOf(dt.getDayOfMonth()) + ", ");
                        mDate.append(dt.dayOfWeek().getAsText());
                    }
                }, mYear, mMonth - 1, mDay);
                mDatePicker.setTitle(getResources().getString(R.string.select_date_msg));
                mDatePicker.show();
            }
        });

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Calendar mcurrentTime = Calendar.getInstance();
                LocalTime localTime = new LocalTime();
                int hour = localTime.getHourOfDay();
                int minute = localTime.getMinuteOfHour();


                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditTask.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        LocalTime time = new LocalTime(selectedHour + ":" + selectedMinute);
                        DateTimeFormatter fmt = DateTimeFormat.forPattern("h:mm a");
                        changedTime = time;
                        mTime.setText(fmt.print(time));
                    }
                }, hour, minute, false);
                mTimePicker.setTitle(getResources().getString(R.string.select_time_msg));
                mTimePicker.show();
            }
        });
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CoordinatorLayout coordinatorLayout = findViewById(R.id.snackbarlayout);
                Task modifiedTask = new Task();
                checkifModified();
                modifiedTask.setTaskName(mTaskName.getText().toString());
                modifiedTask.setId(taskToBeEdited.getId());
                long modifiedDateTime = DateConversion.getDateTime(changedDate, changedTime);
                modifiedTask.setDate(modifiedDateTime);
                modifiedTask.setCompleted(taskToBeEdited.isCompleted());
                ContentValues modifiedValue = new ContentValues();
                modifiedValue.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, modifiedTask.getTaskName());
                modifiedValue.put(TaskContract.TaskEntry.COLUMN_TASK_DATE_TIME, modifiedTask.getDate());
                if (taskModified) {
                    AlramUtil.cancelAlram(getApplicationContext(), taskToBeEdited.getId(), taskToBeEdited.getTaskName());
                    AlramUtil.setAlram(getApplicationContext(), modifiedTask.getDate(), taskToBeEdited.getId(), modifiedTask.getTaskName());
                    getContentResolver().update(TaskContract.TaskEntry.CONTENT_URI.
                                    buildUpon().appendPath(String.valueOf(modifiedTask.getId())).build(),
                            modifiedValue, null, null);
                    Toast.makeText(getApplicationContext(), getString(R.string.task_modified_msg), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditTask.this, MainActivity.class));


                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, getString(R.string._task_not_modified_msg), Snackbar.LENGTH_LONG);

                    snackbar.show();
                }

            }

            private void checkifModified() {
                if (!(taskToBeEdited.getTaskName().equals(mTaskName.getText().toString())
                        && taskToBeEdited.getDate() == DateConversion.getDateTime(changedDate, changedTime))) {
                    taskModified = true;
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), getString(R.string.action_cancel_msg), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditTask.this, MainActivity.class));

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            onBackPressed();

        }

        return true;
    }


 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }*/


}
