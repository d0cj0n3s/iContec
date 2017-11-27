package com.example.jjone.icontec;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class WelcomeScreenActivity extends AppCompatActivity {
    LinearLayout linear_layout_icontect;
    LinearLayout linear_layout_button;
    Animation uptodown;
    Animation downtoup;

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


        /** Handle NFC Intent */
        ArrayList<String> messagesReceivedArray = new ArrayList<>();

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
                Log.d("DB", String.valueOf(messagesReceivedArray.size()));
            } else {
                Log.d("DB", "message not received or null");
            }

            if(messagesReceivedArray.size() > 0) {
                String name = messagesReceivedArray.get(0);
                String phone = messagesReceivedArray.get(1);
                String email = messagesReceivedArray.get(2);

                Log.d("DB", name + " " + phone + " " + email);

                Intent exchange_intent = new Intent(WelcomeScreenActivity.this, ExchangeActivity.class);
                exchange_intent
                        .putExtra("name", name)
                        .putExtra("phone", phone)
                        .putExtra("email", email);

                startActivity(exchange_intent);
                finish();
                
            }
        } else { Log.d("DB", "DOESN't MATCH"); }
    }


    public void pastWelcome (View view)
    {
        final String PREFS_NAME = "MyPrefsFile";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");
            settings.edit().putBoolean("my_first_time", false).commit();
            startActivity(new Intent(this, CreateUserProfile.class));
            finish();
        }
        else
        {
            startActivity(new Intent(this, ContactDisplay.class));
            finish();
        }
    }

}
