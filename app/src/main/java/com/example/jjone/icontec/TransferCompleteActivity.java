package com.example.jjone.icontec;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ImageView;

import static android.content.ContentValues.TAG;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class TransferCompleteActivity extends Activity implements NfcAdapter.CreateBeamUrisCallback
{

    ImageView card;

    String ownerCard = "";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_transfer_complete);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        ownerCard = preferences.getString("cardUri", "No card");
        card = findViewById(R.id.cardView);
        card.setImageURI(Uri.parse(ownerCard));

        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(this);

        if (nfc != null)
        {
            Log.w(TAG, "NFC available. Setting Beam Push URI callback");
            nfc.setBeamPushUrisCallback(this, this);
            //This will be called if the message is sent successfully
            //nfc.setOnNdefPushCompleteCallback(this,this);
        }
        else
        {
            Log.w(TAG, "NFC is not available");
        }
    }

    // BEGIN_INCLUDE(createBeamUris)
    @Override
    public Uri[] createBeamUris(NfcEvent nfcEvent) {
        Log.i(TAG, "Beam event in progress; createBeamUris() called.");
        // Images are served using a content:// URI. See AssetProvider for implementation.
        Uri photoUri = Uri.parse(ownerCard);
        Log.i(TAG, "Sending URI: " + photoUri);
        return new Uri[] {photoUri};
    }
}