package com.example.task;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.start_activity);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                //here you can start your Activity B.
                Intent startActivity = new Intent(StartActivity.this,ScrollingActivity.class);
                startActivity(startActivity);
            }

        }, 3000);

    }
}

