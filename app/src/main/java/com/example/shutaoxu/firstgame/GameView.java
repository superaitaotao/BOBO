package com.example.shutaoxu.firstgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * Created by shutaoxu on 28/1/16.
 */
public class GameView extends SurfaceView implements Runnable {
    public static double SCREEN_RATIO_X, SCREEN_RATIO_Y;
    public static int ALL_SPEED;

    private Context context;

    private Thread gameThread = null;

    private Boolean isPlaying = false, isAlmostNotPlaying = false;
    private Boolean isGameFinished = false;

    private PlayerShip bird;
    private volatile ArrayList<EnermyUnit> enermyUnits;

    private Bitmap pauseBG, playBG;

    //for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private int screenX, screenY;

    private SoundPool soundPool;
    private int jumpSound = 0, bombSound = 1, shieldSound = 2, gameoverSound = 3,
            enlargeSound = 4, speedUpSound = 5, timedSound = 6, freezeSound = 7, levelUpSound =9, breakSound = 8;

    private float currentTime = 0;
    private int livesLeft = 1;
    private int level = 1;
    private float lastTime = 0;
    private float bestTime;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private float swipeX1, swipeX2;
    private Bitmap gameoverBG;

    private float shieldStartTime, frozenStartTime;
    private boolean isTimeShielded = false, isFrozen = false;

    private ArrayList<StarUnit> starUnits;



    public GameView(Context context, int x, int y) throws IOException {
        super(context);
        this.context = context;

        screenX = x;
        screenY = y;

        SCREEN_RATIO_X = (double)x/1280;
        SCREEN_RATIO_Y = (double)y/720;

        ALL_SPEED = (int)(6*SCREEN_RATIO_X);

        //add a bird
        bird = new PlayerShip(context, screenX, screenY);

        //add multiple enermy units
        enermyUnits = new ArrayList<EnermyUnit>();
        EnermyUnit.createEggPics(context);
        //Log.i("Before Enermy", "here");

        for(int i=0; i<6; i++){
            enermyUnits.add(new EnermyUnit(context, screenX, screenY));
            //Log.i("Enermy","added");
        }

        surfaceHolder = getHolder();
        paint = new Paint();

        loadSound();

        sharedPreferences = context.getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        bestTime = sharedPreferences.getInt("HIGH_SCORE", 0);

        gameoverBG = BitmapFactory.decodeResource(context.getResources(), R.drawable.gameover_bg);
        gameoverBG = Bitmap.createScaledBitmap(gameoverBG, modifyNums(600, SCREEN_RATIO_X), modifyNums(350, SCREEN_RATIO_Y), false);

        pauseBG = BitmapFactory.decodeResource(context.getResources(), R.drawable.pause);
        pauseBG = Bitmap.createScaledBitmap(pauseBG, modifyNums(60, SCREEN_RATIO_X), modifyNums(60, SCREEN_RATIO_Y), false);

        playBG = BitmapFactory.decodeResource(context.getResources(), R.drawable.play);
        playBG = Bitmap.createScaledBitmap(playBG, modifyNums(60, SCREEN_RATIO_X), modifyNums(60, SCREEN_RATIO_Y), false);

        starUnits = new ArrayList<>();
        for(int i=0; i<20; i++){
            starUnits.add(new StarUnit(context, screenX, screenY));
        }
    }

    private void loadSound() throws IOException {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        AssetManager assetManager = context.getAssets();
        AssetFileDescriptor descriptor;

        descriptor = assetManager.openFd("jump.wav");
        jumpSound = soundPool.load(descriptor, 0);

        descriptor = assetManager.openFd("bomb.wav");
        bombSound = soundPool.load(descriptor, 0);

        descriptor = assetManager.openFd("shield.wav");
        shieldSound = soundPool.load(descriptor, 0);

        descriptor = assetManager.openFd("gameover.wav");
        gameoverSound = soundPool.load(descriptor, 0);

        descriptor = assetManager.openFd("enlarge.wav");
        enlargeSound = soundPool.load(descriptor, 0);

        descriptor = assetManager.openFd("speed_up.wav");
        speedUpSound = soundPool.load(descriptor, 0);

        descriptor = assetManager.openFd("timed.wav");
        timedSound = soundPool.load(descriptor, 0);

        descriptor = assetManager.openFd("freeze.wav");
        freezeSound = soundPool.load(descriptor, 0);

        descriptor = assetManager.openFd("break.wav");
        breakSound = soundPool.load(descriptor, 0);

        descriptor = assetManager.openFd("level_up.wav");
        levelUpSound = soundPool.load(descriptor, 0);

    }

    @Override
    public void run() {
        while(isPlaying) {
            if (!isGameFinished){
                update();
                draw();
            }else{
                saveHighScore();
                drawGameOverText();
            }
            if(isAlmostNotPlaying){
                isPlaying = false;
            }
            control();
            try{
                //sleep to control the frame rate
                gameThread.sleep(17);
            }catch (InterruptedException e){

            }
        }
    }

    private void saveHighScore(){
        double bestScore = sharedPreferences.getInt("HIGH_SCORE", 0);
        if (bestScore < currentTime){
            editor.putInt("HIGH_SCORE", (int) currentTime);
            editor.commit();
            bestTime = currentTime;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isGameFinished && event.getX()<pauseBG.getWidth()+modifyNums(20, SCREEN_RATIO_X) && event.getY()
                < pauseBG.getWidth() + modifyNums(20, SCREEN_RATIO_Y) && event.getAction() == MotionEvent.ACTION_DOWN){
            if(isPlaying){
                pause();
            }else{
                resume();
            }
            return true;
        }

        if(isPlaying) {
            if(isGameFinished){
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN){
                    swipeX1 = event.getX();
                }
                if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP){
                    swipeX2 = event.getX();
                }
                if (swipeX1 < swipeX2 - (int)(300*SCREEN_RATIO_X) && swipeX1 != 0){
                    reStartGame();
                    swipeX1 = 0;
                    swipeX2 = 0;
                }
            }else {
                //Log.i("TOUCH", event.getX() + "," + event.getY());
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        bird.stopBoosting();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        soundPool.play(jumpSound, 1, 1, 0, 0, 1);
                        bird.startBoosting();
                        break;
                }
            }
        }
        return true;
    }


    private void reStartGame(){
        currentTime = 0;
        isGameFinished = false;
        isAlmostNotPlaying = false;
        isFrozen = false;
        isTimeShielded = false;
        livesLeft = 1;
        enermyUnits.clear();
        for(int i=0; i<6; i++){
            enermyUnits.add(new EnermyUnit(context, screenX, screenY));
            //Log.i("Enermy","added");
        }
        level = 1;
        lastTime = 0;
        ALL_SPEED = modifyNums(7, SCREEN_RATIO_X);
    }

    public void pause(){
        isAlmostNotPlaying = true;
        //soundPool.release();
        try{
            gameThread.join();
        }catch (InterruptedException e){

        }
    }

    public void resume(){
        isPlaying = true;
        isAlmostNotPlaying = false;
        gameThread = new Thread(this);
        gameThread.start();
        //run();
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public void update(){
        currentTime = currentTime + 17.0f/1000;
        //Log.i("CurrentTIme", currentTime+"");
        if(currentTime - lastTime > 20){
            level += 1;
            enermyUnits.add(new EnermyUnit(context, screenX, screenY));
            enermyUnits.add(new EnermyUnit(context, screenX, screenY));
            ALL_SPEED += 1;
            soundPool.play(levelUpSound, 1, 1, 0, 0, 1);
            lastTime = currentTime;
        }

        if(isTimeShielded && currentTime - shieldStartTime > 5){
            isTimeShielded = false;
            bird.setBitmap(0);
        }

        if(isFrozen && currentTime - frozenStartTime > 2){
            isFrozen = false;
        }

        bird.update();

        for(StarUnit s: starUnits){
            s.update();
        }

        Boolean isExplosion = false;
        if(!isFrozen) {
            for (EnermyUnit e : enermyUnits) {
                e.update();
                if (isCollide(e)) {
                    switch (e.getTypeOfEgg()) {
                        case 0:
                            if (!isTimeShielded) {
                                livesLeft -= 1;
                                if (livesLeft == 1) {
                                    bird.setBitmap(0);
                                }
                            }else{
                                soundPool.play(breakSound, 1, 1, 0, 0, 1);
                            }
                            break;
                        case 1:
                            if (livesLeft < 2) {
                                livesLeft = 2;
                                soundPool.play(shieldSound, 1, 1, 0, 0, 1);
                                isTimeShielded = false;
                                bird.setBitmap(1);
                            }
                            break;
                        case 2:
                            for (EnermyUnit ee : enermyUnits) {
                                ee.setX(-100);
                            }
                            soundPool.play(bombSound, 1, 1, 0, 0, 1);
                            isExplosion = true;
                            break;
                        case 3:
                            //Log.i("Purple", "here");
                            for (EnermyUnit ee : enermyUnits) {
                                ee.boostSpeed(5);
                            }
                            soundPool.play(speedUpSound, 1, 1, 0, 0, 1);
                            break;
                        case 4:
                            for (EnermyUnit ee : enermyUnits) {
                                ee.setBitmap(1.5f);
                            }
                            soundPool.play(enlargeSound, 1, 1, 0, 0, 1);
                            break;
                        case 5:
                            bird.setBitmap(2);
                            isTimeShielded = true;
                            livesLeft = 1;
                            shieldStartTime = currentTime;
                            soundPool.play(timedSound, 1, 1, 0, 0, 1);
                            break;
                        case 6:
                            isFrozen = true;
                            frozenStartTime = currentTime;
                            soundPool.play(freezeSound, 1, 1, 0, 0, 1);
                            break;

                        default:
                            break;
                    }

                    if (livesLeft == 0) {
                        isGameFinished = true;
                        soundPool.play(gameoverSound, 1, 1, 0, 0, 1);
                        break;
                    }

                    if (isExplosion) {
                        break;
                    }
                }
            }
        }
    }

    public void drawGameOverText() {
        if (surfaceHolder.getSurface().isValid()) {

            Rect gameOverArea = new Rect(screenX/2 - modifyNums(300, SCREEN_RATIO_X), screenY/2-modifyNums(150, SCREEN_RATIO_Y),
                    screenX/2+modifyNums(300, SCREEN_RATIO_X), screenY/2+modifyNums(200, SCREEN_RATIO_Y));
            canvas = surfaceHolder.lockCanvas(gameOverArea);

            Paint blackPaint = new Paint();
            blackPaint.setColor(Color.BLACK);
            canvas.drawBitmap(gameoverBG,
                    gameOverArea.left, gameOverArea.top,paint);

            //Log.i("Drawing", "drawGameOver");
            Paint whitePaint = new Paint();
            whitePaint.setColor(Color.WHITE);
            //whitePaint.setTextAlign(Paint.Align.CENTER);

            whitePaint.setTextSize(modifyNums(40, SCREEN_RATIO_Y));
            canvas.drawText(context.getString(R.string.game_over), screenX / 2 - modifyNums(100, SCREEN_RATIO_X),
                    screenY / 2 - modifyNums(30, SCREEN_RATIO_Y),whitePaint);


            //whitePaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(context.getString(R.string.swipe_right), screenX / 2 - modifyNums(140, SCREEN_RATIO_X),
                    screenY / 2+modifyNums(30, SCREEN_RATIO_Y) , whitePaint);
            if(bestTime == currentTime){
                canvas.drawText(context.getString(R.string.new_best_time), screenX/2 - modifyNums(90, SCREEN_RATIO_X),
                        screenY / 2+modifyNums(90, SCREEN_RATIO_Y), whitePaint);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    public int modifyNums(int num, double ratio){
        return (int)(num * ratio);
    }

    public void draw() {
        if (surfaceHolder.getSurface().isValid()){
            canvas = surfaceHolder.lockCanvas();

            //this cover the whole canvas black
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            //canvas.drawColor(0, PorterDuff.Mode.CLEAR);

            Paint whitePaint = new Paint();
            whitePaint.setColor(Color.WHITE);
            whitePaint.setTextSize(modifyNums(35, SCREEN_RATIO_Y));

            //canvas.drawBitmap(bird.getBitmap(), bird.getX(), bird.getY(), paint);
            Bitmap bitmap;

            for(StarUnit s: starUnits){
                bitmap = s.getBitmap();
                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                        new Rect(s.getX(), s.getY(), s.getX()+bitmap.getWidth(), s.getY()+bitmap.getHeight()), paint);

            }

           bitmap = bird.getBitmap();
            canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                    new Rect(bird.getX(), bird.getY(), bird.getX()+bitmap.getWidth(),bird.getY()+bitmap.getHeight()), paint);

            EnermyUnit enermy;
            for(int i =0; i<enermyUnits.size(); i++){
                enermy = enermyUnits.get(i);
                bitmap = enermy.getBitmap();

                canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                        new Rect(enermy.getX(), enermy.getY(), enermy.getX()+bitmap.getWidth(), enermy.getY()+bitmap.getHeight()), paint);

            }

            //draw text
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            canvas.drawText(context.getString(R.string.level_label) + level, modifyNums(150, SCREEN_RATIO_X), modifyNums(40, SCREEN_RATIO_Y), whitePaint);
            canvas.drawText(context.getString(R.string.time_label) + df.format(currentTime), screenX / 2 - modifyNums(100, SCREEN_RATIO_X), modifyNums(40, SCREEN_RATIO_Y), whitePaint);
            canvas.drawText(context.getString(R.string.best_time_label) + (int)bestTime, screenX - modifyNums(300, SCREEN_RATIO_X), modifyNums(40, SCREEN_RATIO_Y), whitePaint);
            //Log.i("Drawing", "draw");

            if(!isAlmostNotPlaying) {
                canvas.drawBitmap(pauseBG, new Rect(0, 0, pauseBG.getWidth(), pauseBG.getHeight()), new Rect(10, 10, pauseBG.getWidth() + 10, pauseBG.getHeight() + 10), paint);
            }else{
                canvas.drawBitmap(playBG, new Rect(0, 0, playBG.getWidth(), playBG.getHeight()), new Rect(10, 10, playBG.getWidth() + 10, playBG.getHeight() + 10), paint);

            }

            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }

    public void control(){
    }

    public boolean isCollide(EnermyUnit e){

        int deviation = modifyNums(20, SCREEN_RATIO_Y);
        Rect hit1, hit2;
        hit1 = bird.getHitBox();
        hit2 = e.getHitBox();

        if ((hit2.left <= hit1.right - deviation && hit2.right>= hit1.left + deviation) &&
        (hit2.bottom >= hit1.top + deviation && hit2.top <= hit1.bottom - deviation)){
            //Log.i("Collision", hit2.left + " " + hit2.top + " " + hit1.left + " " + hit1.top);
            e.setX(-100);
            return true;
        }

        return false;

    }
}
