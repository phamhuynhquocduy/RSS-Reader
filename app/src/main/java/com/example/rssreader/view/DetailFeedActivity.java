package com.example.rssreader.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.rssreader.R;
import com.monstertechno.adblocker.AdBlockerWebView;
import com.monstertechno.adblocker.util.AdBlocker;

public class DetailFeedActivity extends AppCompatActivity {
     private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_feed);
        webView = findViewById(R.id.webView);

        new AdBlockerWebView.init(this).initializeWebView(webView);
        webView.setWebViewClient(new Browser_home());
        //Get link url
        Intent intent = getIntent();
        String url = intent.getStringExtra("link");
        webView.loadUrl(url);
    }
    private class Browser_home extends WebViewClient {

        Browser_home() {}

        @SuppressWarnings("deprecation")
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

            return AdBlockerWebView.blockAds(view,url) ? AdBlocker.createEmptyResource() :
                    super.shouldInterceptRequest(view, url);

        }

    }
}