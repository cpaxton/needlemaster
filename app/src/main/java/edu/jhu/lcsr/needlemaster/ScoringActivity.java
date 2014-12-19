package edu.jhu.lcsr.needlemaster;

import edu.jhu.lcsr.needlemaster.util.SystemUiHider;

import android.app.Activity;
import android.os.Bundle;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ScoringActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scoring);


    }
}
