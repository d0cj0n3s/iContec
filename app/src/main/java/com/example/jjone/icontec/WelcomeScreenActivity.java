package com.example.jjone.icontec;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.io.File;
import java.util.ArrayList;

public class WelcomeScreenActivity extends AppCompatActivity {
    LinearLayout linear_layout_icontect;
    LinearLayout linear_layout_button;
    Animation uptodown;
    Animation downtoup;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    //For popup window
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private ConstraintLayout constraintLayout;

    private NfcAdapter mNfcAdapter;

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


        //Checks permissions when app is first launched
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
            {
                Log.d("Write permission", "granted");
                Log.d("Read Contacts", "granted");
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_CONTACTS},1 );
            }
        }

        /** Handle NFC Intent */
        ArrayList<String> messagesReceivedArray = new ArrayList<>();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter != null) {
            mNfcAdapter.setNdefPushMessage(null, this);
         }

        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

            Log.d("DB", "PACKAGE NAME IS MATCHED");
            NdefMessage ndefMessage = null;
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if ((rawMessages != null) && (rawMessages.length > 0)) {
                Log.d("DB", "message received, not null");
                ndefMessage = (NdefMessage) rawMessages[0];
                NdefRecord[] attachedRecords = ndefMessage.getRecords();

                for (NdefRecord record:attachedRecords) {
                    String string = new String(record.getPayload());
                    //Make sure don't pass along our AAR (Android Application Record)
                    if (string.equals(getPackageName())) { continue; }
                    messagesReceivedArray.add(string);
                }
                //Log.d("DB", String.valueOf(messagesReceivedArray.size()));
            } else {
                Log.d("DB", "message not received or null");
            }

            if(messagesReceivedArray.size() > 0) {
                String name = messagesReceivedArray.get(0);
                String phone = messagesReceivedArray.get(1);
                String email = messagesReceivedArray.get(2);


                //Log.d("DB", name + " " + phone + " " + email);
                Intent exchange_intent = new Intent(WelcomeScreenActivity.this, ExchangeActivity.class);
                exchange_intent
                        .putExtra("name", name)
                        .putExtra("phone", phone)
                        .putExtra("email", email)
                        .putExtra("check", "Welcome");

                Log.d("DB", "Recevie ndef in welcome");

                startActivity(exchange_intent);
                finish();

            }
        } else { Log.d("DB", "DOESN't MATCH"); }

        checkStorageFolder();
    }

    // method for the pup that displays the tutorial when the Instructions button is tapped
    @SuppressLint("SetTextI18n")
    public void popUpTutorialWelcome(View view)
    {
        constraintLayout = findViewById(R.id.welcomeCon);

        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup)layoutInflater.inflate(R.layout.tutorial_popup,null);

        popupWindow = new PopupWindow(container, 800,750,true);

        String tutorialMessage = "Thank you for choosing iContec!\n\nThis application is designed to" +
                " make exchanging contact information easier than ever before!\n\nTo begin using the" +
                " application, press the 'Proceed' button located at the bottom of the screen.\n\n";

        ((TextView)popupWindow.getContentView().findViewById(R.id.tutorialText)).setText(tutorialMessage);
        popupWindow.showAtLocation(constraintLayout, Gravity.NO_GRAVITY, 180,500);

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

    //method to check if the storage folder for the cards has been created, and if not, create it
    public void checkStorageFolder()
    {
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Card Folder");

        if (!folder.exists())
        {
            folder.mkdirs();
            Log.i("DB","The directory being made is :" + folder);
        }

    }

}
