package com.example.jjone.icontec;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;


public class ContactDisplay extends AppCompatActivity
{
    ListView contact_list;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    String id;
    String name;
    FloatingActionButton exchange;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    private static int RESULT_LOAD_IMAGE = 1;
    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //For popup window
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private ConstraintLayout constraintLayout;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_display);

        StringBuilder sb = new StringBuilder("");

        contact_list = (ListView) findViewById(R.id.list_contact);
        exchange = (FloatingActionButton) findViewById(R.id.btn_exchange);
        final List<String[]> items_subitems = new LinkedList<String[]>();

        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter != null) {
            mNfcAdapter.setNdefPushMessage(null, this);
        }


        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        // Fill in the owner information box
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String ownerName = sharedpreferences.getString("name", "No name");
        String ownerCard = sharedpreferences.getString("cardUri", "No card");

        TextView ownerIdName = findViewById(R.id.ownerName);
        ImageView ownerIdCard = findViewById(R.id.cardView);
        ownerIdName.setText(ownerName);
        ownerIdCard.setImageURI(Uri.parse(ownerCard));

        ContentResolver resolver = getContentResolver();

        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext())
        {
            id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);


            String phoneNumber="";
            String email="";

            Log.e("My info",id + " = " + name);
            /*sb.append("Contact Id = " + id + "\n");
            sb.append("Contact Name = " + name + "\n");*/


            while (phoneCursor.moveToNext())
            {
                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //Log.i("My info in phone cursor", phoneNumber);
            }

            Cursor emailCursor = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email._ID + " = ?", new String[]{id}, null);

            while (emailCursor.moveToNext())
            {
                email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                //Log.i("My info in email", email);
            }

            String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] addrWhereParams = new String[]{id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
            Cursor addrCur = resolver.query(ContactsContract.Data.CONTENT_URI,null, addrWhere,addrWhereParams, null );

            String street="";
            String city ="";
            String state="";
            while(addrCur.moveToNext()) {
               /* String poBox = addrCur.getString(
                        addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));*/
                street = addrCur.getString(
                        addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                city = addrCur.getString(
                        addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                state = addrCur.getString(
                        addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                /*String postalCode = addrCur.getString(
                        addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                String country = addrCur.getString(
                        addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                String type = addrCur.getString(
                        addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));*/
                //Log.i("My address info in add", street + " " + city + " " + state) ;
            }
            items_subitems.add(new String[]{ id, name, phoneNumber, email, street, city,state});
            Log.i("My info", id + " " + name + " " + phoneNumber + " " + email + " " + street + " " + city + " " + state);

        }
        ArrayAdapter<String[]> items_subitem_Adapter = new ArrayAdapter<String[]>(this, android.R.layout.simple_list_item_2, android.R.id.text1, items_subitems)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);

                String[] entry = items_subitems.get(position);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                String temp = entry[0] + "|" + entry[1] + "|" + entry[2] +"|"+ entry[3] + "|" + entry[4];

                text1.setText(entry[1]);
                text1.setTextSize(20);
                text2.setText(temp);
                Log.i("list view item", temp);
                text2.setVisibility(View.INVISIBLE);
                return view;
            }
        };

        contact_list.setAdapter(items_subitem_Adapter);
        contact_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                TextView contact_name = (TextView) view.findViewById(android.R.id.text1);
                TextView contact_detail = (TextView) view.findViewById(android.R.id.text2);
                //Toast.makeText(ContactDisplay.this, contact_name.getText().toString() + ": " + contact_detail.getText().toString(), Toast.LENGTH_SHORT).show();

                Intent i = new Intent(ContactDisplay.this, UserProfileActivity.class);
                i.putExtra("detail", contact_detail.getText().toString());
                i.putExtra("name", contact_name.getText().toString());
                startActivity(i);
            }
        });

        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ContactDisplay.this, ExchangeActivity.class);
                i.putExtra("check", "main");
                startActivity(i);
            }
        });


    }

    // method for the pup that displays the tutorial when the Instructions button is tapped
    @SuppressLint("SetTextI18n")
    public void popUpTutorialContactDisplay(View view)
    {
        constraintLayout = findViewById(R.id.conDisplay);

        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.tutorial_popup,
                null);

        popupWindow = new PopupWindow(container, 900, 500, true);

        String tutorialMessage = "To view a contact who is listed, touch the entry. To proceed to " +
                "exchanging details, click the big fuscia dot in the lower right corner.";

        ((TextView) popupWindow.getContentView().findViewById(R.id.tutorialText)).setText(tutorialMessage);
        popupWindow.showAtLocation(constraintLayout, Gravity.NO_GRAVITY, 250, 500);

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

    public void onBackPressed()
    {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    // Method for Proceed button. Proceeds to next activity.
    public void toCreateUserProfile (View view)
    {
        startActivity(new Intent(this, CreateUserProfile.class));
    }

}

