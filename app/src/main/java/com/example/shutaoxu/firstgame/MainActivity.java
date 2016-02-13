package com.example.shutaoxu.firstgame;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    GameView gameView;
    FrameLayout layout;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //set full size screen

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        /*backGround = BitmapFactory.decodeResource(this.getResources(), R.drawable.background);
        backGround = Bitmap.createScaledBitmap(backGround,size.x, size.y, false);
        imageView = new ImageView(this);
        imageView.setImageBitmap(backGround);*/

        //imageView = new ImageView(this);
        //imageView.setImageResource(R.drawable.background_copy);

        /*Bitmap playBG = BitmapFactory.decodeResource(this.getResources(),R.drawable.play);
        Bitmap pauseBG = BitmapFactory.decodeResource(this.getResources(), R.drawable.pause);
        final Bitmap finalPlayBG = Bitmap.createScaledBitmap(playBG, (int) (60 * GameView.SCREEN_RATIO_X),
                (int) (60 * GameView.SCREEN_RATIO_Y), false);
        final Bitmap finalPauseBG = Bitmap.createScaledBitmap(pauseBG, (int) (60 * GameView.SCREEN_RATIO_X),
                (int) (60 * GameView.SCREEN_RATIO_Y), false);


        //button.setGravity(Gravity.RIGHT | Gravity.TOP);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        button.setImageBitmap(finalPauseBG);
        button.setBackgroundColor(Color.TRANSPARENT);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameView.isPlaying()){
                    gameView.pause();
                    button.setImageBitmap(finalPlayBG);
                }else{
                    gameView.resume();
                    button.setImageBitmap(finalPauseBG);
                }
            }
        });*/

        button = new Button(this);
        button.setHeight((int) (80 * GameView.SCREEN_RATIO_Y));
        button.setWidth((int) (80 * GameView.SCREEN_RATIO_X));
        button.setPadding(10, 10, 10, 10);
        button.setGravity(Gravity.CENTER);

        layout = new FrameLayout(this);
        layout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
       // layout.addView(imageView);
        try {
            gameView = new GameView(this, size.x, size.y);
        } catch (IOException e) {
            e.printStackTrace();
        }
        layout.addView(gameView);
        //layout.addView(button);

        //gameView.setZOrderOnTop(true);
        //gameView.getHolder().setFormat(PixelFormat.TRANSPARENT);

        setContentView(layout);
    }

    public void addView(){
        layout.addView(button);
    }

    public void dumbView(){
        layout.removeView(button);
    }
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
