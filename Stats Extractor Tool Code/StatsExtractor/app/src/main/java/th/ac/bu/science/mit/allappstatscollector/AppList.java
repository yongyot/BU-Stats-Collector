package th.ac.bu.science.mit.allappstatscollector;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AppList extends GeneralActivity {

    PackageManager packageManager = null;
    List<ApplicationInfo> appList = null;
    AppEntityAdapter listadaptor = null;

    List<ApplicationInfo> installList = new ArrayList<ApplicationInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        SetTitle("App List");

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

        listadaptor = new AppEntityAdapter(this,
                R.layout.layout_app_entity, installList);

        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(listadaptor);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                ApplicationInfo applicationInfo = installList.get(position);

                Intent intent = new Intent(AppList.this, AppInfo.class);
                intent.putExtra("packageName", applicationInfo.packageName);
                startActivity(intent);
            }
        });

        View menu = (View) findViewById(R.id.menuAppList);
        menu.setVisibility(View.GONE);
    }
}