package com.example.moovy.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Query("select * from users_table where userUid = :userUid")
    LiveData<List<User>> getCurrentUser(String userUid);

    @Insert
    void add(User user);
}
