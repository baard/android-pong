package no.bouvet.android.pong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class PongActivity extends Activity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(R.string.menu_new_game).setIcon(android.R.drawable.ic_media_play);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // only one item, no need to check item selected
        Intent game = new Intent(this, GameActivity.class);
        startActivity(game);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}