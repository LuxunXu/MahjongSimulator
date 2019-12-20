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
            concealedHand[tileToPosition(tile)]++;
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
        drawTile(tileToPosition(tile));
    }

    public void drawTile(int position) {
        concealedHand[position]++;
    }

    public boolean canDiscardTile(Tile tile) {
        int i = tileToPosition(tile);
        return concealedHand[i] > 0;
    }

    public void discardTile(Tile tile) {
        discardTile(tileToPosition(tile));
    }

    public void discardTile(int position) {
        this.concealedHand[position]--;
    }

    public boolean canPeng(Tile tile) {
        int i = tileToPosition(tile);
        return concealedHand[i] == 2 || concealedHand[i] == 3;
    }

    public void peng(Tile tile) {
        int i = tileToPosition(tile);
        concealedHand[i] -= 2;
        revealedHand[i] = 3;
    }

    public int canGang(Tile tile) {
        int i = tileToPosition(tile);
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

    public void gangConcealed(Tile tile) {
        int i = tileToPosition(tile);
        concealedHand[i] -= 4;
        revealedHand[i] = 4;
    }

    public void gangRevealed(Tile tile) {
        int i = tileToPosition(tile);
        concealedHand[i] -= 3;
        revealedHand[i] = 4;
    }

    public void gangAttached(Tile tile) {
        int i = tileToPosition(tile);
        concealedHand[i] -= 1;
        revealedHand[i] = 4;
    }

    public Set<Tile> isReady() throws IOException {
        Set<Tile> readySet = new LinkedHashSet<>();
        for (int i = 0; i < 27; i++) {
//            System.out.println(i);
            int[] handCopy = Arrays.copyOf(concealedHand, 27);
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
                sb.append(positionToTile(i));
            }
        }
        sb.append(" ");
        for (int i = 0; i < 27; i++) {
            if (this.revealedHand[i] > 0) {
                for (int j = 0; j < this.revealedHand[i]; j++) {
                    sb.append(positionToTile(i));
                }
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static Tile positionToTile(int i) {
        int number = i % 9 + 1;
        if (i < 9) {
            return new Tile(Simple.B, number);
        } else if (i > 17) {
            return new Tile(Simple.W, number);
        } else {
            return new Tile(Simple.T, number);
        }
    }

    public static int tileToPosition(Tile tile) {
        int index = tile.getNumber() - 1;
        switch (tile.getSimple()) {
            case B:
                return index;
            case T:
                return index + 9;
            case W:
                return index + 18;
        }
        return -1;
    }

    public Tile discardAI() {
        // now randomly choose one
        Random rnd = new Random();
        while (true) {
            int i = rnd.nextInt(27);
            if (concealedHand[i] > 0) {
                return positionToTile(i);
            }
        }
    }


}
