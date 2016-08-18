package th.ac.bu.science.mit.allappstatscollector;

import android.os.Bundle;
import android.view.View;

public class About extends GeneralActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        SetTitle("About");

        View menu = findViewById(R.id.menuAbout);
        menu.setVisibility(View.GONE);
    }
}
