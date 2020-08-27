package com.example.moovy.repositories;

import android.os.AsyncTask;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.moovy.models.AppLocalDatabase;
import com.example.moovy.models.Comment;
import com.example.moovy.models.CommentDao;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CommentsRepository {

    private static CommentsRepository instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CommentDao commentDao = AppLocalDatabase.getInstance().commentDao();

    public static CommentsRepository getInstance() {
        if (instance == null) {
            instance = new CommentsRepository();
        }

        return instance;
    }

    public LiveData<List<Comment>> getComments(String movieId) {
        LiveData<List<Comment>> commentsLiveData = commentDao.getAll(movieId);
        loadCommentsFromFirebase(movieId);
        return commentsLiveData;
    }

    private void loadCommentsFromFirebase(String movieId) {
        db.collection("movies").document(movieId).collection("comments").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Comment comment = document.toObject(Comment.class);
                    comment.setDocumentId(document.getId());
                    new AddCommentAsyncTask(commentDao).execute(comment);
                }
            }
        });
    }

    public void addComment(Comment comment, String movieId) {
        new AddCommentAsyncTask(commentDao).execute(comment);
        addCommentToFirebase(comment, movieId);
    }

    public void addCommentToFirebase(Comment comment, String movieId) {
        db.collection("movies").document(movieId)
                .collection("comments").add(comment);
    }

    public void deleteComment(String movieId, Comment comment) {
        new DeleteCommentAsyncTask(commentDao).execute(comment);
        deleteCommentFromFirebase(movieId, comment.getDocumentId());
    }

    public void deleteCommentFromFirebase(String movieId, String commentId) {
        db.collection("movies").document(movieId)
                .collection("comments").document(commentId).delete();
    }

    private static class AddCommentAsyncTask extends AsyncTask<Comment, Void, Void> {
        private CommentDao commentDao;

        private AddCommentAsyncTask(CommentDao commentDao) {
            this.commentDao = commentDao;
        }

        @Override
        protected Void doInBackground(Comment... comments) {
            commentDao.add(comments[0]);
            return null;
        }
    }

    private static class DeleteCommentAsyncTask extends AsyncTask<Comment, Void, Void> {
        private CommentDao commentDao;

        private DeleteCommentAsyncTask(CommentDao commentDao) {
            this.commentDao = commentDao;
        }

        @Override
        protected Void doInBackground(Comment... comments) {
            commentDao.delete(comments[0]);
            return null;
        }
    }

    private static class DeleteAllCommentsAsyncTask extends AsyncTask<Void, Void, Void> {
        private CommentDao commentDao;

        private DeleteAllCommentsAsyncTask(CommentDao commentDao) {
            this.commentDao = commentDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            commentDao.deleteAll();
            return null;
        }
    }
}
