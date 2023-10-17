package com.example.florist.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.florist.R;
import com.example.florist.model.User;
import com.example.florist.views.homepage.HomepageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RegisterVerificationActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText editText1, editText2, editText3, editText4, editText5, editText6;
    TextView countDownTimer, textViewChange, textViewPhoneNumber, emailUser, idUser;
    private TextWatcher txtCode;
    private String userName, email, password, phoneNumber, identifier;
    int currentProgress = 0;
    int lastProgress = 0;
    int success = 0;
    int selectedETPosition = 0;
    int length = 0;
    private FirebaseAuth mAuth;
    private FirebaseAuthSettings firebaseAuthSettings;
    private String mVerificationId, mVerificationCode, userId;
    private String getmVerificationCode1 = "111111";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private static final String TAG = "PhoneAuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_verification);

        progressBar = findViewById(R.id.progressBar);
        textViewPhoneNumber = findViewById(R.id.textViewPhoneNumber);
        editText1 = findViewById(R.id.editTxt1);
        editText2 = findViewById(R.id.editTxt2);
        editText3 = findViewById(R.id.editTxt3);
        editText4 = findViewById(R.id.editTxt4);
        editText5 = findViewById(R.id.editTxt5);
        editText6 = findViewById(R.id.editTxt6);
        countDownTimer = findViewById(R.id.countDownTimer);
        emailUser = findViewById(R.id.emailUser);
        idUser = findViewById(R.id.idUser);
        textViewChange = findViewById(R.id.textViewChange);
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthSettings = mAuth.getFirebaseAuthSettings();
        FirebaseFirestore db = FirebaseFirestore.getInstance();



        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        actionBar.setElevation(2);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            phoneNumber = extras.getString("phoneNumber");
            userName = extras.getString("userName");
            email = extras.getString("email");
            password = extras.getString("password");
            lastProgress = extras.getInt("lastProgress");
            currentProgress = extras.getInt("currentProgress");
            success = extras.getInt("success");
            identifier = extras.getString("identifier");
        }
        progressBar.setMax(1000);
        int currentProgress = 750;
        ObjectAnimator.ofInt(progressBar, "progress", lastProgress, currentProgress)
                .setDuration(1500)
                .start();

        countDownTimer();

        countDownTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(RegisterVerificationActivity.this, "ResendCode", Toast.LENGTH_SHORT).show();
                countDownTimer();
                resendVerificationCode(phoneNumber, mResendToken);
            }
        });

        textViewChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterVerificationActivity.this, RegisterPhoneActivity.class);
                if(phoneNumber != null) {
                intent.putExtra("phoneNumber", phoneNumber);
                }
                startActivity(intent);
            }
        });

        editText1.addTextChangedListener(new GenericTextWatcher(editText1));
        editText2.addTextChangedListener(new GenericTextWatcher(editText2));
        editText3.addTextChangedListener(new GenericTextWatcher(editText3));
        editText4.addTextChangedListener(new GenericTextWatcher(editText4));
        editText5.addTextChangedListener(new GenericTextWatcher(editText5));
        editText6.addTextChangedListener(new GenericTextWatcher(editText6));

        //By default open keyboard on first edittext
        showKeyboard(editText1);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            emailUser.setText(user.getDisplayName());
            idUser.setText(user.getEmail());

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();
            Log.d("IsEmailVerified", String.valueOf(emailVerified));

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
//            txtViewUserId.setText(user.getUid());
        }


//        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            @Override
//            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                mVerificationCode = phoneAuthCredential.getSmsCode();
//                Log.d("Verification Completed", "Verification Completed");
//                if (mVerificationCode!=null) {
//                    verifyPhoneNumberWithCode(mVerificationCode);
//                }
//            }
//
//            @Override
//            public void onVerificationFailed(@NonNull FirebaseException e) {
//                Log.d("Verification Failed", "Verification Failed");
////                Toast.makeText(RegisterSelectVerificationActivity.this, "Verification Failed", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                super.onCodeSent(s, forceResendingToken);
//                Log.d("onCodeSent", s);
//                mVerificationId = s;
//                mResendToken = forceResendingToken;
//            }
//        };
//        Log.d("Identifier", identifier);
        //IMPORTANT FUNCTION
        identifier = "email";
        if (identifier.equals("phoneNumber") ||
        identifier.substring(2).equals("phoneNumber"))
        {
        textViewPhoneNumber.setText(phoneNumber);
        startPhoneNumberVerification(phoneNumber);
        }
        // Signup via email
        else if (identifier.equals("email")){
            signUpWithEmailAndPassword();
//            String email = "sigamers007@gmail.com";
//            FirebaseDynamicLinks.getInstance()

//                    .getDynamicLink(getIntent())
//                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
//                        @Override
//                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
//                            Log.d("Dynamic link", "Call dynamic link success");
//
//                            Uri deepLink = null;
//                            if (pendingDynamicLinkData != null) {
//                                deepLink = pendingDynamicLinkData.getLink();
//                            }
//                            Log.d("Dynamic link", "callGetDynamicLink, deepLink " + deepLink);
//                        }
//                    }).addOnFailureListener(this, new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w("Dynamic link", "getDynamicLink:onFailure", e);
//                            e.getMessage();
//                        }
//                    });
//
//
//            ActionCodeSettings actionCodeSettings =
//                    ActionCodeSettings.newBuilder()
//                            // URL you want to redirect back to. The domain (www.example.com) for this
//                            // URL must be whitelisted in the Firebase Console.
//                            .setUrl("florist-9a390.web.app")
//                            // This must be true
//                            .setHandleCodeInApp(true)
////                            .setIOSBundleId("com.example.ios")
//                            .setAndroidPackageName(
//                                    "com.example.florist",
//                                    true, /* installIfNotAvailable */
//                                    "12"    /* minimumVersion */)
//                            .setDynamicLinkDomain("https://setaman.page.link/flr")
//                            .build();
//
//            mAuth.sendSignInLinkToEmail(email, actionCodeSettings)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Log.d("Email verification", "Email Sent.");
//                            }
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("Email verification", "Something error");
//                        }
//                    });
//            Intent intent = getIntent();
//            if (intent == null || intent.getData() == null) return;
//            String emailLink = intent.getData().toString();
//
//            // Confirm the link is a sign-in with email link.
//            if (mAuth.isSignInWithEmailLink(emailLink)) {
//
//                // The client SDK will parse the code from the link for you.
//                mAuth.signInWithEmailLink(email, emailLink)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d(TAG, "Successfully signed in with email link!");
//                                    AuthResult result = task.getResult();
//                                    userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                                    User user = new User("wirahadi", email, "Wirahadi1", "6287829515856", true);
//                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                    startActivity(new Intent(RegisterVerificationActivity.this, RegisterSuccessActivity.class));
//                                    // You can access the new user via result.getUser()
//                                    // Additional user info profile *not* available via:
//                                    // result.getAdditionalUserInfo().getProfile() == null
//                                    // You can check if the user is new or existing:
//                                    // result.getAdditionalUserInfo().isNewUser()
//                                } else {
//                                    Log.e(TAG, "Error signing in with email link", task.getException());
//                                }
//                            }
//                        });
//            }
        }
//        autoGenerateCode(mVerificationCode);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), RegisterSelectVerificationActivity.class);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("userName", userName);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("lastProgress", 750);
        intent.putExtra("currentProgress", 500);
        intent.putExtra("success", success);
        startActivityForResult(intent, 0);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                Intent intent = new Intent(RegisterVerificationActivity.this, HomepageActivity.class);
                startActivity(intent);
                Log.d("OnResume", "OnResume");
            }
        }
    }

    private void signUpWithEmailAndPassword(){
        String email = "sigamers007@gmail.com";
        String password = "kills456dt32";
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            User user = new User("wirahadi123", email, password, "0000", true);
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Users").document(userId).set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(RegisterVerificationActivity.this, "Create account with email success!", Toast.LENGTH_SHORT).show();
                                    sendVerificationEmail();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterVerificationActivity.this, "Create account with email failed!",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

    private void doSignInWithEmailAndPassword() {
        String email = "sigamers007@gmail.com";
        String password = "kills456dt32";
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Loging to your account");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                        Toast.makeText(RegisterVerificationActivity.this, "User SignIn Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterVerificationActivity.this, HomepageActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterVerificationActivity.this, "PLease verify your email first", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(RegisterVerificationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDynamicLinkFromFireBase() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Log.i("SignInActivity","We have a Dynamic Link");
                        Uri deepLink = null;

                        if(pendingDynamicLinkData!=null){
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if(deepLink!=null){
                            Log.i("SignInActivity", "Here the Dynamic link \n" + deepLink.toString());

                            String email = deepLink.getQueryParameter("email");
                            String password = deepLink.getQueryParameter("password");

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterVerificationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void sendVerificationEmail() {
        Log.d("Email Verification", "Email Verification");
        FirebaseAuth.getInstance().getCurrentUser()
                .sendEmailVerification()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RegisterVerificationActivity.this, "Email Verification link sent to your email.", Toast.LENGTH_SHORT).show();
                            doSignInWithEmailAndPassword();
                        if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                        }
//                        Intent intent = new Intent(RegisterVerificationActivity.this, HomepageActivity.class);
//                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterVerificationActivity.this, "Email not sent", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void countDownTimer() {

        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                countDownTimer.setClickable(false);
                countDownTimer.setTextSize(24);
                countDownTimer.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(l) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(l) % TimeUnit.MINUTES.toSeconds(1)));
            }

            @Override
            public void onFinish() {
                countDownTimer.setText("Kirim ulang kode OTP");
                countDownTimer.setTextSize(14);
                countDownTimer.setClickable(true);
            }
        }.start();
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, mVerificationCode);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        mVerificationCode = code;
        Log.d("Verification Code", mVerificationCode);
        if (mVerificationCode != null) {
        autoGenerateCode(mVerificationCode);
        }
        Log.d("VerificationCode", mVerificationCode);
        signInWithPhoneAuthCredential(credential);
    }

//    private void createUserFirestore(User user) {
//        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        user = new User(userName, email, password, phoneNumber, true);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("Users").document(userId).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Toast.makeText(RegisterVerificationActivity.this, "Success", Toast.LENGTH_SHORT).show();
//            }
//        });
////                    verifyPhoneNumberWithCode(code);
////                    Intent intent = new Intent(RegisterVerificationActivity.this, RegisterSuccessActivity.class);
////                    startActivity(intent);
//    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            if (identifier.equals("2_phoneNumber")){
                                startActivity(new Intent(RegisterVerificationActivity.this, HomepageActivity.class));
                            } else {
                            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            User user = new User(userName, email, password, phoneNumber, true);
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Users").document(userId).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Intent intent = new Intent(RegisterVerificationActivity.this, RegisterSuccessActivity.class);
                                    startActivity(intent);
                                }
                            });
                            Log.d(TAG, "signInWithCredential:success");
                            }
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    public void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
//        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(myPhoneNumber, mVerificationCode);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .setForceResendingToken(token)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void autoGenerateCode(String code) {
        editText1.setText(String.valueOf(code.charAt(0)));
        editText2.setText(String.valueOf(code.charAt(1)));
        editText3.setText(String.valueOf(code.charAt(2)));
        editText4.setText(String.valueOf(code.charAt(3)));
        editText5.setText(String.valueOf(code.charAt(4)));
        editText6.setText(String.valueOf(code.charAt(5)));
    }

    private void showKeyboard(EditText otpET) {
        otpET.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otpET, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL){
            Log.d("Delete Key", "Executed");
            if (selectedETPosition == 6) {
                if (editText6.length() == 1 && length == 6) {
                    editText6.setText("");
                    selectedETPosition = 5;
                    length = 5;
                    editText6.requestFocus();
                }
//                selectedETPosition = 4;
//                editText5.requestFocus();
            }
            if (selectedETPosition == 5) {
                if (editText6.length() == 0 && length == 5) {
                    editText5.setText("");
                    selectedETPosition = 4;
                    length = 4;
                    editText5.requestFocus();
                }
//                selectedETPosition = 4;
//                editText5.requestFocus();
            }
            else if(selectedETPosition == 4) {
                if (editText5.length() == 0 && length == 4) {
                    editText4.setText("");
                    selectedETPosition = 3;
                    length = 3;
                    editText4.requestFocus();
                }
//                selectedETPosition = 3;
//                editText4.requestFocus();
            }
            else if(selectedETPosition == 3) {
                if (editText4.length() == 0 && length == 3) {
                    editText3.setText("");
                    selectedETPosition = 2;
                    length = 2;
                    editText3.requestFocus();
                }
//                selectedETPosition = 2;
//                editText3.requestFocus();
            }
            else if(selectedETPosition == 2) {
                if (editText3.length() == 0 && length == 2) {
                    editText2.setText("");
                    selectedETPosition = 1;
                    length = 1;
                    editText2.requestFocus();
                }
//                selectedETPosition = 1;
//                editText2.requestFocus();
            }
            else if(selectedETPosition == 1) {
                if (editText2.length() == 0 && length == 1) {
                    editText1.setText("");
                    selectedETPosition = 0;
                    length = 0;
                    editText1.requestFocus();
                }
//                else {
//                selectedETPosition = 0;
//                editText1.requestFocus();
//                }
            }
            return true;
        }
        else {
        return super.onKeyUp(keyCode, event);
        }
    }

    public class GenericTextWatcher implements TextWatcher {

        private View view;
        private GenericTextWatcher(View view) {
            this.view = view;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            String code = "test";
//            if (editable.length() > 0) {
//                if (selectedETPosition == 0) {
//
//                    // Select next edit text
//                    selectedETPosition = 1;
//                    editText2.requestFocus();
//                }
//                else if (selectedETPosition == 1){
//                    selectedETPosition = 2;
//                    editText3.requestFocus();
//                }
//                else if (selectedETPosition == 2){
//                    selectedETPosition = 3;
//                    editText4.requestFocus();
//                }
//                else if (selectedETPosition == 3){
//                    selectedETPosition = 4;
//                    editText5.requestFocus();
//                }
//                else if (selectedETPosition == 4){
//                    selectedETPosition = 5;
//                    editText6.requestFocus();
//                }
//            } else {
//                code = editText1.getText().toString() + editText2.getText().toString() +
//                            editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() +
//                            editText6.getText().toString();
//                Toast.makeText(RegisterVerificationActivity.this, code, Toast.LENGTH_SHORT).show();
//            }
            if (view.getId() == R.id.editTxt1) {
                if (text.length() == 1){
                    selectedETPosition = 1;
                    length = 1;
                    editText2.requestFocus();
                }
            }
            else if (view.getId() == R.id.editTxt2) {
                if (text.length() == 1){
                    selectedETPosition = 2;
                    length = 2;
                    editText3.requestFocus();
                } else if(text.length() == 0) {
                    selectedETPosition = 1;
                    length = 1;
                    editText1.requestFocus();
                }
            }
            else if (view.getId() == R.id.editTxt3) {
                if (text.length() == 1){
                    selectedETPosition = 3;
                    length = 3;
                    editText4.requestFocus();
                } else if (text.length() == 0) {
                    selectedETPosition = 2;
                    length = 2;
                    editText2.requestFocus();
                }
            }
            else if (view.getId() == R.id.editTxt4) {
                if (text.length() == 1){
                    selectedETPosition = 4;
                    length = 4;
                    editText5.requestFocus();
                } else if (text.length() == 0) {
                    selectedETPosition = 3;
                    length = 3;
                    editText3.requestFocus();
                }
            }
            else if (view.getId() == R.id.editTxt5) {
                if (text.length() == 1){
                    selectedETPosition = 5;
                    length = 5;
                    editText6.requestFocus();
                } else if (text.length() == 0) {
                    selectedETPosition = 4;
                    length = 4;
                    editText4.requestFocus();
                }
            }
            else if (view.getId() == R.id.editTxt6) {
                if (text.length() == 1){
                    selectedETPosition = 6;
                    length = 6;
                    code = editText1.getText().toString() + editText2.getText().toString() +
                            editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() +
                            editText6.getText().toString();
                    Toast.makeText(RegisterVerificationActivity.this, code, Toast.LENGTH_SHORT).show();
                } else if (text.length() == 0) {
                    selectedETPosition = 5;
                    editText6.requestFocus();
                    length = 5;
                }
                    }
            if (mVerificationCode!=null) {
                if (mVerificationId.equals(code)) {
//                    userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                    User user = new User(userName, email, password, phoneNumber, true);
//                    FirebaseFirestore db = FirebaseFirestore.getInstance();
//                    db.collection("Users").document(userId).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Toast.makeText(RegisterVerificationActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    verifyPhoneNumberWithCode(code);
//                    Intent intent = new Intent(RegisterVerificationActivity.this, RegisterSuccessActivity.class);
//                    startActivity(intent);
//                    Log.d("userID", "userId : " + userId);
                }
            }
        }
    }
}


