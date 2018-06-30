package com.example.mchwil006.myjournal.database;

/**
 * Created by mchwil006 on 2018/06/29.
 */
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface databaseMethods{

    @Query("SELECT * FROM events ORDER BY priority")
    List<AddEvent> loadAllEvents();

    @Insert
    void insertTask(AddEvent taskEntry);


    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(AddEvent taskEntry);

    @Delete
    void deleteTask(AddEvent taskEntry);

    @Query("SELECT * FROM events WHERE id = :id")
    AddEvent loadTaskById(int id);


}
