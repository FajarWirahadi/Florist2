package com.example.florist.views.homepage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.florist.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomepageActivity extends AppCompatActivity {

    Button btnSignOut;
    TextView txtViewUsername, txtViewEmail, txtViewUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage2);
        btnSignOut = findViewById(R.id.btnSignOut);
        txtViewUsername = findViewById(R.id.txtViewUsername);
        txtViewEmail = findViewById(R.id.txtViewEmail);
        txtViewUserId = findViewById(R.id.txtViewUserId);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            txtViewUsername.setText(user.getDisplayName());
            txtViewEmail.setText(user.getEmail());

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();
            Log.d("IsEmailVerified", String.valueOf(emailVerified));

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            txtViewUserId.setText(user.getUid());

            btnSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    HomepageActivity.this.finishAffinity();
                }
            });
        }
    }
}