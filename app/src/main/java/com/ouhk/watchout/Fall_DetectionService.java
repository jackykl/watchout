package com.ouhk.watchout;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;
/**
 * Created by Ravic on 3/4/15.
 */
public class Fall_DetectionService extends Service implements SensorEventListener, LocationListener
{
    public static MainActivity MAIN_ACTIVITY;
    private static final String TAG = "Fall_DetectionService_____";
    public static final String PREFS_NAME = "MyPrefsFile";
    double[] Ax = new double[100];
    double[] Ay = new double[100];
    double[] Az = new double[100];
    boolean IsCustomSMS = false;
    boolean IsGPS = true;
    boolean IsTimer = false;
    private Sensor mAccelerometer;
    float mAccuracy = 1000.0F;
    double mAge = 40.0D;
    int mCunter = 0;
    String mEName;
    double mFTime = 60000.0D;
    long mFallTime;
    double mHeight = 165.0D;
    String mHerHis;
    boolean mIsFall = false;
    boolean mIsPrimaryFall = false;
    double mLTime = 30000.0D;
    double mLatitude;
    private LocationManager mLocationManager;
    String mLocationText = "";
    double mLongitude;
    String mM;
    double mMean;
    //String mMessage;
    NotificationManager mNotificationManager;
    String mPhoneNo;
    long mPrimaryFallTime;
    private SensorManager mSensorManager;
    double mSex = -1.0D;
    long mSysTime;
    private PowerManager.WakeLock mWakeLock;
    double mWeight = 65.0D;

    public double ANN(double[] paramArrayOfDouble)
    {
//        Log.d(TAG,("Into     ANN      MODULE"));
        System.out.println(Arrays.toString(paramArrayOfDouble));
        double[] arrayOfDouble1 = { -1.495256509D, 0.189159898D, -1.745627963D, -0.136768115D, 0.343453265D, -1.099452714D, -0.008261038D, 0.319077332D, -1.514411266D, 0.764200433D, 0.872964737D };
        double[] arrayOfDouble2 = { 2.280646265D, 0.967741057D, 2.087885364D, -0.141277674D, 0.536584108D, 0.446628999D, 0.040650166D, 0.73195341D, 1.321347553D, -1.932924615D, 1.600606284D };
        double[] arrayOfDouble3 = { 0.739285384D, 1.181761609D, 0.989122626D, 1.165804538D, -1.520534741D, 0.150345993D, 0.361739402D, -0.315164389D, 0.905024674D, 0.177932131D, 0.5298672840000001D };
        double[] arrayOfDouble4 = { -0.942639787D, 0.00578126D, 0.003607329D, 0.013187709D };
        double d1 = arrayOfDouble1[0] + paramArrayOfDouble[0] * arrayOfDouble1[1] + paramArrayOfDouble[1] * arrayOfDouble1[2] + paramArrayOfDouble[2] * arrayOfDouble1[3] + paramArrayOfDouble[3] * arrayOfDouble1[4] + paramArrayOfDouble[4] * arrayOfDouble1[5] + paramArrayOfDouble[5] * arrayOfDouble1[6] + paramArrayOfDouble[6] * arrayOfDouble1[7] + paramArrayOfDouble[7] * arrayOfDouble1[8] + paramArrayOfDouble[8] * arrayOfDouble1[9] + paramArrayOfDouble[9] * arrayOfDouble1[10];
        double d2 = arrayOfDouble2[0] + paramArrayOfDouble[0] * arrayOfDouble2[1] + paramArrayOfDouble[1] * arrayOfDouble2[2] + paramArrayOfDouble[2] * arrayOfDouble2[3] + paramArrayOfDouble[3] * arrayOfDouble2[4] + paramArrayOfDouble[4] * arrayOfDouble2[5] + paramArrayOfDouble[5] * arrayOfDouble2[6] + paramArrayOfDouble[6] * arrayOfDouble2[7] + paramArrayOfDouble[7] * arrayOfDouble2[8] + paramArrayOfDouble[8] * arrayOfDouble2[9] + paramArrayOfDouble[9] * arrayOfDouble2[10];
        double d3 = arrayOfDouble3[0] + paramArrayOfDouble[0] * arrayOfDouble3[1] + paramArrayOfDouble[1] * arrayOfDouble3[2] + paramArrayOfDouble[2] * arrayOfDouble3[3] + paramArrayOfDouble[3] * arrayOfDouble3[4] + paramArrayOfDouble[4] * arrayOfDouble3[5] + paramArrayOfDouble[5] * arrayOfDouble3[6] + paramArrayOfDouble[6] * arrayOfDouble3[7] + paramArrayOfDouble[7] * arrayOfDouble3[8] + paramArrayOfDouble[8] * arrayOfDouble3[9] + paramArrayOfDouble[9] * arrayOfDouble3[10];
        return arrayOfDouble4[0] + d1 * arrayOfDouble4[1] + d2 * arrayOfDouble4[2] + d3 * arrayOfDouble4[3];
    }

    public void FallDetected()
    {
        this.mIsFall = true;
        this.mIsPrimaryFall = false;
        this.mFallTime = this.mSysTime;
        //AudioManager localAudioManager = (AudioManager)getSystemService("audio");
        //localAudioManager.setStreamVolume(3, localAudioManager.getStreamMaxVolume(3), 1);
        //this.mMediaPlayer.start();
        Toast.makeText(this, "Fall detected! ", Toast.LENGTH_LONG).show();
//        Log.d(TAG,"Fall detected! mIsPrimaryFall" + this.mIsPrimaryFall);
        if (this.IsGPS) {
            this.mLocationManager.requestLocationUpdates("gps", 0L, 0.0F, this);
        }
        displayNotification("Fall detected!");
    }

    public double[] Feature(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
//        Log.d(TAG,"in Feature_____________________");

        System.out.println("Ax"+Arrays.toString(paramArrayOfDouble1));

        System.out.println("Ay"+Arrays.toString(paramArrayOfDouble2));

        System.out.println("Az"+Arrays.toString(paramArrayOfDouble3));


        double[] arrayOfDouble1 = new double[100];
        double[] arrayOfDouble2 = new double[10];
        double tmp = 0.0;
        double std = 0.0;
        double variance;
        double mean;
        double tmpAvg = 0.0;
        double max = arrayOfDouble1[0];
        double min = arrayOfDouble1[0];
        double range = 0.0;
        for (int j = 0; j < arrayOfDouble1.length; j++) {
            arrayOfDouble1[j] = Math.pow(Math.pow(paramArrayOfDouble1[j], 2.0D)
                    + Math.pow(paramArrayOfDouble2[j], 2.0D)
                    + Math.pow(paramArrayOfDouble3[j], 2.0D), 0.5D);
        }

        for (int j = 1; j < arrayOfDouble1.length; j++) {
            if(arrayOfDouble1[j] > max) {
                max = arrayOfDouble1[j];
            }
        }

        for (int j = 1; j < arrayOfDouble1.length; j++) {
            if(arrayOfDouble1[j] < min) {
                min = arrayOfDouble1[j];
            }
        }

        for (int j = 0; j < arrayOfDouble1.length; j++) {
            tmp += Math.sqrt(arrayOfDouble1[j]);
            tmpAvg += arrayOfDouble1[j];
        }

        variance = tmp / arrayOfDouble1.length;
        mean = tmpAvg/arrayOfDouble1.length;
        std = Math.sqrt(variance);
        range = max-min;
        arrayOfDouble2[0] = paramDouble4;
        arrayOfDouble2[1] = paramDouble1;
        arrayOfDouble2[2] = paramDouble3;
        arrayOfDouble2[3] = paramDouble2;
        arrayOfDouble2[4] = max;
        arrayOfDouble2[5] = min;
        arrayOfDouble2[6] = mean;
        arrayOfDouble2[7] = range;
        arrayOfDouble2[8] = variance;
        arrayOfDouble2[9] = std;
//        for (int j = 0; ; j++)
//        {
//            if (j >= 100)
//            {
//                double d7 = d6 / 100.0D;
//                double d8 = Math.pow(d7, 0.5D);
//                arrayOfDouble2[0] = paramDouble4;
//                arrayOfDouble2[1] = paramDouble1;
//                arrayOfDouble2[2] = paramDouble3;
//                arrayOfDouble2[3] = paramDouble2;
//                arrayOfDouble2[4] = d1;
//                arrayOfDouble2[5] = d2;
//                arrayOfDouble2[6] = d4;
//                arrayOfDouble2[7] = d5;
//                arrayOfDouble2[8] = d7;
//                arrayOfDouble2[9] = d8;
//
//                arrayOfDouble1[i] = Math.pow(Math.pow(paramArrayOfDouble1[i], 2.0D) + Math.pow(paramArrayOfDouble2[i], 2.0D) + Math.pow(paramArrayOfDouble3[i], 2.0D), 0.5D);
//                if (i == 0)
//                {
//                    d1 = arrayOfDouble1[i];
//                    d2 = arrayOfDouble1[i];
//                    System.out.println("D1_____"+Double.toString(d1)+"D2_____"+Double.toString(d2));
//                }
//                if (d1 < arrayOfDouble1[i]) {
//                    d1 = arrayOfDouble1[i];
//                    System.out.println("D1______" + Double.toString(d1) + "D2____" + Double.toString(d2));
//                }
//                if (d2 > arrayOfDouble1[i]) {
//                    d2 = arrayOfDouble1[i];
//                    System.out.println("D1______"+Double.toString(d1)+"D2_____"+Double.toString(d2));
//                }
//                d3 += arrayOfDouble1[i];
//                System.out.println("D3______"+Double.toString(d3));
//                i++;
//                break;
//            }
//            d6 += Math.pow(arrayOfDouble1[j] - d4, 2.0D);
//        }
//        System.out.println(Arrays.toString(arrayOfDouble2));
        return arrayOfDouble2;
    }

    public void GoNormal()
    {
        String myAge = "56";
        String myHeight = "160";
        String myWeight = "70";
        String myPhoneNo = "12345678";
        this.mIsFall = false;
        this.mIsPrimaryFall = false;
        this.mCunter = 0;
        this.mLocationText = "";
        if (this.IsGPS)
            this.mLocationManager.removeUpdates(this);
        //localTextView2.setText("Acceleration = ");
        //localTextView1.setText("0.0000");
        //this.mMediaPlayer.setLooping(true);
        this.mPhoneNo = myPhoneNo;
            this.mSex = -1.0D;
        for (this.mHerHis = "Her"; ; this.mHerHis = "His")
        {
            if (myAge.length() < 0)
                this.mAge = Double.parseDouble(myAge);
            if (myHeight.length() < 0)
                this.mHeight = Double.parseDouble(myHeight);
            if (myWeight.length() < 0)
                this.mWeight = Double.parseDouble(myWeight);
            displayNotification("Normal situation.");
            this.mSex = -1.0D;
            return;
        }
    }

    public void GoPrimaryFall()
    {
        this.mIsPrimaryFall = true;
        this.mPrimaryFallTime = this.mSysTime;
        displayNotification("Primary Fall Detected!");
    }

    public void Start()
    {
        GoNormal();
        this.mWakeLock.acquire();
        this.mSensorManager.registerListener(this, this.mAccelerometer, 0);
    }

    public void Stop()
    {
        GoNormal();
        this.mSensorManager.unregisterListener(this);
        this.mNotificationManager.cancelAll();
        //this.mMediaPlayer.pause();
        if (this.mWakeLock.isHeld())
            this.mWakeLock.release();
    }

    public void displayNotification(String paramString)
    {
        String myText = paramString;
        Log.i(TAG, paramString);
        Log.i(TAG, "Message received. The Text is _____" + myText);
        //Acquire notification service
        NotificationManager myNotificationManager = (NotificationManager)getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        // Set intent here
        Intent notifyIntent = new Intent(getApplicationContext(), MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent appIntent = PendingIntent.getActivity(getApplicationContext(),0,notifyIntent,0);

        NotificationCompat.Builder myBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("'Watch'Out!")
                .setContentText(myText)
                .setContentIntent(appIntent)
                .setDefaults(Notification.DEFAULT_ALL);
        myNotificationManager.notify(0,myBuilder.build());
    }

    public void onAccuracyChanged(Sensor paramSensor, int paramInt)
    {
    }

    public IBinder onBind(Intent paramIntent)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        this.mFallTime = System.currentTimeMillis();
        this.mPrimaryFallTime = System.currentTimeMillis();
        this.mEName =  "Emma";
        //this.mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.click);
        this.mLocationManager = ((LocationManager)getSystemService(LOCATION_SERVICE));
        this.mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        this.mAccelerometer = this.mSensorManager.getDefaultSensor(1);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "WakeLock");
        this.mNotificationManager = ((NotificationManager)this.getSystemService(NOTIFICATION_SERVICE));
        SharedPreferences localSharedPreferences = getSharedPreferences("MyPrefsFile", 0);
        this.IsCustomSMS = localSharedPreferences.getBoolean("CustomSMS", false);
        this.IsTimer = localSharedPreferences.getBoolean("Timer", false);
        this.IsGPS = localSharedPreferences.getBoolean("GPS", true);
        if (this.IsTimer)
        {
            this.mFTime = (1000.0D * Double.valueOf(localSharedPreferences.getString("FallTime", "")).doubleValue());
            this.mLTime = (1000.0D * Double.valueOf(localSharedPreferences.getString("LieTime", "")).doubleValue());
        }
        Start();
        Toast.makeText(getBaseContext(), "The Fall detection service is Running", Toast.LENGTH_LONG).show();
    }

    public void onDestroy()
    {
        super.onDestroy();
        Stop();
        Toast.makeText(getBaseContext(), "The Fall detection service is Off", Toast.LENGTH_LONG).show();
    }

    public void onLocationChanged(Location paramLocation)
    {
        String userName = "Emma";
        this.mLatitude = paramLocation.getLatitude();
        this.mLongitude = paramLocation.getLongitude();
        if (this.mAccuracy >= paramLocation.getAccuracy())
        {
            Toast.makeText(getApplicationContext(), "The Accuracy is = " + paramLocation.getAccuracy(), Toast.LENGTH_LONG).show();
            this.mLocationText = (this.mHerHis + " current location is: " + "http://www.google.com/maps?q=" + paramLocation.getLatitude() + "," + paramLocation.getLongitude());
        }
        while (true)
        {
            String str = "It appears that " + userName + " has Fallen and may require assistance." + this.mLocationText;
            if ((this.mLocationText.length() > 0) && (this.mSysTime - this.mFallTime > this.mFTime))
            {
                this.mLocationManager.removeUpdates(this);
                //sendSMS(this.mPhoneNo, str);
                this.mLocationText = "";
            }
            this.mAccuracy = paramLocation.getAccuracy();
            return;
        }
    }

    public void onProviderDisabled(String paramString)
    {
        Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_LONG).show();
    }

    public void onProviderEnabled(String paramString)
    {
        Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_LONG).show();
    }

    public void onSensorChanged(SensorEvent paramSensorEvent)
    {
        if (paramSensorEvent.sensor.getType() != 1)
            return;
        this.mSysTime = System.currentTimeMillis();
        this.mMean = Math.pow(Math.pow(paramSensorEvent.values[0], 2.0D) + Math.pow(paramSensorEvent.values[1], 2.0D) + Math.pow(paramSensorEvent.values[2], 2.0D), 0.5D);

        if ((!this.mIsFall))        //MAIN_ACTIVITY.ISFOREGROUND
            this.mM = String.valueOf(this.mMean);
        if ((this.mIsFall) && (this.mSysTime - this.mFallTime < 1000.0D + this.mFTime))
        {
            this.mM = String.valueOf((int)(this.mFTime / 1000.0D) - (this.mSysTime - this.mFallTime) / 1000L);
            if (this.mSysTime - this.mFallTime > this.mFTime)
            {
                this.mFallTime -= 1000L;
            }
        }
        while (true)
        {
            if ((this.mIsPrimaryFall) && (this.mSysTime - this.mPrimaryFallTime < this.mLTime))
            {
                if (this.mSysTime - this.mPrimaryFallTime > this.mLTime - 1000.0D)
                    FallDetected();
                if ((this.mMean > 11.0D) && (this.mSysTime - this.mPrimaryFallTime > 3000L))
                GoNormal();
            }

            if ((!this.mIsFall) && (!this.mIsPrimaryFall))  //True True
            {
                this.Ax[this.mCunter] = (237.00277800000001D * paramSensorEvent.values[0] / 10.0159442D);
                this.Ay[this.mCunter] = (237.00277800000001D * paramSensorEvent.values[1] / 10.0159442D);
                this.Az[this.mCunter] = (237.00277800000001D * paramSensorEvent.values[2] / 10.0159442D);
                this.mCunter = (1 + this.mCunter);
                if (this.mCunter == 100)
                {
                    this.mCunter = 50;
                    System.arraycopy(this.Ax, 50, this.Ax, 0, -50 + this.Ax.length);
                    System.arraycopy(this.Ay, 50, this.Ay, 0, -50 + this.Ay.length);
                    System.arraycopy(this.Az, 50, this.Az, 0, -50 + this.Az.length);
                    System.out.println(Arrays.toString(this.Ax));
                    System.out.println(Arrays.toString(this.Ay));
                    System.out.println(Arrays.toString(this.Az));

//                    Log.d(TAG,"ANN value _________        "+Double.toString(ANN(Feature(this.Ax, this.Ay, this.Az, this.mAge, this.mHeight, this.mWeight, this.mSex))));
//                    Log.d(TAG,"isFall value ______        "+this.mIsFall);
//                    Log.d(TAG,"isPrimaryFall _____        "+this.mIsPrimaryFall);
                    if (ANN(Feature(this.Ax, this.Ay, this.Az, this.mAge, this.mHeight, this.mWeight, this.mSex)) > -0.1D)
                    {
//                        Log.d(TAG,"After ANN ________________________");
//                        Log.d(TAG,"ANN value _________        "+Double.toString(ANN(Feature(this.Ax, this.Ay, this.Az, this.mAge, this.mHeight, this.mWeight, this.mSex))));
//                        Log.d(TAG,"isFall value ______        "+this.mIsFall);
//                        Log.d(TAG,"isPrimaryFall _____        "+this.mIsPrimaryFall);
                        GoPrimaryFall();
                    }
                }
            }
            if ((!this.mIsFall) || (this.mSysTime - this.mFallTime >= this.mFTime) || (this.mMean <= 15.0D))
                break;
            GoNormal();
        }
    }

    public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle)
    {
    }

    public double tansig(double paramDouble)
    {
        return 2.0D / (1.0D + Math.exp(-2.0D * paramDouble)) - 1.0D;
    }
}