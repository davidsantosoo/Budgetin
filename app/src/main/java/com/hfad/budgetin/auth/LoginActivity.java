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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hfad.budgetin.ui.MainActivity;
import com.hfad.budgetin.R;
public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private TextView forgotLink;
    private FirebaseAuth mAuth;
    private Button btnLogin;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.edt_email);
        password = findViewById(R.id.edt_password);
        mAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btn_login);
        progressDialog = new ProgressDialog(this);
        forgotLink = findViewById(R.id.forgot_text);
        mAuth = FirebaseAuth.getInstance();
        checkUser();
    }

    public void goToRegister(View view) {
        Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(i);
    }

    public void loginUser(View view){
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();
        if (TextUtils.isEmpty(emailStr)) {
            email.setError("Email Is Required!");
        }
        if (TextUtils.isEmpty(passwordStr)) {
            password.setError("Password Is Required");
        } else {
            progressDialog.setMessage("Login In Progress");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }

            });
        }
    }

    private void checkUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    public void resetPassword(View view) {
        Intent i = new Intent(getApplicationContext(), ResetPasswordActivity.class);
        startActivity(i);
    }
}
