package app.ipost.receiver;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;

import java.net.URLEncoder;

import app.ipost.R;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {

    int eventID;
    long startTime;
    int isDelivered;
    String receiverName;
    String receiverMail;
    String receiverPhone;
    String mailTitle;
    String mailContent;
    String mailAttachment;
    String smsContent;
    String messengerContent;
    String messengerAttachment;
    String whatsappContent;
    String whatsappAttachment;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            reScheduleAlarms(context);
        } else {
            eventID = intent.getIntExtra("EVENT_ID", 0);
            getPostInfo(context);
        }
    }

    public void reScheduleAlarms(Context context) {
        //DATABESE DEN ÇEK ALARMLARI KUR
    }

    public void getPostInfo(Context context) {
        SQLiteDatabase database_account2 = context.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        Cursor cur2 = database_account2.rawQuery("SELECT * FROM posts WHERE ID=? ", new String[]{eventID + ""});
        if (cur2 != null && cur2.getCount() != 0) {
            cur2.moveToFirst();
            for (int i = 0; i < cur2.getColumnCount(); i++) {
                switch (i % 14) {
                    case 0:
                        //eventID fetched already
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
                            //    sendMessage();
                        }
                        break;
                    case 9:
                        smsContent = cur2.getString(i);
                        if (smsContent != null && !smsContent.contains("null") && smsContent.length() > 1) {
                            sendSMS();
                        }
                        break;
                    case 10:
                        messengerContent = cur2.getString(i);
                        break;
                    case 11:
                        messengerAttachment = cur2.getString(i);
                        if (messengerContent != null && !messengerContent.contains("null") && messengerContent.length() > 1) {
                            //   sendMessenger(context);
                        }
                        break;
                    case 12:
                        whatsappContent = cur2.getString(i);
                        break;
                    case 13:
                        whatsappAttachment = cur2.getString(i);
                        if (whatsappContent != null && !whatsappContent.contains("null") && whatsappContent.length() > 1) {
                            sendWhatsapp(context);
                        }
                        break;
                }
            }
            cur2.close();
        }
    }

    public void sendSMS() {
        String[] phones = receiverPhone.split(";");
        System.out.println("alıcı numaralar SMS: " + receiverPhone);
        try {
            for (String phone : phones) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, smsContent, null, null);
                Thread.sleep(1500);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

  /*  public static MimeMessage createEmailWithAttachment(String to,
                                                        String from,
                                                        String subject,
                                                        String bodyText,
                                                        File file)
            throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/plain");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);

        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName(file.getName());

        multipart.addBodyPart(mimeBodyPart);
        email.setContent(multipart);

        return email;
    }*/

  /*  public Message sendMessage(Gmail service,
                               String userId,)
            throws MessagingException, IOException {
        Message message = createEmailWithAttachment(receiverMail, "", mailTitle, mailContent, new File(mailAttachment));
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }*/

    public void sendWhatsapp(Context context) {
        String[] phones = receiverPhone.split(";"); // contains spaces.
        System.out.println("alıcı numaralar Whatsapp: " + receiverPhone);
        System.out.println(whatsappContent);
        for (int i = 0; i < phones.length; i++) {
            if (!phones[i].contains("+")) {
                phones[i] = "+9" + phones[i];
            }
            String clearPhone = phones[i].replace("+", "").replace(" ", "");
            System.out.println("alıcı numaralarTEMİZ: " + clearPhone);
            try {
                PackageManager packageManager = context.getPackageManager();
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String url = "https://api.whatsapp.com/send?phone=" + clearPhone + "&text=" + URLEncoder.encode(whatsappContent, "UTF-8");
                sendIntent.setPackage("com.whatsapp");
                sendIntent.setData(Uri.parse(url));
                if (sendIntent.resolveActivity(packageManager) != null) {
                    PendingIntent pIntent = PendingIntent.getActivity(context, i, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                    Notification noti = new NotificationCompat.Builder(context)
                            .setContentTitle("iPost")
                            .setContentText(context.getString(R.string.action_required))
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setAutoCancel(true)
                            .setContentIntent(pIntent)
                            .setLargeIcon(bm)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                            .setLights(Color.RED, 500, 500)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).build();

                    NotificationManager notificationManager = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(i, noti);
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*public void sendMessenger(Context context) {
        String mimeType = "image/jpeg";

        ShareToMessengerParams shareToMessengerParams =
                ShareToMessengerParams.newBuilder(contentUri, mimeType)
                        .build();

        String metadata = "{ \"image\" : \"trees\" }";
        ShareToMessengerParams shareToMessengerParams =
                ShareToMessengerParams.newBuilder(contentUri, "image/jpeg")
                        .setMetaData(metadata)
                        .build();

        MessengerUtils.shareToMessenger(
                this,
                REQUEST_CODE_SHARE_TO_MESSENGER,
                shareToMessengerParams);
    }*/
}