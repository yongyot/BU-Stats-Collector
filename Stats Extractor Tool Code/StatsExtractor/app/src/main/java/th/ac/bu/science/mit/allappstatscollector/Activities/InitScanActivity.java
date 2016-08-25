package th.ac.bu.science.mit.allappstatscollector.Activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import th.ac.bu.science.mit.allappstatscollector.Activities.DashBoardActivity;
import th.ac.bu.science.mit.allappstatscollector.R;

public class InitScanActivity extends GeneralActivity {

//    enum InitState {Idle, Initialising}
//
//    InitState initState = InitState.Initialising;
    int tempWaitTime = 4000;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_scan);

        SetTitle(getString(R.string.app_name));

        mHandler = new Handler();
        mHandler.postDelayed(FinishInitialise, tempWaitTime);

        ImageView circleImage = (ImageView) findViewById(R.id.circle3);
        Animation initialiseAnimation = AnimationUtils.loadAnimation(this, R.anim.initialise_anim);
        circleImage.startAnimation(initialiseAnimation);
    }

    Runnable FinishInitialise = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(InitScanActivity.this, DashBoardActivity.class);
            startActivity(intent);
        }
    };
}