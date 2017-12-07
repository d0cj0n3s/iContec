package com.example.jjone.icontec;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ExchangeActivity extends Activity
        implements NfcAdapter.OnNdefPushCompleteCallback, NfcAdapter.CreateNdefMessageCallback {

    private EditText name;
    private EditText phoneNumber;
    private EditText email;
    private ImageView card;
    private Button btnAddMessage;

    private NfcAdapter mNfcAdapter;

    private ArrayList<String> messagesToSendArray = new ArrayList<>();
    private ArrayList<String> messagesReceivedArray = new ArrayList<>();

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    //For popup window
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private LinearLayout linearLayout;

    // Array of uri objects to transfer
    private Uri[] mFileUris = new Uri[10];




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String ownerName = sharedpreferences.getString("name", "No name");
        String ownerPhone = sharedpreferences.getString("phone", "No phone");
        String ownerEmail = sharedpreferences.getString("email", "No email set");
        String ownerCard = sharedpreferences.getString("cardUri", "No card");

        //Log.d("DB", "OnCreate()");
        name =  findViewById(R.id.txtBoxAddMessage);
        phoneNumber = findViewById(R.id.txtBoxAddMessage2);
        email = findViewById(R.id.txtBoxAddMessage3);
        card = findViewById(R.id.cardView);

        name.setText(ownerName);
        phoneNumber.setText(ownerPhone);
        email.setText(ownerEmail);
        card.setImageURI(Uri.parse(ownerCard));

        btnAddMessage =  findViewById(R.id.buttonAddMessage);

        updateTextViews();

        btnAddMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMessage(view);
            }
        });

        startNfcAdapter();

        Intent intent = getIntent();
        String c_name;
        String c_phone;
        String c_email;
        String c_check;

        if(intent != null) {
            c_check = intent.getStringExtra("check");
            //Log.d("DB", c_check);
            if(c_check.equals("Welcome")) {
                c_name = intent.getStringExtra("name");
                c_phone = intent.getStringExtra("phone");
                c_email = intent.getStringExtra("email");

                Log.d("DB","Hi I am here");

                addContact(c_name, c_phone, c_email);
            } else {
                Log.d("DB", "I am out");
            }
        }
    }

    public void startNfcAdapter(){
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            //This will refer back to createNdefMessage for what it will send
            mNfcAdapter.setNdefPushMessageCallback(this, this);
            Log.d("DB", "setNdefPushMessageCallback");

            //This will be called if the message is sent successfully
            mNfcAdapter.setOnNdefPushCompleteCallback(this,this);
            Log.d("DB", "setOnNdefPushCompleteCallback");
        }
        else {
            Toast.makeText(this, "No NFC on this device", Toast.LENGTH_LONG).show();
        }
        handleNfcIntent(getIntent());
    }

    public void addMessage(View view) {

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        String ownerName = name.getText().toString();
        String ownerPhone = phoneNumber.getText().toString();
        String ownerEmail = email.getText().toString();

        editor.putString("name", ownerName);
        editor.putString("phone", ownerPhone);
        editor.putString("email", ownerEmail);
        editor.apply(); // commit change

        //StringBuilder sb = new StringBuilder();
        String getName = sharedpreferences.getString("name", "No name defined");//"No name defined" is the default value.
        String getPhone = sharedpreferences.getString("phone", "No phone number  defined");
        String getEmail = sharedpreferences.getString("email", "No email defined");

        messagesToSendArray.add(getName);
        messagesToSendArray.add(getPhone);
        messagesToSendArray.add(getEmail);

        Toast.makeText(this, "Added Message", Toast.LENGTH_LONG).show();
    }

    private void updateTextViews() {

        if(messagesReceivedArray.size() > 0) {
            String name = messagesReceivedArray.get(0);
            String phone = messagesReceivedArray.get(1);
            String email = messagesReceivedArray.get(2);

            Log.d("DB", name + " " + phone + " " + email);

            addContact(name, phone, email);
        }
    }

    private void addContact(String name, String phone, String email) {

        Log.d("DB", "Add Contact");

        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        intent
                .putExtra(ContactsContract.Intents.Insert.NAME, name)

                .putExtra(ContactsContract.Intents.Insert.PHONE, phone)

                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK)

                .putExtra(ContactsContract.Intents.Insert.EMAIL, email)

                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK);

        Log.d("DB", intent.toString());

        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d("DB", "onSaveInstanceState");
        savedInstanceState.putStringArrayList("messagesToSend", messagesToSendArray);
        savedInstanceState.putStringArrayList("lastMessagesReceived",messagesReceivedArray);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("DB", "onRestoreInstanceState");
        messagesToSendArray = savedInstanceState.getStringArrayList("messagesToSend");
        messagesReceivedArray = savedInstanceState.getStringArrayList("lastMessagesReceived");
    }

    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        //This is called when the system detects that the NdefMessage was successfully sent.
        Log.d("DB", "onNdefPushComplete");
        messagesToSendArray.clear();
        startNfcAdapter();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        //This will be called when another NFC capable device is detected.
        if (messagesToSendArray.size() == 0) {
            return null;
        }

        Log.d("DB", "createNdefRecord");
        NdefRecord[] recordsToAttach = createRecords();

        Log.d("DB", "createNdefMessage");
        return new NdefMessage(recordsToAttach);
    }

    public NdefRecord[] createRecords() {

        Log.d("DB", "createRecords");
        NdefRecord[] records = new NdefRecord[messagesToSendArray.size() + 1];

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            for (int i = 0; i < messagesToSendArray.size(); i++) {

                byte[] payload = messagesToSendArray.get(i).
                        getBytes(Charset.forName("UTF-8"));

                NdefRecord record = new NdefRecord(
                        NdefRecord.TNF_WELL_KNOWN,  //Our 3-bit Type name format
                        NdefRecord.RTD_TEXT,        //Description of our payload
                        new byte[0],                //The optional id for our Record
                        payload);                   //Our payload for the Record

                records[i] = record;
            }
        } else {
            for (int i = 0; i < messagesToSendArray.size(); i++){
                byte[] payload = messagesToSendArray.get(i).
                        getBytes(Charset.forName("UTF-8"));

                NdefRecord record = NdefRecord.createMime("text/plain",payload);
                records[i] = record;
            }
        }
        records[messagesToSendArray.size()] =
                NdefRecord.createApplicationRecord(getPackageName());

        return records;
    }

    private void handleNfcIntent(Intent NfcIntent) {
        Log.d("DB", "handleNfcIntent");
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(NfcIntent.getAction())) {
            Parcelable[] receivedArray =
                    NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(receivedArray != null) {
                messagesReceivedArray.clear();
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] attachedRecords = receivedMessage.getRecords();

                for (NdefRecord record:attachedRecords) {
                    String string = new String(record.getPayload());
                    //Make sure don't pass along our AAR (Android Application Record)
                    if (string.equals(getPackageName())) { continue; }
                    messagesReceivedArray.add(string);
                }
                Toast.makeText(this, "Received " + messagesReceivedArray.size() +
                        " Messages", Toast.LENGTH_LONG).show();
                updateTextViews();
            }
            else {
                Toast.makeText(this, "Received Blank Parcel", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        handleNfcIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("DB", "onResume");
        updateTextViews();
        handleNfcIntent(getIntent());
    }

    // Method for Proceed button. Proceeds to Success activity.
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void sendCard (View view)
    {
        Intent i = new Intent(ExchangeActivity.this, TransferCompleteActivity.class);
        startActivity(i);
    }

    // method for the pup that displays the tutorial when the Instructions button is tapped
    @SuppressLint("SetTextI18n")
    public void popUpTutorialExchangeInfo(View view)
    {
        linearLayout = findViewById(R.id.linearLay);

        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup)layoutInflater.inflate(R.layout.tutorial_popup,null);

        popupWindow = new PopupWindow(container, 900,500,true);

        String tutorialMessage = "To Exchange Contact Information, Touch EXCHANGE INFORMATION. To transmit your chosen business card, " +
                "click SEND CARD. ";

        ((TextView)popupWindow.getContentView().findViewById(R.id.tutorialText)).setText(tutorialMessage);
        popupWindow.showAtLocation(linearLayout, Gravity.NO_GRAVITY, 250,500);

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
}
