package edu.ucr.lxu051;

import edu.ucr.lxu051.Util.Hand;
import edu.ucr.lxu051.Util.HandUtil;
import edu.ucr.lxu051.Util.Simple;
import edu.ucr.lxu051.Util.Tile;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {

//        Hand hand1 = new Hand("NORTH");
//        hand1.initHand("2B 2B 2B 3B 4B 5B 6B 6B 7B 7B 7B 7B 8B");
//        System.out.println(hand1);
//        System.out.println(hand1.isReady());

        Hand hand2 = new Hand("WEST");
        hand2.initHand("5W 5W 5W 6W 7W 7W 7W 8B 9B 7B 3T 4T 2T");
        System.out.println(hand2);
        System.out.println(hand2.isReady());

//        HandUtil handUtil = new HandUtil("1133");
//        System.out.println(handUtil.reduce());
    }
}
