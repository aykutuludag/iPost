package app.ipost.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.ipost.R;
import app.ipost.SignInActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            reScheduleAlarms(context);
        } else {
           /* //BURADA GÖNDERİYİ FACEBOOK, WHATSAPP, SMS VE MAİL OLARAK GÖNDERİYORUZ. GÜNLÜK BİLDİRİM İŞİ DE BURADA HALLOLACAK
            String phoneNumber = intent.getStringExtra("phone");
            String message = intent.getStringExtra("message");
            boolean isSuccess;
            System.out.println(phoneNumber + message);
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                isSuccess = true;
            } catch (Exception ex) {
                isSuccess = false;
                ex.printStackTrace();
                System.out.println(ex);
            }

            System.out.println("AQQQQQ" + isSuccess);*/

            intent = new Intent(context, SignInActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy EEEE", Locale.getDefault());

            Notification noti = new NotificationCompat.Builder(context)
                    .setContentTitle("Günlük Burçlar")
                    .setContentText(sdf.format(new Date()) + ": " + "Günlük burç yorumunuz")
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

    public void reScheduleAlarms(Context context) {
        //DATABESE DEN ÇEK ALARMLARI KUR
    }
}