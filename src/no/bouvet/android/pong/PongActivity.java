package no.bouvet.android.pong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class PongActivity extends Activity {
    private static final int MENU_NEW_GAME = 0;
    private static final int MENU_CONFIG = 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_NEW_GAME, 0, "Join game").setIcon(android.R.drawable.ic_media_play);
        menu.add(0, MENU_CONFIG, 0, "Preferences").setIcon(android.R.drawable.ic_menu_preferences);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_NEW_GAME:
            Intent game = new Intent(this, GameActivity.class);
            startActivity(game);
            return true;
        case MENU_CONFIG:
            Intent intent = new Intent(this, ConfigActivity.class);
            startActivity(intent);
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