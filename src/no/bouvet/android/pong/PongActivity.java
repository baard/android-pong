package no.bouvet.android.pong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class PongActivity extends Activity {
    private static final int MENU_NEW_GAME = 0;
    private static final int MENU_JOIN_GAME = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_NEW_GAME, 0, R.string.menu_new_game).setIcon(android.R.drawable.ic_media_play);
        menu.add(0, MENU_JOIN_GAME, 0, R.string.menu_join_game).setIcon(android.R.drawable.ic_media_ff);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_NEW_GAME:
            Intent game = new Intent(this, GameActivity.class);
            startActivity(game);
            return true;
        case MENU_JOIN_GAME:
            Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}