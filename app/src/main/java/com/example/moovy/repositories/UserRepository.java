package com.example.moovy.repositories;

import androidx.lifecycle.MutableLiveData;

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

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }

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
            }
        });
    }

    public void addUser(User user, String userUid) {
        user.setUserUid(userUid);
        user.setAdmin(false);
        db.collection("users").add(user);
    }
}
