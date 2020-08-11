package com.example.moovy.repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.moovy.UserDataLoadListener;
import com.example.moovy.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRepository {

    private static UserRepository instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private User user;
    private MutableLiveData<User> userData = new MutableLiveData<>();
    private static Context mContext;
    private static UserDataLoadListener userDataLoadListener;

    public static UserRepository getInstance(Context context) {
        mContext = context;

        if (instance == null) {
            instance = new UserRepository();
        }

        userDataLoadListener = (UserDataLoadListener) mContext;
        return instance;
    }

    public MutableLiveData<User> getUser() {
        loadUser();
        return userData;
    }

    private void loadUser() {
        String currentUserUid = firebaseAuth.getCurrentUser().getUid();
        db.collection("users").whereEqualTo("userUid", currentUserUid).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                userData.setValue(user);
                userDataLoadListener.onUserLoad();
            }
        });
    }

    public void addUser(User user, String userUid) {
        user.setUserUid(userUid);
        user.setAdmin(false);
        db.collection("users").add(user);
    }
}
