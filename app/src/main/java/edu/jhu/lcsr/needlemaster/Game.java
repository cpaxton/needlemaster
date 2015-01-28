package edu.jhu.lcsr.needlemaster;

import edu.jhu.lcsr.grid.ThreadTheNeedleGame;
import edu.jhu.lcsr.needlemaster.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Game extends Activity {

    ThreadTheNeedleGame gameView;
    int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        TextView instructions = (TextView)findViewById(R.id.textView);

        gameView = (ThreadTheNeedleGame)findViewById(R.id.needleGameView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            level = extras.getInt("EXTRA_LEVEL_NUMBER", 0);
        } else {
            level = 2;
        }

        String text = gameView.initialize(level);
        instructions.setText(text);
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {

        if(!gameView.isFinished()) {

            switch (me.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    System.out.println("starting move");
                    gameView.startMove(me.getX(), me.getY());
                    gameView.updateMove(me.getX(), me.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    gameView.updateMove(me.getX(), me.getY());
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    System.out.println("ending move");
                    gameView.updateMove(me.getX(), me.getY());
                    gameView.endMove();
                    break;
            }
        }
        else if (me.getActionMasked() == MotionEvent.ACTION_DOWN) {
            this.finish();
            Intent i = new Intent(getApplicationContext(), ScoringActivity.class);
            i.putExtra("GATES_PASSED", gameView.getPassedGates());
            i.putExtra("GATES_TOTAL", gameView.getNumGates());
            i.putExtra("PATH_LENGTH", gameView.getPathLength());
            i.putExtra("TISSUE", gameView.getDamage());
            i.putExtra("DEEP_TISSUE", gameView.checkFailureSurfaces());
            i.putExtra("TIME_REMAINING", gameView.getTimeRemaining());
            i.putExtra("EXTRA_LEVEL_NUMBER", level);
            i.putExtra("LEVEL_PASSED", gameView.checkPassedLevel());
            startActivity(i);
            finish();
        }
        return true;
    }
}
