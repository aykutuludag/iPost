package app.ipost;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class ThemesActivity extends AppCompatActivity {

    ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3, expandableLayout4, expandableLayout5;

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
}