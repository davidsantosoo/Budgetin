package com.hfad.budgetin.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.hfad.budgetin.R;
import com.hfad.budgetin.model.User;
public class RegisterActivity extends AppCompatActivity {
    private Button btnRegister;
    private ProgressDialog progressDialog;
    private EditText username, email, password, confirmPassword, location;
    FirebaseUser mUser;
    private FirebaseAuth mAuth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.edt_email);
        username = findViewById(R.id.edt_username);
        password = findViewById(R.id.edt_password);
        confirmPassword = findViewById(R.id.edt_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        location = findViewById(R.id.edt_location);
        progressDialog = new ProgressDialog(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = email.getText().toString();
                String passwordStr = password.getText().toString();
                String confirmStr = confirmPassword.getText().toString();
                String usernameStr = username.getText().toString();
                String locationStr = location.getText().toString();

                if (TextUtils.isEmpty(usernameStr)) {
                    username.setError("Username Is Required!");
                    username.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(locationStr)) {
                    location.setError("Store Location Is Required!");
                    location.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(emailStr)) {
                    email.setError("Email Is Required!");
                    email.requestFocus();
                    return;
                }
                if (!emailStr.matches(emailPattern)) {
                    email.setError("Enter correct email pattern!");
                    email.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(passwordStr)) {
                    password.setError("Password Is Required!");
                    password.requestFocus();
                    return;
                }
                if (passwordStr.length()<8 ) {
                    password.setError("Password must at least 8 character!");
                    password.requestFocus();
                    return;
                }
                if (!passwordStr.equals(confirmStr)){
                    confirmPassword.setError("Password field must be equal!");
                    confirmPassword.requestFocus();
                    return;
                }
                    else
                 {
                    progressDialog.setMessage("registration In Progress");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    mAuth = FirebaseAuth.getInstance();
                    mUser = mAuth.getCurrentUser();

                    mAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(usernameStr,emailStr,locationStr);
                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "user has been registered successfully", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                                progressDialog.dismiss();
                                            }
                                            else {
                                                Toast.makeText(RegisterActivity.this, "Failed to register! try again!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    public void goToLogin(View view) {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }
}