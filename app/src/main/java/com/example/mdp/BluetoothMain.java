package com.example.mdp;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BluetoothMain extends AppCompatActivity {
    private static final String TAG = "BluetoothMain";

    public ArrayList<BluetoothDevice> BTDeviceList = new ArrayList<>();
    public ArrayList<BluetoothDevice> BTPairedDevicesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_bluetoothmain);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            //ONCLICK BLUETOOTH SEARCH BUTTON
            case R.id.home:


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

        }

        switch (item.getItemId()) {
            case R.id.reconfigure:
                
                Intent intent = new Intent(getApplicationContext(), ReconfigBottomFragment.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
