package com.example.jjone.icontec;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;

public class CreateUserProfile extends AppCompatActivity
{
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    //For popup window
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);
        setTitle("User Profile Creation");

        isStoragePermissionGranted();
        isContactsPermissionGranted();
    }
    
    // delete method added for testing. Deletes the shared preferences. 
    public void deleteAllPref(View view)
    {
        SharedPreferences preferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    // Called when the user taps the Send button
    @SuppressLint("CommitPrefEdits")
    public void sendMessage(View view) throws IOException
    {
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        EditText name = findViewById(R.id.name);
        EditText phoneNumber = findViewById(R.id.phoneNumber);
        EditText email = findViewById(R.id.email);

        String ownerName = name.getText().toString();
        String ownerPhone = phoneNumber.getText().toString();
        String ownerEmail = email.getText().toString();

        editor.putString("name", ownerName);
        editor.putString("phone", ownerPhone);
        editor.putString("email", ownerEmail);
        editor.apply(); // commit changes
        Toast.makeText(CreateUserProfile.this,"Thanks! Your profile is created!",Toast.LENGTH_LONG).show();
    }

    //retrieve the business card for the user and display on the screen
    // Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
 //startActivityForResult(i, RESULT_LOAD_IMAGE);

    // method for the pup that displays the tutorial when the Instructions button is tapped
    @SuppressLint("SetTextI18n")
    public void popUpTutorialUserCreate(View view)
    {
        constraintLayout = findViewById(R.id.userCon);

        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup)layoutInflater.inflate(R.layout.tutorial_popup,null);

        popupWindow = new PopupWindow(container, 900,600,true);

        String tutorialMessage = "Enter in the contact details that you wish to transfer to another" +
                " user. At the moment there are no constraints on the form of the entries. To save " +
                "your entries, click Save. To proceed, click Next.";

        ((TextView)popupWindow.getContentView().findViewById(R.id.tutorialText)).setText(tutorialMessage);
        popupWindow.showAtLocation(constraintLayout, Gravity.NO_GRAVITY, 250,500);

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

    // methods to check if the appropriate permissions have been granted
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
    public boolean isContactsPermissionGranted()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            }
            else
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                    return false;
                }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    // Method for Proceed button. Proceeds to next activity.
    public void pastCreateUserProfile (View view)
    {
        startActivity(new Intent(this, ContactDisplay.class));
    }
}
