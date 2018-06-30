package com.example.mchwil006.myjournal;

/**
 * Created by mchwil006 on 2018/06/29.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.example.mchwil006.myjournal.database.AddEvent;
import com.example.mchwil006.myjournal.database.event;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;


public class ViewActivities extends AppCompatActivity implements eventAdapter.ItemClickListener {

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private eventAdapter mAdapter;

    // COMPLETED (1) Create AppDatabase member variable for the Database
    private event mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activities);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerViewTasks);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new eventAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // COMPLETED (3) get the position from the viewHolder parameter
                        int position = viewHolder.getAdapterPosition();
                        List<AddEvent> tasks = mAdapter.getTasks();
                        mDb.taskDao().deleteTask(tasks.get(position));
                        retrieveTasks();
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddActivity
                Intent addTaskIntent = new Intent(ViewActivities.this, AddActivity.class);
                startActivity(addTaskIntent);
            }
        });

        // COMPLETED (2) Initialize member variable for the data base
        mDb = event.getInstance(getApplicationContext());
    }

    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddActivity,
     * so this re-queries the database data for any changes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.setTasks(mDb.taskDao().loadAllEvents());
    }


    private void retrieveTasks() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<AddEvent> tasks = mDb.taskDao().loadAllEvents();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setTasks(tasks);
                    }
                });
            }
        });
    }
    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent

        Intent intent = new Intent(ViewActivities.this, AddActivity.class);
        intent.putExtra(AddActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }
}