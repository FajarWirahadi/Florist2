package com.example.florist.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.florist.R;
import com.example.florist.model.User;
import com.example.florist.views.homepage.HomepageActivity;
import com.example.florist.views.splashscreen.OnboardingActivity;
import com.example.florist.views.splashscreen.SplashScreen;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    TextView textViewShowPassword,textViewRegister;
    EditText editTextEmail, editTextPassword;
    Button btnLogin;
    int lastProgress, currentProgress;
    private boolean isPasswordVisible, isValid;
    private String emailPhoneNumber, password, mVerificationCode, mVerificationId;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textViewShowPassword = findViewById(R.id.textViewShowPassword);
        textViewRegister = findViewById(R.id.textViewRegister);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        mAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        ProgressDialog progressDialog = new ProgressDialog(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setElevation(0);

        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Loging to your account");
        progressDialog.setCancelable(false);

        textViewShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePassVisibility();
            }
        });
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, RegisterPhoneActivity.class);
                Login.this.startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailPhoneNumber = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                CollectionReference usersRef = db.collection("Users");
                String regex = "^(.+)@(.+)$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(emailPhoneNumber);
                if (matcher.matches()) {
                    Intent intent = new Intent(Login.this, HomepageActivity.class);
                    LoginIdentifier("email", emailPhoneNumber, usersRef, intent, progressDialog);
                } else if (emailPhoneNumber.matches("^[0-9]+$")) {
                    emailPhoneNumber = emailPhoneNumber.substring(1);
                    Intent intent = new Intent(Login.this, HomepageActivity.class);
//                    intent.putExtra("phoneNumber", "+62"+emailPhoneNumber);
//                    intent.putExtra("identifier", "2_phoneNumber");
                    LoginIdentifier("phoneNumber","+62" +  emailPhoneNumber, usersRef, intent, progressDialog);
                } else {
                    Toast.makeText(Login.this, "Account not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void togglePassVisibility(){
        if(isPasswordVisible) {
            String pass = editTextPassword.getText().toString();
            editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editTextPassword.setText(pass);
            editTextPassword.setSelection(pass.length());
        }else {
            String pass = editTextPassword.getText().toString();
            editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            editTextPassword.setText(pass);
            editTextPassword.setSelection(pass.length());
        }
        isPasswordVisible = !isPasswordVisible;
    }

    private void LoginIdentifier(String identifier, String emailPhoneNumber, CollectionReference usersRef,
                                 Intent intent, ProgressDialog progressDialog) {
        Log.d("EmailPhoneNumber", emailPhoneNumber);
        usersRef.whereEqualTo(identifier, emailPhoneNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("document", document.getId() + " => " + document.getData());
//                                Log.d("Passwword", String.valueOf(document.get("password")));
                                if (password.equals(String.valueOf(document.get("password"))) &&
                                        emailPhoneNumber.equals(String.valueOf(document.get(identifier)))) {
                                    final String email = String.valueOf(document.get("email"));
                                    mAuth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    progressDialog.show();
                                                    final Handler handler = new Handler();
                                                    final int delay = 1000; //milisecond
                                                    boolean stop = false;

                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (user!=null){
                                                                user.reload();
                                                                if (user.isEmailVerified()){
                                                                    Toast.makeText(Login.this, "Current User is Exist - Login Success", Toast.LENGTH_SHORT).show();
                                                                    progressDialog.dismiss();
                                                                }
                                                            }
                                                            handler.postDelayed(this, delay);
                                                        }
                                                    }, delay);
                                                    handler.removeCallbacksAndMessages(null);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Login.this, "Login with email failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        }
                    }
                });

    }
    private void verifyPhoneNumberWithCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        mVerificationCode = code;
        Log.d("Verification Code", mVerificationCode);
//        if (mVerificationCode != null) {
//            autoGenerateCode(mVerificationCode);
//        }
        Log.d("VerificationCode", mVerificationCode);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
}