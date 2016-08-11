package th.ac.bu.science.mit.allappstatscollector;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class AppList extends AppCompatActivity {

    PackageManager packageManager = null;
    List<ApplicationInfo> appList = null;
    AppEntityAdapter listadaptor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        packageManager = getPackageManager();
        appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        listadaptor = new AppEntityAdapter(this,
                R.layout.layout_app_entity, appList);

        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(listadaptor);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                ApplicationInfo applicationInfo = appList.get(position);

                Intent intent = new Intent(AppList.this, AppInfo.class);
                intent.putExtra("packageName", applicationInfo.packageName);
                startActivity(intent);


            }
        });
    }

    public void BackHome(View view) {
        Intent intent = new Intent(this, DashBoard.class);
        startActivity(intent);
    }

    public void ShowSettings(View view) {

    }

    public void ShowHelp(View view) {

    }
}