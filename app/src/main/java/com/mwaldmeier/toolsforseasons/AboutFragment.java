package com.mwaldmeier.toolsforseasons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by michael on 12/13/2015.
 */
public class AboutFragment extends android.app.Fragment {

    MainActivity mainActivity;

    public AboutFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        mainActivity = (MainActivity) getActivity();

        ((Button) rootView.findViewById(R.id.rateBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.goToRatePage();
            }
        });

        return rootView;
    }
}