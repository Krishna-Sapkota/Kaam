package com.project.krishna.kaam;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
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
import com.project.krishna.kaam.alram.ReminderBroadcastReceiver;
import com.project.krishna.kaam.datamodel.TaskContract;
import com.project.krishna.kaam.util.DateConversion;
import com.project.krishna.kaam.util.TimePickerLimited;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static com.project.krishna.kaam.util.DataSQL.getNextAutoIncrement;
import static org.joda.time.LocalDateTime.parse;

public class AddTaskActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "ALARAM";
    private static final String AM_PM = "h:mm a";
    private Button addTaskButton, cancelButton;
    EditText taskName;
    Toolbar toolbar;
    DateTime dateTime;
    LocalTime localTime;
    public boolean todayChoosed = false;
    boolean dateChoosed = false;
    EditText remindDate, remindTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        remindDate = findViewById(R.id.et_date_field);
        remindTime = findViewById(R.id.et_time_field);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addTaskButton = findViewById(R.id.btn_ok);
        cancelButton = findViewById(R.id.btn_cancel);
        taskName = findViewById(R.id.et_task_name);

        addTaskButton.setOnClickListener(new AddTaskButtonListener());
        cancelButton.setOnClickListener(new AddTaskButtonListener());
        remindDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                LocalDate mcurrentDate = new LocalDate();
                int mYear = mcurrentDate.getYear();
                int mMonth = mcurrentDate.getMonthOfYear();
                int mDay = mcurrentDate.getDayOfMonth();
                DatePickerDialog mDatePicker = new DatePickerDialog(AddTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        remindDate.setText("");
                        Log.d("DEBUG", "year: " + year + " month: " + month + " day: " + day);
                        DateTime dt = new DateTime(year, month + 1, day, 0, 0);
                        setDate(dt);
                        if ((dateTime.toLocalDate()).equals(new LocalDate())) {
                            todayChoosed = true;
                            Log.i("TOD", "yes");
                        } else {
                            todayChoosed = false;
                        }
                        remindDate.append(String.valueOf(dt.getYear()) + " ");
                        remindDate.append(dt.monthOfYear().getAsText() + " ");
                        remindDate.append(String.valueOf(dt.getDayOfMonth()) + ", ");
                        remindDate.append(dt.dayOfWeek().getAsText());
                        dateChoosed = true;
                    }
                }, mYear, mMonth - 1, mDay);
                mDatePicker.setTitle(getResources().getString(R.string.select_date_msg));
                mDatePicker.getDatePicker().setMinDate(DateTime.now().getMillis());
                mDatePicker.show();
            }
        });

        remindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Calendar mcurrentTime = Calendar.getInstance();
                LocalTime localTime = new LocalTime();
                int hour = localTime.getHourOfDay();
                int minute = localTime.getMinuteOfHour();

                TimePickerDialog mTimePicker;

               /* mTimePicker = new TimePickerDialog(AddTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        LocalTime time = new LocalTime(selectedHour+":"+selectedMinute);
                        setTime(time);
                        DateTimeFormatter fmt = DateTimeFormat.forPattern("h:mm a");

                        remindTime.setText(fmt.print(time));

                    }
                }, hour, minute,false);
                mTimePicker.setTitle(getResources().getString(R.string.select_time_msg));
                mTimePicker.show();*/
                if (dateChoosed) {
                    TimePickerLimited timePickerLimited = new TimePickerLimited(AddTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            LocalTime time = new LocalTime(selectedHour + ":" + selectedMinute);
                            setTime(time);
                            DateTimeFormatter fmt = DateTimeFormat.forPattern(AM_PM);

                            remindTime.setText(fmt.print(time));

                        }
                    }, hour, minute, false);

                    Log.i("TOD", "LIMITED");
                    if (todayChoosed) {
                        //timePickerLimited.setMin(hour, minute);
                    }

                    timePickerLimited.setTitle(getResources().getString(R.string.select_time_msg));
                    timePickerLimited.show();
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.snackBar), getString(R.string.date_first_msg), Snackbar.LENGTH_SHORT);
                    snackbar.show();

                }

            }
        });
    }

    private void setDate(DateTime dt) {
        dateTime = dt;
    }

    private void setTime(LocalTime time) {
        localTime = time;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }


    private class AddTaskButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            int id = view.getId();
            if (id == addTaskButton.getId()) {

                if (taskName.getText().toString().trim().length() > 0 && remindDate.getText().
                        toString().trim().length() > 0 && remindTime.getText().toString().trim().length() > 0) {
                    long dateTimeInMili = DateConversion.getDateTime(dateTime, localTime);
                    short complete = 0;
                    int taskId = getNextAutoIncrement(getBaseContext());

                    AlramUtil.setAlram(getApplicationContext(), dateTimeInMili, taskId, taskName.getText().toString());
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, taskName.getText().toString());
                    contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DATE_TIME, dateTimeInMili);
                    contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_COMPLETED, complete);
                    getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);

                    Toast.makeText(AddTaskActivity.this, getString(R.string.added_reminder_msg), Toast.LENGTH_SHORT).show();
                    Intent mainActivity = new Intent(AddTaskActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.snackBar), getString(R.string.complete_field_msg), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
            if (id == cancelButton.getId()) {
                Toast.makeText(AddTaskActivity.this, getString(R.string.action_cancel_msg), Toast.LENGTH_SHORT).show();
                Intent mainActivity = new Intent(AddTaskActivity.this, MainActivity.class);
                startActivity(mainActivity);
            }
        }
    }


}
