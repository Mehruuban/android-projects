package com.mehru.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mehru.chatapp.Utils.FirebaseUtils;
import com.mehru.chatapp.model.UserModel;

public class LoginUserNameActivity extends AppCompatActivity {

    TextView userNameInput;
    AppCompatButton letMeInBtn;
    ProgressBar progressBar;

    String phoneNumber;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_user_name);

        userNameInput = findViewById(R.id.login_userName);
        letMeInBtn = findViewById(R.id.login_complete_profile);
        progressBar = findViewById(R.id.login_progressBar);

        // Get phone number from Intent
        phoneNumber = getIntent().getStringExtra("phone");

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Phone number not found!", Toast.LENGTH_SHORT).show();
            finish(); // Exit activity
            return;
        }

        // Try to fetch existing username (if user already exists)
        getUserName();

        letMeInBtn.setOnClickListener(v -> setUsername());
    }

    void setUsername() {
        String username = userNameInput.getText().toString().trim();

        if (username.isEmpty() || username.length() < 3) {
            userNameInput.setError("User Name must be at least 3 characters");
            return;
        }

        setInProgress(true);

        // If user already exists, just update the username
        if (userModel != null) {
            userModel.setUserName(username);
        } else {
            userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtils.currentUserId());
        }

        FirebaseUtils.currentUserDetails()
                .set(userModel, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginUserNameActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Failed to save username: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    void getUserName() {
        setInProgress(true);
        FirebaseUtils.currentUserDetails()
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        setInProgress(false);
                        if (task.isSuccessful()) {
                            userModel = task.getResult().toObject(UserModel.class);
                            if (userModel != null && userModel.getUserName() != null) {
                                userNameInput.setText(userModel.getUserName());
                            }
                        } else {
                            Toast.makeText(LoginUserNameActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }
}
