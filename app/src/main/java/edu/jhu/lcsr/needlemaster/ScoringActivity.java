package edu.jhu.lcsr.needlemaster;

import edu.jhu.lcsr.needlemaster.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ScoringActivity extends Activity {

    int level;
    int numGates;
    int passedGates;
    int failedGates;
    double pathLength;
    long timeRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scoring);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            level = extras.getInt("EXTRA_LEVEL_NUMBER", 0);
            numGates = extras.getInt("GATES_TOTAL",0);
            passedGates = extras.getInt("GATES_PASSED",0);
            failedGates = extras.getInt("GATES_FAILED", 0);
            pathLength = extras.getDouble("PATH_LENGTH",0.0);
            timeRemaining = extras.getLong("TIME_REMAINING",0l);

            final TextView gatesView = (TextView) findViewById(R.id.gatesView);
            gatesView.setText(passedGates + "/" + numGates);

            final TextView pathLengthView = (TextView) findViewById(R.id.lengthView);
            pathLengthView.setText("" + String.format("%.02f", pathLength));

            double t = (double)timeRemaining / 1000.0;
            final TextView timeView = (TextView) findViewById(R.id.timeView);
            timeView.setText("" + String.format("%.02f", t));

        } else {
            level = 2;
        }

        final Button retryButton = (Button) findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Game.class);
                i.putExtra("EXTRA_LEVEL_NUMBER", level);
                startActivity(i);
            }
        });

        final Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Game.class);
                i.putExtra("EXTRA_LEVEL_NUMBER", level + 1);
                startActivity(i);
            }
        });

        final Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (level == 4) {
            nextButton.setEnabled(false);
        }

    }
}
