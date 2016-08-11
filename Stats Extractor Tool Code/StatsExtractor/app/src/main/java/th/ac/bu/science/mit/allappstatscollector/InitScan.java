package th.ac.bu.science.mit.allappstatscollector;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class InitScan extends AppCompatActivity {

    enum InitState {Idle, Initialising}

    InitState initState = InitState.Initialising;
    int mInterval = 10;
    Handler mHandler;
    int maxHeight;
    ImageView circle;
    ImageView circle2;
    int loopCount = 0;
    int tempMaxLoop = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_scan);

        circle = (ImageView) findViewById(R.id.circle);
        circle2 = (ImageView) findViewById(R.id.circle2);

        mHandler = new Handler();
        StartRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                int height = circle.getHeight();
                maxHeight = circle2.getHeight();
                height += 10;
                if (height > maxHeight) {
                    height = 10;
                    loopCount++;
                    if (loopCount > tempMaxLoop) {
                        FinishInitialise();
                    }
                }
                circle.getLayoutParams().height = height;
                circle.requestLayout();
            } finally {
                if (initState == InitState.Initialising) {
                    mHandler.postDelayed(mStatusChecker, mInterval);
                }
            }
        }
    };

    void FinishInitialise() {
        initState = InitState.Idle;
        StopRepeatingTask();

        Intent intent = new Intent(this, DashBoard.class);
        startActivity(intent);
    }

    void StartRepeatingTask() {
        mStatusChecker.run();
    }

    void StopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
}
