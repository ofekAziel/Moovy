package com.example.moovy.models;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.moovy.application.MyApplication;

@Database(entities = {Movie.class}, version = 1)
public abstract class AppLocalDatabase extends RoomDatabase {

    private static AppLocalDatabase instance;
    public abstract MovieDao movieDao();

    public static synchronized AppLocalDatabase getInstance() {
        if (instance == null) {
            instance = Room.databaseBuilder(MyApplication.context, AppLocalDatabase.class,
                    "moovy_databse")
                    .fallbackToDestructiveMigration().build();
        }

        return instance;
    }
}
