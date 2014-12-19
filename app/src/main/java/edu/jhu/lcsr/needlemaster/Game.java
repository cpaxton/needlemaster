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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        TextView instructions = (TextView)findViewById(R.id.textView);

        gameView = (ThreadTheNeedleGame)findViewById(R.id.needleGameView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int num = extras.getInt("EXTRA_LEVEL_NUMBER", 0);
            String text = gameView.initialize(num);
            instructions.setText(text);
        } else {
            String text = gameView.initialize(2);
            instructions.setText(text);
        }

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
            i.putExtra("TISSUE", 0);
            i.putExtra("DEEP_TISSUE", 0);
            i.putExtra("TIME_REMAINING", gameView.getTimeRemaining());
            startActivity(i);
        }
        return true;
    }
}
