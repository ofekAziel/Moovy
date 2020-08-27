package com.example.moovy.models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CommentDao {

    @Query("select * from comments_table where movieId = :movieId order by date desc")
    LiveData<List<Comment>> getAll(String movieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void add(Comment comment);

    @Delete
    void delete(Comment comment);

    @Query("delete from comments_table where movieId = :movieId")
    void deleteMovieComments(String movieId);

    @Query("delete from comments_table")
    void deleteAll();
}
