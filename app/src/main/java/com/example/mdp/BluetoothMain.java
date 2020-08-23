package com.example.mdp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothMain extends AppCompatActivity {
    private static final String TAG = "BluetoothMain";

    public ArrayList<BluetoothDevice> BTDeviceList = new ArrayList<>();
    public ArrayList<BluetoothDevice> BTPairedDeviceList = new ArrayList<>();

    public DeviceListAdapter DeviceListAdapter;
    public DeviceListAdapter PairedDeviceListAdapter;

    //Bounded Device
    static BluetoothDevice myBTDevice;
    BluetoothDevice myBTConnectionDevice;
    BluetoothAdapter myBluetoothAdapter;

    //ListView, Button, TextView
    ListView ListDevices;
    ListView ListPairedDevices;
    Button sendBtn;
    Button searchBtn;
    Button connectBtn;
    TextView receiveMsgView;
    TextView BTDevice;
    TextView pairedDevice;
    EditText sendMsg;
    ProgressDialog myProgressDialog;
    StringBuilder receiveMsg;
    Intent connectIntent;

    //UUID
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static BluetoothDevice getBluetoothDevice(){
        return myBTDevice;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_bluetoothmain);


        connectBtn = (Button) findViewById(R.id.connectBtn);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        ListDevices = (ListView) findViewById(R.id.listDevice);
        ListPairedDevices = (ListView) findViewById(R.id.listPairedDevice);;
        receiveMsgView = (TextView) findViewById(R.id.receiveMsg);
        BTDevice = (TextView) findViewById(R.id.BTDevice);
        pairedDevice = (TextView) findViewById(R.id.pairedDevice);
        sendMsg = (EditText) findViewById(R.id.sendMsg);
        receiveMsg = new StringBuilder();
        myBTDevice = null;


        //REGISTER BROADCAST RECEIVER FOR INCOMING MSG
        LocalBroadcastManager.getInstance(this).registerReceiver(btConnectionReceiver, new IntentFilter("btConnectionStatus"));
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter("receiveMsg"));

        //REGISTER BROADCAST WHEN BOND STATE CHANGES (E.G PAIRING)
        IntentFilter bondFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(bondingBroadcastReceiver, bondFilter);

        //REGISTER DISCOVERABILITY BROADCAST RECEIVER
        IntentFilter intentFilter = new IntentFilter(myBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(discoverabilityBroadcastReceiver, intentFilter);

        //REGISTER ENABLE/DISABLE BT BROADCAST RECEIVER
        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(enableBTBroadcastReceiver, BTIntent);

        //REGISTER DISCOVERED DEVICE BROADCAST RECEIVER
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryBroadcastReceiver, discoverDevicesIntent);

        //REGISTER START DISCOVERING BROADCAST RECEIVER
        IntentFilter discoverStartedIntent = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(discoveryStartedBroadcastReceiver, discoverStartedIntent);

        //REGISTER END DISCOVERING BROADCAST RECEIVER
        IntentFilter discoverEndedIntent = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryEndedBroadcastReceiver, discoverEndedIntent);

        BTDeviceList = new ArrayList<>();
        BTPairedDeviceList = new ArrayList<>();
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //ONCLICK LISTENER FOR PAIRED DEVICE LIST
        ListPairedDevices.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //CANCEL DEVICE SEARCH DISCOVERY
                        myBluetoothAdapter.cancelDiscovery();
                        myBTDevice = BTPairedDeviceList.get(i);
                        ListDevices.setAdapter(DeviceListAdapter);
                        Log.d(TAG, "onItemClick: Paired Device = " + BTPairedDeviceList.get(i).getName());
                        Log.d(TAG, "onItemClick: DeviceAddress = " + BTPairedDeviceList.get(i).getAddress());
                    }
                }
        );

        //ONCLICK LISTENER FOR SEARCH DEVICE LIST
        ListDevices.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        //CANCEL DEVICE SEARCH DISCOVERY
                        myBluetoothAdapter.cancelDiscovery();
                        Log.d(TAG, "onItemClick: Item Selected");

                        String deviceName = BTDeviceList.get(i).getName();
                        String deviceAddress = BTDeviceList.get(i).getAddress();

                        ListPairedDevices.setAdapter(PairedDeviceListAdapter);
                        Log.d(TAG, "onItemClick: DeviceName = " + deviceName);
                        Log.d(TAG, "onItemClick: DeviceAddress = " + deviceAddress);

                        //CREATE BOND if > JELLY BEAN
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            Log.d(TAG, "Trying to pair with: " + deviceName);
                            //CREATE BOUND WITH SELECTED DEVICE
                            BTDeviceList.get(i).createBond();
                            //ASSIGN SELECTED DEVICE INFO TO myBTDevice
                            myBTDevice = BTDeviceList.get(i);
                        }
                    }
                }
        );

        //SEARCH BUTTON
        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d(TAG, "onClick: search button");
                enableBT();
                BTDeviceList.clear();

            }
        });

        //CONNECT BUTTON
        connectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (myBTDevice == null) {
                    Toast.makeText(BluetoothMain.this, "There is no Paired Device! Please Search/Select a Device.",
                            Toast.LENGTH_LONG).show();
                } else if(myBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED){
                    Toast.makeText(BluetoothMain.this, "Bluetooth has been connected",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    Log.d(TAG, "onClick: connect button");
                    startBTConnection(myBTDevice, myUUID);
                }
                ListPairedDevices.setAdapter(PairedDeviceListAdapter);
            }
        });

        //END BUTTON
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                byte[] bytes = sendMsg.getText().toString().getBytes(Charset.defaultCharset());
                BluetoothChat.writeMsg(bytes);
                sendMsg.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                Intent intent = new Intent(BluetoothMain.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
        }

        switch (item.getItemId()) {
            case R.id.reconfigure:
                Intent intent = new Intent(BluetoothMain.this, ReconfigBottomFragment.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(BluetoothMain.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    //BROADCAST RECEIVER FOR BLUETOOTH CONNECTION STATUS
    BroadcastReceiver btConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Receiving connection status");

            String connectionStatus = intent.getStringExtra("ConnectionStatus");
            myBTConnectionDevice = intent.getParcelableExtra("Device");

            //DISCONNECTED FROM BLUETOOTH CHAT
            if(connectionStatus.equals("disconnect")){

                Log.d("BluetoothMain:","Device Disconnected");

                if(connectIntent != null) {
                    //Stop Bluetooth Connection Service
                    stopService(connectIntent);
                }

                //RECONNECT DIALOG MSG
                AlertDialog alertDialog = new AlertDialog.Builder(BluetoothMain.this).create();
                alertDialog.setTitle("BLUETOOTH DISCONNECTED");
                alertDialog.setMessage("Connection with device: '"+myBTConnectionDevice.getName()+"' has ended. Do you wish to reconnect?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startBTConnection(myBTConnectionDevice, myUUID);
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

            //SUCCESSFULLY CONNECTED TO BLUETOOTH DEVICE
            else if(connectionStatus.equals("connect")){

                Log.d("BluetoothMain:","Device Connected");
                Toast.makeText(BluetoothMain.this, "Connection Established: "+ myBTConnectionDevice.getName(),
                        Toast.LENGTH_LONG).show();
            }

            //BLUETOOTH CONNECTION FAILED
            else if(connectionStatus.equals("connectionFail")) {
                Toast.makeText(BluetoothMain.this, "Connection Failed: "+ myBTConnectionDevice.getName(),
                        Toast.LENGTH_LONG).show();
            }

        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND to turn on the bluetooth
    private final BroadcastReceiver enableBTBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(myBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, myBluetoothAdapter.ERROR);

                switch (state) {
                    //BLUETOOTH TURNED OFF STATE
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "OnReceiver: STATE OFF");
                        break;
                    //BLUETOOTH TURNING OFF STATE
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "OnReceiver: STATE TURNING OFF");
                        break;
                    //BLUETOOTH TURNED ON STATE
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "OnReceiver: STATE ON");

                        //TURN DISCOVERABILITY ON
                        discoverabilityON();

                        break;
                    //BLUETOOTH TURNING ON STATE
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "OnReceiver: STATE TURNING ON");
                        break;
                }
            }
        }
    };


    // Create a BroadcastReceiver for ACTION_FOUND to turn on the discovery for bluetooth
    private final BroadcastReceiver discoverabilityBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //DEVICE IS IN DISCOVERABLE MODE
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "OnReceiver: DISCOVERABILITY ENABLED");

                        //DISCOVER OTHER DEVICES
                        startSearch();

                        //START BLUETOOTH CONNECTION SERVICE WHICH WILL START THE ACCEPT THREAD TO LISTEN FOR CONNECTION
                        connectIntent = new Intent(BluetoothMain.this, BluetoothConnectionService.class);
                        connectIntent.putExtra("serviceType", "listen");

                        startService(connectIntent);

                        //CHECK PAIRED DEVICE LIST
                        checkPairedDevice();
                        break;
                    //WHEN DEVICE IS NOT IN DISCOVERABLE MODE
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "OnReceiver: DISCOVERABILITY DISABLED, ABLE TO RECEIVE CONNECTION");
                        break;
                    //BLUETOOTH TURNING ON STATE
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "OnReceiver: DISCOVERABILITY DISABLED, NOT ABLE TO RECEIVE CONNECTION");
                        break;
                    //BLUETOOTH TURNED ON STATE
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "OnReceiver: CONNECTING");
                        break;
                    //BLUETOOTH TURNED ON STATE
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "OnReceiver: CONNECTED");
                        break;
                }
            }
        }
    };


    // Create a BroadcastReceiver for ACTION_FOUND Getting Discovered Devices Info
    private final BroadcastReceiver discoveryBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "SEARCH ME!");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTDeviceList.add(device);
                Log.d(TAG, "OnReceive: " + device.getName() + ": " + device.getAddress());
                DeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, BTDeviceList);
                ListDevices.setAdapter(DeviceListAdapter);

            }
        }
    };

    // Create a BroadcastReceiver for ACTION_FOUND for paring device
    private final BroadcastReceiver bondingBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {

                //FOR BONDING DEVICE
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //IF ITS ALREADY BOUNDED
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {

                    Log.d(TAG, "BoundReceiver: Bond Bonded with: " + device.getName());

                    myProgressDialog.dismiss();

                    Toast.makeText(BluetoothMain.this, "Bound Successfully With: " + device.getName(), Toast.LENGTH_LONG).show();
                    myBTDevice = device;
                    checkPairedDevice();
                    ListDevices.setAdapter(DeviceListAdapter);

                }
                //BONDING WITH ANOTHER DEVICES
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BoundReceiver: Bonding With Another Device");

                    myProgressDialog = ProgressDialog.show(BluetoothMain.this, "Bonding With Device", "Please Wait...", true);
                }
                //TO BREAK A BOND
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BoundReceiver: Breaking Bond");
                    myProgressDialog.dismiss();
                    //DIALOG MSG POPUP
                    AlertDialog alertDialog = new AlertDialog.Builder(BluetoothMain.this).create();
                    alertDialog.setTitle("Bonding Status");
                    alertDialog.setMessage("Bond Disconnected!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                    myBTDevice = null;
                }

            }
        }
    };

    // Create a BroadcastReceiver for ACTION_DISCOVERY_STARTED
    private final BroadcastReceiver discoveryStartedBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Log.d(TAG, "STARTED DISCOVERY!!!");
                BTDevice.setText("Searching for device ");
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_DISCOVERY_FINISHED, ending the process
    private final BroadcastReceiver discoveryEndedBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Log.d(TAG, "DISCOVERY ENDED!!!");

                BTDevice.setText("Device found");

            }
        }
    };


    //BROADCAST RECEIVER FOR INCOMING MESSAGE
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "Receiving Msg!!!");
            String msg = intent.getStringExtra("receivingMsg");
            receiveMsg.append(msg + "\n");
            receiveMsgView.setText(receiveMsg);

        }
    };

    //TURNING ON/OFF BLUETOOTH
    public void enableBT() {
        //DEVICE DOES NOT HAVE BLUETOOTH
        if (myBluetoothAdapter == null) {
            Toast.makeText(BluetoothMain.this, "Device Does Not Support Bluetooth.",
                    Toast.LENGTH_LONG).show();
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        //IF BLUETOOTH NOT ENABLED
        if (!myBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);
        }
        //IF BLUETOOTH ENABLED
        if (myBluetoothAdapter.isEnabled()) {
            discoverabilityON();
        }
    }

    // TURN DISCOVERABILITY ON
    private void discoverabilityON() {

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 900);
        startActivity(discoverableIntent);

    }

    // Check BT permission in manifest (For Start Discovery)
    private void checkBTPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION);

            permissionCheck += ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != 0) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");

        }
    }

    //Discovering other device
    private void startSearch() {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if (myBluetoothAdapter.isDiscovering()) {
            myBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "BTDiscovery: canceling discovery");

            //check BT permission in manifest
            checkBTPermission();

            myBluetoothAdapter.startDiscovery();
            Log.d(TAG, "BTDiscovery: enable discovery");
        }
        if (!myBluetoothAdapter.isDiscovering()) {

            //check BT permission in manifest
            checkBTPermission();
            myBluetoothAdapter.startDiscovery();
            Log.d(TAG, "BTDiscovery: enable discovery");
        }
    }


    //Call Bluetooth chat service
    public void startBTConnection(BluetoothDevice device, UUID uuid) {

        Log.d(TAG, "StartBTConnection: Initializing RFCOM Bluetooth Connection");
        connectIntent = new Intent(BluetoothMain.this, BluetoothConnectionService.class);
        connectIntent.putExtra("serviceType", "connect");
        connectIntent.putExtra("device", device);
        connectIntent.putExtra("id", uuid);

        Log.d(TAG, "StartBTConnection: Starting Bluetooth Connection Service!");
        startService(connectIntent);
    }

    public void checkPairedDevice() {
        //CHECK IF THERE IS PAIRED DEVICES
        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        BTPairedDeviceList.clear();
        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices) {
                Log.d(TAG, "PAIRED DEVICES: " + device.getName() + "," + device.getAddress());
                BTPairedDeviceList.add(device);
            }
            pairedDevice.setText("Paired Devices: ");
            PairedDeviceListAdapter = new DeviceListAdapter(this, R.layout.device_adapter_view, BTPairedDeviceList);
            ListPairedDevices.setAdapter(PairedDeviceListAdapter);

        } else {
            pairedDevice.setText("No Paired Devices: ");

            Log.d(TAG, "NO PAIRED DEVICE!!");
        }
    }

    // CHECKING OF INCOMING MESSAGE TYPE
    public String checkIncomingMsgType(String msg) {
        String msgType = null;
        String[] splitedMsg = msg.split(":");
        switch (splitedMsg[0]) {

            //RobotStatus
            case "status":
                // Statements
                msgType = "robotstatus";
                break; // optional

            //Auto / Manual Refresh Of Map
            case "maprefresh":
                // Statements
                msgType = "maprefresh";
                break; // optional

            default: // Optional
                Log.d(TAG, "Checking Msg Type: Error - " + splitedMsg[0] + ":" + splitedMsg[1]);
                break;
            // Statements
        }
        return msgType;

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Bluetooth activity: Destroyed");
        super.onDestroy();
        unregisterReceiver(discoverabilityBroadcastReceiver);
        unregisterReceiver(discoveryBroadcastReceiver);
        unregisterReceiver(bondingBroadcastReceiver);
        unregisterReceiver(discoveryStartedBroadcastReceiver);
        unregisterReceiver(discoveryEndedBroadcastReceiver);
        unregisterReceiver(enableBTBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(btConnectionReceiver);
    }
}