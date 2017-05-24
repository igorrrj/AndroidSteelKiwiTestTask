package com.example.igor.gitapiexample.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static com.example.igor.gitapiexample.Constants.INTENT_EXTRA_URL;


public class LoginWebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getIntent().getStringExtra(INTENT_EXTRA_URL));
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (uri.getScheme().equals("http")) {
                    Intent data = new Intent();
                    data.setData(uri);
                    setResult(RESULT_OK, data);
                    finish();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        setContentView(webView);
    }
}
