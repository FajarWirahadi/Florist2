package com.example.florist.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.florist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class RegisterActivity extends AppCompatActivity {

    String[] items = {"Laki - laki", "Perempuan"};
    AutoCompleteTextView autoCompleteText;
    ArrayAdapter<String> adapterItems;
    EditText editTextPassword, editTextEmail;
    TextView textViewShowPasswordRegister, minimumCharPassword, uppercaseCharPassword, minimumNumberPassword;
    ImageView minimumCharPasswordCheck, uppercaseCharPasswordCheck, minimumNumberPasswordCheck;
    Button btnRegister;
    public boolean isPasswordVisible;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        autoCompleteText = findViewById(R.id.autoCompleteText);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);
        editTextEmail = findViewById(R.id.editTextEmail);
        textViewShowPasswordRegister = findViewById(R.id.textViewShowPasswordRegister);
        minimumCharPassword = findViewById(R.id.minimumCharPassword);
        uppercaseCharPassword = findViewById(R.id.uppercaseCharPassword);
        minimumNumberPassword = findViewById(R.id.minimumNumberPassword);
        minimumCharPasswordCheck = findViewById(R.id.minimumCharPasswordCheck);
        uppercaseCharPasswordCheck = findViewById(R.id.uppercaseCharPasswordCheck);
        minimumNumberPasswordCheck = findViewById(R.id.minimumNumberPasswordCheck);
        btnRegister = findViewById(R.id.btnRegister);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, items);
        autoCompleteText.setAdapter(adapterItems);
        autoCompleteText.setDropDownBackgroundDrawable(autoCompleteText.getResources().getDrawable(R.drawable.rouded_error_edittext2));
        mAuth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        textViewShowPasswordRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePassVisibility();
            }
        });

        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
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
                Log.d("Integer warna", String.valueOf(a));
                if (a == pass && b == pass && c == pass){
                    String email = editTextEmail.getText().toString();
                    String password = editTextPassword.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password)
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
                    });
                    Intent intent = new Intent(RegisterActivity  .this, HomepageActivity.class);
                    RegisterActivity.this.startActivity(intent);
                } else {
                    editTextPassword.setBackground(getDrawable(R.drawable.rouded_error_edittext));
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
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivityForResult(intent, 0);
        return true;
    }
    public void togglePassVisibility(){
        if (isPasswordVisible) {
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
    public void checkMinimumCharPassword(){
        if (editTextPassword.getText().toString().length() <= 8) {
            minimumCharPassword.setTextColor(getColor(R.color.red));
            minimumCharPasswordCheck.setImageResource(R.drawable.error_check);
        } else {
            minimumCharPassword.setTextColor(getColor(R.color.green));
            minimumCharPasswordCheck.setImageResource(R.drawable.success_check);
        }
    }
    public void checkUppercaseCharPassword(){
        String password = editTextPassword.getText().toString();
        boolean hasUppercase = !password.equals(password.toLowerCase());
        if (hasUppercase == false) {
            uppercaseCharPassword.setTextColor(getColor(R.color.red));
            uppercaseCharPasswordCheck.setImageResource(R.drawable.error_check);
        } else {
            uppercaseCharPassword.setTextColor(getColor(R.color.green));
            uppercaseCharPasswordCheck.setImageResource(R.drawable.success_check);
        }
    }
    public void checkMinimumNumberPassword(){
        String password = editTextPassword.getText().toString();
        if (password.matches("^.*[0-9]+.*$") == true){
            minimumNumberPassword.setTextColor(getColor(R.color.green));
            minimumNumberPasswordCheck.setImageResource(R.drawable.success_check);
        } else {
            minimumNumberPassword.setTextColor(getColor(R.color.red));
            minimumNumberPasswordCheck.setImageResource(R.drawable.error_check);
        }
    }
}