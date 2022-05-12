package com.hfad.budgetin.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.hfad.budgetin.R;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText edtemailreset;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        edtemailreset = findViewById(R.id.edt_email_forgot);
        mAuth = FirebaseAuth.getInstance();
    }

    public void sendVerif(View view) {
        String email = edtemailreset.getText().toString();
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> Toast.makeText(ResetPasswordActivity.this, "Reset Link Sent To your Email",
                Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(ResetPasswordActivity.this, "Error! Reset Link is not sent" + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}