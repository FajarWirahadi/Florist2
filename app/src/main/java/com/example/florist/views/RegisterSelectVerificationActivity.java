package com.example.florist.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.florist.R;
import com.example.florist.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterSelectVerificationActivity extends AppCompatActivity {

    ProgressBar progressBar;
    RelativeLayout layoutPhone, layoutEmail;
    TextView textViewphoneNumber, textViewEmail;
    User user;
    Context context;
    private String userName, email, password, phoneNumber, identifier;
    boolean isLogin;
    String myPhoneNumber = "+6285258454453";
    int currentProgress = 0;
    int success = 0;
    int lastProgress = 0;
    private FirebaseAuth mAuth;
    private String mVerificationId, mVerificationCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_select_verification);
        setupUI(findViewById(R.id.parent));
        progressBar = findViewById(R.id.progressBar);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutEmail = findViewById(R.id.layoutEmail);
        textViewphoneNumber = findViewById(R.id.supportingText);
        textViewEmail = findViewById(R.id.textViewEmail);
        mAuth = FirebaseAuth.getInstance();
        context = this;


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        actionBar.setElevation(2);

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            phoneNumber = extras.getString("phoneNumber");
            userName = extras.getString("userName");
            email = extras.getString("email");
            password = extras.getString("password");
            currentProgress = extras.getInt("currentProgress");
            lastProgress = extras.getInt("lastProgress");
            success = extras.getInt("success");
            textViewphoneNumber.setText(phoneNumber);
            textViewEmail.setText(email);
//            Log.d("userName", userName);
//            Log.d("email", email);
//            Log.d("password", password);
//            Log.d("phoneNumber", phoneNumber);
        } else {
            textViewphoneNumber.setText(phoneNumber);
            textViewEmail.setText(email);

        }

//        if (lastProgress == 0) {
//            lastProgress = 330;
//            currentProgress = 660;
//        } else {
//        }
        progressBar.setMax(1000);
//        int currentProgress = 500;
        ObjectAnimator.ofInt(progressBar, "progress", lastProgress, currentProgress)
                .setDuration(1500)
                .start();





        layoutPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterSelectVerificationActivity.this, RegisterVerificationActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("userName", userName);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                intent.putExtra("lastProgress", 500);
                intent.putExtra("currentProgress", 750);
                intent.putExtra("success", success);
                intent.putExtra("identifier", "phoneNumber");
//                intent.putExtra("phoneNumber", myPhoneNumber);
//                intent.putExtra("verificationId", mVerificationId);
//                intent.putExtra("verificationCode", mVerificationCode);
                startActivity(intent);
            }
        });

        layoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterSelectVerificationActivity.this, RegisterVerificationActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("userName", userName);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                intent.putExtra("lastProgress", 500);
                intent.putExtra("currentProgress", 750);
                intent.putExtra("success", success);
                intent.putExtra("identifier", "email");
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
        intent.putExtra("currentProgress", 250);
        intent.putExtra("lastProgress", 500);
        intent.putExtra("success", success);
        startActivityForResult(intent, 0);
        return true;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),0
            );
        }
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(RegisterSelectVerificationActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

}