package th.ac.bu.science.mit.allappstatscollector;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AppInfo extends AppCompatActivity {

    String packageName;
    PackageManager packageManager = null;
    ApplicationInfo appInfo = null;
    PackageInfo packageInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        try {
            Intent intent = getIntent();
            packageName = intent.getExtras().getString("packageName");

            packageManager = getPackageManager();
            packageInfo = packageManager.getPackageInfo(packageName,
                    PackageManager.GET_PERMISSIONS
//                            | PackageManager.GET_PROVIDERS
            );
            appInfo = packageInfo.applicationInfo;

            ShowHeader();
            ShowInfo();
            ShowPermission();


//            if (packageInfo.providers != null) {
//                for (ProviderInfo provider : packageInfo.providers)
//                    Log.w("myInfo", "Provider " + provider.toString());
//            }

//            if (packageInfo.permissions != null) {
//                for (PermissionInfo pms : packageInfo.permissions) {
//                    Log.w("myInfo", "Permission Label " + pms.loadLabel(packageManager));
//                    Log.w("myInfo", "Permission Desc " + pms.loadDescription(packageManager));
//                }
//            }
//
//            if (packageInfo.activities != null) {
//                for (ActivityInfo activity : packageInfo.activities)
//                    Log.w("myInfo", "activities " + activity.toString());
//            }
//
//            if (packageInfo.configPreferences != null) {
//                Log.w("myInfo", "configPreferences not null");
//            }
//
//            if (packageInfo.instrumentation != null) {
//                Log.w("myInfo", "featureGroups not null");
//            }
//
//            if (packageInfo.services != null) {
//                Log.w("myInfo", "services not null");
//            }

//            for (String permission : permissions) {
//                Log.w("myInfo", "permission " + permission);
//            }
//            Log.w("myInfo", "requestedPermissions " + packageInfo.requestedPermissions.toString());
//            for (PermissionInfo permission : packageInfo.permissions) {
//                Log.w("myInfo", "permission " + permission.toString());
//            }

//            for (String key : meta.keySet()) {
//                Object value = meta.get(key);
//                Log.d("Info", String.format("%s %s (%s)", key,
//                        value.toString(), value.getClass().getName()));
//            }
//            Log.w("Info", appInfo.metaData.;
        } catch (final PackageManager.NameNotFoundException e) {
        }
    }

    void ShowHeader() {
        TextView textAppName = (TextView) findViewById(R.id.text_app_name);
        TextView textPackageName = (TextView) findViewById(R.id.text_package_name);
        ImageView iconView = (ImageView) findViewById(R.id.app_icon);

        textAppName.setText(appInfo.loadLabel(packageManager));
        textPackageName.setText(packageName);
        iconView.setImageDrawable(appInfo.loadIcon(packageManager));
    }

    void ShowInfo() {
        TextView textVersion = (TextView) findViewById(R.id.text_version_name);
        TextView textVersionCode = (TextView) findViewById(R.id.text_version_code);

        textVersion.setText(packageInfo.versionName);
        textVersionCode.setText(((Integer) packageInfo.versionCode).toString());
    }

    void ShowPermission() {
        ViewGroup permissionList = (ViewGroup) findViewById(R.id.permission_list);

        try {
            if (packageInfo.requestedPermissions != null) {
                for (String permission : packageInfo.requestedPermissions) {
                    PermissionInfo pmInfo = packageManager.getPermissionInfo(permission, 0);

                    TextView textPermissionLabel = new TextView(this);
                    textPermissionLabel.setTextSize(16);
                    textPermissionLabel.setTypeface(null, Typeface.BOLD);
                    TextView textPermissionDesc = new TextView(this);
                    textPermissionDesc.setTextSize(16);
                    textPermissionDesc.setPadding(0, 0, 0, 30);

                    textPermissionLabel.setText(String.format("%s (%s)", pmInfo.loadLabel(packageManager), permission));
                    textPermissionDesc.setText(pmInfo.loadDescription(packageManager));

                    permissionList.addView(textPermissionLabel);
                    permissionList.addView(textPermissionDesc);
                    textPermissionDesc.requestLayout();
                }
            }
        } catch (final PackageManager.NameNotFoundException e) {
        }
    }

    public void BackHome(View view) {
        Intent intent = new Intent(this, DashBoard.class);
        startActivity(intent);
    }

    public void ShowAppList(View view) {
        Intent intent = new Intent(this, AppList.class);
        startActivity(intent);
    }

    public void ShowSettings(View view) {

    }

    public void ShowHelp(View view) {

    }
}
