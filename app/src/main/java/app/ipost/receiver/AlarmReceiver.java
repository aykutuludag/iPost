package app.ipost.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;

import app.ipost.R;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {

    int eventID;
    long startTime;
    int isDelivered;
    String receiverName, receiverMail, receiverPhone, mailTitle, mailContent, mailAttachment, smsContent, messengerContent, messengerAttachment, whatsappContent, whatsappAttachment;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            reScheduleAlarms(context);
        } else {
            eventID = intent.getIntExtra("EVENT_ID", 0);
            getPostInfo(context, intent);
        }
    }

    public void reScheduleAlarms(Context context) {
        //DATABESE DEN ÇEK ALARMLARI KUR
    }

    public void getPostInfo(Context context, Intent intent) {
        SQLiteDatabase database_account2 = context.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        Cursor cur2 = database_account2.rawQuery("SELECT * FROM posts WHERE ID=? ", new String[]{eventID + ""});
        if (cur2 != null && cur2.getCount() != 0) {
            cur2.moveToFirst();
            for (int i = 0; i < cur2.getColumnCount(); i++) {
                switch (i % 14) {
                    case 0:
                        eventID = cur2.getInt(i);
                        break;
                    case 1:
                        receiverName = cur2.getString(i);
                        break;
                    case 2:
                        receiverMail = cur2.getString(i);
                        break;
                    case 3:
                        receiverPhone = cur2.getString(i);
                        break;
                    case 4:
                        startTime = cur2.getLong(i);
                        break;
                    case 5:
                        isDelivered = cur2.getInt(i);
                        break;
                    case 6:
                        mailTitle = cur2.getString(i);
                        break;
                    case 7:
                        mailContent = cur2.getString(i);
                        break;
                    case 8:
                        mailAttachment = cur2.getString(i);
                        if (mailTitle != null && !mailTitle.contains("null")) {
                            sendMail(context);
                        }
                        break;
                    case 9:
                        smsContent = cur2.getString(i);
                        if (smsContent != null && !smsContent.contains("null")) {
                            sendSMS();
                        }
                        break;
                    case 10:
                        messengerContent = cur2.getString(i);
                        break;
                    case 11:
                        messengerAttachment = cur2.getString(i);
                        if (messengerContent != null && !messengerContent.contains("null")) {
                            sendMessenger(context);
                        }
                        break;
                    case 12:
                        whatsappContent = cur2.getString(i);
                        break;
                    case 13:
                        whatsappAttachment = cur2.getString(i);
                        if (whatsappContent != null && !whatsappContent.contains("null")) {
                            sendWhatsapp(context);
                        }
                        break;
                }
            }
            cur2.close();
        }
    }

    public void sendSMS() {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(receiverPhone, null, smsContent, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendMail(Context context) {

    }

    public void sendWhatsapp(Context context) {
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        String toNumber = receiverPhone; // contains spaces.
        toNumber = toNumber.replace("+", "").replace(" ", "");
        sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_TEXT, whatsappContent);
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(whatsappAttachment));
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.setType("image/*");

        PendingIntent pIntent = PendingIntent.getActivity(context, eventID, sendIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        Notification noti = new NotificationCompat.Builder(context)
                .setContentTitle("iPost")
                .setContentText("Action requiered: Click here to send Whatsapp message.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .setLargeIcon(bm)
                .setDefaults(Notification.DEFAULT_ALL).build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti);
    }

    public void sendMessenger(Context context) {

    }
}