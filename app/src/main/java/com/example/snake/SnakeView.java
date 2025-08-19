package com.example.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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
            if(now - lastTick >= stepTime){
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

        /*if(!gridReady){
            int w = canvas.getWidth(), h = canvas.getHeight();
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
        }*/

        canvas.drawColor(Color.BLACK);

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
    }

    @Override
    public boolean onTouchEvent(MotionEvent e){
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = e.getX();
                downY = e.getY();
                return true;

            case MotionEvent.ACTION_MOVE:
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
