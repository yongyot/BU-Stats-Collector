package th.ac.bu.science.mit.allappstatscollector.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import th.ac.bu.science.mit.allappstatscollector.R;

public class GeneralActivity extends AppCompatActivity {

    public void SetTitle(String title) {
        TextView textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(title);
    }

    public void BackHome(View view) {
        Intent intent = new Intent(this, DashBoardActivity.class);
        startActivity(intent);
    }

    public void ShowAppList(View view) {
        Intent intent = new Intent(this, AppListActivity.class);
        startActivity(intent);
    }

    public void ShowSettings(View view) {
        Intent intent = new Intent(this, SettingsScreenActivity.class);
        startActivity(intent);
    }

    public void ShowAbout(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
