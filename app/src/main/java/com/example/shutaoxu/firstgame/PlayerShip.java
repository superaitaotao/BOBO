package com.example.shutaoxu.firstgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Hashtable;

/**
 * Created by shutaoxu on 28/1/16.
 */
public class PlayerShip {
    private Bitmap bitmap;
    private int x, y, y_min, y_max, x_min, x_max;
    private int speed = 0;
    private int MAX_SPEED = 20, MIN_SPEED = 1, GRAVITY = 8;
    private boolean isBoosting = false;
    private Rect hitBox;
    private Context context;

    public void startBoosting() {
        isBoosting = true;
    }

    public void stopBoosting() {
        isBoosting = false;
    }

    public PlayerShip(Context context, int screenX, int screenY){
        this.context = context;
        x = 200;
        y = 0;
        speed = 1;
        setBitmap(0);
        hitBox = new Rect(x, y, x+bitmap.getWidth(), y+bitmap.getHeight());
        setLimit(screenY, screenX);
        modifyNumbers();
    }
    public void modifyNumbers(){
        MAX_SPEED = (int)(MAX_SPEED * GameView.SCREEN_RATIO_Y);
        MIN_SPEED = (int)(MIN_SPEED * GameView.SCREEN_RATIO_Y);
        GRAVITY = (int)(GRAVITY * GameView.SCREEN_RATIO_Y);
        x = (int)(x*GameView.SCREEN_RATIO_X);
        //y = (int)(y*GameView.SCREEN_RATIO_Y);
        speed = (int)(speed*GameView.SCREEN_RATIO_Y);
    }

    public void setBitmap(int type){
        switch(type){
            case 0:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bird);
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)(70*GameView.SCREEN_RATIO_X), (int)(70*GameView.SCREEN_RATIO_Y), false);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.protected_bird);
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (70 * GameView.SCREEN_RATIO_X), (int) (70 * GameView.SCREEN_RATIO_Y), false);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.timed_bird);
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (70 * GameView.SCREEN_RATIO_X), (int) (70 * GameView.SCREEN_RATIO_Y), false);
                break;
        }

        bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
    }

    public void update(){
        if(isBoosting) {
            speed += (int)(2 * GameView.SCREEN_RATIO_Y);
        }else{
            speed -= (int)(5 * GameView.SCREEN_RATIO_Y);
        }

        if (speed > MAX_SPEED){
            speed = MAX_SPEED;
        }

        if (speed < MIN_SPEED){
            speed = MIN_SPEED;
        }

        y -= speed - GRAVITY;

        if (y < y_min){
            y = y_min;
        }

        if (y > y_max){
            y = y_max;
        }

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    private void setLimit(int y_max, int x_max){
        this.y_min = 0;
        this.y_max = y_max - bitmap.getHeight();
        this.x_min = 0;
        this.x_max = x_max - bitmap.getWidth();
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public Rect getHitBox(){
        return hitBox;
    }

}
