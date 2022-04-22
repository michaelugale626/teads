package com.example.testingteads;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.testingteads.teads.CustomInReadWebviewClient;
import com.example.testingteads.teads.SyncAdWebView;

import java.io.File;
import java.io.IOException;

import tv.teads.sdk.AdOpportunityTrackerView;
import tv.teads.sdk.AdPlacementSettings;
import tv.teads.sdk.AdRatio;
import tv.teads.sdk.AdRequestSettings;
import tv.teads.sdk.InReadAdListener;
import tv.teads.sdk.InReadAdPlacement;
import tv.teads.sdk.TeadsSDK;
import tv.teads.sdk.renderer.InReadAdView;


public class MainActivity extends AppCompatActivity implements SyncAdWebView.Listener{

    SyncAdWebView webviewHelperSynch;
    InReadAdPlacement adPlacement;
    WebView myWebView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // 1. Setup the settings
        AdPlacementSettings placementSettings = new AdPlacementSettings.Builder()
                .enableDebug()
                .build();

        // 2. Create the InReadAdPlacement
        adPlacement = TeadsSDK.INSTANCE.createInReadPlacement(this, 84242, placementSettings);
        webviewHelperSynch = new SyncAdWebView(this, myWebView, this::onHelperReady,"#teads-placement-slot");
        myWebView.setWebViewClient(new CustomInReadWebviewClient(webviewHelperSynch, "InRead Direct WebView"));
        myWebView.loadUrl("file:///android_asset/demo.html");
    }

    @Override
    public void onHelperReady(@NonNull ViewGroup adContainer) {

        AdRequestSettings requestSettings = new AdRequestSettings.Builder()
                .pageSlotUrl("http://teads.com")
                .build();//


        // 3. Request the ad and register to the listener in it
        adPlacement.requestAd(requestSettings,new InReadAdListener() {
            @Override
            public void onFailToReceiveAd(@NonNull String s) {
                webviewHelperSynch.clean();
            }

            @Override
            public void onAdReceived(@NonNull InReadAdView inReadAdView, @NonNull AdRatio adRatio) {
                webviewHelperSynch.registerAdView(inReadAdView);
                webviewHelperSynch.updateSlot(adRatio.getAdSlotRatio(myWebView.getMeasuredWidth()));
            }

            @Override
            public void onAdRatioUpdate(@NonNull AdRatio adRatio) {
                webviewHelperSynch.updateSlot(adRatio.getAdSlotRatio(myWebView.getMeasuredWidth()));
            }

            @Override
            public void onAdImpression() {

            }

            @Override
            public void onAdExpandedToFullscreen() {

            }

            @Override
            public void onAdError(int i, @NonNull String s) {
                webviewHelperSynch.clean();
            }

            @Override
            public void onAdCollapsedFromFullscreen() {

            }

            @Override
            public void onAdClosed() {
                webviewHelperSynch.closeAd();
            }

            @Override
            public void onAdClicked() {

            }

            @Override
            public void adOpportunityTrackerView(@NonNull AdOpportunityTrackerView adOpportunityTrackerView) {
                webviewHelperSynch.registerTrackerView(adOpportunityTrackerView);
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        webviewHelperSynch.onConfigurationChanged();
    }
}

