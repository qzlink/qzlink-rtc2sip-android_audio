package com.sdk.android.ui;
/*
 * @creator      dean_deng
 * @createTime   2019/2/18 9:27
 * @Desc         ${TODO}
 */

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sdk.android.R;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout mRlBack;
    WebView mWv;
    TextView mTvName;
    private String mUrl = "http://www.webrtc2sip.com/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        initView();

        mRlBack.setOnClickListener(this);
        initWebView();
        mWv.loadUrl(mUrl);
        mWv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("shouldOverrideUrlLoading]=" + url);
                view.loadUrl(url);
                return true;
            }
        });
        mWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String title = view.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    if (mTvName != null)
                        mTvName.setText(view.getTitle());
                }
                System.out.println("url=" + url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                // android 5.0?????????????????????Mixed Content
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.getSettings().setMixedContentMode(
                            WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                }
            }
        });
        mWv.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                // H5??????????????????????????????????????????????????????
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    private void initView() {
        mRlBack = findViewById(R.id.rl_back);
        mTvName = findViewById(R.id.tv_name);
        mWv = findViewById(R.id.wv);
    }

    private void initWebView() {
        WebSettings webSettings = mWv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);//??????webview?????????????????????
        webSettings.setLoadWithOverviewMode(true);//??????webview????????????????????????
        webSettings.setDisplayZoomControls(false);//??????webview????????????
        webSettings.setJavaScriptEnabled(true); // ????????????javascript??????
        webSettings.setAllowFileAccess(true); // ??????????????????
        webSettings.setBuiltInZoomControls(true); // ????????????????????????
        webSettings.setSupportZoom(true); // ????????????
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
        }
    }
}
