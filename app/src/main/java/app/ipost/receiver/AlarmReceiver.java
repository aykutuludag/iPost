package app.ipost.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            reScheduleAlarms(context);
        } else {
            //BURADA GÖNDERİYİ FACEBOOK, WHATSAPP, SMS VE MAİL OLARAK GÖNDERİYORUZ. GÜNLÜK BİLDİRİM İŞİ DE BURADA HALLOLACAK
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

            System.out.println("AQQQQQ" + isSuccess);
        }
    }

    public void reScheduleAlarms(Context context) {
        //DATABESE DEN ÇEK ALARMLARI KUR
    }
}