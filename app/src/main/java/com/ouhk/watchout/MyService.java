package com.ouhk.watchout;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Ravic on 1/30/15.
 */
public class MyService extends Service {
    private static final String TAG = "MyService";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "MyService Started", Toast.LENGTH_LONG).show();
        //Note: You can start a new thread and use it for long background processing from here.
        new Thread(new ClientThread()).start();
        Log.d(TAG, "onCreate in MyService.java");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();

        Log.d(TAG, "onDestroy");
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Log.d(TAG, "Runnable");
            CommonUtilities myCU = new CommonUtilities();
            myCU.socketConnection(getApplicationContext());
        }
    }
}
