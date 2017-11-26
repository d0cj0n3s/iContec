package com.example.jjone.icontec;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class WelcomeScreenActivity extends AppCompatActivity {
    LinearLayout linear_layout_icontect;
    LinearLayout linear_layout_button;
    Animation uptodown;
    Animation downtoup;

    //For popup window
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private ConstraintLayout constraintLayout;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome_screen);
        linear_layout_icontect = (LinearLayout)findViewById(R.id.layout_icontec);
        linear_layout_button = (LinearLayout) findViewById(R.id.layout_button);
        uptodown = AnimationUtils.loadAnimation(this,R.anim.upanddown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downandup);
        linear_layout_icontect.setAnimation(uptodown);
        linear_layout_button.setAnimation(downtoup);
    }

    // method for the pup that displays the tutorial when the Instructions button is tapped
    @SuppressLint("SetTextI18n")
    public void popUpTutorialWelcome(View view)
    {
        constraintLayout = findViewById(R.id.welcomeCon);

        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup)layoutInflater.inflate(R.layout.tutorial_popup,null);

        popupWindow = new PopupWindow(container, 900,1000,true);

        String tutorialMessage = "Thank you for choosing iContec!\n\nThis application is designed to" +
                " make exchanging contact information easier than ever before!\n\nTo begin using the" +
                " application, press the 'Proceed' button located at the bottom of the screen.\n\n";

        ((TextView)popupWindow.getContentView().findViewById(R.id.tutorialText)).setText(tutorialMessage);
        popupWindow.showAtLocation(constraintLayout, Gravity.NO_GRAVITY, 250,750);

        container.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    // Method for Proceeds button.
    public void pastWelcome (View view)
    {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String owner = sharedpreferences.getString("name", "No name");

        // If the owner's name is not set, choose activity to start
        if(owner.equals("No name"))
            startActivity(new Intent(this, CreateUserProfile.class));
        else
            startActivity(new Intent(this, ContactDisplay.class));
    }


}
