package edu.ucr.lxu051.Util;

import java.io.IOException;
import java.util.*;

public class Hand {
    private int[] concealedHand; //Bing 0-8, Tiao 9-17, Wan 18-26
    private int[] revealedHand;
    private Orientation orientation;

    public Hand(String orientation) {
        this.concealedHand = new int[27];
        this.revealedHand = new int[27];
        this.orientation = Orientation.valueOf(orientation);
    }

    public void initHand(ArrayList<Tile> handList) { // list should be 13 long
        if (handList.size() != 13) {
            throw new IllegalArgumentException("Initialization should be 13 tiles.");
        }
        for (Tile tile : handList) {
            int index = tile.getNumber() - 1;
            switch (tile.getSimple()) {
                case B:
                    concealedHand[index]++;
                    break;
                case T:
                    concealedHand[index + 9]++;
                    break;
                case W:
                    concealedHand[index + 18]++;
                    break;
            }
        }
    }

    public void initHand(String handString) {
        ArrayList<Tile> list = new ArrayList<>();
        String[] tileTokens = handString.split("\\s");
        for (String tileToken : tileTokens) {
            list.add(new Tile(tileToken));
        }
        initHand(list);
    }

    public Set<Tile> isReady() throws IOException {
        Set<Tile> readySet = new LinkedHashSet<>();
        for (int i = 0; i < 27; i++) {
//            System.out.println(i);
            int[] handCopy = Arrays.copyOf(this.concealedHand, 27);
            if (isReadyHelper(handCopy, i)) {
                readySet.add(positionToTile(i));
            }
        }
        return readySet;
    }

    private boolean isReadyHelper(int[] curHand, int i) throws IOException {
        curHand[i]++;
        if (curHand[i] > 4) {
            return false;
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
            if (handUtil.reduce() == false) {
                return false;
            }
        }
        return true;
    }

    private Tile positionToTile(int i) {
        int number = i % 9 + 1;
        if (i < 9) {
            return new Tile(Simple.B, number);
        } else if (i > 17) {
            return new Tile(Simple.W, number);
        } else {
            return new Tile(Simple.T, number);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(orientation + ":\t");
        for (int i = 0; i < 27; i++) {
            for (int j = 0; j < this.concealedHand[i]; j++) {
                sb.append(positionToTile(i));
            }
        }
        sb.append(" ");
        for (int i = 0; i < 27; i++) {
            for (int j = 0; j < this.revealedHand[i]; j++) {
                sb.append(positionToTile(i));
            }
            sb.append(" ");
        }
        return sb.toString();
    }
}
