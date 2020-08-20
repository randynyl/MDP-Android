package com.example.mdp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements IMainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            Fragment topFragment = new MainTopFragment();
            doTopFragmentTransaction(topFragment, "top_main", false, "");
            Fragment bottomFragment = new MainBottomFragment();
            doBottomFragmentTransaction(bottomFragment, "bottom_main", false, "");
        }
    }


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
        }

        else if (fragmentTag.equals("backToMain")) {
            MainTopFragment topFragment = new MainTopFragment();
            doTopFragmentTransaction(topFragment, fragmentTag, false, message);
            MainBottomFragment bottomFragment = new MainBottomFragment();
            doBottomFragmentTransaction(bottomFragment, fragmentTag, false, message);
        }

        else if (fragmentTag.equals("reconfig")) {
            ReconfigTopFragment topFragment = new ReconfigTopFragment();
            doTopFragmentTransaction(topFragment, fragmentTag, true, message);
            ReconfigBottomFragment bottomFragment = new ReconfigBottomFragment();
            doBottomFragmentTransaction(bottomFragment, fragmentTag, true, message);
        }

        else if (fragmentTag.equals("reconfig_select")) {
            ReconfigSelectTopFragment topFragment = new ReconfigSelectTopFragment();
            doTopFragmentTransaction(topFragment, fragmentTag, true, message);
            ReconfigSelectBottomFragment bottomFragment = new ReconfigSelectBottomFragment();
            doBottomFragmentTransaction(bottomFragment, fragmentTag, true, message);
        }

        else if(fragmentTag.equals("back_config")) {
            ReconfigTopFragment topFragment = new ReconfigTopFragment();
            doTopFragmentTransaction(topFragment, fragmentTag, false, message);
            ReconfigBottomFragment bottomFragment = new ReconfigBottomFragment();
            doBottomFragmentTransaction(bottomFragment, fragmentTag, false, message);
        }

    }
}
