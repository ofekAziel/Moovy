package com.example.moovy.repositories;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moovy.models.AppLocalDatabase;
import com.example.moovy.models.Movie;
import com.example.moovy.models.MovieDao;
import com.example.moovy.models.User;
import com.example.moovy.models.UserDao;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class UserRepository {

    private static UserRepository instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private UserDao userDao = AppLocalDatabase.getInstance().userDao();
    private User user;
    private MutableLiveData<User> userData = new MutableLiveData<>();

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }

        return instance;
    }

    public LiveData<List<User>> getUser() {
        String currentUserUid = firebaseAuth.getCurrentUser().getUid();
        LiveData<List<User>> userLiveData = userDao.getCurrentUser(currentUserUid);
        loadUserFromFirebase(currentUserUid);
        return userLiveData;
    }

    private void loadUserFromFirebase(String currentUserUid) {
        db.collection("users").whereEqualTo("userUid", currentUserUid).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                    user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                    userData.setValue(user);
                }
            }
        });
    }

    public void addUser(User user, String userUid) {
        user.setUserUid(userUid);
        user.setAdmin(false);
        new AddUserAsyncTask(userDao).execute(user);
        addUserToFirebase(user);
    }

    private void addUserToFirebase(User user) {
        db.collection("users").add(user);
    }

    private static class AddUserAsyncTask extends AsyncTask<User, Void, Void> {
        private UserDao userDao;

        private AddUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }
        @Override
        protected Void doInBackground(User... users) {
            userDao.add(users[0]);
            return null;
        }
    }
}
