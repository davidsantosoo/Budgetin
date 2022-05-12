package com.hfad.budgetin.ui.tips;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hfad.budgetin.R;

public class WebActivity2 extends AppCompatActivity {
    WebView webView;
    WebSettings webSettings;
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web2);
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView1);
        webSettings = webView.getSettings();
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.indeed.com/career-advice/career-development/creating-value-for-customers");
        progressBar.setVisibility(View.GONE);
    }
}