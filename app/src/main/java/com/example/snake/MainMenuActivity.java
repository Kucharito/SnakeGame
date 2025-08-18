package com.example.snake;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);

        Button startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> {
            // Start the game activity
            startActivity(new Intent(MainMenuActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        Button optionsButton = findViewById(R.id.optionsButton);

        optionsButton.setOnClickListener(v -> {
            // Start the options activity
            startActivity(new Intent(MainMenuActivity.this, OptionsActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        Button infoButton = findViewById(R.id.infoButton);

        infoButton.setOnClickListener(v -> {
            // Start the info activity
            startActivity(new Intent(MainMenuActivity.this, InfoActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> {
            // Close the app
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }
}
