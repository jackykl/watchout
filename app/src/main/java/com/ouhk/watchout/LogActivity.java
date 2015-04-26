package com.ouhk.watchout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ravic on 4/22/15.
 */
public class LogActivity extends Activity {
    ListView list;
    TextView ver;
    TextView name;
    TextView api;
    Button Btngetdata;
    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    static List<NameValuePair> nameValuePairs;
    //URL to get JSON Array
    private static String url = "http://192.168.0.4:8080/fetchfalllog";
    //JSON Node Names

    //status, dateTime
    private static final String TAG_OS = "android";
    private static final String TAG_VER = "status";
    private static final String TAG_NAME = "dateTime";
    private static final String TAG_API = "api";

    JSONArray android = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_log);
        oslist = new ArrayList<HashMap<String, String>>();
        new Loader().execute();
        new JSONParse().execute();
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
//        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ver = (TextView)findViewById(R.id.vers);
            name = (TextView)findViewById(R.id.name);
            api = (TextView)findViewById(R.id.api);
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            //Log.d("JSON", json.toString());
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
//            pDialog.dismiss();
            try {
                // Getting JSON Array from URL
                android = json.getJSONArray(TAG_OS);
                for(int i = 0; i < android.length(); i++){
                    JSONObject c = android.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String ver = "Event : " + c.getString(TAG_VER);
                    String name = c.getString(TAG_NAME);
                    long cTime = Long.parseLong(name);
                    Calendar cl  = Calendar.getInstance();
                    cl.setTimeInMillis(cTime*1000);
                    int hour = cl.get(Calendar.HOUR_OF_DAY);
                    int newHour = 0;
                    String time = "";
                    if (hour >= 12){
                        newHour = hour - 12;
                        time = "Time  : " + newHour + " : " + cl.get(Calendar.MINUTE) + " : " + cl.get(Calendar.SECOND) + "  PM";
                    }else{
                        time = "Time  : " + newHour + " : " + cl.get(Calendar.MINUTE) + " : " + cl.get(Calendar.SECOND) + "  AM";
                    }
                    String date = "Date  : " + cl.get(Calendar.DAY_OF_MONTH) + "  -  " + cl.get(Calendar.MONTH) + "  -  " + cl.get(Calendar.YEAR);
                    // Adding value HashMap key => value

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_VER, ver);
                    map.put(TAG_NAME, time);
                    map.put(TAG_API, date);

                    oslist.add(map);
                    list=(ListView)findViewById(R.id.list);

                    ListAdapter adapter = new SimpleAdapter(LogActivity.this, oslist,
                            R.layout.list_v,
                            new String[] { TAG_VER,TAG_NAME,TAG_API }, new int[] {
                            R.id.vers,R.id.name,R.id.api});

                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Toast.makeText(LogActivity.this, "You Clicked at " + oslist.get(+position).get("name"), Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class Loader extends AsyncTask<Void, Void, JSONObject> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (Build.VERSION.SDK_INT >= 11) {
                dialog = new ProgressDialog(LogActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
            }else{
                dialog = new ProgressDialog(LogActivity.this);
            }
            dialog.setMessage(Html.fromHtml("<b>" + "Loading..." + "</b>"));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            return postJsonObject("http://192.168.0.4:8080/storesetting", makingJson());
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            if (result!=null) {
                dialog.dismiss();
            }else {
                Toast.makeText(LogActivity.this, "Successfully post json object", Toast.LENGTH_LONG).show();
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


}
