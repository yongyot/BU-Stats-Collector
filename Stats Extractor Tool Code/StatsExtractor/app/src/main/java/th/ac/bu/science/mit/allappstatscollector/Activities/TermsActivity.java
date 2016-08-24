package th.ac.bu.science.mit.allappstatscollector.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import th.ac.bu.science.mit.allappstatscollector.R;

class TermsActivity extends GeneralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        SetTitle(getString(R.string.app_name));
    }

    public void ToggleAccept(View view) {
        CheckBox isAccept = (CheckBox) findViewById(R.id.checkbox_is_accept);
        Button buttonStart = (Button) findViewById(R.id.button_start);
        if (isAccept.isChecked()) {
            buttonStart.setEnabled(true);
        } else {
            buttonStart.setEnabled(false);
        }
    }

    public void StartMonitoring(View view) {
        Intent intent = new Intent(this, InitScanActivity.class);
        startActivity(intent);
    }
}
