package com.example.snake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OptionsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        SharedPreferences sharedPreferences = getSharedPreferences("SnakePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Toast.makeText(this, "Options Activity", Toast.LENGTH_SHORT).show();

        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            // Handle back button click
            Intent intent = new Intent(OptionsActivity.this, MainMenuActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        Button buttonWhite = findViewById(R.id.buttonWhite);
        buttonWhite.setOnClickListener(v -> {
            // Handle button click for white color option
            Toast.makeText(this, "White color selected", Toast.LENGTH_SHORT).show();
            editor.putString("snake_color", "white");
            editor.apply();
        });

        Button buttonRed = findViewById(R.id.buttonRed);
        buttonRed.setOnClickListener(v -> {
            // Handle button click for red color option
            Toast.makeText(this, "Red color selected", Toast.LENGTH_SHORT).show();
            editor.putString("snake_color", "red");
            editor.apply();
        });

        Button buttonBlue = findViewById(R.id.buttonBlue);
        buttonBlue.setOnClickListener(v -> {
            // Handle button click for blue color option
            Toast.makeText(this, "Blue color selected", Toast.LENGTH_SHORT).show();
            editor.putString("snake_color", "blue");
            editor.apply();
        });

        Button buttonFruitRed = findViewById(R.id.buttonFruitRed);
        buttonFruitRed.setOnClickListener(v -> {
            // Handle button click for red fruit option
            Toast.makeText(this, "Red fruit selected", Toast.LENGTH_SHORT).show();
            editor.putString("fruit_color", "red");
            editor.apply();
        });

        Button buttonFruitWhite = findViewById(R.id.buttonFruitWhite);
        buttonFruitWhite.setOnClickListener(v ->{
            // Handle button click for white fruit option
            Toast.makeText(this, "White fruit selected", Toast.LENGTH_SHORT).show();
            editor.putString("fruit_color", "white");
            editor.apply();
        });

        Button buttonFruitBlue = findViewById(R.id.buttonFruitBlue);
        buttonFruitBlue.setOnClickListener(v -> {
            // Handle button click for blue fruit option
            Toast.makeText(this, "Blue fruit selected", Toast.LENGTH_SHORT).show();
            editor.putString("fruit_color", "blue");
            editor.apply();
        });




        // Initialize UI components and set up listeners here
        // For example, you can set up buttons to change game settings
    }

    // Add methods to handle options changes, like saving preferences or updating the game settings
}
