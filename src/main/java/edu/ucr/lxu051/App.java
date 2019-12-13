package edu.ucr.lxu051;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {

    public static void main( String[] args ) throws IOException {

//        Hand hand2 = new Hand("WEST");
//        hand2.initHand("5W 5W 5W 6W 7W 7W 7W 8B 9B 7B 3T 4T 2T");
//        System.out.println(hand2);
//        System.out.println(hand2.isReady());

//        HandUtil handUtil = new HandUtil("1133");
//        System.out.println(handUtil.reduce());

        Game game = new Game();
        game.initGame();
        System.out.println(game.getPlayerHand(0));
        System.out.println(game.getPlayerHand(1));
        System.out.println(game.getPlayerHand(2));
        System.out.println(game.getPlayerHand(3));

    }

}
