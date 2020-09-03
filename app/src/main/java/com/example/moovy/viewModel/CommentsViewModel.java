package com.example.moovy.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.moovy.models.Comment;
import com.example.moovy.repositories.CommentsRepository;

import java.util.List;

public class CommentsViewModel extends ViewModel {

    private CommentsRepository commentsRepository;
    private LiveData<List<Comment>> comments;

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

    public void deleteMovieComments(String movieId) {
        commentsRepository.deleteMovieComments(movieId);
    }

    public void deleteComment(String movieId, Comment comment) {
        if (comments != null) {
            commentsRepository.deleteComment(movieId, comment);
        }
    }
}
