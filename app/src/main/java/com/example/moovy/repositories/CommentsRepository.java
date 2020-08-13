package com.example.moovy.repositories;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.moovy.models.Comment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommentsRepository {

    private static CommentsRepository instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Comment> comments = new ArrayList<>();

    public static CommentsRepository getInstance() {
        if (instance == null) {
            instance = new CommentsRepository();
        }

        return instance;
    }

    public MutableLiveData<List<Comment>> getComments(String movieId) {
        loadComments(movieId);
        MutableLiveData<List<Comment>> commentsData = new MutableLiveData<>();
        commentsData.setValue(comments);
        return commentsData;
    }

    private void loadComments(String movieId) {
        db.collection("movies").document(movieId)
                .collection("comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                comments.clear();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Comment comment = document.toObject(Comment.class);
                    comment.setId(document.getId());
                    comments.add(comment);
                }
            }
        });
    }

    public void addComment(Comment comment, String movieId) {
        db.collection("movies").document(movieId)
                .collection("comments").add(comment);
    }

    public void deleteComment(String movieId, String commentId) {
        db.collection("movies").document(movieId)
                .collection("comments").document(commentId).delete();
    }
}
