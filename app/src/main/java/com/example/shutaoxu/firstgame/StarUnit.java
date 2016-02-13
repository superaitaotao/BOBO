package com.example.shutaoxu.firstgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by shutaoxu on 12/2/16.
 */
public class StarUnit {
    private Bitmap bitmap;
    private int x, y, y_min, y_max, x_min, x_max;
    private int speed = 0;
    private int MAX_SPEED = 30, MIN_SPEED = 1;
    private Context context;
    private Random generator;

    public StarUnit(Context context, int screenX, int screenY){
        generator = new Random();

        this.context = context;

        x = screenX + generator.nextInt(screenX);
        y = generator.nextInt(screenY);

        speed = GameView.ALL_SPEED;

        setLimit(screenY, screenX);
        setBitmap(generator.nextInt(3));
        modifyNumbers();
    }
    public void modifyNumbers(){
        MAX_SPEED = (int)(MAX_SPEED * GameView.SCREEN_RATIO_Y);
        MIN_SPEED = (int)(MIN_SPEED * GameView.SCREEN_RATIO_Y);
        //y = (int)(y*GameView.SCREEN_RATIO_Y);
        speed = (int)(speed*GameView.SCREEN_RATIO_Y);
    }

    public void setBitmap(int type){
        switch(type){
            case 0:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_star);
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (80 * GameView.SCREEN_RATIO_X), (int) (80 * GameView.SCREEN_RATIO_Y), false);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_star);
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (80 * GameView.SCREEN_RATIO_X), (int) (80 * GameView.SCREEN_RATIO_Y), false);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.yellow_star);
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (80 * GameView.SCREEN_RATIO_X), (int) (80 * GameView.SCREEN_RATIO_Y), false);
                break;
        }
    }

    public void update(){
        //int boost = 0;

        x -= speed;

        if(x < - bitmap.getWidth()){
            x = x_max + generator.nextInt(x_max);
            y = generator.nextInt(y_max);
            //speed = GameView.ALL_SPEED;
            //speed =(int) (10*GameView.SCREEN_RATIO_X);
        }
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    private void setLimit(int y_max, int x_max){
        this.y_min = 0;
        this.y_max = y_max;
        this.x_min = 0;
        this.x_max = x_max;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}
