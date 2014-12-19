package edu.jhu.lcsr.needlemaster;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        final Button backButton = (Button) findViewById(R.id.aboutBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
