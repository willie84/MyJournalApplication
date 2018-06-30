package com.example.mchwil006.myjournal.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;


/**
 * Created by mchwil006 on 2018/06/29.
 */






    @Entity(tableName = "events")
    public class AddEvent {

        @PrimaryKey(autoGenerate = true)
        private int id;
        private String description;
        private int priority;
        @ColumnInfo(name = "updated_at")
        private Date updatedAt;

        @Ignore
        public AddEvent(String description, int priority, Date updatedAt) {
            this.description = description;
            this.priority = priority;
            this.updatedAt = updatedAt;
        }

        public AddEvent(int id, String description, int priority, Date updatedAt) {
            this.id = id;
            this.description = description;
            this.priority = priority;
            this.updatedAt = updatedAt;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getPriority() {
            return priority;
        }
        public String toString(){return
        "Your event titled: "+description+" that you indicated on: "+updatedAt;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

