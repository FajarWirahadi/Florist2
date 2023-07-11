package com.example.florist.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.florist.R;

public class RegisterSelectVerificationActivity extends AppCompatActivity {

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_select_verification);

        progressBar = findViewById(R.id.progressBar);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        progressBar.setMax(1000);
        int currentProgress = 500;
        ObjectAnimator.ofInt(progressBar, "progress", currentProgress)
                .setDuration(1500)
                .start();



    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), RegisterSelectVerificationActivity.class);
        startActivityForResult(intent, 0);
        return true;
    }
}