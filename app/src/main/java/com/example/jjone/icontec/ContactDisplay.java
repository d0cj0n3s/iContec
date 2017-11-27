package com.example.jjone.icontec;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_display);

        StringBuilder sb = new StringBuilder("");

        contact_list = (ListView) findViewById(R.id.list_contact);
        exchange = (FloatingActionButton)findViewById(R.id.btn_exchange);
        final List<String[]> items_subitems = new LinkedList<String[]>();

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }


        ContentResolver resolver = getContentResolver();

        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null,null,null, null);

        while (cursor.moveToNext())
        {
            id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{ id }, null);

            items_subitems.add(new String[]{ name, id} );
            /*Log.i("My info",id + " = " + name);
            sb.append("Contact Id = " + id + "\n");
            sb.append("Contact Name = " + name + "\n");*/


            while(phoneCursor.moveToNext())
            {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.i("My info", phoneNumber);
            }

            Cursor emailCursor = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email._ID + " = ?" ,new String[]{ id},null);

            while(emailCursor.moveToNext())
            {
                String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                Log.i("My info", email);
            }
        }
        ArrayAdapter<String[]> items_subitem_Adapter = new ArrayAdapter<String[]>(this, android.R.layout.simple_list_item_2,android.R.id.text1, items_subitems)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View view= super.getView(position, convertView, parent);

                String[] entry = items_subitems.get(position);
                TextView text1 = (TextView)view.findViewById(android.R.id.text1);
                TextView text2 = (TextView)view.findViewById(android.R.id.text2);
                text1.setText(entry[0]);
                text1.setTextSize(20);
                text2.setText(entry[1]);
                text2.setVisibility(View.INVISIBLE);
                return view;
            }
        };

        contact_list.setAdapter(items_subitem_Adapter);
        contact_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView contact_name = (TextView)view.findViewById(android.R.id.text1);
                TextView contact_id = (TextView)view.findViewById(android.R.id.text2);
                Toast.makeText(ContactDisplay.this, contact_name.getText().toString() + ": " + contact_id.getText().toString(), Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }
}