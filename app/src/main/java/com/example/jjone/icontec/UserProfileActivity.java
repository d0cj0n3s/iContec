package com.example.jjone.icontec;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_WRITE_CONTACTS = 200;
    EditText name_field, phone_field, address_field;
    String detail;
    String id;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSIONS_REQUEST_WRITE_CONTACTS);
        }
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            detail = extras.getString("detail");
            id = extras.getString("ID");
        }
        final String[] detailarray = detail.split("\\|");
//        Log.i("Array info", Integer.toString(detailarray.length) + " " + detailarray[0] + " " + detailarray[1] + " " + detailarray[2] + " " + detailarray[4]);
        name_field = (EditText) findViewById(R.id.edittxt_name);
        phone_field = (EditText) findViewById(R.id.editTxt_work_phone);
        address_field = (EditText) findViewById(R.id.editTxt_address);

        save = (Button) findViewById(R.id.btn_save);


        name_field.setText(detailarray[1]);
        if(detailarray.length == 3)
        {
            phone_field.setText(detailarray[2]);
        }
        if (detailarray.length > 3)
        {
            address_field.setText(detailarray[4]);
        }
//
        String cContactIdString = ContactsContract.Contacts._ID;
        Uri cContact_Content_URI = ContactsContract.Contacts.CONTENT_URI;

        String selection = cContactIdString + " = ? " ;
        String[] selectionArgs = new String[]{String.valueOf(id)};

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(cContact_Content_URI, null, selection, selectionArgs, null);

        if((cur!=null) && (cur.getCount() > 0))
        {
            cur.moveToFirst();
            while((cur!=null) && (cur.isAfterLast() == false))
            {
                if(cur.getColumnIndex(cContactIdString) >= 0)
                {
                    if(id.equals(cur.getString(cur.getColumnIndex(cContactIdString))))
                    {
                        String temp_name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Log.i("My info", temp_name );
                        break;
                    }
                }
                cur.moveToNext();
            }
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    ContentResolver contentResolver  = getContentResolver();

                    String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

                    String[] addParams = new String[]{detailarray[0], ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                    String[] nameParams = new String[]{detailarray[0], ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
                    String[] numberParams = new String[]{detailarray[0], ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};

                    ArrayList<ContentProviderOperation> ops = new ArrayList<android.content.ContentProviderOperation>();

                    if(!name_field.getText().toString().equals(""))
                    {
                        ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                                .withSelection(where,nameParams)
                                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name_field.getText().toString())
                                .build());
                    }
                    if(!address_field.getText().toString().equals(""))
                    {
                        ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                                .withSelection(where,addParams)
                                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address_field.getText().toString())
                                .build());
                    }

                    if(!phone_field.getText().toString().equals(""))
                    {

                        ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
                                .withSelection(where,numberParams)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone_field.getText().toString())
                                .build());
                    }
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                Intent i = new Intent(UserProfileActivity.this, ContactDisplay.class);
                startActivity(i);
            }
        });

    }

}