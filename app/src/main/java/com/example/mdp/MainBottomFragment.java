package com.example.mdp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class MainBottomFragment extends Fragment {

    private IMainActivity mIMainActivity;
    private SwitchCompat updateSwitchCompat;
    private Button exploreBtn, shortestPathBtn, waypointBtn, updateBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_bottom, container, false);
        exploreBtn = view.findViewById(R.id.button_explore);
        shortestPathBtn = view.findViewById(R.id.button_shortest_path);
        waypointBtn = view.findViewById(R.id.button_waypoint);
        updateBtn = view.findViewById(R.id.button_update);
        updateSwitchCompat = view.findViewById(R.id.switchButtonUpdate);

        //Save switch state
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("save", Context.MODE_PRIVATE);
        updateSwitchCompat.setChecked(sharedPreferences.getBoolean("value", true));
        updateBtn.setVisibility(sharedPreferences.getInt("visibility", View.VISIBLE));

        updateSwitchCompat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (updateSwitchCompat.isChecked()){
                    // Auto update
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("save", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.putInt("visibility", View.GONE);
                    editor.apply();
                    updateSwitchCompat.setChecked(true);
                    updateBtn.setVisibility(View.GONE);
                } else {
                    // Manual update
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("save", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.putInt("visibility", View.VISIBLE);
                    editor.apply();
                    updateSwitchCompat.setChecked(false);
                    updateBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        waypointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMainActivity.inflateFragment("waypoint", "");
            }
        });


        exploreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //run explore
            }
        });

        shortestPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //run shortest path
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
