package com.example.onlinebookreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ImageView userImageView;
    private TextView usernameTextView;
    private Button loginButton, registerButton, savedBooksButton, logoutButton;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();

        userImageView = findViewById(R.id.userImageView);
        usernameTextView = findViewById(R.id.usernameTextView);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        savedBooksButton = findViewById(R.id.savedBooksButton);
        logoutButton = findViewById(R.id.logoutButton);

        loginButton.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, LoginActivity.class)));
        registerButton.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, RegisterActivity.class)));
        savedBooksButton.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, SavedBooksActivity.class)));
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            updateUI(null);
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            NavigationHelper.navigate(AccountActivity.this, item.getItemId());
            return true;
        });

        // Set the selected item in the BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.navigation_account);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            userImageView.setVisibility(View.VISIBLE);
            usernameTextView.setVisibility(View.VISIBLE);
            usernameTextView.setText(user.getEmail());  // Assuming email as username
            loginButton.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            savedBooksButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            userImageView.setVisibility(View.GONE);
            usernameTextView.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
            savedBooksButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        }
    }
}
