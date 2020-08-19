package com.example.mdp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainBottomFragment extends Fragment {

    private IMainActivity mIMainActivity;

    private Button BluetoothBtn, ReconfigBtn, WaypointBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_bottom, container, false);
        BluetoothBtn = view.findViewById(R.id.button_bluetooth);
        ReconfigBtn = view.findViewById(R.id.button_reconfig);
        WaypointBtn = view.findViewById(R.id.button_waypoint);

        BluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMainActivity.inflateFragment("bluetooth", "");
            }
        });
        return view;
    }







    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mIMainActivity = (IMainActivity) getActivity();
    }
}
