package com.example.android.voicerecorderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class WelcomeActivity extends AppCompatActivity {
    Animation bottomAnim;
    TextView slogan;
    LottieAnimationView wavesAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        bottomAnim = AnimationUtils.loadAnimation(this ,R.anim.bottom_animation);
        slogan = findViewById(R.id.textSlogan);
        wavesAnimation = findViewById(R.id.FirstAnimation);
       // slogan.animate().translationY(2000).alpha(100).setDuration(3000).setStartDelay(1000);
        slogan.setAnimation(bottomAnim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this , MainActivity.class);
                startActivity(intent);
            }
        },3000);
    }
}