package com.ouhk.watchout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "Main Activity";
    boolean mIsInForeground = true;
    private WebView myWebView;
    static List<NameValuePair> nameValuePairs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        final String myName = sharedPrefs.getString("username",null);
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
        new Loader().execute();
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

    class Loader extends AsyncTask<Void, Void, JSONObject> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                dialog = new ProgressDialog(MainActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
            }else{
                dialog = new ProgressDialog(MainActivity.this);
            }
            dialog.setMessage(Html.fromHtml("<b>" + "Loading..." + "</b>"));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            return postJsonObject("http://192.168.246.45:8080/storesetting", makingJson());
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            if (result!=null) {
                dialog.dismiss();
            }else {
                Toast.makeText(MainActivity.this, "Successfully post json object", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }
    }

    public JSONObject makingJson() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        JSONObject jobj = new JSONObject();
        String userName = sharedPrefs.getString("username", null);
        String elderlyName = sharedPrefs.getString("elderlyname",null);
        String phoneNumber = sharedPrefs.getString("phonenumber",null);
        try {
            jobj.put("username", userName);
            jobj.put("elderlyname", elderlyName);
            jobj.put("phonenumber", phoneNumber);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jobj;
    }


    public JSONObject postJsonObject(String url, JSONObject loginJobj){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL

            //http://localhost:9000/api/products/GetAllProducts
            HttpPost httpPost = new HttpPost(url);

            System.out.println(url);
            String json = "";

            // 4. convert JSONObject to JSON to String

            json = loginJobj.toString();

            System.out.println(json);
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("settings",json));
            // 5. set json to StringEntity
//            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        JSONObject json = null;
        try {

            json = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 11. return result

        return json;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public class JavaScriptInterface{
        Context myContext;

        JavaScriptInterface(Context c) {
            myContext = c;
        }
        @JavascriptInterface
        public void toAndroidActivity() {
            Log.d(TAG,"Going to log activity");
            Intent intent = new Intent(getApplicationContext(), LogActivity.class);
            //Intent foreGroundInstance = new Intent(getApplicationContext(), Fall_DetectionServices.class);
            //foreGroundInstance.putExtra("mIsInForeground",mIsInForeground);
            startActivity(intent);
            //startService(foreGroundInstance);
        }
        @JavascriptInterface
        public void toMedicationReminder() {
            Intent intent = new Intent(getApplicationContext(), AlarmListActivity.class);
            startActivity(intent);
        }

        @JavascriptInterface
        public void toSettings() {
            Log.d(TAG,"Going to settings activity");
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
        }
    }
}
