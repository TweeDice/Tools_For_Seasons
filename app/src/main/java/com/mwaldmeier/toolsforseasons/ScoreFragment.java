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
    List<TextView> playerSummonLbls = new ArrayList<>();
    Seasons ThisGame;
    SoundPool sp;
    int soundID;
    List<Button> bonusBtns = new ArrayList<>();
    ImageView dayMarkerImgView;
    ImageView yearMarkerImgView;
    int currentYear;
    MainActivity mainActivity;

    public ScoreFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_score, container, false);
        ThisGame = (Seasons) getActivity().getApplication();
        mainActivity = (MainActivity) getActivity();

        //set up drop sound
        sp = mainActivity.getSoundPool();
        soundID = sp.load(getActivity().getApplicationContext(), R.raw.blop, 1);

        ((ImageButton) rootView.findViewById(R.id.backBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.goToPage(2);
            }
        });

//TODO clean
        //Hide player 3 & 4
        ((RelativeLayout) rootView.findViewById(R.id.playerScoreSheetFragmentLayout3)).setVisibility(View.GONE);
        ((RelativeLayout) rootView.findViewById(R.id.playerScoreSheetFragmentLayout4)).setVisibility(View.GONE);
        if (ThisGame.getNumPlayers() >= 3) {
            ((RelativeLayout) rootView.findViewById(R.id.playerScoreSheetFragmentLayout3)).setVisibility(View.VISIBLE);

            if (ThisGame.getNumPlayers() == 4) {
                ((RelativeLayout) rootView.findViewById(R.id.playerScoreSheetFragmentLayout4)).setVisibility(View.VISIBLE);
            }
        }

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

        moveDayMarker();
        return rootView;
    }

    private void setUpBonusBtns(View rootView) {
        bonusBtns.add((Button) rootView.findViewById(R.id.playerBonusBtn1));
        bonusBtns.add((Button) rootView.findViewById(R.id.playerBonusBtn2));
        if (ThisGame.getNumPlayers() >= 3) {
            bonusBtns.add((Button) rootView.findViewById(R.id.playerBonusBtn3));
            if (ThisGame.getNumPlayers() == 4) {
                bonusBtns.add((Button) rootView.findViewById(R.id.playerBonusBtn4));
            }
        }


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
            button.setText(ThisGame.getBonusPenaltyFroPlayer(finalI).toString());
            i += 1;
        }


    }

    private void bonusBtnClick(final Integer playerNum) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mainActivity);
        alert
                .setTitle("Take Bonus Action For Player " + playerNum.toString() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (ThisGame.changeBonusUsedForPlayer(playerNum, true)) {
                            updatePlayerBonus(playerNum);
                        } else {
                            Toast.makeText(getActivity(), "You cannot use Bonus Actions more than 3 times.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNeutralButton("Remove One", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (ThisGame.changeBonusUsedForPlayer(playerNum, false)) {
                            updatePlayerBonus(playerNum);
                        } else {
                            Toast.makeText(getActivity(), "You have no bonuses to remove.",
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
        bonusBtns.get((playerNum - 1)).setText(ThisGame.getBonusPenaltyFroPlayer(playerNum).toString());
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
        List<Button> plusSummonBtns = new ArrayList<>();
        List<Button> minusSummonBtns = new ArrayList<>();

        plusBtns.add((Button) rootView.findViewById(R.id.addOneScoreBtn1));
        plusBtns.add((Button) rootView.findViewById(R.id.addOneScoreBtn2));
        minusBtns.add((Button) rootView.findViewById(R.id.minusOneScoreBtn1));
        minusBtns.add((Button) rootView.findViewById(R.id.minusOneScoreBtn2));
        plusSummonBtns.add((Button) rootView.findViewById(R.id.addOneSummonBtn1));
        plusSummonBtns.add((Button) rootView.findViewById(R.id.addOneSummonBtn2));
        minusSummonBtns.add((Button) rootView.findViewById(R.id.minusOneSummonBtn1));
        minusSummonBtns.add((Button) rootView.findViewById(R.id.minusOneSummonBtn2));
        if (ThisGame.getNumPlayers() >= 3) {
            plusBtns.add((Button) rootView.findViewById(R.id.addOneScoreBtn3));
            minusBtns.add((Button) rootView.findViewById(R.id.minusOneScoreBtn3));
            plusSummonBtns.add((Button) rootView.findViewById(R.id.addOneSummonBtn3));
            minusSummonBtns.add((Button) rootView.findViewById(R.id.minusOneSummonBtn3));
            if (ThisGame.getNumPlayers() == 4) {
                plusBtns.add((Button) rootView.findViewById(R.id.addOneScoreBtn4));
                minusBtns.add((Button) rootView.findViewById(R.id.minusOneScoreBtn4));
                plusSummonBtns.add((Button) rootView.findViewById(R.id.addOneSummonBtn4));
                minusSummonBtns.add((Button) rootView.findViewById(R.id.minusOneSummonBtn4));
            }
        }

        for (int i=0;i<plusBtns.size();i++) {
            final int playerNum = i +1 ;
            plusBtns.get(i).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ThisGame.addOneToScoreFor(playerNum);
                    refreshScores();
                }
            });
            minusBtns.get(i).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ThisGame.removeOneFromScoreFor(playerNum);
                    refreshScores();

                }
            });
            plusSummonBtns.get(i).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ThisGame.changeSummonForPlayer(playerNum, true);
                    refreshSummons();

                }
            });
            minusSummonBtns.get(i).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ThisGame.changeSummonForPlayer(playerNum, false);
                    refreshSummons();
                }
            });
        }
    }

    private void playDropSound() {
        if (mainActivity.getSoundOn().equals("1")) {
            sp.play(soundID, 1, 1, 0, 0, 1);
        }
    }

    private void updateDayYear(boolean forward) {
        ThisGame.changeDay(forward);
        moveDayMarker();
    }

    private void moveDayMarker() {
        float dayDegrees = (30*(ThisGame.getDay()-1));
        dayMarkerImgView.setRotation(dayDegrees);
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
    private void refreshSummons() {
        Integer i = 1;
        for (TextView playerScore :
                playerSummonLbls) {
            playerScore.setText(ThisGame.getSummonForPlayer(i).toString());
            i += 1;
        }
    }
    private void setUpPlayerScoreLbls(View rootView) {
        playerScoreLbls.add((TextView) rootView.findViewById(R.id.playerScore1));
        playerScoreLbls.add((TextView) rootView.findViewById(R.id.playerScore2));
        playerSummonLbls.add((TextView) rootView.findViewById(R.id.playerSummon1));
        playerSummonLbls.add((TextView) rootView.findViewById(R.id.playerSummon2));
        if (ThisGame.getNumPlayers() >= 3) {
            playerScoreLbls.add((TextView) rootView.findViewById(R.id.playerScore3));
            playerSummonLbls.add((TextView) rootView.findViewById(R.id.playerSummon3));
            if (ThisGame.getNumPlayers() == 4) {
                playerScoreLbls.add((TextView) rootView.findViewById(R.id.playerScore4));
                playerSummonLbls.add((TextView) rootView.findViewById(R.id.playerSummon4));
            }
        }
        refreshScores();
        refreshSummons();
    }
}