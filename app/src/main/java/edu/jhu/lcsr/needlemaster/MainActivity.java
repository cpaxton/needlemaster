package edu.jhu.lcsr.needlemaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.jhu.lcsr.needlemaster.util.SystemUiHider;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.newGameButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Game.class);
                i.putExtra("EXTRA_LEVEL_NUMBER",0);
                startActivity(i);
            }
        });

        final Button button2 = (Button) findViewById(R.id.selectLevelButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SelectLevelActivity.class);
                startActivity(i);
            }
        });

        final Button button3 = (Button) findViewById(R.id.aboutButton);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(i);
            }
        });

        final Button button4 = (Button) findViewById(R.id.optionsButton);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Options.class);
                startActivity(i);
            }
        });


    }

}