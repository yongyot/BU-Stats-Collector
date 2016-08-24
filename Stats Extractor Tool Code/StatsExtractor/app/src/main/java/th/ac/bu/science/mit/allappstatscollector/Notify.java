package th.ac.bu.science.mit.allappstatscollector;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;

import th.ac.bu.science.mit.allappstatscollector.Activities.MainActivity;

/**
 * Created by Komal on 3/8/2016.
 */
public class Notify {

    static int id=0;
    public static void showNotification(Context context, String message)
    {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder =new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle("Stats Collector - Info");

        int icon;
        if(Build.VERSION.SDK_INT>21) {
            icon = R.mipmap.ic_test;
            notificationBuilder.setColor(Color.parseColor("#78909C"));
        }
        else
            icon=R.mipmap.ic_test103;

        notificationBuilder.setSmallIcon(icon);
        notificationBuilder.setContentText(message).build();
        notificationBuilder.setPriority(Notification.PRIORITY_LOW);
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));


        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(resultPendingIntent);

        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(id, notificationBuilder.build());

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(650);

        /**/
        id++;
        /**/
    }
}
