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

public class WaypointBottomFragment extends Fragment {
    private IMainActivity mIMainActivity;
    private Button confirmBtn, clearBtn, backBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waypoint_bottom, container, false);
        confirmBtn = view.findViewById(R.id.button_waypoint_confirm);
        clearBtn = view.findViewById(R.id.button_waypoint_clear);
        backBtn = view.findViewById((R.id.button_waypoint_back));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMainActivity.inflateFragment("backToMain", "");
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

