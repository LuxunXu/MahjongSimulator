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
//        Tile t = new Tile("1W");
//        System.out.println(t);
//
//        Hand hand1 = new Hand("NORTH");
//        hand1.initHand("1W 1W 1W 2T 3T 5B 7B 9B 9B 9B 3W 4W 2T");
//        System.out.println(hand1);
//
//        Hand hand2 = new Hand("WEST");
//        hand2.initHand("5W 1W 1W 2T 3T 5B 7B 8B 9B 9B 3W 4W 2T");
//        System.out.println(hand2);

        HandUtil handUtil = new HandUtil("3");
        System.out.println(handUtil.reduce());
    }
}
