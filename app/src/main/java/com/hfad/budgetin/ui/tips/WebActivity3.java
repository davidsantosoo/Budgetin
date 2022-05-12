package com.hfad.budgetin.ui.tips;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hfad.budgetin.R;

public class WebActivity3 extends AppCompatActivity {

    WebView webView;
    WebSettings webSettings;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web3);
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView1);
        webSettings = webView.getSettings();
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.bankrate.com/banking/savings/how-to-set-savings-goals/");
        progressBar.setVisibility(View.GONE);
    }
}