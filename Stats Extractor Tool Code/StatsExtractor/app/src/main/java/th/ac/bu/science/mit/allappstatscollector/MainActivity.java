package th.ac.bu.science.mit.allappstatscollector;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import CoreStats.NET;


public class MainActivity extends ActionBarActivity
{
    static Context context;
    final Handler mHandler = new Handler();
    TextView tvWarning, tvWarningText, tvMacAddress;
    Button btnStart, btnUsage;
    Runnable runnable;
    int uiUpdateInterval=3;
    boolean isAppPaused=false;

    public static Handler handlerFileUpload=new Handler(new Handler.Callback()    {
        @Override
        public boolean handleMessage(Message msg)
        {
            Bundle bundle=msg.getData();
            String text=bundle.getString("message","default Message");
            ShowMessage.message(text,context);
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=MainActivity.this;
        context=getApplicationContext();
        Settings.loadSettings(context);

        btnStart = (Button)findViewById(R.id.btnStartExtracting);
        btnUsage = (Button)findViewById(R.id.btnUsageAccess);
        tvWarning = (TextView)findViewById(R.id.tvWarning);
        tvWarningText = (TextView)findViewById(R.id.tvWarningText);
        tvMacAddress = (TextView)findViewById(R.id.tvMacAddress);
    }

    private BroadcastReceiver mMessageRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(!isAppPaused)
            {
                //update UI
                if(isMyServiceRunning(BackgroundIntentService.class))
                {
                    btnStart.setText("Stop Collecting Data");
                    tvWarning.setTextColor(Color.GREEN);

                    tvWarning.setText("Status:");
                    tvWarningText.setText("Collecting data....");
                    setCounter();
                }
                else
                {
                    btnStart.setText("Start Collecting Data");
                    tvWarning.setTextColor(Color.parseColor("#FFA500"));    //orange color
                    tvWarning.setText("Status:");
                    tvWarningText.setText("Ready for Collection.");
                }
            }
        }
    };

    @Override
    protected void onStart()
    {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageRecevier,new IntentFilter("updateUI"));

        isAppPaused=false;

        super.onStart();

        if(Settings.isUsageAccessGranted(context))      //if user access is granted set the start button to visible and user access to invisible.
        {
            btnStart.setVisibility(View.VISIBLE);
            btnUsage.setVisibility(View.INVISIBLE);

            if(isMyServiceRunning(BackgroundIntentService.class))        //access is already granted and service is running.
            {
                btnStart.setText("Stop Collecting Data");
                tvWarning.setTextColor(Color.GREEN);

                tvWarning.setText("Status:");
                tvWarningText.setText("Collecting data....");
                setCounter();
            }
            else
            {
                btnStart.setText("Start Collecting Data");
                tvWarning.setTextColor(Color.parseColor("#FFA500"));    //orange color
                tvWarning.setText("Status:");
                tvWarningText.setText("Ready for Collection.");
            }
        }
        else                                            //user access is not granted. Set the btnStart to invisble and usage access to visible.
        {
            btnStart.setVisibility(View.INVISIBLE);
            btnUsage.setVisibility(View.VISIBLE);
            tvWarning.setText("Warning:");
            tvWarning.setTextColor(Color.RED);
            tvWarningText.setText("Please turn on usage access first.");
        }

        if(Settings.WIFI_INTERFACE==null || Settings.MAC==null)
        {
            return;
        }

        tvMacAddress.setText("Mac Address " + Settings.getMacAddress());

        try {
            if (NetworkInterface.getByName("rmnet0") == null) {
                Notify.showNotification(context, "Data interface error.");
            }
        }
        catch(Exception ex)
        {
            Log.d(Settings.TAG,"Can not find data interface name. Details: "+ex.toString());
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final Context con =getApplicationContext();

         if (!prefs.getBoolean("firstTime", false)) {

             new Thread(new Runnable() {
                 public void run() {
                     SharedPreferences.Editor editor = prefs.edit();
                     editor.putBoolean("firstTime", true);
                     editor.commit();

                     //Log.d("hash-code", "generating first time.......-----");
                     HashGen hashGen = new HashGen();
                     hashGen.getAllAppInfo(con);

                 }
             }).start();
         }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        try
        {
            unregisterReceiver(mMessageRecevier);
        }
        catch (Exception ex)
        {

        }

        isAppPaused=true;

    }
    public void setCounter()
    {
        try {
            final TextView tvCounter = (TextView) findViewById(R.id.tvCounter);
            final TextView tvFileSize = (TextView) findViewById(R.id.tvFileSize);

            tvCounter.setText("Session: " + Integer.toString(BackgroundIntentService.counter));
            tvFileSize.setText("File Size: " + Long.toString(StatsFileManager.getFileSize(context)) + " KB");

            mHandler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {

                    tvCounter.setText("Session: " + Integer.toString(BackgroundIntentService.counter));
                    tvFileSize.setText("File Size: " + Long.toString(StatsFileManager.getFileSize(context)) + " KB");
                    mHandler.postDelayed(this, Settings.getInterval() * 1000);


                }
            }, Settings.getInterval() * 1000);
        }
        catch(Exception ex)
        {
            Notify.showNotification(context,"Error while setting counter text");
        }
    }

    public void onClickStart(View v)
    {
        if(isMyServiceRunning(BackgroundIntentService.class)!=true) {      //service is stopped. Start it.
            Intent i = new Intent(this, BackgroundIntentService.class);
            try
            {
                    stopService(i);
            }
            catch (Exception ex)
            {
                Log.d(Settings.TAG, "Error occurred while stopping service before starting a new instance of it. Details:"+ex.toString());
            }

            startService(i);
            btnStart.setText("Stop Collecting Stats..");
            tvWarning.setTextColor(Color.GREEN);
            tvWarning.setText("Status");
            tvWarningText.setText("Collecting data....");

            setCounter();
        }
        else
        {
            stopService(new Intent(this,BackgroundIntentService.class));
            BackgroundIntentService.stop_request=true;
           // ShowMessage.message("Service stopped.", context);

            btnStart.setText("Start Collecting Stats..");
            tvWarning.setTextColor(Color.parseColor("#FFA500"));
            tvWarning.setText("Status");
            tvWarningText.setText("Ready for Collection.");

            mHandler.removeCallbacks(runnable);
        }
    }

    public void gotoUsageAccess(View v)
    {
        Intent i = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(i);
           }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy()
    {
        mHandler.removeCallbacks(null);
        super.onDestroy();
    }

    public void onClickShowGUI(View view) {
        Intent intent = new Intent(this, Terms.class);
        startActivity(intent);
    }
}