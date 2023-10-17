package com.example.florist.views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.florist.R;

public class RegisterPhoneActivity extends AppCompatActivity {
    TextView textViewHintPhoneNumber, textViewLogin;
    EditText editTextPhoneNumber;
    String phoneNumber;
    int currentProgress = 0;
    int lastProgress = 0;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);

        textViewHintPhoneNumber = findViewById(R.id.textViewHintPhoneNumber);
        textViewLogin = findViewById(R.id.textViewLogin);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        btnRegister = findViewById(R.id.btnRegister);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        actionBar.setElevation(0);

        editTextPhoneNumber.addTextChangedListener(new GenericTextWatcher(editTextPhoneNumber));

        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            phoneNumber = extras.getString("phoneNumber");
            editTextPhoneNumber.setText(phoneNumber.substring(3, phoneNumber.length()));
            editTextPhoneNumber.requestFocus();
        } else {
            editTextPhoneNumber.setText("");
            editTextPhoneNumber.requestFocus();
        }
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = (textViewHintPhoneNumber.getText().toString()
                        +(editTextPhoneNumber.getText().toString()));
                Intent intent = new Intent(RegisterPhoneActivity.this, RegisterActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("currentProgress", 250);
                intent.putExtra("lastProgress", 0);


                if (editTextPhoneNumber.getText().toString().length() < 8) {
                    editTextPhoneNumber.setBackground(AppCompatResources.getDrawable(RegisterPhoneActivity.this ,R.drawable.rounded_error_edittext));
                } else {
                    RegisterPhoneActivity.this.startActivity(intent);
                }
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterPhoneActivity.this, Login.class);
                startActivity(intent);
            }
        });




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
            if (view.getId() == R.id.editTextPhoneNumber) {
                if (text.length() >= 8) {
                    editTextPhoneNumber.setBackground(AppCompatResources.getDrawable(RegisterPhoneActivity.this ,R.drawable.rounded_success_edittext));
                    btnRegister.setBackground(AppCompatResources.getDrawable(RegisterPhoneActivity.this, R.drawable.rounded_success_button));
                } else {
                    btnRegister.setBackground(AppCompatResources.getDrawable(RegisterPhoneActivity.this, R.drawable.rounded_gray_button));
                    editTextPhoneNumber.setBackground(AppCompatResources.getDrawable(RegisterPhoneActivity.this ,R.drawable.rounded_normal_edittext));

                }
            }

        }
    }
}