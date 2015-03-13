package com.ouhk.watchout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "Main Activity";
    boolean mIsInForeground = true;
    private WebView myWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        final String myName = "Emma";
        myWebView = new WebView(this);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.addJavascriptInterface(new JavaScriptInterface(this),"Android");
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                myWebView.loadUrl("javascript:change('" + myName + "')");
            }
        });
        myWebView.loadUrl("file:///android_asset/webview/index.html");
        this.setContentView(myWebView);
        onStartService();
    }

    public void onStartService() {
        startService(new Intent(this, MyService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        this.mIsInForeground = false;
    }

    protected void onResume() {
        super.onResume();
        this.mIsInForeground = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MyService.class));
        stopService(new Intent(this, Fall_DetectionService.class));
    }

    public class JavaScriptInterface{
        Context myContext;

        JavaScriptInterface(Context c) {
            myContext = c;
        }
        @JavascriptInterface
        public void toAndroidActivity() {
            String toast = "Going into activity!";
            Toast.makeText(myContext, toast, Toast.LENGTH_SHORT).show();
            Intent foreGroundInstance = new Intent(getApplicationContext(), Fall_DetectionService.class);
            foreGroundInstance.putExtra("mIsInForeground",mIsInForeground);
            startService(foreGroundInstance);
            Log.d(TAG, "______________________________Going to fall_DetectionService!");
        }
    }
}
