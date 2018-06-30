package com.example.mchwil006.myjournal.database;

/**
 * Created by mchwil006 on 2018/06/29.
 */

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

@Database(entities = {AddEvent.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class event extends RoomDatabase {

    private static final String LOG_TAG = event.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "todolist";
    private static event sInstance;

    public static event getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        event.class, event.DATABASE_NAME)
                        // Queries should be done in a separate thread to avoid locking the UI
                        // We will allow this ONLY TEMPORALLY to see that our DB is working
                        .allowMainThreadQueries()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract databaseMethods taskDao();

}
