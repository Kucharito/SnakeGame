package com.example.snake;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Toast.makeText(this, "Options Activity", Toast.LENGTH_SHORT).show();

        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            // Handle back button click
            Intent intent = new Intent(OptionsActivity.this, MainMenuActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Initialize UI components and set up listeners here
        // For example, you can set up buttons to change game settings
    }

    // Add methods to handle options changes, like saving preferences or updating the game settings
}
