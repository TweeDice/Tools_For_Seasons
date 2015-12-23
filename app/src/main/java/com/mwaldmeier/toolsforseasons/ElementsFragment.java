package com.mwaldmeier.toolsforseasons;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ElementsFragment extends android.app.Fragment {
    Seasons ThisGame;
    List<View> PlayerViews = new ArrayList<>();
    List<List> PlayerImgViewLists = new ArrayList<>();
    List<TextView> PlayerTotalElementsViews = new ArrayList<>();
    SoundPool sp;
    int soundID;
    List<TextView> playerScoreLbls = new ArrayList<>();
    MainActivity mainActivity;
    Context appContext;

    public ElementsFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_elements, container, false);

        ThisGame = ((Seasons) getActivity().getApplication());
        mainActivity = (MainActivity) getActivity();
        appContext = getActivity().getApplicationContext();

        //set up drop sound
        sp = (mainActivity).getSoundPool();
        soundID = sp.load(appContext, R.raw.blop, 1);

        ((ImageButton) rootView.findViewById(R.id.backBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.goToPage(1);
            }
        });

        rootView.findViewById(R.id.playerGoodsBox3).setVisibility(View.GONE);
        rootView.findViewById(R.id.playerGoodsBox4).setVisibility(View.GONE);

        //set up middle pool
        rootView.findViewById(R.id.fireImg).setOnTouchListener(new MyTouchListener());
        rootView.findViewById(R.id.waterImg).setOnTouchListener(new MyTouchListener());
        rootView.findViewById(R.id.windImg).setOnTouchListener(new MyTouchListener());
        rootView.findViewById(R.id.earthImg).setOnTouchListener(new MyTouchListener());
        rootView.findViewById(R.id.elementsPoolBox).setOnDragListener(new MyDragListenerForPlayerGoods());

        //set up player elements
        PlayerViews.add(rootView.findViewById(R.id.playerGoodsBox1));
        PlayerViews.add(rootView.findViewById(R.id.playerGoodsBox2));

        PlayerViews.get(0).setOnDragListener(new MyDragListenerForGoodsPool());
        PlayerViews.get(1).setOnDragListener(new MyDragListenerForGoodsPool());

        if (ThisGame.getNumPlayers() > 2) {
            PlayerViews.add(rootView.findViewById(R.id.playerGoodsBox3));
            PlayerViews.get(2).setVisibility(View.VISIBLE);
            PlayerViews.get(2).setOnDragListener(new MyDragListenerForGoodsPool());

            if (ThisGame.getNumPlayers() == 4) {
                PlayerViews.add(rootView.findViewById(R.id.playerGoodsBox4));
                PlayerViews.get(3).setVisibility(View.VISIBLE);
                PlayerViews.get(3).setOnDragListener(new MyDragListenerForGoodsPool());
            }
        }

        setListForImgs(rootView);
        setGoodCountsForAllPlayers();

        setListForTotals(rootView);
        setAllElementTotals();

        setUpPlayerScoreLbls(rootView);
        setUpScoreBtns(rootView);


        return rootView;
    }

    private void setListForTotals(View rootView) {
        PlayerTotalElementsViews.add((TextView) rootView.findViewById(R.id.totalElementCount1));
        PlayerTotalElementsViews.add((TextView) rootView.findViewById(R.id.totalElementCount2));
        if (ThisGame.getNumPlayers() > 2) {
            PlayerTotalElementsViews.add((TextView) rootView.findViewById(R.id.totalElementCount3));
            if (ThisGame.getNumPlayers() == 4) {
                PlayerTotalElementsViews.add((TextView) rootView.findViewById(R.id.totalElementCount4));
            }
        }
    }

    private void setAllElementTotals() {
        int i = 1;
        for (TextView view :
                PlayerTotalElementsViews) {
            setSumElementCountLblForPlayerView(i);
            i +=1;
        }
    }

    private void setSumElementCountLblForPlayerView(int playterNum) {
        Integer sum = ThisGame.getSumElementsForPlayer(playterNum);
        PlayerTotalElementsViews.get((playterNum - 1)).setText(sum.toString());
        if (sum >= 7) {
            PlayerTotalElementsViews.get((playterNum - 1)).setTextColor(Color.RED);
        } else {
            PlayerTotalElementsViews.get((playterNum - 1)).setTextColor(Color.GRAY);
        }
    }

    private void setListForImgs(View rootView) {
        List<View> player1Imgs = new ArrayList<>();
        player1Imgs.add(rootView.findViewById(R.id.fireImg1));
        player1Imgs.add(rootView.findViewById(R.id.waterImg1));
        player1Imgs.add(rootView.findViewById(R.id.windImg1));
        player1Imgs.add(rootView.findViewById(R.id.earthImg1));

        List<View> player2Imgs = new ArrayList<>();
        player2Imgs.add(rootView.findViewById(R.id.fireImg2));
        player2Imgs.add(rootView.findViewById(R.id.waterImg2));
        player2Imgs.add(rootView.findViewById(R.id.windImg2));
        player2Imgs.add(rootView.findViewById(R.id.earthImg2));

        PlayerImgViewLists.add(player1Imgs);
        PlayerImgViewLists.add(player2Imgs);

        if (ThisGame.getNumPlayers() > 2) {
            List<View> player3Imgs = new ArrayList<>();
            player3Imgs.add(rootView.findViewById(R.id.fireImg3));
            player3Imgs.add(rootView.findViewById(R.id.waterImg3));
            player3Imgs.add(rootView.findViewById(R.id.windImg3));
            player3Imgs.add(rootView.findViewById(R.id.earthImg3));
            PlayerImgViewLists.add(player3Imgs);

            if (ThisGame.getNumPlayers() == 4) {
                List<View> player4Imgs = new ArrayList<>();
                player4Imgs.add(rootView.findViewById(R.id.fireImg4));
                player4Imgs.add(rootView.findViewById(R.id.waterImg4));
                player4Imgs.add(rootView.findViewById(R.id.windImg4));
                player4Imgs.add(rootView.findViewById(R.id.earthImg4));
                PlayerImgViewLists.add(player4Imgs);
            }
        }

        setListenersForImgs();
    }

    private void setListenersForImgs() {
        for (List imgList :
                PlayerImgViewLists) {

            for (Object imgView :
                    imgList) {
                ((View) imgView).setOnTouchListener(new MyTouchListener());
            }

        }
    }

    private void setGoodCountsForAllPlayers() {
        for (int i = 1; i <= PlayerViews.size(); i++) {
            setGoodCountLblForPlayerView(i);
        }
    }

    private void setGoodCountLblForPlayerView(Integer playerNum) {
        for (Object imgViewObj :
                PlayerImgViewLists.get((playerNum - 1))) {
            ImageView imgView = ((ImageView) imgViewObj);
            ElementType thisGood = null;
            String countStr = null;

            //determine what good this is
            for (ElementType goodType :
                    ElementType.values()) {
                if (goodType.getImgValue().equals(imgView.getTag().toString())) {
                    thisGood = goodType;
                    break;
                }
            }

            if (thisGood != null) {
                countStr = (ThisGame.getElementCountForPlayer(playerNum, thisGood)).toString();

                //get the text viw for this good
                ViewGroup container = (ViewGroup) imgView.getParent();
                TextView countLblView = (TextView) container.getChildAt((container.indexOfChild(imgView) + 1));

                countLblView.setText(countStr);
            }
        }
    }

    private ElementType getGoodTypeFromImg(View view) {
        ElementType goodType = null;
        String imgStr = (String) view.getTag();
        switch (imgStr) {
            case ("fireImg"):
                goodType = ElementType.FIRE;
                break;
            case ("windImg"):
                goodType = ElementType.WIND;
                break;
            case ("waterImg"):
                goodType = ElementType.WATER;
                break;
            case ("earthImg"):
                goodType = ElementType.EARTH;
                break;
        }
        return goodType;
    }

    private Integer getPlayerNumFromView(View view) {
        Integer playerNum = null;
        switch (view.getId()) {
            case (R.id.playerGoodsBox1):
                playerNum = 1;
                break;
            case (R.id.playerGoodsBox2):
                playerNum = 2;
                break;
            case (R.id.playerGoodsBox3):
                playerNum = 3;
                break;
            case (R.id.playerGoodsBox4):
                playerNum = 4;
                break;
        }

        return playerNum;
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListenerForGoodsPool implements View.OnDragListener {
        Drawable enterShape = getResources().getDrawable(R.drawable.player_elements_holder_shape_droptarget);
        Drawable normalShape = getResources().getDrawable(R.drawable.player_elements_holder_shape);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundDrawable(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundDrawable(normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    Integer playerNum = null;
                    Integer oldPlayerNum = null;
                    ElementType goodType = null;
                    View imgView = (View) event.getLocalState();
                    RelativeLayout containerView = (RelativeLayout) v;
                    View originalDragView = (View) imgView.getParent().getParent().getParent();

                    oldPlayerNum = getPlayerNumFromView(originalDragView);
                    playerNum = getPlayerNumFromView(containerView);

                    if (! playerNum.equals(oldPlayerNum)) {
                        //test if this came from another player
                        if (oldPlayerNum != null && mainActivity.getP2POn().equals("1") || oldPlayerNum == null) {

                            goodType = getGoodTypeFromImg(imgView);

                            ThisGame.addOneElementToPlayer(playerNum, goodType);
                            setGoodCountLblForPlayerView(playerNum);
                            setSumElementCountLblForPlayerView(playerNum);

                            //test if this came from another player
                            if (oldPlayerNum != null && mainActivity.getP2POn().equals("1")) {
                                ThisGame.removeOneElementFromPlayer(oldPlayerNum, goodType);
                                setGoodCountLblForPlayerView(oldPlayerNum);
                                setSumElementCountLblForPlayerView(oldPlayerNum);
                            }
                            playDropSound();
                        }
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundDrawable(normalShape);
                default:
                    break;
            }
            return true;
        }
    }

    class MyDragListenerForPlayerGoods implements View.OnDragListener {
        Drawable enterShape = getResources().getDrawable(R.drawable.player_elements_holder_shape_droptarget);
        Drawable normalShape = getResources().getDrawable(R.drawable.player_elements_holder_shape);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //v.setBackgroundDrawable(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setBackgroundDrawable(normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    Integer playerNum = null;
                    ElementType goodType = null;
                    View imgView = (View) event.getLocalState();
                    //View goodsHolderVeiw = (View) (imgView.getParent());
                    View playerContainerView = (View) imgView.getParent().getParent().getParent();

                    playerNum = getPlayerNumFromView(playerContainerView);

                    if (playerNum != null) {

                        goodType = getGoodTypeFromImg(imgView);

                        ThisGame.removeOneElementFromPlayer(playerNum, goodType);
                        setGoodCountLblForPlayerView(playerNum);
                        setSumElementCountLblForPlayerView(playerNum);
                        playDropSound();
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //v.setBackgroundDrawable(normalShape);

                default:
                    break;
            }
            return true;
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

        }
    }

    private void refreshScores() {
        Integer i = 1;
        for (TextView playerScore :
                playerScoreLbls) {
            Integer score = ThisGame.getPlayerScoreFor(i);
            playerScore.setText(score.toString());
            i += 1;
        }
    }
    private void playDropSound() {
        if (mainActivity.getSoundOn().equals("1")) {
            sp.play(soundID, 1, 1, 0, 0, 1);
        }
    }
    private void setUpPlayerScoreLbls(View rootView) {
        playerScoreLbls.add((TextView) rootView.findViewById(R.id.playerScore1));
        playerScoreLbls.add((TextView) rootView.findViewById(R.id.playerScore2));
        if (ThisGame.getNumPlayers() >= 3) {
            playerScoreLbls.add((TextView) rootView.findViewById(R.id.playerScore3));
            if (ThisGame.getNumPlayers() == 4) {
                playerScoreLbls.add((TextView) rootView.findViewById(R.id.playerScore4));
            }
        }
        refreshScores();
    }
}
