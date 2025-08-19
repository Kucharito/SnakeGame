package com.example.snake;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
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

    private boolean gridReady = false;
    private boolean snakeInited = false;

    private static final float CELL_SIZE = 20f;

    private int cols = 20;
    private int rows = 28;
    private float cell;
    private float offsetX, offsetY;

    private final ArrayDeque<Point> snake = new ArrayDeque<>();
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    private Direction direction = Direction.RIGHT;
    private int targetLength = 6;

    private long stepTime = 140;
    private long lastTick = 0;

    private float downX, downY;

    private boolean isPaused = false;
    private RectF resumeButton;
    private RectF mainMenuButton;
    private RectF exitButton;
    private RectF pauseButton;


    public SnakeView(Context context) {
        super(context);
        holder = getHolder();
        paint = new Paint();
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);

    }

    @Override
    public void run() {
        while(running){
            long now = System.currentTimeMillis();
            if(now - lastTick >= stepTime && !isPaused){
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

        newX = (newX + cols) % cols;
        newY = (newY + rows) % rows;

        snake.addFirst(new Point(newX, newY));

        if(snake.size() > targetLength){
            snake.removeLast();
        }
    }

    private void draw() {
        if (!holder.getSurface().isValid()) return;
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return;

        canvas.drawColor(Color.BLACK);


        if(pauseButton != null){
            paint.setColor(Color.GRAY);
            canvas.drawRoundRect(pauseButton, 50, 50, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("II", pauseButton.centerX(),pauseButton.centerY()+15, paint);
        }

        // --- had ---
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);   // farba hada
        float pad = 3f;
        for (Point p : snake) {
            float left   = offsetX + p.x * cell + pad;
            float top    = offsetY + p.y * cell + pad;
            float right  = left + cell - 2 * pad;
            float bottom = top  + cell - 2 * pad;
            canvas.drawRect(left, top, right, bottom, paint);
        }

        if(isPaused){
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
            canvas.drawText("Resume", centerX, centerY - 100, paint);
            canvas.drawText("Main Menu", centerX, centerY + 100, paint);
            canvas.drawText("Exit", centerX, centerY + 300, paint);

        }
        holder.unlockCanvasAndPost(canvas);
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
            snake.add(new Point(snakeX - 1, snakeY));
            snake.add(new Point(snakeX - 2, snakeY));
            snake.add(new Point(snakeX - 3, snakeY));
            snake.add(new Point(snakeX - 4, snakeY));
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
                if(!isPaused && pauseButton != null && pauseButton.contains(x, y)) {
                    isPaused = true; // Pause the game
                    return true;
                }


                if(isPaused) {
                    if (resumeButton.contains(x, y)) {
                        isPaused = false; // Toggle pause state
                    } else if (mainMenuButton.contains(x, y)) {
                        // Handle main menu action
                        // For example, start MainMenuActivity
                        Intent intent = new Intent(getContext(), MainMenuActivity.class);
                        getContext().startActivity(intent);
                    } else if (exitButton.contains(x, y)) {
                        // Handle exit action
                        ((MainActivity) getContext()).finish();
                    }
                }

                downX = x;
                downY = y;
                return true;

            case MotionEvent.ACTION_MOVE:
                if(isPaused) return true; // Ignore moves while paused
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







}
