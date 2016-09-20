package th.ac.bu.science.mit.allappstatscollector.Activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import th.ac.bu.science.mit.allappstatscollector.APIs.APIVirusTotal;
import th.ac.bu.science.mit.allappstatscollector.Managers.RestAPIManager;
import th.ac.bu.science.mit.allappstatscollector.Models.ModelVirusTotalReport;
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
        ArrayList<String> resourceList = new ArrayList<String> ();
        resourceList.add("YOOOO");
        resourceList.add("c7ede8a9100c0bf480ab764fef4961d7");

        APIVirusTotal virusTotalAPI = RestAPIManager.api.create(APIVirusTotal.class);
        final Call<List<ModelVirusTotalReport>> call = virusTotalAPI.GetReport("78660282aeaedccc679bb9b2e33095916ff8d356be6e77d05ef04a284c42deff",
                resourceList);

        call.enqueue(new Callback<List<ModelVirusTotalReport>>() {
            @Override
            public void onResponse(Call<List<ModelVirusTotalReport>> call, Response<List<ModelVirusTotalReport>> response) {
                String toSet = response.body().size() + " responses";

                for (int index=0; index < response.body().size(); index++) {
                    ModelVirusTotalReport report = response.body().get(index);
                    toSet += "\n"+ index + " : " + report.toString();
                }
                Log.w("myInfo", toSet);
            }

            @Override
            public void onFailure(Call<List<ModelVirusTotalReport>> call, Throwable t) {
                Log.w("myInfo", "Something went wrong: " + t.getMessage());
            }
        });
    }
}
