package th.ac.bu.science.mit.allappstatscollector.Activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.net.NetworkInterface;
import th.ac.bu.science.mit.allappstatscollector.BackgroundIntentService;
import th.ac.bu.science.mit.allappstatscollector.HashGen;
import th.ac.bu.science.mit.allappstatscollector.Notify;
import th.ac.bu.science.mit.allappstatscollector.R;
import th.ac.bu.science.mit.allappstatscollector.Settings;
import th.ac.bu.science.mit.allappstatscollector.StatsFileManager;
import th.ac.bu.science.mit.allappstatscollector.Utils.SharePrefs;


public class MainActivity extends ActionBarActivity {

    private static String TAG = "MainActivity";
    private static Context mContext;
    private final Handler mHandler = new Handler();
    private TextView tvWarning, tvWarningText, tvMacAddress, tvLastTime;
    private Button btnStart, btnUsage;
    private Runnable mRunnable;
    private boolean isAppPaused = false;

    // ---------------------------------
    // ----------- Lifecycle -----------
    // ---------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        mContext = getApplicationContext();
        Settings.loadSettings(mContext);

        btnStart = (Button)findViewById(R.id.btnStartExtracting);
        btnUsage = (Button)findViewById(R.id.btnUsageAccess);
        tvWarning = (TextView)findViewById(R.id.tvWarning);
        tvWarningText = (TextView)findViewById(R.id.tvWarningText);
        tvMacAddress = (TextView)findViewById(R.id.tvMacAddress);
        tvLastTime = (TextView)findViewById(R.id.tvLastTime);
    }

    @Override
    protected void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageRecevier, new IntentFilter("updateUI"));
        isAppPaused = false;
        super.onStart();

        setView();
        boolean isFirstTime = SharePrefs.getPreferenceBoolean(mContext, "firstTime", false);
        if (!isFirstTime){
            initHasFile();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mMessageRecevier);
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }

        isAppPaused = true;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(null);
        super.onDestroy();
    }

    private void setView(){
        tvMacAddress.setText("ID: " + Settings.getMacAddress());

        if(Settings.isUsageAccessGranted(mContext)){      //if user access is granted set the start button to visible and user access to invisible.

            btnStart.setVisibility(View.VISIBLE);
            btnUsage.setVisibility(View.INVISIBLE);

            if(isServiceRunning(BackgroundIntentService.class)){ //access is already granted and service is running.

                btnStart.setText("Stop Collecting Data");
                tvWarning.setTextColor(Color.GREEN);

                tvWarning.setText("Status:");
                tvWarningText.setText("Collecting data....");
                setCounter();
            } else {
                btnStart.setText("Start Collecting Data");
                tvWarning.setTextColor(Color.parseColor("#FFA500"));    //orange color
                tvWarning.setText("Status:");
                tvWarningText.setText("Ready for Collection.");
            }
        } else {                                            //user access is not granted. Set the btnStart to invisble and usage access to visible.

            btnStart.setVisibility(View.INVISIBLE);
            btnUsage.setVisibility(View.VISIBLE);
            tvWarning.setText("Warning:");
            tvWarning.setTextColor(Color.RED);
            tvWarningText.setText("Please turn on usage access first.");
        }
    }

    private void initHasFile(){

        if(/*Settings.WIFI_INTERFACE != null &&*/ Settings.MAC != null) {

            try {
                if (NetworkInterface.getByName("rmnet0") == null) {
                    Notify.showNotification(mContext, "Data interface error.");
                }
            } catch(Exception ex) {
                Log.d(Settings.TAG,"Can not find data interface name. Details: "+ex.toString());
            }

            new Thread(new Runnable() {
                public void run() {
                    SharePrefs.setPreference(mContext, "firstTime", true);

                    HashGen hashGen = new HashGen();
                    hashGen.getAllAppInfo(mContext);

                }
            }).start();
        }
    }

    // ------------------------------
    // -------- Event Click ---------
    // ------------------------------

    public void onClickStart(View v) {

        if(!isServiceRunning(BackgroundIntentService.class)) { //service is stopped. Start it.
            Intent intent = new Intent(this, BackgroundIntentService.class);
            try {
                stopService(intent);
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
            }

            startService(intent);
            btnStart.setText("Stop Collecting Stats..");
            tvWarning.setTextColor(Color.GREEN);
            tvWarning.setText("Status");
            tvWarningText.setText("Collecting data....");

            setCounter();
        } else {
            stopService(new Intent(this,BackgroundIntentService.class));
            BackgroundIntentService.stop_request=true;

            btnStart.setText("Start Collecting Stats..");
            tvWarning.setTextColor(Color.parseColor("#FFA500"));
            tvWarning.setText("Status");
            tvWarningText.setText("Ready for Collection.");

            mHandler.removeCallbacks(mRunnable);
        }
    }

    public void onClickTurnOnUserAccess(View v) {
        Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }

    public void onClickShowGUI(View view) {
        Intent intent = new Intent(this, TermsActivity.class);
        startActivity(intent);
    }

    private BroadcastReceiver mMessageRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(!isAppPaused) {
                //update UI
                if(isServiceRunning(BackgroundIntentService.class)) {
                    btnStart.setText("Stop Collecting Data");
                    tvWarning.setTextColor(Color.GREEN);

                    tvWarning.setText("Status:");
                    tvWarningText.setText("Collecting data....");
                    setCounter();
                } else {
                    btnStart.setText("Start Collecting Data");
                    tvWarning.setTextColor(Color.parseColor("#FFA500"));    //orange color
                    tvWarning.setText("Status:");
                    tvWarningText.setText("Ready for Collection.");
                }
            }
        }
    };

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void setCounter() {
        try {
            final TextView tvCounter = (TextView) findViewById(R.id.tvCounter);
            final TextView tvFileSize = (TextView) findViewById(R.id.tvFileSize);

            tvCounter.setText("Session: " + Integer.toString(BackgroundIntentService.counter));
            tvFileSize.setText("File Size: " + Long.toString(StatsFileManager.getFileSize(mContext)) + " KB");
            tvLastTime.setText("เวลาที่ส่งไฟล์ล่าสุด: " + SharePrefs.getPreferenceString(mContext, "las_time_upload", ""));
            Log.d("emji", "get currentDateandTime: " + SharePrefs.getPreferenceString(mContext, "las_time_upload", ""));
            mHandler.postDelayed(mRunnable = new Runnable() {
                @Override
                public void run() {

                    tvCounter.setText("Session: " + Integer.toString(BackgroundIntentService.counter));
                    tvFileSize.setText("File Size: " + Long.toString(StatsFileManager.getFileSize(mContext)) + " KB");
                    mHandler.postDelayed(this, Settings.getInterval() * 1000);


                }
            }, Settings.getInterval() * 1000);
        } catch(Exception ex) {
            Notify.showNotification(mContext, "Error while setting counter text");
        }
    }
}