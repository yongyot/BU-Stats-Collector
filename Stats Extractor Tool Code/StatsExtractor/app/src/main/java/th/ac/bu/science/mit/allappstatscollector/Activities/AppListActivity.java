package th.ac.bu.science.mit.allappstatscollector.Activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import th.ac.bu.science.mit.allappstatscollector.Adapters.AppEntityAdapter;
import th.ac.bu.science.mit.allappstatscollector.R;

public class AppListActivity extends GeneralActivity {

    PackageManager packageManager = null;
    List<ApplicationInfo> appList = null;
    AppEntityAdapter listadaptor = null;

    List<ApplicationInfo> installList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        SetTitle("App List");

        boolean isSuspicious = false;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                isSuspicious = extras.getBoolean("is_suspicious");
            }
        } else {
            isSuspicious= (boolean) savedInstanceState.getSerializable("is_suspicious");
        }

        packageManager = getPackageManager();
        appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo info : appList) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    installList.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isSuspicious) {
            ShowMalwareMockup();
        } else {
            ShowAllApp();
        }

        View menu = findViewById(R.id.menuAppList);
        menu.setVisibility(View.GONE);
    }

    void ShowAllApp () {
        listadaptor = new AppEntityAdapter(this,
                R.layout.layout_app_entity, installList);

        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(listadaptor);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                ApplicationInfo applicationInfo = installList.get(position);

                Intent intent = new Intent(AppListActivity.this, AppInfoActivity.class);
                intent.putExtra("packageName", applicationInfo.packageName);
                startActivity(intent);
            }
        });
    }

    void ShowMalwareMockup () {
        List<ApplicationInfo> malwareList = new ArrayList<>();
        malwareList.add(installList.get(0));
        malwareList.add(installList.get(1));
        malwareList.add(installList.get(2));

        listadaptor = new AppEntityAdapter(this,
                R.layout.layout_app_suspicious, malwareList);

        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(listadaptor);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                ApplicationInfo applicationInfo = installList.get(position);

                Intent intent = new Intent(AppListActivity.this, AppInfoActivity.class);
                intent.putExtra("packageName", applicationInfo.packageName);
                startActivity(intent);
            }
        });
    }
}