package com.example.ncwphilly.travelingsalesman;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import java.io.IOException;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    String option;
    GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        option = getIntent().getExtras().getString("spinnerItem");
        try {
            gameView = new GameView(this, option);
            setContentView(gameView);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        RelativeLayout activity_menu;
        getMenuInflater().inflate(R.menu.gamemenu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.undo:
                ArrayList<PointLine> PointLines = gameView.getPointLines();
                if (PointLines.size() > 0) {
                    PointLines.remove(PointLines.size() - 1);
                    gameView.invalidate();
                }
                return true;
            case R.id.clear:
                PointLines = gameView.getPointLines();
                if (PointLines.size() > 0) {
                    PointLines.clear();
                    gameView.invalidate();
                }
                return true;
            case R.id.quit:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
