package com.ouhk.watchout;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat.Builder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.InetSocketAddress;

/**
 * Created by Ravic on 1/29/15.
 */
public class CommonUtilities {
    // Tag used to log messages.
    static final String TAG = "Common Ultilities:";
    public Context context;
    public Socket myClient;
    public InputStreamReader in;
    public BufferedReader br;

    public void socketConnection(Context _context) {
        Log.d(TAG, "______________________________Runnable in Common Util");
        InetSocketAddress myIsa = new InetSocketAddress("123.202.86.187",8089);
        try {
            this.context = _context;
            Log.d(TAG,"_______________in Try block");
            myClient = new Socket();
            myClient.connect(myIsa, 1000);
            in = new InputStreamReader(myClient.getInputStream());
            br = new BufferedReader(in);
            char[] buffer = new char[300];
            int count = br.read(buffer, 0, 300);
            String reply = new String(buffer, 0, count);
            onMessageReceived(_context, reply);
        }catch (IOException e) {
            Log.d(TAG, "Problem");
            Log.d(TAG, e.getMessage());
            Log.d(TAG, "Server IP is "+myIsa);
        }
    }


    public void socketConnectionClosed() {
        try {
            in.close();
            br.close();
            myClient.close();
        }catch (IOException e) {
            System.out.print("Problem");
        }
    }

    // Add a notification
    public void onMessageReceived(Context _context, String reply) {
        String myText = reply;
        this.context = _context;
        Log.i(TAG, "Message received. The Text is _____"+myText);
        //Acquire notification service
        NotificationManager myNotificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        // Set intent here
        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent appIntent = PendingIntent.getActivity(context,0,notifyIntent,0);

        NotificationCompat.Builder myBuilder = new NotificationCompat.Builder(_context)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle("'Watch'Out!")
                        .setContentText(reply)
                        .setContentIntent(appIntent)
                        .setDefaults(Notification.DEFAULT_ALL);
        /*
        Notification notification = new Notification();
        //Status bar icon
        notification.icon = R.drawable.ic_launcher;
        //Status bar text
        notification.tickerText = myText;
        //Ringtone, vibrate, LED light
        notification.defaults = Notification.DEFAULT_ALL;
        //Notification title and content
        //notification.setLatestEventInfo(context,"Title","content",appIntent);
        */
        myNotificationManager.notify(0,myBuilder.build());

    }

}
