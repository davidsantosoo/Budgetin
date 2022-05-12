package com.hfad.budgetin.ui.tips;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.hfad.budgetin.R;

public class TipsActivity extends AppCompatActivity {

    CardView card1,card2,card3,card4 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        card1 = findViewById(R.id.cardView1);
        card2= findViewById(R.id.cardView2);
        card3  = findViewById(R.id.cardView3);
        card4 = findViewById(R.id.cardView4);


        card1.setOnClickListener(v -> startActivity(new Intent(TipsActivity.this, WebActivity1.class)));
        card2.setOnClickListener(v -> startActivity(new Intent(TipsActivity.this, WebActivity2.class)));
        card3.setOnClickListener(v -> startActivity(new Intent(TipsActivity.this, WebActivity3.class)));
        card4.setOnClickListener(v -> startActivity(new Intent(TipsActivity.this, WebActivity4.class)));

    }


}