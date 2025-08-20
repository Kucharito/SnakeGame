package com.example.snake;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_info);

            Button backButton = findViewById(R.id.backButton);

            backButton.setOnClickListener(v -> {
                // Handle back button click
                Intent intent = new Intent(InfoActivity.this, MainMenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            });

            // Initialize UI components and set up listeners here
            // For example, you can display game instructions or credits
        }

        // Add methods to handle any interactions in the Info Activity, if needed
}
