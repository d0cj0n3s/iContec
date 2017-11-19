package com.example.jjone.icontec;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class WelcomeScreenActivity extends AppCompatActivity {
    LinearLayout linear_layout;
    Animation uptodown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        linear_layout = (LinearLayout)findViewById(R.id.layout_icontec);
        uptodown = AnimationUtils.loadAnimation(this,R.anim.upanddown);
        linear_layout.setAnimation(uptodown);
        //


    }
}
