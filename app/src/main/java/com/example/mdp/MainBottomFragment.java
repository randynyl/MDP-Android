package com.example.mdp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainBottomFragment extends Fragment {
    private FragmentMainBottomListener listener;
    private IMainActivity mIMainActivity;
    private Button BluetoothBtn, ReconfigBtn, WaypointBtn;
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
        BluetoothBtn = view.findViewById(R.id.button_bluetooth);
        ReconfigBtn = view.findViewById(R.id.button_reconfig);
        WaypointBtn = view.findViewById(R.id.button_waypoint);
        upBtn = view.findViewById(R.id.up_arrow);
        downBtn = view.findViewById(R.id.down_arrow);
        leftBtn = view.findViewById(R.id.left_arrow);
        rightBtn = view.findViewById(R.id.right_arrow);

        BluetoothBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMainActivity.inflateFragment("bluetooth", "");
            }
        });

        ReconfigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMainActivity.inflateFragment("reconfig", "");
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
