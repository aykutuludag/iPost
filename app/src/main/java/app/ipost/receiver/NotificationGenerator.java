package app.ipost.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Locale;

import app.ipost.R;
import app.ipost.SignInActivity;

class NotificationGenerator {

    static void generateNotification(Context context) {
        //BURADA GÜNLÜK RAPOR OLACAK

        Intent intent = new Intent(context, SignInActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy EEEE", Locale.getDefault());

        Notification noti = new NotificationCompat.Builder(context)
                .setContentTitle("iPost")
                .setContentText("Günlük rapor: ")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .setLargeIcon(bm)
                .setDefaults(Notification.DEFAULT_ALL).build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti);
    }
}