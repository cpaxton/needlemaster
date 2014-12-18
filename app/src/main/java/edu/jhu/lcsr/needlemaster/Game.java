package edu.jhu.lcsr.needlemaster;

import edu.jhu.lcsr.grid.ThreadTheNeedleGame;
import edu.jhu.lcsr.grid.needlegame.Needle;
import edu.jhu.lcsr.needlemaster.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Game extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

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
        switch(me.getActionMasked()) {
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
        return true;
    }
}
