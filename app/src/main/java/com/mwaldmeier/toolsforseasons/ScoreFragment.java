package com.mwaldmeier.toolsforseasons;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 12/13/2015.
 */
public class ScoreFragment extends android.app.Fragment {

    List<TextView> playerScoreLbls = new ArrayList<>();
    Seasons ThisGame;
    SoundPool sp;
    int soundID;
    List<Button> bonusBtns = new ArrayList<>();
    ImageView dayMarkerImgView;
    ImageView yearMarkerImgView;
    int currentYear;

    public ScoreFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_score, container, false);
        ThisGame = ((Seasons) getActivity().getApplication());

        //set up drop sound
        sp = ((MainActivity) getActivity()).getSoundPool();
        soundID = sp.load(getActivity().getApplicationContext(), R.raw.blop, 1);

        ((ImageButton) rootView.findViewById(R.id.backBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.goToPage(2);
            }
        });

        //Hide player 3 & 4
        ((RelativeLayout) rootView.findViewById(R.id.playerScoreSheetFragmentLayout3)).setVisibility(View.GONE);
        ((RelativeLayout) rootView.findViewById(R.id.playerScoreSheetFragmentLayout4)).setVisibility(View.GONE);

        setUpPlayerScoreLbls(rootView);
        setUpScoreBtns(rootView);

        dayMarkerImgView = (ImageView) rootView.findViewById(R.id.dayMarker);
        yearMarkerImgView = (ImageView) rootView.findViewById(R.id.yearMarker);
        rootView.findViewById(R.id.minusOneDayBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDayYear(false);

            }
        });

        rootView.findViewById(R.id.plusOneDayBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDayYear(true);
            }
        });

        setUpBonusBtns(rootView);

        return rootView;
    }

    private void setUpBonusBtns(View rootView) {
        bonusBtns.add((Button) rootView.findViewById(R.id.playerBonusBtn1));
        bonusBtns.add((Button) rootView.findViewById(R.id.playerBonusBtn2));
        bonusBtns.add((Button) rootView.findViewById(R.id.playerBonusBtn3));
        bonusBtns.add((Button) rootView.findViewById(R.id.playerBonusBtn4));
        int i = 1;
        for (Button button :
                bonusBtns) {
            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bonusBtnClick(finalI);
                }
            });
            i += 1;
        }
    }

    private void bonusBtnClick(final Integer playerNum) {
        AlertDialog.Builder alert = new AlertDialog.Builder(((MainActivity) getActivity()));
        alert
                .setTitle("Take Bonus Action For Player " + playerNum.toString() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (ThisGame.addBonusUsedForPlayer(playerNum)) {
                            updatePlayerBonus(playerNum);
                        } else {
                            Toast.makeText(getActivity(), "You cannot use Bonus Actions more than 4 times.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });


        alert.show();
    }

    private void updatePlayerBonus(Integer playerNum) {
        bonusBtns.get((playerNum-1)).setText(ThisGame.getBonusPenaltyFroPlayer(playerNum).toString());
    }

    private void setUpPlayerScoreLbls(View rootView) {
        playerScoreLbls.add((TextView) rootView.findViewById(R.id.playerScore1));
        playerScoreLbls.add((TextView) rootView.findViewById(R.id.playerScore2));
        if (ThisGame.getNumPlayers() >= 3) {
            playerScoreLbls.add((TextView) rootView.findViewById(R.id.playerScore3));
            ((RelativeLayout) rootView.findViewById(R.id.playerScoreSheetFragmentLayout3)).setVisibility(View.VISIBLE);
            if (ThisGame.getNumPlayers() == 4) {
                playerScoreLbls.add((TextView) rootView.findViewById(R.id.playerScore4));
                ((RelativeLayout) rootView.findViewById(R.id.playerScoreSheetFragmentLayout4)).setVisibility(View.VISIBLE);
            }
        }

        refreshScores();

    }

    private void refreshScores() {
        Integer i = 1;

        for (TextView playerScore :
                playerScoreLbls) {
            playerScore.setText(ThisGame.getPlayerScoreFor(i).toString());
            i += 1;
        }
    }

    private void setUpScoreBtns(View rootView) {
        List<Button> plusBtns = new ArrayList<>();
        List<Button> minusBtns = new ArrayList<>();

        plusBtns.add((Button) rootView.findViewById(R.id.addOneScoreBtn1));
        plusBtns.add((Button) rootView.findViewById(R.id.addOneScoreBtn2));
        minusBtns.add((Button) rootView.findViewById(R.id.minusOneScoreBtn1));
        minusBtns.add((Button) rootView.findViewById(R.id.minusOneScoreBtn2));
        if (ThisGame.getNumPlayers() >= 3) {
            plusBtns.add((Button) rootView.findViewById(R.id.addOneScoreBtn3));
            minusBtns.add((Button) rootView.findViewById(R.id.minusOneScoreBtn3));
            if (ThisGame.getNumPlayers() == 4) {
                plusBtns.add((Button) rootView.findViewById(R.id.addOneScoreBtn4));
                minusBtns.add((Button) rootView.findViewById(R.id.minusOneScoreBtn4));
            }
        }

        Integer i = 1;
        for (Button plusBtn :
                plusBtns) {
            final Integer finalI = i;
            plusBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ThisGame.addOneToScoreFor(finalI);
                    playDropSound();
                    refreshScores();

                }
            });
            i += 1;
        }

        i = 1;
        for (Button minusBtn :
                minusBtns) {
            final Integer finalI = i;
            minusBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (ThisGame.removeOneFromScoreFor(finalI)) {
                        playDropSound();
                        refreshScores();

                    }
                }
            });
            i += 1;
        }

    }

    private void playDropSound() {
        if (((MainActivity) getActivity()).getSoundOn().equals("1")) {
            sp.play(soundID, 1, 1, 0, 0, 1);
        }
    }

    private void updateDayYear(boolean forward) {
        ThisGame.changeDay(forward);
        moveDayMarker(forward);
    }

    private void moveDayMarker(boolean forward) {
        int moveDegree = 30;
        if (!forward) {
            moveDegree = moveDegree * -1;
        }
        dayMarkerImgView.setRotation(dayMarkerImgView.getRotation() + moveDegree);
        playDropSound();
        checkYear();
    }

    private void checkYear() {
        if (currentYear != ThisGame.getYear()) {
            changeYear(ThisGame.getYear());
        }
    }

    private void changeYear(Integer year) {
        currentYear = year;
        if (currentYear == 1) {
            yearMarkerImgView.setImageResource(R.drawable.board_year_marker_1);
        } else if (currentYear == 2) {
            yearMarkerImgView.setImageResource(R.drawable.board_year_marker_2);
        } else {
            yearMarkerImgView.setImageResource(R.drawable.board_year_marker_3);
        }
    }
}