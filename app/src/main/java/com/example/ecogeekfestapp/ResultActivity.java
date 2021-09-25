package com.example.ecogeekfestapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private static final String EXTRA_PLANT = "extra_plant";

    public static Intent createIntent(Context context, String plant) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra(EXTRA_PLANT, plant);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView plantTextView = findViewById(R.id.suggestedPlantText);

        String plant = getIntent().getStringExtra(EXTRA_PLANT);
        if (plant == null || plant.isEmpty()) {
            plantTextView.setText(R.string.no_plant);
        } else {
            plantTextView.setText(plant);
        }

    }
}
