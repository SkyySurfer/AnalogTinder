package flirt.and.date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.net.MalformedURLException;
import java.net.URL;


public class MyView extends Activity {
    String url;
    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    WebView web;
    boolean loadingFinished = false;
    String currentUrl;


    private class MyWebViewClient extends WebViewClient {
        private MyWebViewClient() {
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            currentUrl = url;
            LogClass.log("onPageStarted");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            LogClass.log("shouldOverrideUrlLoading");
            currentUrl = url;

            return false;
        }



        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            view.loadData("<h2>Ошибка! Не удалось загрузить страницу!.</h2>", "text/html", "utf-8");
        }
        public void onPageFinished(WebView view, String url) {
            LogClass.log("onPageFinished");
            currentUrl = url;
            CookieSyncManager.getInstance().sync();
            if (!loadingFinished) {

                loadingFinished = true;
                SharedPreferencesEditor sharedPreferencesEditor = new SharedPreferencesEditor(getApplicationContext());

                String domain = "";
                try {
                    URL uurl = new URL(url);
                    domain = uurl.getHost();
                    domain = "http://" + domain;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                sharedPreferencesEditor.putIntoStorage(Constants.url, domain);
            }
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_view);
        web = findViewById(R.id.web);

        url = getIntent().getStringExtra("url");

            if (isNetworkConnected()) {
                WebSettings mWebSettings = web.getSettings();
                mWebSettings.setJavaScriptEnabled(true);
                mWebSettings.setDomStorageEnabled(true);
                mWebSettings.setDatabaseEnabled(true);
                mWebSettings.setSupportZoom(false);
                mWebSettings.setAllowFileAccess(true);
                mWebSettings.setAllowFileAccess(true);
                mWebSettings.setAllowContentAccess(true);
                web.loadUrl(url);
                web.setWebChromeClient(new WebChromeClient() {
                    /* access modifiers changed from: protected */
                    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                        MyView.this.mUploadMessage = uploadMsg;
                        Intent i = new Intent("android.intent.action.GET_CONTENT");
                        i.addCategory("android.intent.category.OPENABLE");
                        i.setType("image/*");
                        MyView.this.startActivityForResult(Intent.createChooser(i, "File Browser"), 1);
                    }

                    @RequiresApi(api = 21)
                    public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                        if (uploadMessage != null) {
                            uploadMessage.onReceiveValue(null);
                            uploadMessage = null;
                        }
                        uploadMessage = filePathCallback;
                        try {
                            MyView.this.startActivityForResult(fileChooserParams.createIntent(), 100);
                            return true;
                        } catch (ActivityNotFoundException e) {
                            uploadMessage = null;
                            Toast.makeText(MyView.this.getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                        MyView.this.mUploadMessage = uploadMsg;
                        Intent intent = new Intent("android.intent.action.GET_CONTENT");
                        intent.addCategory("android.intent.category.OPENABLE");
                        intent.setType("image/*");
                        MyView.this.startActivityForResult(Intent.createChooser(intent, "File Browser"), 1);
                        Log.i("test_tag", "openFileChooser: ");
                    }

                    /* access modifiers changed from: protected */
                    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                        mUploadMessage = uploadMsg;
                        Intent i = new Intent("android.intent.action.GET_CONTENT");
                        i.addCategory("android.intent.category.OPENABLE");
                        i.setType("image/*");
                        MyView.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), 1);
                    }

                });
                web.setWebViewClient(new MyWebViewClient());
                return;
            }
            web.setWebViewClient(new MyWebViewClient());
            web.loadData("<h2>Ошибка! Отсутствует интернет подключение.</h2>", "text/html", "utf-8");
            web.setWebChromeClient(new WebChromeClient() {
            });
        }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (requestCode == 100 && this.uploadMessage != null) {
                this.uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                this.uploadMessage = null;
            }
        } else if (requestCode != 1) {
            Toast.makeText(getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
        } else if (this.mUploadMessage != null) {
            Object data = (intent == null || resultCode != -1) ? null : intent.getData();
            this.mUploadMessage.onReceiveValue((Uri)data);
            this.mUploadMessage = null;
        }
    }
    @Override
    public void onBackPressed() {
            if(web.canGoBack()) {
                web.goBack();
            } else {
                web.reload();
            }
    }

    public boolean isNetworkConnected() {
        if (((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo() == null) {
            return false;
        }
        return true;
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        web.saveState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onUserLeaveHint() {
        SharedPreferencesEditor sharedPreferencesEditor = new SharedPreferencesEditor(getApplicationContext());
        sharedPreferencesEditor.putIntoStorage(Constants.urlBeforeUserLeaveHint,currentUrl);
        LogClass.log("onUserLeaveHint");
        super.onUserLeaveHint();
    }


    @Override
    protected void onStop() {
        LogClass.log("OnStop");
        SharedPreferencesEditor sharedPreferencesEditor = new SharedPreferencesEditor(getApplicationContext());
        sharedPreferencesEditor.deleteValue(Constants.urlBeforeUserLeaveHint);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        SharedPreferencesEditor sharedPreferencesEditor = new SharedPreferencesEditor(getApplicationContext());
        sharedPreferencesEditor.deleteValue(Constants.urlBeforeUserLeaveHint);
        LogClass.log("OnDestroy");
        super.onDestroy();
    }
}





