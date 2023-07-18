package com.example.florist.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.florist.R;
import com.example.florist.model.User;

public class RegisterSelectVerificationActivity extends AppCompatActivity {

    ProgressBar progressBar;
    RelativeLayout layoutPhone, layoutWhatsapp;
    TextView textViewphoneNumber;
    User user;
    Context context;
    String userName, email, password, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_select_verification);

        progressBar = findViewById(R.id.progressBar);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutWhatsapp = findViewById(R.id.layoutWhatsapp);
        textViewphoneNumber = findViewById(R.id.supportingText);
        context = this;


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        progressBar.setMax(1000);
        int currentProgress = 500;
        ObjectAnimator.ofInt(progressBar, "progress", currentProgress)
                .setDuration(1500)
                .start();

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            phoneNumber = extras.getString("phoneNumber");
            userName = extras.getString("userName");
            email = extras.getString("email");
            password = extras.getString("password");
            textViewphoneNumber.setText(phoneNumber);
            Log.d("userName", userName);
            Log.d("email", email);
            Log.d("password", password);
            Log.d("phoneNumber", phoneNumber);
            user = new User(userName, email, password, phoneNumber);
        } else {
            textViewphoneNumber.setText("");
        }




        layoutPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterSelectVerificationActivity.this, RegisterVerificationActivity.class);
                startActivity(intent);
            }
        });



    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("userName", userName);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivityForResult(intent, 0);
        return true;
    }
}