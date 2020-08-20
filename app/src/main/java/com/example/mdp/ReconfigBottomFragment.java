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

public class ReconfigBottomFragment extends Fragment {
    private IMainActivity mIMainActivity;

    private Button reconfigBtn, f1Btn, f2Btn, backBtn;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reconfig_bottom, container, false);

        reconfigBtn = view.findViewById(R.id.button_reconfig_select);
        f1Btn = view.findViewById(R.id.button_f1);
        f2Btn = view.findViewById(R.id.button_f2);
        backBtn = view.findViewById(R.id.button_back_main);

        reconfigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMainActivity.inflateFragment("reconfig_select", "");
            }
        });

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

