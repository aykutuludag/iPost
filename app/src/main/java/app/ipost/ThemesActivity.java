package app.ipost;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;


public class ThemesActivity extends AppCompatActivity implements View.OnClickListener {

    ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3, expandableLayout4, expandableLayout5;
    Button siyahSet, buyRed, buyGreen, buyPurple, buyOrange;
    IInAppBillingService mService;
    ServiceConnection mServiceConn;
    SharedPreferences prefs;
    boolean hasRed, hasGreen, hasPurple, hasOrange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);

        // Analytics
        Tracker t = AnalyticsApplication.getDefaultTracker();
        t.setScreenName("Themes");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        expandableLayout1 = findViewById(R.id.expandableLayout1);
        expandableLayout2 = findViewById(R.id.expandableLayout2);
        expandableLayout3 = findViewById(R.id.expandableLayout3);
        expandableLayout4 = findViewById(R.id.expandableLayout4);
        expandableLayout5 = findViewById(R.id.expandableLayout5);

        siyahSet = findViewById(R.id.siyah_set);
        siyahSet.setOnClickListener(this);

        buyOrange = findViewById(R.id.turuncu_buy);
        buyOrange.setOnClickListener(this);

        buyPurple = findViewById(R.id.mor_buy);
        buyPurple.setOnClickListener(this);

        buyGreen = findViewById(R.id.yesil_buy);
        buyGreen.setOnClickListener(this);

        buyRed = findViewById(R.id.kirmizi_buy);
        buyRed.setOnClickListener(this);

        //Learn the current state. Does user has theme xx?
        InAppBilling();
        prefs = getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);
        hasRed = prefs.getBoolean("Red", false);
        hasGreen = prefs.getBoolean("Green", false);
        hasPurple = prefs.getBoolean("Purple", false);
        hasOrange = prefs.getBoolean("Orange", false);

        if (hasRed) {
            buyRed.setText("Set");
        } else {
            buyRed.setText("0.99 $");
        }

        if (hasGreen) {
            buyGreen.setText("Set");
        } else {
            buyGreen.setText("0.99 $");
        }

        if (hasPurple) {
            buyPurple.setText("Set");
        } else {
            buyPurple.setText("0.99 $");
        }

        if (hasOrange) {
            buyOrange.setText("Set");
        } else {
            buyOrange.setText("0.99 $");
        }
    }

    public void expandableButton1(View view) {
        expandableLayout1.toggle(); // toggle expand and collapse
        expandableLayout2.collapse();
        expandableLayout3.collapse();
        expandableLayout4.collapse();
        expandableLayout5.collapse();
    }

    public void expandableButton2(View view) {
        expandableLayout2.toggle(); // toggle expand and collapse
        expandableLayout1.collapse();
        expandableLayout3.collapse();
        expandableLayout4.collapse();
        expandableLayout5.collapse();
    }

    public void expandableButton3(View view) {
        expandableLayout3.toggle(); // toggle expand and collapse
        expandableLayout1.collapse();
        expandableLayout2.collapse();
        expandableLayout4.collapse();
        expandableLayout5.collapse();
    }

    public void expandableButton4(View view) {
        expandableLayout4.toggle(); // toggle expand and collapse
        expandableLayout1.collapse();
        expandableLayout2.collapse();
        expandableLayout3.collapse();
        expandableLayout5.collapse();
    }

    public void expandableButton5(View view) {
        expandableLayout5.toggle(); // toggle expand and collapse
        expandableLayout1.collapse();
        expandableLayout2.collapse();
        expandableLayout3.collapse();
        expandableLayout4.collapse();
    }

    private void InAppBilling() {
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                try {
                    checkOwnedThemes();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    public void checkOwnedThemes() throws RemoteException {
        Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
        if (ownedItems.getInt("RESPONSE_CODE") == 0) {
            ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            if (ownedSkus != null) {
                if (ownedSkus.contains("red")) {
                    prefs.edit().putBoolean("Red", true).apply();
                } else {
                    prefs.edit().putBoolean("Red", false).apply();
                }

                if (ownedSkus.contains("green")) {
                    prefs.edit().putBoolean("Green", true).apply();
                } else {
                    prefs.edit().putBoolean("Green", false).apply();
                }

                if (ownedSkus.contains("Orange")) {
                    prefs.edit().putBoolean("Orange", true).apply();
                } else {
                    prefs.edit().putBoolean("Orange", false).apply();
                }

                if (ownedSkus.contains("purple")) {
                    prefs.edit().putBoolean("Purple", true).apply();
                } else {
                    prefs.edit().putBoolean("Purple", false).apply();
                }
            }
        }
    }

    public void buyTheme(String themeName, int requestCode) throws RemoteException, IntentSender.SendIntentException {
        Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), themeName, "inapp",
                "tYMgwhg1DVikb4R4iLNAO5pNj");
        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
        if (pendingIntent != null) {
            startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, new Intent(), 0,
                    0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.siyah_set:
                prefs.edit().putString("DefaultTheme", "Black").apply();
                Toast.makeText(ThemesActivity.this, "New theme: BLACK. All things setting up and will be ready soon!",
                        Toast.LENGTH_LONG).show();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                break;
            case R.id.turuncu_buy:
                if (hasOrange) {
                    prefs.edit().putString("DefaultTheme", "Orange").apply();
                    Toast.makeText(ThemesActivity.this, "New theme: ORANGE. All things setting up and will be ready soon!",
                            Toast.LENGTH_LONG).show();
                    Intent i5 = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i5);
                    finish();
                } else {
                    try {
                        buyTheme("orange", 1001);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.mor_buy:
                if (hasPurple) {
                    prefs.edit().putString("DefaultTheme", "Purple").apply();
                    Toast.makeText(ThemesActivity.this, "New theme: PURPLE. All things setting up and will be ready soon!",
                            Toast.LENGTH_LONG).show();
                    Intent i4 = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i4);
                    finish();
                } else {
                    try {
                        buyTheme("purple", 1002);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.yesil_buy:
                if (hasGreen) {
                    prefs.edit().putString("DefaultTheme", "Green").apply();
                    Toast.makeText(ThemesActivity.this, "New theme: GREEN. All things setting up and will be ready soon!",
                            Toast.LENGTH_LONG).show();

                    Intent i3 = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i3);
                    finish();
                } else {
                    try {
                        buyTheme("green", 1003);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.kirmizi_buy:
                if (hasRed) {
                    prefs.edit().putString("DefaultTheme", "Red").apply();
                    Toast.makeText(ThemesActivity.this, "New theme: RED",
                            Toast.LENGTH_LONG).show();
                    Intent i2 = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i2);
                    finish();
                } else {
                    try {
                        buyTheme("red", 1004);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(ThemesActivity.this, "You bought Theme: ORANGE. All things setting up and will be ready soon!",
                        Toast.LENGTH_LONG).show();

                prefs.edit().putBoolean("Orange", true).apply();
                prefs.edit().putString("DefaultTheme", "Orange").apply();

                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(ThemesActivity.this, "Error in purchasing process. Try again later...",
                        Toast.LENGTH_LONG).show();
                prefs.edit().putBoolean("Orange", false).apply();
            }
        }

        if (requestCode == 1002) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(ThemesActivity.this, "You bought Theme: PURPLE. All things setting up and will be ready soon!",
                        Toast.LENGTH_LONG).show();

                prefs.edit().putBoolean("Purple", true).apply();
                prefs.edit().putString("DefaultTheme", "Purple").apply();

                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(ThemesActivity.this, "Error in purchasing process. Try again later...",
                        Toast.LENGTH_LONG).show();
                prefs.edit().putBoolean("Purple", false).apply();
            }
        }

        if (requestCode == 1003) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(ThemesActivity.this, "You bought Theme: RED. All things setting up and will be ready soon!",
                        Toast.LENGTH_LONG).show();

                prefs.edit().putBoolean("Green", true).apply();
                prefs.edit().putString("DefaultTheme", "Green").apply();

                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(ThemesActivity.this, "Error in purchasing process. Try again later...",
                        Toast.LENGTH_LONG).show();
                prefs.edit().putBoolean("Green", false).apply();
            }
        }

        if (requestCode == 1004) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(ThemesActivity.this, "You bought Theme: RED. All things setting up and will be ready soon!",
                        Toast.LENGTH_LONG).show();

                prefs.edit().putBoolean("Red", true).apply();
                prefs.edit().putString("DefaultTheme", "Red").apply();

                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(ThemesActivity.this, "Error in purchasing process. Try again later...",
                        Toast.LENGTH_LONG).show();
                prefs.edit().putBoolean("Red", false).apply();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceConn != null) {
            unbindService(mServiceConn);
        }
    }
}