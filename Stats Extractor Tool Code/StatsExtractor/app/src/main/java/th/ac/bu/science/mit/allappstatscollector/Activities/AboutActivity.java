package th.ac.bu.science.mit.allappstatscollector.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import th.ac.bu.science.mit.allappstatscollector.BuildConfig;
import th.ac.bu.science.mit.allappstatscollector.R;

class AboutActivity extends GeneralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        SetTitle("About");

        View menu = findViewById(R.id.menuAbout);
        menu.setVisibility(View.GONE);

        TextView versionName = (TextView) findViewById(R.id.textVersionName);
        TextView versionCode = (TextView) findViewById(R.id.textVersionCode);

        versionName.setText(BuildConfig.VERSION_NAME);
        versionCode.setText(((Integer) BuildConfig.VERSION_CODE).toString());
    }
}
