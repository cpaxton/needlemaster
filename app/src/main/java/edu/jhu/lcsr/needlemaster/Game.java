package edu.jhu.lcsr.needlemaster;

import edu.jhu.lcsr.grid.ThreadTheNeedleGame;
import edu.jhu.lcsr.needlemaster.util.SystemUiHider;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;


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

        gameView = (ThreadTheNeedleGame)findViewById(R.id.needleGameView);
        gameView.initialize(1);

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
        }
        return true;
    }
}
