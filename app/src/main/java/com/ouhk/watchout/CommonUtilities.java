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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by Ravic on 1/29/15.
 */
public class CommonUtilities {
    // Tag used to log messages.
    static final String TAG = "Common Ultilities:";
    public Context context;
//    public Socket myClient;
    public InputStreamReader in;
    public BufferedReader br;
    private SocketData SockData = new SocketData();

    public void socketConnection(Context _context) {
        Log.d(TAG, "______________________________Runnable in Common Util");
        try {
            SockData.setServerIP("192.168.0.4");
            SockData.setServerPort(8089);
            SockData.setSocketObj(new java.net.Socket());
            SockData.setSocketTimeout(30000);
            InetAddress serverAddr = InetAddress.getByName(SockData.getServerIP());
            SocketAddress sc_add = new InetSocketAddress(serverAddr, SockData.getServerPort());
            SockData.getSocketObj().connect(sc_add, SockData.getSocketTimeout());
            SockData.setOutStream(SockData.getSocketObj().getOutputStream());
            SockData.setInStream(SockData.getSocketObj().getInputStream());

            SockData.setDataOut(new DataOutputStream(SockData.getSocketObj().getOutputStream()));
            SockData.setDataIn(new DataInputStream(SockData.getSocketObj().getInputStream()));
            SockData.setInStreamReader(new InputStreamReader(SockData.getDataIn()));
            SockData.setBr(new BufferedReader(SockData.getInStreamReader()));
            Log.d(TAG, "Socket Running");
            receivedData(_context);
        }catch (Exception e) {
            Log.d(TAG, "Problem");
            Log.d(TAG, e.toString());
            // Log.d(TAG, "Server IP is "+myIsa);
        }
    }

    public void receivedData(Context _context) throws IOException {
        while(true) {
            Log.d(TAG, "____________Received DATA Method Entry.......");
            String recvData = SockData.getBr().readLine()+"\n";
            Log.d(TAG, "Data: " + recvData);
            onMessageReceived(_context, recvData);
        }
    }

//    public void socketConnectionClosed() {
//        try {
//            in.close();
//            br.close();
//            myClient.close();
//        }catch (IOException e) {
//            System.out.print("Problem");
//        }
//    }

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
                        .setTicker(reply)
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
