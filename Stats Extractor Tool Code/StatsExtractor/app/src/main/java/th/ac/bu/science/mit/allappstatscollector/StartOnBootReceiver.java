package th.ac.bu.science.mit.allappstatscollector;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Komal on 2/5/2016.
 */
public class StartOnBootReceiver   extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, BackgroundIntentService.class);
            context.startService(pushIntent);
        }
    }
}
