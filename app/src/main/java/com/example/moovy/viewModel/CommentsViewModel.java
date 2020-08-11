package com.example.moovy.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moovy.models.Comment;
import com.example.moovy.repositories.CommentsRepository;

import java.util.List;

public class CommentsViewModel extends ViewModel {

    private CommentsRepository commentsRepository;
    private MutableLiveData<List<Comment>> comments;

    public void init(Context context) {
        commentsRepository = CommentsRepository.getInstance(context);
    }

    public LiveData<List<Comment>> getComments(String movieId) {
        if (comments == null) {
            comments = commentsRepository.getComments(movieId);
        }

        return comments;
    }

    public void addComment(Comment comment, String movieId) {
        if (comments != null) {
            commentsRepository.addComment(comment, movieId);
        }
    }
}
