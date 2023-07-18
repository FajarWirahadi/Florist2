package com.example.florist.views;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.florist.R;
import com.example.florist.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class RegisterActivity extends AppCompatActivity {
    ProgressBar progressBar;
    AutoCompleteTextView autoCompleteTextView;
    String[] items = {"Laki - laki", "Perempuan"};
    ArrayAdapter<String> adapterItems;
    EditText editTextUserName, editTextPassword, editTextPassword2,editTextEmail;
    TextView textViewShowPasswordRegister, textViewShowPasswordRegister2, minimumCharPassword, uppercaseCharPassword, minimumNumberPassword;
    ImageView minimumCharPasswordCheck, uppercaseCharPasswordCheck, minimumNumberPasswordCheck;
    Button btnRegister;
    public boolean isPasswordVisible;
    String userName, email, password, phoneNumber;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressBar = findViewById(R.id.progressBar);
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);
        editTextPassword2 = findViewById(R.id.editTextPasswordRegister2);
        editTextEmail = findViewById(R.id.editTextEmail);
        textViewShowPasswordRegister = findViewById(R.id.textViewShowPasswordRegister);
        textViewShowPasswordRegister2 = findViewById(R.id.textViewShowPasswordRegister2);
        minimumCharPassword = findViewById(R.id.minimumCharPassword);
        uppercaseCharPassword = findViewById(R.id.uppercaseCharPassword);
        minimumNumberPassword = findViewById(R.id.minimumNumberPassword);
        minimumCharPasswordCheck = findViewById(R.id.minimumCharPasswordCheck);
        uppercaseCharPasswordCheck = findViewById(R.id.uppercaseCharPasswordCheck);
        minimumNumberPasswordCheck = findViewById(R.id.minimumNumberPasswordCheck);
//        autoCompleteTextView = findViewById(R.id.autoCompleteText);
        btnRegister = findViewById(R.id.btnRegister);
        /*adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, items);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setDropDownBackgroundDrawable(autoCompleteTextView.getResources().getDrawable(R.drawable.rouded_error_edittext2));*/

        progressBar.setMax(1000);
        int currentProgress = 330;
        ObjectAnimator.ofInt(progressBar, "progress", currentProgress)
                .setDuration(1500)
                .start();
        /*autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterItems.getItem(i).toString();
                Log.d("test", "hai");
            }
        });*/
        mAuth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));


        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            userName = extras.getString("userName");
            email = extras.getString("email");
            password = extras.getString("password");
            phoneNumber = extras.getString("phoneNumber");
            editTextUserName.setText(userName);
            editTextEmail.setText(email);

            editTextPassword.setText(password);
            editTextPassword2.setText(password);
//            Log.d("Bundle userName", phoneNumber);
//            Log.d("Bundle email", email);
//            Log.d("Bundle password", password);
        }

        textViewShowPasswordRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePassVisibility(editTextPassword);
            }
        });
        textViewShowPasswordRegister2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePassVisibility(editTextPassword2);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a,b,c,pass;
                checkMinimumCharPassword();
                checkUppercaseCharPassword();
                checkMinimumNumberPassword();
                pass = getColor(R.color.green);
                a = minimumCharPassword.getCurrentTextColor();
                b = uppercaseCharPassword.getCurrentTextColor();
                c = minimumNumberPassword.getCurrentTextColor();
                boolean isSame = checkPassword();
                if (a == pass && b == pass && c == pass){
                     email = editTextEmail.getText().toString();
                     password = editTextPassword.getText().toString();
                     userName = editTextUserName.getText().toString();
                    /*mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("Create email", "createUserWithEmail:Success");
                                FirebaseUser user = mAuth.getCurrentUser();
                            } else {
                                Log.w("Create email" ,"createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });*/
                    if (isSame) {
                        Intent intent = new Intent(RegisterActivity.this, RegisterSelectVerificationActivity.class);
                        intent.putExtra("phoneNumber", phoneNumber);
                        intent.putExtra("userName", userName);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        RegisterActivity.this.startActivity(intent);
                    }



                } else {
                    editTextPassword2.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_error_edittext));
                    editTextPassword2.findFocus();
                }
            }
        });

//        String email;
//        String password;
//
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("Create email", "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w("Create email", "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, RegisterPhoneActivity.class);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivityForResult(intent, 0);
        return true;
    }
//    ActivityResultLauncher<String> mGetContent = registerForActivityResult
//            (new ActivityResultContracts.GetContent(),
//            new ActivityResultCallback<Uri>() {
//                @Override
//                public void onActivityResult(Uri result) {
//
//                }
//            });
    public void togglePassVisibility(EditText editText){
        if (isPasswordVisible) {
            String pass = editText.getText().toString();
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setText(pass);
            editText.setSelection(pass.length());
        }else {
            String pass = editText.getText().toString();
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setText(pass);
            editText.setSelection(pass.length());
        }
        isPasswordVisible = !isPasswordVisible;
    }
    public void checkMinimumCharPassword(){
        if (editTextPassword2.getText().toString().length() <= 8) {
            minimumCharPassword.setTextColor(getColor(R.color.red));
            minimumCharPasswordCheck.setImageResource(R.drawable.error_check);
        } else {
            minimumCharPassword.setTextColor(getColor(R.color.green));
            minimumCharPasswordCheck.setImageResource(R.drawable.success_check);
        }
    }
    public void checkUppercaseCharPassword(){
        String password = editTextPassword2.getText().toString();
        boolean hasUppercase = !password.equals(password.toLowerCase());
        if (!hasUppercase) {
            uppercaseCharPassword.setTextColor(getColor(R.color.red));
            uppercaseCharPasswordCheck.setImageResource(R.drawable.error_check);
        } else {
            uppercaseCharPassword.setTextColor(getColor(R.color.green));
            uppercaseCharPasswordCheck.setImageResource(R.drawable.success_check);
        }
    }
    public void checkMinimumNumberPassword(){
        String password = editTextPassword2.getText().toString();
        if (password.matches("^.*[0-9]+.*$")){
            minimumNumberPassword.setTextColor(getColor(R.color.green));
            minimumNumberPasswordCheck.setImageResource(R.drawable.success_check);
        } else {
            minimumNumberPassword.setTextColor(getColor(R.color.red));
            minimumNumberPasswordCheck.setImageResource(R.drawable.error_check);
        }
    }
    public boolean checkPassword(){
        if (editTextPassword.getText().toString().equals(editTextPassword2.getText().toString())) {
            return true;
        } else {
            editTextPassword2.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_error_edittext));
            editTextPassword.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_error_edittext));
            editTextPassword2.findFocus();
            return false;
        }
    }
}