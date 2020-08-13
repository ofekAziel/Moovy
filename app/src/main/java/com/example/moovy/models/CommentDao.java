package com.example.moovy.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CommentDao {

    @Query("select * from comments_table")
    LiveData<List<Comment>> getAll();

    @Insert
    void add(Comment comment);

    @Delete
    void delete(Comment comment);
}
