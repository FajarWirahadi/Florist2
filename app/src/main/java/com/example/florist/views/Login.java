package com.example.florist.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.florist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    TextView textViewShowPassword,textViewRegister;
    EditText editTextEmail, editTextPassword;
    Button btnLogin;
    private boolean isPasswordVisible;
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
        textViewShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePassVisibility();
            }
        });
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, RegisterActivity.class);
                Login.this.startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(Login.this, HomepageActivity.class);
                            Login.this.startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, "Authentication failed!.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
}