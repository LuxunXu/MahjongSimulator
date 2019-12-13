package edu.ucr.lxu051;

import edu.ucr.lxu051.Util.Hand;
import edu.ucr.lxu051.Util.Simple;
import edu.ucr.lxu051.Util.Tile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Game {
    private LinkedList<Tile> tileMountain;
    private LinkedList<Tile>[] discardedPile;
    private int tileLeft;
    private Hand[] players;

    public Game() {
        tileMountain = new LinkedList<>();
        discardedPile = new LinkedList[4];
        players = new Hand[4];
    }

    public void initGame() {
        genMountain();
        Random rnd = new Random();
        Collections.shuffle(tileMountain, rnd);
        players[0] = new Hand("EAST"); discardedPile[0] = new LinkedList<>();
        players[1] = new Hand("SOUTH"); discardedPile[1] = new LinkedList<>();
        players[2] = new Hand("WEST"); discardedPile[2] = new LinkedList<>();
        players[3] = new Hand("NORTH"); discardedPile[3] = new LinkedList<>();
        distributeStartingTiles();
    }

    private void distributeStartingTiles() {
        for (int i = 0; i < players.length; i++) {
            LinkedList<Tile> tempHand = new LinkedList<>();
            for (int j = 0; j < 13; j++) {
                tempHand.add(tileMountain.pollFirst());
                tileLeft--;
            }
            players[i].initHand(tempHand);
        }
    }

    private void genMountain() {
        for (Simple simple : Simple.values()) {
            for (int num = 1; num <= 9; num++) {
                for (int i = 0; i < 4; i++) {
                    tileMountain.add(new Tile(simple, num));
                }
            }
        }
        tileLeft = tileMountain.size();
    }

    public String getPlayerHand(int pos) {
        return players[pos].toString();
    }

    public int getTileLeft() {
        return tileLeft;
    }
}
