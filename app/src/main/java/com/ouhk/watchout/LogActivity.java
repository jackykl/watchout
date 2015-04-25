package com.ouhk.watchout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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

    //URL to get JSON Array
    private static String url = "http://192.168.246.45:8080/fetchfalllog";
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

        new JSONParse().execute();
//        Btngetdata = (Button)findViewById(R.id.getdata);
//        Btngetdata.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                new JSONParse().execute();
//
//            }
//        });

    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
//        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ver = (TextView)findViewById(R.id.vers);
            name = (TextView)findViewById(R.id.name);
            api = (TextView)findViewById(R.id.api);
//            pDialog = new ProgressDialog(LogActivity.this);
//            pDialog.setMessage("Getting Data ...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();

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

}
