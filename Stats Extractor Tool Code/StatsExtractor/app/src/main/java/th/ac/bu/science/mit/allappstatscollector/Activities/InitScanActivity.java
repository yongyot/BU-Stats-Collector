package th.ac.bu.science.mit.allappstatscollector.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;

import th.ac.bu.science.mit.allappstatscollector.Managers.VirusTotalManager;
import th.ac.bu.science.mit.allappstatscollector.Models.AppsInfo;
import th.ac.bu.science.mit.allappstatscollector.R;

public class InitScanActivity extends GeneralActivity {

    VirusTotalManager.ResponseListener listener = new VirusTotalManager.ResponseListener() {
        @Override
        public void OnGetReportProgress (String message, int progress) {
            Log.w ("myInfo", "OnGetReportProgress " + message + progress);
            TextView textProgress = (TextView) findViewById(R.id.textProgress);
            textProgress.setText(message);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setProgress(progress);
        }

        @Override
        public void OnGetReportFinished (boolean isSuccessed, List<AppsInfo> appList, String reason) {
            Log.w ("myInfo", "OnGetReportFinished");
            if (isSuccessed) {
                for (AppsInfo app : appList) {
                    Log.w("myInfo", "response : " + app.packageName + " : " + app.hash );
                    if (app.report != null) {
                        Log.w("myInfo", "report : " + app.report.toString());
                    }
                    //Check report for malware
                    //Write app list, hash and result to file
                }

                GoToDashboard();
            } else {
                Log.e("myInfo", "Listener Failed : " + reason);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_scan);

        SetTitle(getString(R.string.app_name));

        ImageView circleImage = (ImageView) findViewById(R.id.circle3);
        Animation initialiseAnimation = AnimationUtils.loadAnimation(this, R.anim.initialise_anim);
        circleImage.startAnimation(initialiseAnimation);

        GetReport();
    }

    public void GetReport () {
        VirusTotalManager virusTotalManager = new VirusTotalManager(this, listener);
        virusTotalManager.execute();
    }

    void GoToDashboard () {
        Intent intent = new Intent(InitScanActivity.this, DashBoardActivity.class);
        startActivity(intent);
    }

}
