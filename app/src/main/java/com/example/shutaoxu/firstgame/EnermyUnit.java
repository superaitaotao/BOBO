package com.example.shutaoxu.firstgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by shutaoxu on 7/2/16.
 */
public class EnermyUnit {
    private int y, x, speed, maxY, maxX;
    private Bitmap bitmap;
    private Random generator;
    private Rect hitBox;
    private int typeOfEgg;
    private Context context;
    private static ArrayList<Bitmap> eggs;

    public EnermyUnit(Context context,int screenX, int screenY) {
        this.context = context;
        generator = new Random();
        setBitmap(1);

        maxX = screenX;
        maxY = screenY - 100;

        y = generator.nextInt(maxY);
        x = maxX + generator.nextInt(maxX);
        speed = GameView.ALL_SPEED;

        hitBox = new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());

    }

    public static void createEggPics(Context context){
        eggs = new ArrayList<>();

        int[] names = {R.drawable.egg, R.drawable.blue_egg, R.drawable.red_egg, R.drawable.purple_egg,  R.drawable.bad_egg,
                R.drawable.yellow_egg, R.drawable.green_egg,};
        Bitmap bitmap;
        for(int s : names) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), s);
            eggs.add(Bitmap.createScaledBitmap(bitmap, (int) (70 * GameView.SCREEN_RATIO_X),
                    (int) (70 * GameView.SCREEN_RATIO_Y), false));
        }
    }

    public void setBitmap(float ratio){
        if (ratio == 1){
            typeOfEgg = setTypeOfEgg();
            bitmap = eggs.get(typeOfEgg);
        }else {
            int size = (int)(70 * GameView.SCREEN_RATIO_Y* ratio);
            switch (typeOfEgg) {
                case 0:
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.egg);
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
                    break;
                case 1:
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_egg);
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
                    break;
                case 2:
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_egg);
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
                    break;
                case 3:
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.purple_egg);
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
                    break;
                case 4:
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bad_egg);
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
                    break;
                case 5:
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.yellow_egg);
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
                    break;
                case 6:
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_egg);
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
                    break;
                default:
                    break;
            }
        }
    }
    public void update(){
        x -= speed;
        if (x<= -bitmap.getWidth()){
            y = generator.nextInt(maxY);
            x = maxX + generator.nextInt(maxX);
            speed = GameView.ALL_SPEED;
            //this.speed = generator.nextInt((int)(6*GameView.SCREEN_RATIO_Y)) + speedBoost;
            setBitmap(1);
        }

        hitBox.left = x;
        hitBox.top = y;
        hitBox.right = x + bitmap.getWidth();
        hitBox.bottom = y + bitmap.getHeight();
    }

    public  Bitmap getBitmap() {
        return bitmap;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void boostSpeed(int x){
        speed += (int)(x * GameView.SCREEN_RATIO_X);
    }

    public void setX(int x){
        this.x = x;
    }

    public Rect getHitBox(){
        return hitBox;
    }

    public int getTypeOfEgg(){
        return typeOfEgg;
    }

    private int setTypeOfEgg(){
        int typeOfEgg = generator.nextInt(100);

        if (typeOfEgg > 95){
            typeOfEgg = 2;//red egg
        }else{
            if(typeOfEgg > 90){
                typeOfEgg = 1;//blue egg
            }else{
                if(typeOfEgg > 85){
                    typeOfEgg = 3;
                }else{
                   if(typeOfEgg > 80){
                       typeOfEgg = 4;
                   }else{
                       if(typeOfEgg >75) {
                           typeOfEgg = 5;
                       }else{
                           if(typeOfEgg > 70){
                               typeOfEgg = 6;
                           }else{
                               typeOfEgg = 0;
                           }
                       }
                   }
                }
            }
        }

        return typeOfEgg;
    }
}
