package th.ac.bu.science.mit.allappstatscollector;

import android.os.Bundle;
import android.view.View;

public class SettingsScreen extends GeneralActivity {

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
