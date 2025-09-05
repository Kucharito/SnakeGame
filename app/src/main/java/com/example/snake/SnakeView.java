package com.example.snake;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayDeque;
import java.util.Random;

public class SnakeView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean running;
    private final SurfaceHolder holder;
    private Paint paint;
    private Point fruit;

    private boolean gridReady = false;
    private boolean snakeInited = false;

    private static final float CELL_SIZE = 20f;

    private int cols = 20;
    private int rows = 28;
    private float cell;
    private float offsetX, offsetY;
    private int score = 0;

    private final ArrayDeque<Point> snake = new ArrayDeque<>();
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    private Direction direction = Direction.RIGHT;
    private int targetLength = 2;

    private long stepTime = 140;
    private long lastTick = 0;

    private float downX, downY;

    private boolean isPaused = false;
    private boolean isGameover = false;

    private int highScore = 0;

    private RectF resumeButton;
    private RectF mainMenuButton;
    private RectF exitButton;
    private RectF pauseButton;
    private RectF restartButton;
    private SoundPool soundPool;
    private int soundFruitEaten;
    private boolean soundsReady = false;

    public SnakeView(Context context) {
        super(context);
        holder = getHolder();
        paint = new Paint();
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
        generateFruit();

        SharedPreferences sharedPreferences = context.getSharedPreferences("SnakePrefs", Context.MODE_PRIVATE);
        highScore = sharedPreferences.getInt("highScore", 0);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build();

        soundFruitEaten = soundPool.load(context, R.raw.bite, 1);

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (status == 0) {
                soundsReady = true;
            }
        });

    }

    private void playHit(){
        if(soundsReady) {
            soundPool.play(soundFruitEaten, 1, 1, 0, 0, 1);
        }
    }

    public void updateHighScore(){
        if(score > highScore){
            highScore = score;
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("SnakePrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highScore", highScore);
            editor.apply();
        }
    }

    @Override
    public void run() {
        while(running){
            long now = System.currentTimeMillis();
            if(now - lastTick >= stepTime && !isPaused && !isGameover){
                update();
                lastTick = now;
            }

            draw();
            try{
                Thread.sleep(1000 / 60); // 60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void update(){
        if(!snakeInited || !gridReady){
            return;
        }
        Point head = snake.peekFirst();
        if(head == null) return;
        int newX = head.x;
        int newY = head.y;
        switch (direction){
            case UP:
                newY--;
                break;
            case DOWN:
                newY++;
                break;
            case LEFT:
                newX--;
                break;
            case RIGHT:
                newX++;
                break;
        }

        // Check for collision with walls
        if (newX < 0 || newX >= cols || newY < 0 || newY >= rows) {
            //running = false;
            isGameover = true;
            isPaused = true;
            resumeButton = null;
            mainMenuButton = null;
            exitButton = null;
            return;
        }


        int biteIndex = indexOfCellInSnake(newX, newY);

        snake.addFirst(new Point(newX, newY));


        if(newX == fruit.x && newY == fruit.y){
            // Snake ate the fruit
            targetLength++;
            incrementScore();
            generateFruit();
            playHit();

        }

        if(biteIndex !=-1){
            int newLen = biteIndex + 1;
            targetLength = newLen;
            while(snake.size()> newLen){
                snake.removeLast();
                if(snake.size() > 1){
                    score = targetLength * 10;
                    updateHighScore();
                }

            }
        }
        else{
            if(snake.size() > targetLength){
                snake.removeLast();
            }
        }

    }


    private void draw() {
        if (!holder.getSurface().isValid()) return;
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return;

        canvas.drawColor(Color.BLACK);

        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(getContext().getString(R.string.score) + score, 20, 50, paint);
        //incrementScore();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SnakePrefs", Context.MODE_PRIVATE);
        String snakeColor = sharedPreferences.getString("snake_color", "green");

        if(snake != null){
            switch (snakeColor) {
                case "white":
                    paint.setColor(Color.WHITE);
                    break;
                case "red":
                    paint.setColor(Color.RED);
                    break;
                case "blue":
                    paint.setColor(Color.BLUE);
                    break;
                default:
                    paint.setColor(Color.GREEN); // Default color
            }
        }


        paint.setStyle(Paint.Style.FILL);
        //paint.setColor(Color.GREEN);   // farba hada
        float pad = 3f;
        for (Point p : snake) {
            float left   = offsetX + p.x * cell + pad;
            float top    = offsetY + p.y * cell + pad;
            float right  = left + cell - 2 * pad;
            float bottom = top  + cell - 2 * pad;
            canvas.drawRect(left, top, right, bottom, paint);
        }



        if(pauseButton != null){
            paint.setColor(Color.GRAY);
            canvas.drawRoundRect(pauseButton, 50, 50, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("II", pauseButton.centerX(),pauseButton.centerY()+15, paint);
        }

        // --- had ---

        if(fruit != null){
            String fruitColor = sharedPreferences.getString("fruit_color", "red");
            switch (fruitColor) {
                case "white":
                    paint.setColor(Color.WHITE);
                    break;
                case "red":
                    paint.setColor(Color.RED);
                    break;
                case "blue":
                    paint.setColor(Color.BLUE);
                    break;
                default:
                    paint.setColor(Color.RED); // Default color
            }
        }

        //paint.setColor(Color.RED); // farba ovocia
        float fruitLeft   = offsetX + fruit.x * cell + pad;
        float fruitTop    = offsetY + fruit.y * cell + pad;
        float fruitRight  = fruitLeft + cell - 2 * pad;
        float fruitBottom = fruitTop  + cell - 2 * pad;
        canvas.drawRect(fruitLeft, fruitTop, fruitRight, fruitBottom, paint);



        if(isPaused && !isGameover){
            Paint overlayPaint = new Paint();
            overlayPaint.setColor(Color.argb(128, 0, 0, 0)); // semi-transparent black
            canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(100);
            paint.setTextAlign(Paint.Align.CENTER);

            float centerX = getWidth() / 2f;
            float centerY = getHeight() / 2f;

            resumeButton = new RectF(centerX - 200, centerY - 150, centerX + 200, centerY - 50);
            mainMenuButton = new RectF(centerX - 200, centerY + 50, centerX + 200, centerY + 150);
            exitButton = new RectF(centerX - 200, centerY + 250, centerX + 200, centerY + 350);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.GRAY);
            canvas.drawRoundRect(resumeButton, 50, 50, paint);
            canvas.drawRoundRect(mainMenuButton, 50, 50, paint);
            canvas.drawRoundRect(exitButton, 50, 50, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(60);
            canvas.drawText(getContext().getString(R.string.resume), centerX, centerY - 80, paint);
            canvas.drawText(getContext().getString(R.string.main_menu), centerX, centerY + 120, paint);
            canvas.drawText(getContext().getString(R.string.exit), centerX, centerY + 320, paint);

        }

        if (isGameover) {
            Paint overlay = new Paint();
            overlay.setColor(Color.argb(160, 0, 0, 0));
            canvas.drawRect(0, 0, getWidth(), getHeight(), overlay);

            Paint gameOverPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            gameOverPaint.setColor(Color.WHITE);
            gameOverPaint.setTextSize(100);
            gameOverPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(getContext().getString(R.string.game_over), getWidth()/2f, getHeight()/2f - 250, gameOverPaint);

            float cx = getWidth()/2f, cy = getHeight()/2f + 20;
            restartButton = new RectF(cx - 200, cy - 80, cx + 200, cy + 20);
            mainMenuButton = new RectF(cx - 200, cy + 120, cx + 200, cy + 220);


            Paint btn = new Paint();
            btn.setColor(Color.GRAY);
            canvas.drawRoundRect(restartButton, 50, 50, btn);
            canvas.drawRoundRect(mainMenuButton, 50, 50, btn);

            gameOverPaint.setTextSize(60);
            canvas.drawText(getContext().getString(R.string.restart_button), cx, cy - 5, gameOverPaint);
            canvas.drawText(getContext().getString(R.string.main_menu), cx, cy + 190, gameOverPaint);

            gameOverPaint.setTextSize(80);
            canvas.drawText(getContext().getString(R.string.high_score) + highScore, getWidth()/2f, getHeight()/2f - 130, gameOverPaint);
        }


        holder.unlockCanvasAndPost(canvas);



    }

    public void incrementScore(){
        if(isSnake(fruit.x, fruit.y)){
            score = score + 10;
            updateHighScore();
        }
    }

    public void generateFruit(){
        Random random = new Random();
        int fruitX = random.nextInt(cols);
        int fruitY = random.nextInt(rows);

        // Ensure the fruit does not spawn on the snake
        while (isSnake(fruitX, fruitY)) {
            fruitX = random.nextInt(cols);
            fruitY = random.nextInt(rows);
        }
        fruit = new Point(fruitX, fruitY);
    }

    private boolean isSnake(int x, int y){
        for (Point p : snake) {
            if (p.x == x && p.y == y) {
                return true;
            }
        }
        return false;
    }

    private int indexOfCellInSnake(int x, int y){
        int i=0;
        for (Point p:snake){
            if(p.x == x && p.y == y){
                return i;
            }
            i++;
        }
        return -1;
    }



    public void resume(){
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause(){
        running = false;
        try{
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float cellPx = CELL_SIZE * getResources().getDisplayMetrics().density;

        cols = Math.max(10, (int) Math.floor(w / cellPx));
        rows = Math.max(10, (int) Math.floor(h / cellPx));

        cell = Math.min(
                (float) w / (float) cols,
                (float) h / (float) rows
        );

        offsetX = (w - cols * cell) / 2f;
        offsetY = (h - rows * cell) / 2f;

        gridReady = true;

        if(!snakeInited){
            snake.clear();
            int snakeX = cols / 2;
            int snakeY = rows / 2;
            snake.add(new Point(snakeX, snakeY));
            /*snake.add(new Point(snakeX - 1, snakeY));
            snake.add(new Point(snakeX - 2, snakeY));
            snake.add(new Point(snakeX - 3, snakeY));
            snake.add(new Point(snakeX - 4, snakeY));*/
            snakeInited = true;
        }
        float buttonSize = 100; // Adjust size as needed
        pauseButton = new RectF(
                w - buttonSize - 20, // Right margin
                20,                  // Top margin
                w - 20,              // Left margin
                20 + buttonSize      // Bottom margin
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                // 0) klik na pauzu (iba ak nie je game over)
                if (!isGameover && !isPaused && pauseButton != null && pauseButton.contains(x, y)) {
                    isPaused = true;
                    return true;
                }

                // 1) GAME OVER má prednosť
                if (isGameover) {
                    if (restartButton != null && restartButton.contains(x, y)) {
                        restartGame();
                        return true;
                    }
                    if (mainMenuButton != null && mainMenuButton.contains(x, y)) {
                        getContext().startActivity(new Intent(getContext(), MainMenuActivity.class));
                        return true;
                    }
                    return true; // klik mimo restartu počas game over
                }

                // 2) PAUZA (iba ak nie je game over)
                if (isPaused) {
                    if (resumeButton != null && resumeButton.contains(x, y)) { isPaused = false; return true; }
                    if (mainMenuButton != null && mainMenuButton.contains(x, y)) {
                        getContext().startActivity(new Intent(getContext(), MainMenuActivity.class));
                        return true;
                    }
                    if (exitButton != null && exitButton.contains(x, y)) {
                        ((MainActivity) getContext()).finish();
                        return true;
                    }
                    return true;
                }

                // 3) začiatok swipu
                downX = x; downY = y;
                return true;


            case MotionEvent.ACTION_MOVE:
                if(isPaused || isGameover) return true; // Ignore moves while paused
                float deltaX = e.getX() - downX;
                float deltaY = e.getY() - downY;
                if(Math.abs(deltaX) > Math.abs(deltaY)){
                    if(deltaX > 0 && direction != Direction.LEFT){
                        direction = Direction.RIGHT;
                    } else if(deltaX < 0 && direction != Direction.RIGHT){
                        direction = Direction.LEFT;
                    }
                } else {
                    if(deltaY > 0 && direction != Direction.UP){
                        direction = Direction.DOWN;
                    } else if(deltaY < 0 && direction != Direction.DOWN){
                        direction = Direction.UP;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                return true;
        }

        return super.onTouchEvent(e);
    }

    public void restartGame(){
            score = 0;
            targetLength = 2;
            snake.clear();
            int snakeX = cols / 2;
            int snakeY = rows / 2;
            snake.add(new Point(snakeX, snakeY));
            /*snake.add(new Point(snakeX - 1, snakeY));
            snake.add(new Point(snakeX - 2, snakeY));
            snake.add(new Point(snakeX - 3, snakeY));
            snake.add(new Point(snakeX - 4, snakeY));*/
            direction = Direction.RIGHT;
            generateFruit();
            isGameover = false;
            isPaused = false;
            lastTick = System.currentTimeMillis();

    }







}
