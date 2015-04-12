package com.ouhk.watchout;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.ouhk.watchout.sqlite.DatabaseHelper;
import com.ouhk.watchout.sqlite.SQLiteUtils;

public class EditActivity extends Activity{

	private String alerttime = "";
	private String datetime;
	private String content;
	private String tempContent,tempDatetime1,tempDatetime,tempAlerttime;
	private int index=0;
	private UserInfo user;
	private TimeSetDialog timeSetDialog=null;
	private Button backButton,timeSetButton;
	private TextView datetext,alertTextView;
	private EditText edittext;
	Calendar calendar = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_activity);
		
		backButton = (Button)findViewById(R.id.backButton);
		timeSetButton = (Button)findViewById(R.id.timeSet);
		datetext = (TextView)findViewById(R.id.dateText);
		edittext = (EditText)findViewById(R.id.editText);
		alertTextView = (TextView)findViewById(R.id.timeText);
		
		user = new UserInfo();
		user.setAlerttime(alerttime);
		timeSetButton.setOnClickListener( new OnClickListener(){
			@Override
			public void onClick(View v) {
				timeSetDialog = new TimeSetDialog(EditActivity.this);
				timeSetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						alerttime = timeSetDialog.alerttime;
						if(alerttime != null)
						    alertTextView.setText(Utils.timeTransfer(alerttime));
						else
							alertTextView.setText("");
						calendar = timeSetDialog.calendar;
						user.setAlerttime(alerttime);
					}
				});
				timeSetDialog.show();
			}
		});
		
		backButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("android.intent.extra.INTENT");
		datetime = bundle.getString("datetime");
		content = bundle.getString("content");
		alerttime = bundle.getString("alerttime");
		index = bundle.getInt("index");
		tempContent = new String(content);
		tempDatetime = new String(datetime);
		tempAlerttime = new String(alerttime);
		Time time = new Time();
		if(datetime.equals(""))
		{
			time.setToNow();
		}
		else{
			time.set(Long.parseLong(datetime));
		}
		int month = time.month+1;
		int day = time.monthDay;
		int hour = time.hour;
		int minute = time.minute;
		tempDatetime1 = month+"/"+day+"/"+'\n'+Utils.format(hour)+":"+Utils.format(minute);
		datetext.setText(tempDatetime1);
		edittext.setText(content);
		String tempS = new String(alerttime);
		if(!alerttime.equals(""))
		   alertTextView.setText(Utils.timeTransfer(tempS));
		else alertTextView.setText("");
		edittext.setSelection(content.length());
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		edittext = (EditText)findViewById(R.id.editText);
		Time time = new Time();
		time.setToNow();

		user.setAlerttime(alerttime);
		datetime =""+time.toMillis(true);
		user.setDatetime(datetime);
		time.set(time.toMillis(true));

		content = edittext.getText().toString();
		user.setContent(content);
		
		if((!content.isEmpty() && !tempContent.equals(content)) || !alerttime.equals("") && !alerttime.equals(tempAlerttime)){
			ArrayList<HashMap<String,String>> list = Utils.getList();
			SQLiteUtils sqlite = new SQLiteUtils();
			System.out.println("---------------------------");
			DatabaseHelper dbHelper = sqlite.createDBHelper(EditActivity.this);
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("datetime",user.getDatetime());
			map.put("content",user.getContent());
			map.put("alerttime",user.getAlerttime());
			
			if(tempContent.isEmpty())  {
				list.add(map);
				sqlite.insert(dbHelper,user);
			}
			else {
				list.set(index, map);
				sqlite.delete(dbHelper, tempDatetime);
				sqlite.insert(dbHelper,user);
			}
			if(!alerttime.equals(tempAlerttime) && !alerttime.equals(""))
			{
                System.out.println("TempTime is "+tempAlerttime);
                System.out.println("The alert time is "+alerttime);
				System.out.println("alerttime done!");
				alertSet();
			}
				
		}
	}
	
	
	private void alertSet(){
		Intent intent = new Intent("android.intent.action.ALARMRECEIVER");
		intent.putExtra("datetime", datetime);
	    intent.putExtra("content", content);
	    intent.putExtra("alerttime",alerttime);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(EditActivity.this, 0, intent, 0);
	    AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
	    alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);
	    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),(24 * 60 * 60 * 1000), pendingIntent);
	}
}
