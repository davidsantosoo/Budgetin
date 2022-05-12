package com.hfad.budgetin.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfad.budgetin.R;
import com.hfad.budgetin.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity { ;
    Animation animation;
    private ImageView imageView;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        animation = AnimationUtils.loadAnimation(this,R.anim.animation);
        imageView = findViewById(R.id.iv_splash);
        textView  = findViewById(R.id.tv_splash);


        imageView.setAnimation(animation);
        textView.setAnimation(animation);

        new Handler().postDelayed(() -> {
           Intent i = new Intent( SplashActivity.this, LoginActivity.class);
           startActivity(i);
            finish();
        },3000);

    }


}