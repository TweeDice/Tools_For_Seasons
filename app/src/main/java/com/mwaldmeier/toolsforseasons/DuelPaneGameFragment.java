package com.mwaldmeier.toolsforseasons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by michael on 12/13/2015.
 */
public class DuelPaneGameFragment extends android.app.Fragment {

    MainActivity mainActivity;

    public DuelPaneGameFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_duel_pane_game, container, false);
        mainActivity = (MainActivity) getActivity();

        return rootView;
    }
}