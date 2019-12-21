package edu.ucr.lxu051.Util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

public class Player extends Hand {
    public Player(Orientation orientation) {
        super(orientation);
    }

    public int discardAI() {
        // now randomly choose one
        Random rnd = new Random();
        while (true) {
            int i = rnd.nextInt(27);
            if (getConcealedHand()[i] > 0) {
                return i;
            }
        }
    }

    public int decideAction() throws IOException { // int = 27 means gang
        if (canHu()) {
            hu();
            return 200;
        }
        LinkedList<Integer> canGangConcealedSet = canGangConcelaed();
        if (!canGangConcealedSet.isEmpty()) {
            int i = canGangConcealedSet.getFirst();
            gangConcealed(i);
            return 300;
        }
        LinkedList<Integer> canGangAttachedSet = canGangAttached();
        if (!canGangAttachedSet.isEmpty()) {
            int i = canGangAttachedSet.getFirst();
            gangAttached(i);
            return 300;
        }
        return discardAI();
    }
}
