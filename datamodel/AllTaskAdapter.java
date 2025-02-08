package com.project.krishna.kaam.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.krishna.kaam.R;
import com.project.krishna.kaam.util.DateConversion;

import org.joda.time.DateTime;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class AllTaskAdapter extends RecyclerView.Adapter<AllTaskAdapter.TaskViewHolder> {


    private ArrayList<Task> taskList;
    private final Context context;
    private int selectedPos = RecyclerView.NO_POSITION;
    final private TaskClickListener mOnClickListener;
    boolean hour12format;


    public void restoreItem(Task deletedItem, int deletedIndex) {
        Uri uri = TaskContract.TaskEntry.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_ID, deletedItem.getId());
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_NAME, deletedItem.getTaskName());
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_DATE_TIME, deletedItem.getDate());
        int com = 0;
        if (deletedItem.isCompleted()) {
            com = 1;
        }
        contentValues.put(TaskContract.TaskEntry.COLUMN_TASK_COMPLETED, com);
        context.getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);
        taskList.add(deletedIndex, deletedItem);
        notifyItemInserted(deletedIndex);


    }

    public interface TaskClickListener {
        void onThumnailClick(int clickedIndex);
    }


    public AllTaskAdapter(Context c, ArrayList<Task> task, TaskClickListener listener, boolean hour12) {
        mOnClickListener = listener;
        taskList = task;
        context = c;
        hour12format = hour12;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context c = parent.getContext();
        int layout_card = R.layout.task_card;
        LayoutInflater layoutInflater = LayoutInflater.from(c);
        View root = layoutInflater.inflate(layout_card, parent, false);

        return new TaskViewHolder(root);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        /*int taskIndex=taskList.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_NAME);
        int dateTimeIndex=taskList.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DATE_TIME);

        long date=taskList.getLong(dateTimeIndex);*/
        String name = taskList.get(position).getTaskName();
        long td = taskList.get(position).getDate();
        boolean completed = false;
        boolean passeDue = false;
        boolean today = false;
        boolean yesterday = false;
        boolean tomorrow = false;
        boolean future = false;
        DateTime current = DateTime.now();
        DateTime taskDate = new DateTime(taskList.get(position).getDate());
        if (current.isAfter(taskList.get(position).getDate())) {
            passeDue = true;
        }
        if (current.isBefore(taskDate)) {
            future = true;
        }
        if (DateConversion.isTomorrow(taskDate)) {
            tomorrow = true;
        }
        if (DateConversion.isYesterday(taskDate)) {
            yesterday = true;
        }
        if (DateConversion.isToday(taskDate)) {
            today = true;
        }


        //DateTime td=new DateTime(date);
        if (taskList.get(position).isCompleted()) {
            completed = true;
            int comco = this.context.getResources().getColor(R.color.comBackgraound);
            Drawable com = this.context.getResources().getDrawable(R.drawable.ic_timer_com);

            holder.cardView.setBackgroundColor(comco);
            holder.tName.setTextColor(Color.BLACK);
            holder.tDate.setTextColor(Color.BLACK);
            holder.tTime.setTextColor(Color.BLACK);
            holder.taskLabel.setTextColor(Color.BLACK);
            holder.dateLabel.setTextColor(Color.BLACK);
            holder.timeLabel.setTextColor(Color.BLACK);
            holder.timerIcon.setVisibility(View.VISIBLE);
            holder.timerIcon.setImageDrawable(com);
            holder.passedDue.setVisibility(View.VISIBLE);
            holder.passedDue.setText(context.getString(R.string.completed_msg));
        }
        int color;

        if (DateConversion.isToday(new DateTime(taskList.get(position).getDate())) && !completed) {
            int todayColor = this.context.getResources().getColor(R.color.upcoming);
            holder.cardView.setBackgroundColor(todayColor);

            Drawable resImg = this.context.getResources().getDrawable(R.drawable.ic_timer);
            holder.timerIcon.setVisibility(View.VISIBLE);
            holder.passedDue.setVisibility(View.VISIBLE);
            holder.passedDue.setTextColor(Color.BLACK);

            holder.timerIcon.setImageDrawable(resImg);
            holder.passedDue.setText(context.getString(R.string.upcoming_today_msg));
            today = true;
        }
        if (passeDue && !completed && today) {
            Drawable resImg = this.context.getResources().getDrawable(R.drawable.ic_timer_off);
            int textColor = this.context.getResources().getColor(R.color.colorSecondaryText);
            int todayColor = this.context.getResources().getColor(R.color.passedDue);
            holder.cardView.setBackgroundColor(todayColor);

            holder.timerIcon.setImageDrawable(resImg);
            holder.timerIcon.setVisibility(View.VISIBLE);
            holder.passedDue.setTextColor(textColor);
            holder.passedDue.setVisibility(View.VISIBLE);
            holder.passedDue.setText(context.getString(R.string.passed_today_msg));
        }
        if (!completed && !today && tomorrow) {
            Drawable resImg = this.context.getResources().getDrawable(R.drawable.ic_timer);
            int upcomingTomorrowColor = this.context.getResources().getColor(R.color.upcoming);
            int textColor = this.context.getResources().getColor(R.color.colorDivider);
            holder.cardView.setBackgroundColor(upcomingTomorrowColor);
            holder.timerIcon.setImageDrawable(resImg);
            holder.timerIcon.setVisibility(View.VISIBLE);
            holder.passedDue.setTextColor(Color.BLACK);
            holder.passedDue.setVisibility(View.VISIBLE);
            holder.passedDue.setText(context.getString(R.string.tomorrow_msg));

        }

        if (!completed && !today && yesterday) {
            int passedYesterdayColor = this.context.getResources().getColor(R.color.passedDue);
            int passedYesterdayText = this.context.getResources().getColor(R.color.colorSecondaryText);

            holder.cardView.setBackgroundColor(passedYesterdayColor);
            Drawable resImg = this.context.getResources().getDrawable(R.drawable.ic_timer_off);
            holder.timerIcon.setImageDrawable(resImg);
            holder.timerIcon.setVisibility(View.VISIBLE);
            holder.passedDue.setTextColor(passedYesterdayText);
            holder.passedDue.setVisibility(View.VISIBLE);
            holder.passedDue.setText(context.getString(R.string.passed_yesterday_msg));

        }
        if (!tomorrow && !yesterday && !today && passeDue && !completed) {
            int passedColor = this.context.getResources().getColor(R.color.passedDue);
            holder.cardView.setBackgroundColor(passedColor);
            int passedTextColor = this.context.getResources().getColor(R.color.colorSecondaryText);


            Drawable resImg = this.context.getResources().getDrawable(R.drawable.ic_timer_off);
            holder.timerIcon.setVisibility(View.VISIBLE);
            holder.passedDue.setVisibility(View.VISIBLE);
            holder.passedDue.setTextColor(passedTextColor);

            holder.timerIcon.setImageDrawable(resImg);
            holder.passedDue.setText(context.getString(R.string.passed_msg));
        }
        if (!yesterday && !today && !tomorrow && !passeDue) {
            int futureColor = this.context.getResources().getColor(R.color.upcoming);
            holder.cardView.setBackgroundColor(futureColor);
            Drawable resImg = this.context.getResources().getDrawable(R.drawable.ic_timer);
            holder.timerIcon.setVisibility(View.VISIBLE);
            holder.passedDue.setVisibility(View.VISIBLE);
            holder.passedDue.setTextColor(Color.BLACK);

            holder.timerIcon.setImageDrawable(resImg);
            holder.passedDue.setText(context.getString(R.string.upcoming_msg));
        }

        String fulldate = DateConversion.fromMiliToDate(new DateTime(td));


        holder.tName.setText(name);
        holder.tDate.setText(fulldate);
        if (hour12format) {
            holder.tTime.setText(DateConversion.toAMPM(new DateTime(td)));
        } else {
            holder.tTime.setText(DateConversion.getTimein24(td));
        }


    }

    @Override
    public int getItemCount() {
        if (taskList != null)
            return taskList.size();
        else return 0;

    }

    public void removeItem(int position) {
        Log.i("POS-", "here" + position);
        Log.i("POS-", "size" + taskList.size());

        String selection = TaskContract.TaskEntry.COLUMN_TASK_ID;
        String[] args = {String.valueOf(taskList.get(position).getId())};
        context.getContentResolver().delete(TaskContract.TaskEntry.CONTENT_URI.
                        buildUpon().appendPath(String.valueOf(taskList.get(position).getId())).build(),
                null, null);

        taskList.remove(position);

        notifyItemRemoved(position);
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        //final ImageView poster;

        TextView tName, taskLabel;
        TextView tDate, dateLabel;
        TextView tTime, timeLabel;
        CardView cardView;
        ImageView timerIcon;
        TextView passedDue;
        public RelativeLayout viewBackgroundDelete, viewBackgroundEdit, viewForeground;


        public TaskViewHolder(View itemView) {

            super(itemView);
            //poster=itemView.findViewById(R.id.iv_poster);
            tName = itemView.findViewById(R.id.taskname);
            tDate = itemView.findViewById(R.id.reminddate);
            tTime = itemView.findViewById(R.id.remindtime);
            taskLabel = itemView.findViewById(R.id.taskLabel);
            dateLabel = itemView.findViewById(R.id.dateLabel);
            timeLabel = itemView.findViewById(R.id.timeLabel);
            timerIcon = itemView.findViewById(R.id.timer);
            passedDue = itemView.findViewById(R.id.passed_due);
            viewBackgroundDelete = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            cardView = itemView.findViewById(R.id.card_view);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            int clickedPosition = getAdapterPosition();

            mOnClickListener.onThumnailClick(clickedPosition);

            return true;
        }
    }

    //RecyclerView recyclerView = findViewById(R.id.recycler_view);

// attaching the touch helper to recycler view

}

