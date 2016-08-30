package th.ac.bu.science.mit.allappstatscollector;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Komal on 3/14/2016.
 */
public class RestartServiceReceiver  extends BroadcastReceiver {

    private static final String TAG = "stats-result";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(Settings.TAG,"Notification Received");
        if(!isMyServiceRunning(BackgroundIntentService.class,context)){
            context.startService(new Intent(context.getApplicationContext(), BackgroundIntentService.class));
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

