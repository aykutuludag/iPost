package app.ipost.functionality;

import android.app.Activity;
import android.telephony.SmsManager;


public class SendSMS extends Activity {

    public static boolean sendSMS(String phoneNo, String msg) {
        boolean isSuccess;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            isSuccess = true;
        } catch (Exception ex) {
            isSuccess = false;
            ex.printStackTrace();
        }
        return isSuccess;
    }
}
