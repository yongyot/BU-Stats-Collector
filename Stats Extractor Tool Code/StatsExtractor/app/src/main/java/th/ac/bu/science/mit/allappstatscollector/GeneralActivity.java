package th.ac.bu.science.mit.allappstatscollector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class GeneralActivity extends AppCompatActivity {

    public void SetTitle(String title) {
        TextView textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(title);
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
        Intent intent = new Intent(this, SettingsScreen.class);
        startActivity(intent);
    }

    public void ShowAbout(View view) {
        Intent intent = new Intent(this, About.class);
        startActivity(intent);
    }
}
