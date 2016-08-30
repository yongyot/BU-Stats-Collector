package th.ac.bu.science.mit.allappstatscollector.Activities;

import android.os.Bundle;
import android.view.View;

import th.ac.bu.science.mit.allappstatscollector.R;

public class SettingsScreenActivity extends GeneralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SetTitle("Settings");

        View menu = findViewById(R.id.menuSettings);
        menu.setVisibility(View.GONE);
    }

    public void OnClickSave (View view) {

    }
}
