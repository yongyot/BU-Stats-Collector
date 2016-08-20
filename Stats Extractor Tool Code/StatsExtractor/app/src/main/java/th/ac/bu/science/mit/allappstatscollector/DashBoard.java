package th.ac.bu.science.mit.allappstatscollector;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DashBoard extends GeneralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        SetTitle(getString(R.string.app_name));

        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> installList = new ArrayList<ApplicationInfo>();
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
}
