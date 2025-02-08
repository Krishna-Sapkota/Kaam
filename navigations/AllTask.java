package com.project.krishna.kaam.navigations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.project.krishna.kaam.AnalyticsApplication;
import com.project.krishna.kaam.EditTask;
import com.project.krishna.kaam.HideShowScrollListener;
import com.project.krishna.kaam.MainActivity;
import com.project.krishna.kaam.R;
import com.project.krishna.kaam.alram.AlramUtil;
import com.project.krishna.kaam.datamodel.AllTaskAdapter;
import com.project.krishna.kaam.datamodel.Task;
import com.project.krishna.kaam.datamodel.TaskContract;
import com.project.krishna.kaam.util.DateConversion;
import com.project.krishna.kaam.widget.WidgetUpdateService;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static android.support.v7.widget.helper.ItemTouchHelper.Callback.getDefaultUIUtil;

import com.project.krishna.kaam.AnalyticsApplication;

public class AllTask extends Fragment implements AllTaskAdapter.TaskClickListener, TaskItemTouchHelper.RecyclerItemTouchHelperListener, LoaderManager.LoaderCallbacks<Cursor> {
    public static final int TASK_LOADER = 5;
    public static final String TASK_TO_BE_EDITED = "task_edit";
    private static final String TASK_LIST_STATE = "list_state";
    RecyclerView allTaskList;
    Context context;
    private CoordinatorLayout coordinatorLayout;
    Cursor cursor;
    AllTaskAdapter taskAdapter;
    ArrayList<Task> taskArrayList;
    ImageView deleteIcon;
    ImageView editIcon;
    View view;
    String fragmentBy;
    public static String FRAGMENT_CREATED_BY = "fragby";
    private Bundle bundle;
    boolean hour12format = false;
    private Parcelable scrollState;
    Bundle state;

    private AdView mAdView;
    Tracker mTracker;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_all_task, container, false);
        MobileAds.initialize(getActivity(),
                getString(R.string.test_ad_id));
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();


        context = getContext();
        state = new Bundle();


        allTaskList = (RecyclerView) view.findViewById(R.id.rv_task_list);
        coordinatorLayout = view.findViewById(R.id.coordinator_layout);


        mAdView = view.findViewById(R.id.adView);


        AdRequest adRequest = new AdRequest.Builder().
                build();
        mAdView.loadAd(adRequest);


        LinearLayoutManager layout = new LinearLayoutManager(context);
        allTaskList.setLayoutManager(layout);
        fragmentBy = getTag();

        Log.i("SCREEN", "screen name: " + fragmentBy);
        mTracker.setScreenName(getString(R.string.screen_analytics) + fragmentBy);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        bundle = new Bundle();
        bundle.putString(FRAGMENT_CREATED_BY, fragmentBy);
        String hour12 = getString(R.string.hour_12);
        if (getTimePreference().equals(hour12)) {
            hour12format = true;
        }


        getLoaderManager().initLoader(TASK_LOADER, bundle, AllTask.this);


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new TaskItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(allTaskList);
        if (!fragmentBy.equals(getString(R.string.screen_com))) {


            ItemTouchHelper.SimpleCallback itemTouchHelperCallbackRight = new TaskItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
            new ItemTouchHelper(itemTouchHelperCallbackRight).attachToRecyclerView(allTaskList);
        }


        final BottomNavigationView navigation = getActivity().findViewById(R.id.navigation);
        final FloatingActionButton fab = getActivity().findViewById(R.id.fab);

        allTaskList.addOnScrollListener(new HideShowScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //super.onScrollStateChanged(recyclerView, newState);
                Log.i("SC", "scroll vertically down" + allTaskList.canScrollVertically(1));
                Log.i("SC", "scroll vertically up" + allTaskList.canScrollVertically(-1));
                if (!allTaskList.canScrollVertically(1) && !allTaskList.canScrollVertically(-1)) {
                    onShow();
                }
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && !allTaskList.canScrollVertically(1) && !allTaskList.canScrollVertically(-1)) {
                    onHide();
                }

            }

            @Override
            public void onHide() {

                fab.animate().setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(0).scaleY(0);
                // do your hiding animation here
                //navigation.setVisibility(View.GONE);
                navigation.animate()
                        .alpha(0f)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                navigation.setVisibility(View.GONE);
                            }
                        });

            }

            @Override
            public void onShow() {
                fab.animate().setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(1).scaleY(1);
                // do your showing animation here
                navigation.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                navigation.setVisibility(View.VISIBLE);
                            }
                        });

            }
        });


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TASK_LIST_STATE, allTaskList.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            scrollState = savedInstanceState.getParcelable(TASK_LIST_STATE);
            allTaskList.getLayoutManager().onRestoreInstanceState(scrollState);
            Log.i("TAG", "restored");

        }

    }

    public String getName() {
        return fragmentBy;
    }

    private List<Task> builtTaskList(Cursor cursor) {
        List<Task> taskList = new ArrayList<>();
        List<Task> todayList = new ArrayList<>();
        cursor.moveToFirst();
        boolean todayTask = false;

        while (!cursor.isAfterLast()) {
            Task task = new Task();
            boolean completed = false;
            int idIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_ID);
            int id = cursor.getInt(idIndex);
            int taskIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_NAME);
            String tName = cursor.getString(taskIndex);
            int dateTimeIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_DATE_TIME);
            long date = cursor.getLong(dateTimeIndex);
            int cColoumn = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_TASK_COMPLETED);
            int com = cursor.getInt(cColoumn);
            if (com != 0) completed = true;
            task.setId(id);
            task.setTaskName(tName);
            task.setDate(date);
            task.setCompleted(completed);

            if (DateConversion.isToday(new DateTime(task.getDate())) && !task.isCompleted()) {
                todayList.add(task);
            }


            if (fragmentBy.equals(getString(R.string.screen_com)) && task.isCompleted()) {
                taskList.add(task);
            }

            if (fragmentBy.equals(getString(R.string.screen_today))) {
                if (DateConversion.isToday(new DateTime(task.getDate())) && !task.isCompleted()) {
                    todayTask = true;
                    taskList.add(task);
                }
            }


            if (fragmentBy.equals(getString(R.string.screen_all)) && !task.isCompleted()) {
                taskList.add(task);

            }
            cursor.moveToNext();
        }

        //cursor.close();
        WidgetUpdateService.startBakingService(getContext(), (ArrayList<Task>) todayList);

        return taskList;
    }

    @Override
    public void onThumnailClick(int clickedIndex) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.recycler_layout);
        // constraintLayout.setSelected(true);
        showBottomSheetDialog(clickedIndex);

    }

    private String getTimePreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sharedPreferences.getString(getString(R.string.time_format), getString(R.string.default_time_format));
    }


    public RecyclerView getAllTaskList() {
        return allTaskList;
    }


    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((AllTaskAdapter.TaskViewHolder) viewHolder).viewForeground;

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
        editIcon = view.findViewById(R.id.edit_icon);
        deleteIcon = view.findViewById(R.id.delete_icon);


        if (dX > 0) {
            Log.i("DX", "value x" + dX + " value " + dY);

            //editIcon.setVisibility(View.INVISIBLE);
            deleteIcon.setVisibility(View.GONE);
            editIcon.setVisibility(View.VISIBLE);

        }
        if (dX < 0) {
            editIcon.setVisibility(View.GONE);
            deleteIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof AllTaskAdapter.TaskViewHolder && direction == ItemTouchHelper.LEFT) {
            // get the removed item name to display it in snack bar
            // String name = cursor

            // backup of removed item for undo purpose
            final Task deletedItem = builtTask(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            taskAdapter.removeItem(viewHolder.getAdapterPosition());
            AlramUtil.cancelAlram(getContext(), deletedItem.getId(), deletedItem.getTaskName());
            // showing snack bar with Undo option
            Log.i("DELETED", "ID OF DELETE" + deletedItem.getId());
            CoordinatorLayout layout = getActivity().findViewById(R.id.container);
            Snackbar snackbar = Snackbar
                    .make(layout, getString(R.string.task_delete_msg), Snackbar.LENGTH_LONG);
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if (taskAdapter.getItemCount() == 0) {
                        getActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);
                    }
                }
            });
            snackbar.setAction(getString(R.string.undo_message), new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //undo is selected, restore the deleted item
                    taskAdapter.restoreItem(deletedItem, deletedIndex);
                    String date = DateConversion.fromMiliToDate(new DateTime((deletedItem.getDate())));
                    String time = DateConversion.toAMPM(new DateTime(deletedItem.getDate()));
                    Log.i("DELETED", "ID OF DELETE" + deletedItem.getId());
                    Log.i("DELETED", "date" + date + " Time" + time);
                    AlramUtil.setAlram(getContext(), deletedItem.getDate(), deletedItem.getId(), deletedItem.getTaskName());
                    getActivity().findViewById(R.id.navigation).setVisibility(View.VISIBLE);

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            getActivity().findViewById(R.id.navigation).setVisibility(View.INVISIBLE);

            snackbar.show();

        }

        if (viewHolder instanceof AllTaskAdapter.TaskViewHolder && direction == ItemTouchHelper.RIGHT) {
            Intent editTaskActivity = new Intent(context, EditTask.class);
            Task editItem = builtTask(viewHolder.getAdapterPosition());
            Bundle taskObject = new Bundle();
            taskObject.putParcelable(TASK_TO_BE_EDITED, editItem);
            editTaskActivity.putExtras(taskObject);
            //startActivityForResult(editTaskActivity,1);
            startActivity(editTaskActivity);

        }
    }


    private Task builtTask(int adapterPosition) {
        Task task;
        task = taskArrayList.get(adapterPosition);
        return task;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        Uri uri = TaskContract.TaskEntry.CONTENT_URI;
        String[] mProjection = new String[]{
                TaskContract.TaskEntry.COLUMN_TASK_ID,
                TaskContract.TaskEntry.COLUMN_TASK_NAME,
                TaskContract.TaskEntry.COLUMN_TASK_DATE_TIME,
                TaskContract.TaskEntry.COLUMN_TASK_COMPLETED};
        String selection = null;
        String[] selectionArgs = null;

      /*  if(bundle.getString(FRAGMENT_CREATED_BY)=="Completed"){
            selection=TaskContract.TaskEntry.COLUMN_TASK_COMPLETED+" =? ";
            selectionArgs= new String[]{"1"};
        }
        if(bundle.getString(FRAGMENT_CREATED_BY).equals("All")){
            selection=TaskContract.TaskEntry.COLUMN_TASK_COMPLETED+" =? ";
            selectionArgs=new String[]{"0"};
        }*/

        String sortOrder = null;
        cursor = getContext().getContentResolver().query(uri, mProjection, selection, selectionArgs,
                sortOrder);
        cursor.moveToFirst();

        switch (loaderID) {
            case TASK_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        uri,        // Table to query or contentprovider uri
                        mProjection,     // Projection to return
                        selection,            // No selection clause
                        selectionArgs,            // No selection arguments
                        sortOrder             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.cursor = data;
        taskArrayList = (ArrayList<Task>) builtTaskList(cursor);

        taskAdapter = new AllTaskAdapter(context, taskArrayList, this, hour12format);
        allTaskList.setAdapter(taskAdapter);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public void showBottomSheetDialog(final int clickedIndex) {
        BottomSheetTaskFragment.MyDialogCloseListener closeListener = new BottomSheetTaskFragment.MyDialogCloseListener() {
            @Override
            public void handleDialogClose(DialogInterface dialog, boolean dismissed) {
                if (!dismissed)
                    reload(clickedIndex);
            }
        };
        Task selectedTask = taskArrayList.get(clickedIndex);

        BottomSheetTaskFragment bottomSheetFragment = new BottomSheetTaskFragment();
        bottomSheetFragment.setSelectedTask(selectedTask);
        bottomSheetFragment.DismissListener(closeListener);

        bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());


    }

    private void reload(int clickedIndex) {
        taskArrayList.remove(clickedIndex);
        taskAdapter.notifyItemRemoved(clickedIndex);

    }


}

