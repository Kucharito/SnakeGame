package com.example.snake;

import android.content.Context;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SnakeView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean running;
    private final SurfaceHolder holder;
    private Paint paint;

    public SnakeView(Context context) {
        super(context);
        holder = getHolder();
        paint = new Paint();
    }

    @Override
    public void run() {
        while(running){
            update();
            draw();
            try{
                Thread.sleep(1000 / 60); // 60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update(){
        // Update game logic here

    }

    private void draw(){


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

}
