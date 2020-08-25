package com.example.mdp;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements IMainActivity, MainBottomFragment.FragmentMainBottomListener {
    private static final String TAG = "MainActivity";
    BluetoothDevice myBTConnectionDevice;

    static String connectedDevice;
    boolean connectedState;
    boolean currentActivity;

    private MainTopFragment mainTopFragment;
    private MainBottomFragment mainBottomFragment;

    //UUID
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectedDevice = null;
        connectedState = false;
        currentActivity = true;

        //REGISTER BROADCAST RECEIVER FOR INCOMING MSG
        LocalBroadcastManager.getInstance(this).registerReceiver(btConnectionReceiver, new IntentFilter("btConnectionStatus"));
        //LocalBroadcastManager.getInstance(this).registerReceiver(incomingMsgReceiver, new IntentFilter("IncomingMsg"));

        if (savedInstanceState == null) {
            Fragment topFragment = new MainTopFragment();
            doTopFragmentTransaction(topFragment, "top_main", false, "");
            Fragment bottomFragment = new MainBottomFragment();
            doBottomFragmentTransaction(bottomFragment, "bottom_main", false, "");
        }

        mainTopFragment = new MainTopFragment();
        mainBottomFragment = new MainBottomFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_top, mainTopFragment)
                .replace(R.id.fragment_container_bottom, mainBottomFragment)
                .commit();
    }

    @Override
    public void onInputMainBottomSent(String direction) {
        mainTopFragment.moveRobot(direction);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            //ONCLICK BLUETOOTH SEARCH BUTTON
            case R.id.bluetooth:


                Intent intent = new Intent(getApplicationContext(), BluetoothMain.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

        }

        switch (item.getItemId()) {
            case R.id.reconfigure:

                inflateFragment("reconfig", "");

                //Intent intent = new Intent(getApplicationContext(), ReconfigBottomFragment.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                //startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    //BROADCAST RECEIVER FOR BLUETOOTH CONNECTION STATUS
    BroadcastReceiver btConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "Receiving btConnectionStatus Msg!!!");

            String connectionStatus = intent.getStringExtra("ConnectionStatus");
            myBTConnectionDevice = intent.getParcelableExtra("Device");
            //myBTConnectionDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            //DISCONNECTED FROM BLUETOOTH CHAT
            if (connectionStatus.equals("disconnect")) {

                Log.d("MainActivity:", "Device Disconnected");
                connectedDevice = null;
                connectedState = false;
             //   connectionStatusBox.setText(R.string.btStatusOffline);

                if (currentActivity) {

                    //RECONNECT DIALOG MSG
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("BLUETOOTH DISCONNECTED");
                    alertDialog.setMessage("Connection with device: '" + myBTConnectionDevice.getName() + "' has ended. Do you want to reconnect?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    //START BT CONNECTION SERVICE
                                    Intent connectIntent = new Intent(MainActivity.this, BluetoothConnectionService.class);
                                    connectIntent.putExtra("serviceType", "connect");
                                    connectIntent.putExtra("device", myBTConnectionDevice);
                                    connectIntent.putExtra("id", myUUID);
                                    startService(connectIntent);
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                }
            }
            //SUCCESSFULLY CONNECTED TO BLUETOOTH DEVICE
            else if (connectionStatus.equals("connect")) {

                connectedDevice = myBTConnectionDevice.getName();
                connectedState = true;
                Log.d("MainActivity:", "Device Connected " + connectedState);
               // connectionStatusBox.setText(connectedDevice);
                Toast.makeText(MainActivity.this, "Connection Established: " + myBTConnectionDevice.getName(),
                        Toast.LENGTH_LONG).show();
            }

            //BLUETOOTH CONNECTION FAILED
            else if (connectionStatus.equals("connectionFail")) {
                Toast.makeText(MainActivity.this, "Connection Failed: " + myBTConnectionDevice.getName(),
                        Toast.LENGTH_LONG).show();
            }

        }
    };


    private void doTopFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack, String message){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(!message.equals("")){
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.intent_message), message);
            fragment.setArguments(bundle);
        }
        transaction.replace(R.id.fragment_container_top, fragment, tag);
        if(addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }
    private void doBottomFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack, String message){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(!message.equals("")){
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.intent_message), message);
            fragment.setArguments(bundle);
        }
        transaction.replace(R.id.fragment_container_bottom, fragment, tag);
        if(addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    @Override
    public void inflateFragment(String fragmentTag, String message) {
        if (fragmentTag.equals("bluetooth")) {
            BluetoothTopFragment topFragment = new BluetoothTopFragment();
            doTopFragmentTransaction(topFragment, fragmentTag, true, message);
            BluetoothBottomFragment bottomFragment = new BluetoothBottomFragment();
            doBottomFragmentTransaction(bottomFragment, fragmentTag, true, message);
        } else if (fragmentTag.equals("backToMain")) {
            MainTopFragment topFragment = new MainTopFragment();
            doTopFragmentTransaction(topFragment, fragmentTag, false, message);
            MainBottomFragment bottomFragment = new MainBottomFragment();
            doBottomFragmentTransaction(bottomFragment, fragmentTag, false, message);
        } else if (fragmentTag.equals("reconfig")) {
            ReconfigTopFragment topFragment = new ReconfigTopFragment();
            doTopFragmentTransaction(topFragment, fragmentTag, true, message);
            ReconfigBottomFragment bottomFragment = new ReconfigBottomFragment();
            doBottomFragmentTransaction(bottomFragment, fragmentTag, true, message);
        } else if (fragmentTag.equals("reconfig_select")) {
            ReconfigSelectTopFragment topFragment = new ReconfigSelectTopFragment();
            doTopFragmentTransaction(topFragment, fragmentTag, true, message);
            ReconfigSelectBottomFragment bottomFragment = new ReconfigSelectBottomFragment();
            doBottomFragmentTransaction(bottomFragment, fragmentTag, true, message);
        } else if (fragmentTag.equals("back_config")) {
            ReconfigTopFragment topFragment = new ReconfigTopFragment();
            doTopFragmentTransaction(topFragment, fragmentTag, false, message);
            ReconfigBottomFragment bottomFragment = new ReconfigBottomFragment();
            doBottomFragmentTransaction(bottomFragment, fragmentTag, false, message);
        } else if (fragmentTag.equals("waypoint")) {
            WaypointTopFragment topFragment = new WaypointTopFragment();
            doTopFragmentTransaction(topFragment, fragmentTag, true, message);
            WaypointBottomFragment bottomFragment = new WaypointBottomFragment();
            doBottomFragmentTransaction(bottomFragment, fragmentTag, true, message);
        }

    }
}
