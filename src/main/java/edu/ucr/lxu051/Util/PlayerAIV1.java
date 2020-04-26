package edu.ucr.lxu051.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PlayerAIV1 extends Player {

    public PlayerAIV1(Orientation orientation) {
        super(orientation);
    }

    private int numberOfPairs() {
        int count = 0;
        for (int i = 0; i < 27; i++) {
            count += getConcealedHand()[i] / 2;
        }
        return count;
    }

    public int discardAI() throws IOException {
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
        for (int i = 0; i < 27; i++) {
            int[] handCopy = Arrays.copyOf(getConcealedHand(), 27);
            if (handCopy[i] > 0) {
                handCopy[i]--;
                if (isReady(handCopy)) {
                    return i;
                }
            }
        }

        List<Integer> bianZhang = Arrays.asList(0, 8, 9, 17, 18, 26);
        List<Integer> threeAndSeven = Arrays.asList(2, 6, 11, 15, 20, 24);
        for (int i : bianZhang) {
            if (getConcealedHand()[i] == 1) {
                return i;
            }
        }

        int tryTime = 0;
        while (tryTime < 50) {
            int i = rnd.nextInt(27);
            if (bianZhang.contains(i)) {
                continue;
            }
            if (getConcealedHand()[i] == 1 && !threeAndSeven.contains(i)) {
                if (getConcealedHand()[i-1] == 0 && getConcealedHand()[i+1] == 0) {
                    return i;
                }
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
            hu("ZiMo");
            System.out.println(toString());
            return 200;
        }
        if (numberOfPairs() >= 5) {
            return discardAI();
        }
        LinkedList<Integer> canGangConcealedSet = canGangConcealed();
        if (!canGangConcealedSet.isEmpty() && canGang) {
            int i = canGangConcealedSet.getFirst();
            gangConcealed(i);
            return 300;
        }
        LinkedList<Integer> canGangAttachedSet = canGangAttached();
        if (!canGangAttachedSet.isEmpty() && canGang) {
            System.out.println(getOrientation() + " wants to JiaGang.");
            int i = canGangAttachedSet.getFirst();
            return 400 + i;
        }
        return discardAI();
    }

    public boolean wantPeng(int i) {
        return numberOfPairs() < 5;
    }

    public boolean wantGang(int i) {
        return numberOfPairs() < 5;
    }

}
