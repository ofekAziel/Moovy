package com.example.moovy.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moovy.models.Comment;
import com.example.moovy.repositories.CommentsRepository;

import java.util.List;

public class CommentsViewModel extends ViewModel {

    private CommentsRepository commentsRepository;
    private MutableLiveData<List<Comment>> comments;

    public void init() {
        commentsRepository = CommentsRepository.getInstance();
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

    public void deleteComment(String movieId, String commentId) {
        if (comments != null) {
            commentsRepository.deleteComment(movieId, commentId);
        }
    }
}
