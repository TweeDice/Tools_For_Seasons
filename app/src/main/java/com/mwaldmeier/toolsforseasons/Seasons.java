package com.mwaldmeier.toolsforseasons;

import android.app.Application;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by michael on 12/13/2015.
 */
public class Seasons extends Application{
    private Integer NumPlayers;
    private List<Player> Players = new ArrayList<>();
    private Integer Year;
    private Integer Day;
    private static List<Integer> BonusPenalty;
    static{
        List<Integer> temp = new ArrayList<>();
        temp.add(0);
        temp.add(-5);
        temp.add(-12);
        temp.add(-20);
        BonusPenalty = Collections.unmodifiableList(temp);
    }

    public void setUpNewGame(Integer numPlayers) {
        this.NumPlayers = numPlayers;
        Players.clear();
        Integer i = 1;
        while (i <= this.NumPlayers) {
            Players.add(new Player(i));
            i += 1;
        }
        this.Year = 1;
        this.Day = 1;

    }

    public Integer getDay() {
        return Day;
    }

    public Integer getYear() {
        if (this.Day < 13) {
            this.Year = 1;
        } else if (this.Day < 25) {
            this.Year = 2;
        } else {
            this.Year = 3;
        }
        return Year;
    }
    public void changeDay(boolean forward) {
        if (forward) {
            this.Day += 1;
        } else {
            this.Day -= 1;
        }
    }

    public void addOneToScoreFor(Integer playerNum) {
        Players.get((playerNum - 1)).addOneToScore();
    }

    public boolean removeOneFromScoreFor(Integer playerNum) {
        return Players.get((playerNum -1)).removeOneFromScore();
    }

    public Integer getPlayerScoreFor(Integer playerNum) {
        return Players.get((playerNum - 1)).getScore();
    }

    public Integer getNumPlayers() {
        return NumPlayers;
    }

    public void addOneElementToPlayer(Integer playerNum, ElementType goodType) {
        Players.get((playerNum - 1)).addOneToElement(goodType);
    }

    public boolean removeOneElementFromPlayer(Integer playerNum, ElementType goodType) {
        return Players.get((playerNum - 1)).removeOneFromElement(goodType);
    }

    public Integer getElementCountForPlayer(Integer playerNum, ElementType goodType) {
        return Players.get((playerNum-1)).getElementCount(goodType);
    }

    public Integer getSumElementsForPlayer(Integer playerNum) {
        return Players.get((playerNum-1)).getSumElements();
    }

    public boolean changeBonusUsedForPlayer(Integer playerNum, boolean increase) {
        boolean didChange = false;
        if (increase) {
            didChange = Players.get((playerNum -1)).addBonusUsed();
        } else {
            didChange = Players.get((playerNum -1)).removeBonusUsed();
        }
        return didChange;
    }

    public Integer getBonusPenaltyFroPlayer(Integer playerNum) {
        return Players.get((playerNum - 1)).getBonusPenalty();
    }

    public Integer getSummonForPlayer(Integer playerNum) {
        return Players.get((playerNum - 1)).getSummon();
    }
    public boolean changeSummonForPlayer(Integer playerNum, boolean increase) {
        boolean didChagne = false;
        if (increase) {
            didChagne = Players.get((playerNum - 1)).addSummon();
        } else {
            didChagne = Players.get((playerNum - 1)).decreaseSummon();
        }
        return didChagne;
    }

    private class Player {
        private final Integer ID;
        private List<Element> elements = new ArrayList<>();
        private Integer Score;
        private View goodsView;
        private int bonusesUsed;
        private Integer Summon;

        public Player(Integer id) {
            this.ID = id;
            for (ElementType goodType:
                    ElementType.values()) {
                elements.add(new Element(goodType));
            }
            this.Score = 0;
            this.goodsView = null;
            bonusesUsed = 0;
            this.Summon = 0;
        }

        public Integer getSummon() {
            return Summon;
        }
        private boolean addSummon() {
            boolean increased = false;
                if (Summon < 15) {
                    Summon += 1;
                    increased = true;
                }
            return increased;
        }
        private boolean decreaseSummon() {
            boolean decreased = false;
            if (Summon > 0) {
                Summon -= 1;
                decreased = true;
            }
            return decreased;
        }

        public Integer getBonusPenalty() {
            return BonusPenalty.get(bonusesUsed);
        }

        public boolean addBonusUsed() {
            boolean didIncreased = false;
            if (bonusesUsed < 3) {
                bonusesUsed += 1;
                didIncreased = true;
            }
            return didIncreased;
        }

        public boolean removeBonusUsed() {
            boolean didDecress = false;
            if (bonusesUsed > 0) {
                bonusesUsed -= 1;
                didDecress = true;
            }
            return didDecress;
        }

        public Integer getID() {
            return ID;
        }

        public Integer getScore() {
            return Score;
        }

        public Integer getSumElements() {
            Integer count = 0;

            for (Element element:
                 elements) {
                count += element.getCount();
            }

            return count;
        }

        public void addOneToScore() {
                this.Score += 1;
        }

        public boolean removeOneFromScore() {
            if (this.Score > 0) {
                this.Score -= 1;
                return true;
            } else {
                return false;
            }
        }

        public void addOneToElement(ElementType goodType) {
            for (Element element :
                    elements) {
                if (element.getType() == goodType) {
                    element.addOne();
                }
            }
        }

        public boolean removeOneFromElement(ElementType goodType) {
            boolean didRemove = false;
            for (Element element :
                    elements) {
                if (element.getType() == goodType) {
                    didRemove = element.removeOne();
                }
            }
            return didRemove;
        }

        public Integer getElementCount(ElementType goodType) {
            Integer count = null;
            for (Element element :
                    elements) {
                if (element.getType() == goodType) {
                    count = element.getCount();
                    break;
                }
            }
            return count;
        }

        private class Element {
            private final ElementType Type;
            private Integer Count;
            private View goodImgView;

            public Element(ElementType type) {
                this.Type = type;
                this.Count = 0;
                this.goodImgView = null;
            }

            public ElementType getType() {
                return Type;
            }

            public Integer getCount() {
                return Count;
            }
            public void addOne() {
                    this.Count += 1;
            }
            public boolean removeOne() {
                if (this.Count > 0) {
                    this.Count -= 1;
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
}
