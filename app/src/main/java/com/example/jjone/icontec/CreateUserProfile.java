package com.example.jjone.icontec;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);
         setTitle("User Profile Creation");
    }
    
    // delete method added for testing. Deletes the shared preferences. 
    public void deleteAllPref(View view)
    {
        getApplicationContext().getSharedPreferences(MyPREFERENCES, 0).edit().clear().commit();
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

    // Method to test if the information is actually in the shared preferences
    public void readFromPref(View view)
    {
        SharedPreferences prefs = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
            String name = prefs.getString("name", "No name defined");//"No name defined" is the default value.
            String phone = prefs.getString("phone", "No phone number  defined");
            String email = prefs.getString("email", "No email defined");
            sb.append(name + "\n" + phone + "\n" + email + "\n");
        TextView output = findViewById(R.id.response);
        output.setText(sb);
        Toast.makeText(CreateUserProfile.this,"Thanks",Toast.LENGTH_LONG).show();

    }
}
