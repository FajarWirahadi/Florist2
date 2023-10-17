package com.example.florist.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.florist.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    ProgressBar progressBar;
    AutoCompleteTextView autoCompleteTextView;
    String[] items = {"Laki - laki", "Perempuan"};
    ArrayAdapter<String> adapterItems;
    EditText editTextUserName, editTextPassword, editTextPassword2,editTextEmail;
    TextView textViewShowPasswordRegister, textViewShowPasswordRegister2, minimumCharPassword, uppercaseCharPassword, minimumNumberPassword;
    ImageView minimumCharPasswordCheck, uppercaseCharPasswordCheck, minimumNumberPasswordCheck;
    Button btnRegister;
    public boolean isPasswordVisible1, isPasswordVisible2, emailValidation, usernameValidation, passwordValidation;
    String userName, email, password, phoneNumber;
    int success = 0;
    int currentProgress = 0;
    int lastProgress;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupUI(findViewById(R.id.parent));

        progressBar = findViewById(R.id.progressBar);
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);
        editTextPassword2 = findViewById(R.id.editTextPasswordRegister2);
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

        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            userName = extras.getString("userName");
            email = extras.getString("email");
            password = extras.getString("password");
            phoneNumber = extras.getString("phoneNumber");
            currentProgress = extras.getInt("currentProgress");
            lastProgress = extras.getInt("lastProgress");
            success = extras.getInt("success");

            editTextUserName.setText(userName);
            editTextEmail.setText(email);

            editTextPassword.setText(password);
            editTextPassword2.setText(password);
//            Log.d("Bundle userName", phoneNumber);
//            Log.d("Bundle email", email);
//            Log.d("Bundle password", password);
            if (success != 0) {
            changeValidationColors(success);
            }
        }
//        if (currentProgress == 0) {
//            currentProgress = 330;
//            lastProgress = 0;
//        }
        progressBar.setMax(1000);
//        int currentProgress = 330;
        ObjectAnimator.ofInt(progressBar, "progress", lastProgress, currentProgress)
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
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        actionBar.setElevation(2);


//        hideSoftKeyboard(this);
        editTextUserName.requestFocus();
        editTextUserName.addTextChangedListener(new GenericTextWatcher(editTextUserName));
        editTextEmail.addTextChangedListener(new GenericTextWatcher(editTextEmail));
        editTextPassword.addTextChangedListener(new GenericTextWatcher(editTextPassword));
        editTextPassword2.addTextChangedListener(new GenericTextWatcher(editTextPassword2));


        textViewShowPasswordRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePassVisibilityPassword1(editTextPassword, textViewShowPasswordRegister);
            }
        });
        textViewShowPasswordRegister2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePassVisibilityPassword2(editTextPassword2, textViewShowPasswordRegister2);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int a,b,c,pass;
                pass = getColor(R.color.bg_success);
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
                        success = pass;
//                        Log.d("buttonValidation", String.valueOf(btnRegister.getBackground()));
                        Intent intent = new Intent(RegisterActivity.this, RegisterSelectVerificationActivity.class);
                        intent.putExtra("phoneNumber", phoneNumber);
                        intent.putExtra("userName", userName);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("lastProgress", 250);
                        intent.putExtra("currentProgress", 500);
                        intent.putExtra("success", success);
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
        intent.putExtra("currentProgress", 250);
        intent.putExtra("lastProgress", 0);
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
    public void togglePassVisibilityPassword1(EditText editText, TextView textView){
        if (isPasswordVisible1) {
            String pass = editText.getText().toString();
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setText(pass);
            editText.setSelection(pass.length());
            textView.setText(R.string.lihat);
        }else {
            String pass = editText.getText().toString();
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setText(pass);
            editText.setSelection(pass.length());
            textView.setText(R.string.sembunyikan);
        }
        isPasswordVisible1 = !isPasswordVisible1;
    }
    public void togglePassVisibilityPassword2(EditText editText, TextView textView){
        if (isPasswordVisible2) {
            String pass = editText.getText().toString();
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setText(pass);
            editText.setSelection(pass.length());
            textView.setText(R.string.lihat);
        }else {
            String pass = editText.getText().toString();
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setText(pass);
            editText.setSelection(pass.length());
            textView.setText(R.string.sembunyikan);
        }
        isPasswordVisible2 = !isPasswordVisible2;
    }
    public boolean checkMinimumCharPassword(){
        if (editTextPassword2.getText().toString().length() <= 8) {
            minimumCharPassword.setTextColor(getColor(R.color.gray_500));
            minimumCharPasswordCheck.setImageResource(R.drawable.normal_check);
            return false;
        } else {
            minimumCharPassword.setTextColor(getColor(R.color.bg_success));
            minimumCharPasswordCheck.setImageResource(R.drawable.success_check);
            return true;
        }
    }
    public boolean checkUppercaseCharPassword(){
        String password = editTextPassword2.getText().toString();
        boolean hasUppercase = !password.equals(password.toLowerCase());
        if (!hasUppercase) {
            uppercaseCharPassword.setTextColor(getColor(R.color.gray_500));
            uppercaseCharPasswordCheck.setImageResource(R.drawable.normal_check);
            return false;
        } else {
            uppercaseCharPassword.setTextColor(getColor(R.color.bg_success));
            uppercaseCharPasswordCheck.setImageResource(R.drawable.success_check);
            return true;
        }
    }
    public boolean checkMinimumNumberPassword(){
        String password = editTextPassword2.getText().toString();
        if (password.matches("^.*[0-9]+.*$")){
            minimumNumberPassword.setTextColor(getColor(R.color.bg_success));
            minimumNumberPasswordCheck.setImageResource(R.drawable.success_check);
            return true;
        } else {
            minimumNumberPassword.setTextColor(getColor(R.color.gray_500));
            minimumNumberPasswordCheck.setImageResource(R.drawable.normal_check);
            return false;
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

    public void changeValidationColors(int i) {
        minimumCharPassword.setTextColor(i);
        uppercaseCharPassword.setTextColor(i);
        minimumNumberPassword.setTextColor(i);
        uppercaseCharPasswordCheck.setImageResource(R.drawable.success_check);
        minimumNumberPasswordCheck.setImageResource(R.drawable.success_check);
        minimumCharPasswordCheck.setImageResource(R.drawable.success_check);
        btnRegister.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_success_button));
        editTextUserName.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_success_edittext));
        editTextEmail.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_success_edittext));
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
                    hideSoftKeyboard(RegisterActivity.this);
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceStete) {
        super.onSaveInstanceState(savedInstanceStete);

        savedInstanceStete.putInt("success", success);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int color = savedInstanceState.getInt("success");

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
            if (view.getId() == R.id.editTextUserName) {
                if (text.length() > 0) {
                    editTextUserName.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_success_edittext));
                    usernameValidation = true;

                } else {
                    editTextUserName.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_normal_edittext));
                    usernameValidation = false;
                }
            } else if(view.getId() == R.id.editTextEmail) {
                String regex = "^(.+)@(.+)$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(text);
                Log.d("Email", "" + text.length());
                if (matcher.matches()) {
                    editTextEmail.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_success_edittext));
                    emailValidation = true;
                } else {
                    editTextEmail.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_normal_edittext));
                    emailValidation = false;
                }
            } else if(view.getId() == R.id.editTextPasswordRegister2) {
                boolean a = checkUppercaseCharPassword();
                boolean b = checkMinimumCharPassword();
                boolean c = checkMinimumNumberPassword();
                boolean check = true;
//                Log.d("isUppercase", "" + a);
                if
                (check == a &&
                check == b &&
                check == c) {
                    editTextPassword2.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_success_edittext));
                    passwordValidation = true;
                } else {
                    editTextPassword2.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_normal_edittext));
                    passwordValidation = false;
                }
            }
            String password1 = editTextPassword.getText().toString().trim();
            String password2 = editTextPassword2.getText().toString().trim();
            boolean a = usernameValidation;
            boolean b = emailValidation;
            boolean c = passwordValidation;
            boolean pass = true;
            if (a == pass &&
                    b == pass &&
                    c == pass &&
            password1.equals(password2)) {
                btnRegister.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_success_button));
            } else {
                btnRegister.setBackground(AppCompatResources.getDrawable(RegisterActivity.this, R.drawable.rounded_gray_button));
            }
         }
    }
}