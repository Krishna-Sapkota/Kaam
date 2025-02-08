package com.project.krishna.kaam.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.project.krishna.kaam.R;
import com.project.krishna.kaam.datamodel.Task;

import java.util.ArrayList;

import static com.project.krishna.kaam.widget.KaamWidget.todayTask;


public class GridWidgetService extends RemoteViewsService {
    ArrayList<Task> today;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext(), intent);
    }


    class GridRemoteViewsFactory implements RemoteViewsFactory {

        Context mContext = null;

        public GridRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;

        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            today = todayTask;
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {


            return today.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_grid_view_item);

            String taskName = today.get(position).getTaskName();
            views.setTextViewText(R.id.widget_grid_view_item, taskName);
            Intent fillInIntent = new Intent();
            views.setOnClickFillInIntent(R.id.widget_grid_view_item, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }


}