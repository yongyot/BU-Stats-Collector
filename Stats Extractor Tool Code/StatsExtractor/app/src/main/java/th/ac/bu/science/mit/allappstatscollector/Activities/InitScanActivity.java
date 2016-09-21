package th.ac.bu.science.mit.allappstatscollector.Activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import th.ac.bu.science.mit.allappstatscollector.Managers.VirusTotalManager;
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

//        mHandler = new Handler();
//        mHandler.postDelayed(FinishInitialise, tempWaitTime);

        GetReport();

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

    public void GetReport () {
        Log.w ("myInfo", "Manager");
        VirusTotalManager virusTotalManager = new VirusTotalManager();
        virusTotalManager.GetAllAppReport(this);

        virusTotalManager.SendAPI(new VirusTotalManager.ResponseListener() {
            @Override
            public void OnSuccess () {
                Log.w ("myInfo", "SUCCESS");
            }

            @Override
            public void OnFailed (String reason) {
                Log.w ("myInfo", "FAILED");
            }
                                  }
        );
    }
}
