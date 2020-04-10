package edu.ucr.lxu051.Util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

public class Player extends Hand {
    public Player(Orientation orientation) {
        super(orientation);
    }

    public void decideForfeitedSimple() {
        int[] curHand = getConcealedHand();
        int[] simpleCount = new int[3];
        for (int i = 0; i < 27; i++) {
            simpleCount[i / 9] += curHand[i];
        }
        int min = Integer.MAX_VALUE;
        int minSimple = 0;
        for (int i = 0; i < 3; i++) {
            if (simpleCount[i] == 0) {
                setForfeitedSimple(Tool.indexToSimple(i));
                return;
            }
            if (simpleCount[i] < min) {
                min = simpleCount[i];
                minSimple = i;
            }
        }
        setForfeitedSimple(Tool.indexToSimple(minSimple));
    }

    public int discardAI() {
        Random rnd = new Random();
        rnd.setSeed(1);

        // First discard forfeited simple
        int forfeitedSimpleIndex = Tool.simpleToIndex(getForfeitedSimple());
        for (int i = 9 * forfeitedSimpleIndex; i < 9 * forfeitedSimpleIndex + 9; i++) {
            if (getConcealedHand()[i] > 0) {
                return i;
            }
        }


        // some AI
        int tryTime = 0;
        while (tryTime < 50) {
            int i = rnd.nextInt(27);
            if (getConcealedHand()[i] == 1) {
                return i;
            }
            tryTime++;
        }

        // now randomly choose one
        while (true) {
            int i = rnd.nextInt(27);
            if (getConcealedHand()[i] > 0) {
                return i;
            }
        }
    }

    public int decideAction(boolean canGang) throws IOException { // int = 27 means gang
        if (canHu()) {
            System.out.println(getOrientation() + " declared ZiMo.");
            hu();
            System.out.println(toString());
            return 200;
        }
        LinkedList<Integer> canGangConcealedSet = canGangConcealed();
        if (!canGangConcealedSet.isEmpty() && canGang) {
            int i = canGangConcealedSet.getFirst();
            gangConcealed(i);
            return 300;
        }
        LinkedList<Integer> canGangAttachedSet = canGangAttached();
        if (!canGangAttachedSet.isEmpty() && canGang) {
            int i = canGangAttachedSet.getFirst();
            return 400 + i;
        }
        return discardAI();
    }

    public boolean wantHu(int i) {
        return true;
    }

    public boolean wantPeng(int i) {
        return true;
    }

    public boolean wantGang(int i) {
        return true;
    }
}
