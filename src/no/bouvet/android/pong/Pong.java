package no.bouvet.android.pong;

import no.bouvet.android.pong.PongView.PongThread;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class Pong extends Activity {
    
    private PongThread mPongThread;

    private PongView mPongView;
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(R.string.menu_new_game);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPongThread.doStart();
        return true;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);

        // get handles to the LunarView from XML, and its LunarThread
        mPongView = (PongView) findViewById(R.id.pong);
        mPongThread = mPongView.getThread();
        
        if (savedInstanceState != null) {
            // we are being restored: resume a previous game
            mPongThread.restoreState(savedInstanceState);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mPongView.getThread().pause(); // pause game when Activity pauses
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPongThread.saveState(outState);
    }
}