package com.example.mdp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class MainBottomFragment extends Fragment {
    private FragmentMainBottomListener listener;
    private IMainActivity mIMainActivity;
    private SwitchCompat updateSwitchCompat;
    private Button exploreBtn, shortestPathBtn, waypointBtn, updateBtn;
    private ImageButton upBtn, downBtn, leftBtn, rightBtn;
    private static final String UP = "up", DOWN = "down", LEFT = "left", RIGHT = "right";

    public interface FragmentMainBottomListener{
        void onInputMainBottomSent(String direction);
    }

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

        upBtn = view.findViewById(R.id.up_arrow);
        downBtn = view.findViewById(R.id.down_arrow);
        leftBtn = view.findViewById(R.id.left_arrow);
        rightBtn = view.findViewById(R.id.right_arrow);

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

        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String direction = UP;
                listener.onInputMainBottomSent(direction);
            }
        });

        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String direction = DOWN;
                listener.onInputMainBottomSent(direction);
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String direction = LEFT;
                listener.onInputMainBottomSent(direction);
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String direction = RIGHT;
                listener.onInputMainBottomSent(direction);
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mIMainActivity = (IMainActivity) getActivity();
        if (context instanceof FragmentMainBottomListener) {
            listener = (FragmentMainBottomListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + "FragmentMBFListener has not been implemented!");
            }
        }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
