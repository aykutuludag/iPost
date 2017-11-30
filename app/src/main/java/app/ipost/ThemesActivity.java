package app.ipost;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ViewFlipper;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import app.ipost.adapter.OnSwipeTouchListener;

public class ThemesActivity extends AppCompatActivity {

    ViewFlipper mViewFlipper;
    private float initialX;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);

        // Analytics
        Tracker t = AnalyticsApplication.getDefaultTracker();
        t.setScreenName("Themes");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        mViewFlipper = findViewById(R.id.myViewFlipper);
        mViewFlipper.setOnTouchListener(new OnSwipeTouchListener(ThemesActivity.this) {
            public void onSwipeTop() {

            }

            public void onSwipeRight() {
                if (mViewFlipper.getDisplayedChild() != 0) {
                    mViewFlipper.showPrevious();
                }
            }

            public void onSwipeLeft() {
                if (mViewFlipper.getDisplayedChild() != 4) {
                    mViewFlipper.showNext();
                }
            }

            public void onSwipeBottom() {

            }

        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float finalX = touchevent.getX();
                if (initialX > finalX) {
                    if (mViewFlipper.getDisplayedChild() != 4) {
                        mViewFlipper.showNext();
                    }
                } else {
                    if (mViewFlipper.getDisplayedChild() != 4) {
                        mViewFlipper.showPrevious();
                    }
                }
                break;
        }
        return false;
    }
}