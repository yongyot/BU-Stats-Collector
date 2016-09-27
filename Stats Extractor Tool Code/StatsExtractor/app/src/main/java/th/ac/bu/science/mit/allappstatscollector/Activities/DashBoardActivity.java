package th.ac.bu.science.mit.allappstatscollector.Activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import th.ac.bu.science.mit.allappstatscollector.R;

public class DashBoardActivity extends GeneralActivity {

    boolean isSuspicious = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        SetTitle(getString(R.string.app_name));

        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> installList = new ArrayList<>();
        for (ApplicationInfo info : appList) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    installList.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        TextView textTotalApp = (TextView) findViewById(R.id.textTotalApp);
        textTotalApp.setText(String.format("%s",installList.size()));

        View menu = (View) findViewById(R.id.menuHome);
        menu.setVisibility(View.GONE);
    }

    public void OnMalwareMockupClick (View view){
        ImageView circle = (ImageView) findViewById(R.id.circle);
        circle.setImageResource(R.drawable.circle_red);

        TextView status = (TextView) findViewById(R.id.textStatus);
        status.setTextColor(Color.RED);
        status.setText(String.format("%d %s", 1, getResources().getString(R.string.dashboard_suspicious)));
        isSuspicious = true;
    }

    public void OnDashboardCircleClick (View view) {
        if (isSuspicious) {
            ShowMockupMalware(view);
        } else {
            ShowAppList(view);
        }
    }

    void ShowMockupMalware (View view) {
        Intent intent = new Intent(this, AppListActivity.class);
        boolean isSuspicious = this.isSuspicious;
        intent.putExtra("is_suspicious", isSuspicious);
        startActivity(intent);
    }
}
