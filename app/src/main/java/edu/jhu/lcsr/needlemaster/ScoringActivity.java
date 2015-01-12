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
    boolean passed;
    boolean deepTissue;
    double damage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int score = 0;
        int gatesScore;
        int timeScore;
        int pathScore;
        int damageScore;

        setContentView(R.layout.activity_scoring);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            level = extras.getInt("EXTRA_LEVEL_NUMBER", 0);
            numGates = extras.getInt("GATES_TOTAL",0);
            passedGates = extras.getInt("GATES_PASSED",0);
            failedGates = extras.getInt("GATES_FAILED", 0);
            pathLength = extras.getDouble("PATH_LENGTH",0.0);
            timeRemaining = extras.getLong("TIME_REMAINING",0l);
            passed = extras.getBoolean("LEVEL_PASSED", true);
            deepTissue = extras.getBoolean("DEEP_TISSUE", false);
            damage = extras.getDouble("TISSUE",0.0);

            final TextView gatesView = (TextView) findViewById(R.id.gatesView);
            final TextView gatesScoreView = (TextView) findViewById(R.id.gatesScore);
            gatesView.setText(passedGates + "/" + numGates);
            if (0 == numGates) {
                gatesScore = 1000;
            } else {
                gatesScore = (int) (1000.0 * (double) passedGates / numGates);
            }
            gatesScoreView.setText("" + gatesScore);
            score += gatesScore;

            final TextView pathLengthView = (TextView) findViewById(R.id.lengthView);
            final TextView pathLengthScoreView = (TextView) findViewById(R.id.pathScore);
            pathLengthView.setText("" + String.format("%.02f", pathLength));
            pathScore = (int)(-50 * pathLength);
            score += pathScore;
            pathLengthScoreView.setText("" + pathScore);

            final TextView tissueView = (TextView) findViewById(R.id.tissueTimeView);
            final TextView tissueScoreView = (TextView) findViewById(R.id.tissueScore);
            tissueView.setText("" + String.format("%.02f",damage));
            damageScore = (int)(-4 * damage);
            tissueScoreView.setText("" + damageScore);
            score += damageScore;

            double t = (double)timeRemaining / 1000.0;
            final TextView timeView = (TextView) findViewById(R.id.timeView);
            final TextView timeScoreView = (TextView) findViewById(R.id.timeScore);
            timeView.setText("" + String.format("%.02f", t));
            if (timeRemaining > 5000) {
                timeScore = 1000;
            } else {
                timeScore = (int) (1000.0 * (double)(timeRemaining) / 5000.0);
            }
            timeScoreView.setText("" + timeScore);
            score += timeScore;

            final TextView deepTissueView = (TextView) findViewById((R.id.deepTissueView));
            final TextView deepTissueScoreView = (TextView) findViewById(R.id.deepTissueScore);
            if (deepTissue) {
                deepTissueView.setText("HIT!");
                deepTissueScoreView.setText("-1000");
                score -= 1000;
            } else {
                deepTissueView.setText("avoided");
                deepTissueScoreView.setText("0");
            }


            TextView scoreView = (TextView) findViewById(R.id.scoreView);
            scoreView.setText("" + score + "/1000");


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

        final Button backButton = (Button) findViewById(R.id.aboutBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (level == 7 || !passed) {
            nextButton.setEnabled(false);
            nextButton.setVisibility(View.INVISIBLE);
        }

    }
}
