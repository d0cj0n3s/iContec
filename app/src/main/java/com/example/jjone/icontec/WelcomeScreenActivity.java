package com.example.jjone.icontec;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

public class WelcomeScreenActivity extends AppCompatActivity {
    LinearLayout linear_layout_icontect;
    LinearLayout linear_layout_button;
    Animation uptodown;
    Animation downtoup;
    Button proceed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        linear_layout_icontect = (LinearLayout)findViewById(R.id.layout_icontec);
        linear_layout_button = (LinearLayout) findViewById(R.id.layout_button);
        uptodown = AnimationUtils.loadAnimation(this,R.anim.upanddown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downandup);
        linear_layout_icontect.setAnimation(uptodown);
        linear_layout_button.setAnimation(downtoup);

        proceed = (Button) findViewById(R.id.btn_proceed);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(WelcomeScreenActivity.this, ContactDisplay.class);
                startActivity(i);
            }
        });



    }
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }
}
