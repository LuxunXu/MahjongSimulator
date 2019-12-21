package edu.ucr.lxu051.Util;

import java.io.IOException;
import java.util.*;

public class Hand {
    private int[] concealedHand; //Bing 0-8, Tiao 9-17, Wan 18-26
    private int[] revealedHand;
    private Simple forfeitedSimple;
    private Orientation orientation;
    private boolean finished;

    public Hand(Orientation orientation) {
        concealedHand = new int[27];
        revealedHand = new int[27];
        this.orientation = orientation;
        finished = false;
    }

    public void initHand(LinkedList<Tile> handList) { // list should be 13 long
        if (handList.size() != 13) {
            throw new IllegalArgumentException("Initialization should be 13 tiles.");
        }
        for (Tile tile : handList) {
            concealedHand[Tool.tileToPosition(tile)]++;
        }
    }

    public void initHand(String handString) {
        LinkedList<Tile> list = new LinkedList<>();
        String[] tileTokens = handString.split("\\s");
        for (String tileToken : tileTokens) {
            list.add(new Tile(tileToken));
        }
        initHand(list);
    }

    public void drawTile(Tile tile) {
        drawTile(Tool.tileToPosition(tile));
    }

    public void drawTile(int position) {
        concealedHand[position]++;
    }

    public boolean canDiscardTile(int i) {
        return concealedHand[i] > 0;
    }

//    public void discardTile(Tile tile) {
//        discardTile(Tool.tileToPosition(tile));
//    }

    public void discardTile(int position) {
        concealedHand[position]--;
    }

    public boolean canPeng(int i) {
        return concealedHand[i] == 2 || concealedHand[i] == 3;
    }

    public void peng(int i) {
        concealedHand[i] -= 2;
        revealedHand[i] = 3;
    }

    /* Need redo
    public int canGang(Tile tile) {
        int i = Tool.tileToPosition(tile);
        if (concealedHand[i] == 4) {
            return 1; // canGangConcealed
        } else if (concealedHand[i] == 3) {
            return 2; // canGangRevealed
        } else if (revealedHand[i] == 3 && concealedHand[i] == 1) {
            return 3; // canGangAttatched
        } else {
            return 0;
        }
    }
    */

    public LinkedList<Integer> canGangConcelaed() {
        LinkedList<Integer> candidates = new LinkedList<>();
        for (int i = 0; i < 27; i++) {
            if (concealedHand[i] == 4) {
                candidates.add(i);
            }
        }
        return candidates;
    }

    public void gangConcealed(int i) {
        concealedHand[i] -= 4;
        revealedHand[i] = 4;
    }

    public boolean canGangRevealed(int i) {
        return concealedHand[i] == 3;
    }

    public void gangRevealed(int i) {
        concealedHand[i] -= 3;
        revealedHand[i] = 4;
    }

    public LinkedList<Integer> canGangAttached() {
        LinkedList<Integer> candidates = new LinkedList<>();
        for (int i = 0; i < 27; i++) {
            if (revealedHand[i] == 3 && concealedHand[i] == 1) {
                candidates.add(i);
            }
        }
        return candidates;
    }

    public void gangAttached(int i) {
        concealedHand[i] -= 1;
        revealedHand[i] = 4;
    }

    public void hu() {
        finished = true;
    }

    public boolean canHu() throws IOException {
        return canHuHelper(concealedHand);
    }

    private boolean canHuHelper(int[] curHand) throws IOException {
        boolean isSevenPairs = isSevenPairs(curHand);
        if (isSevenPairs) {
            return true;
        }
        ArrayList<String> testSplits = new ArrayList<>();
        String split = "";
        for (int j = 0; j < 27; j++) {
            if (curHand[j] == 0) {
                if (!split.isEmpty()) {
                    testSplits.add(split);
                }
                split = "";
            } else {
                if (j % 9 == 0) {
                    if (!split.isEmpty()) {
                        testSplits.add(split);
                    }
                    split = "" + curHand[j];
                } else {
                    split += curHand[j];
                }
            }
        }
        if (!split.isEmpty()) {
            testSplits.add(split);
        }
        for (String testSplit : testSplits) {
            HandUtil handUtil = new HandUtil(testSplit);
            if (!handUtil.reduce()) {
                return false;
            }
        }
        return true;
    }

    public Set<Tile> isReady() throws IOException {
        Set<Tile> readySet = new LinkedHashSet<>();
        for (int i = 0; i < 27; i++) {
            int[] handCopy = Arrays.copyOf(concealedHand, 27);
            if (isReadyHelper(handCopy, i)) {
                readySet.add(Tool.positionToTile(i));
            }
        }
        return readySet;
    }

    private boolean isReadyHelper(int[] curHand, int i) throws IOException {
        curHand[i]++;
        if (curHand[i] > 4) {
            return false;
        }
        return canHuHelper(curHand);
    }

    private boolean isSevenPairs(int[] curHand) {
        int count = 0;
        for (int num : curHand) {
            if (num > 0) {
                if (num % 2 == 0) {
                    count += num / 2;
                }
            }
        }
        return count == 7;
    }

    public int[] getConcealedHand() {
        return concealedHand;
    }

    public int[] getRevealedHand() {
        return revealedHand;
    }

    public Simple getForfeitedSimple() {
        return forfeitedSimple;
    }

    public Orientation getOrientation() {
        return orientation;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(orientation).append(":\t");
        for (int i = 0; i < 27; i++) {
            for (int j = 0; j < this.concealedHand[i]; j++) {
                sb.append(Tool.positionToTile(i));
            }
        }
        sb.append(" ");
        for (int i = 0; i < 27; i++) {
            if (this.revealedHand[i] > 0) {
                for (int j = 0; j < this.revealedHand[i]; j++) {
                    sb.append(Tool.positionToTile(i));
                }
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
