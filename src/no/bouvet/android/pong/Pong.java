package no.bouvet.android.pong;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class Pong extends Activity {
    private PongView mPongView;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(R.string.menu_new_game);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // only one menu-item
        mPongView.doStart();
        return true;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        mPongView = (PongView) findViewById(R.id.pong);
        
        if (savedInstanceState != null) {
            mPongView.restoreState(savedInstanceState);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mPongView.pause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPongView.saveState(outState);
    }
}