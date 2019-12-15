package edu.ucr.lxu051;


import edu.ucr.lxu051.UI.BufferedImageTranscoder;
import edu.ucr.lxu051.Util.*;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Game extends JPanel {
    private final String PIC_SRC = "src/main/resources/Pic/Regular/";
    private LinkedList<Tile> tileMountain;
    private Map<Orientation, LinkedList<Tile>> discardedPile;
    private int tileLeft;
    private Map<Orientation, Hand> players;

    public Game() {
        tileMountain = new LinkedList<>();
        discardedPile = new HashMap<>();
        players = new HashMap<>();
    }

    public void initGame() throws IOException {
        genMountain();
        Random rnd = new Random();
        Collections.shuffle(tileMountain, rnd);
        for (Orientation orientation : Orientation.values()) {
            players.put(orientation, new Hand(orientation.name()));
            discardedPile.put(orientation, new LinkedList<>());
        }
        distributeStartingTiles();
//        revalidate();
    }

    private void distributeStartingTiles() {
        for (Orientation orientation : Orientation.values()) {
            LinkedList<Tile> tempHand = new LinkedList<>();
            for (int j = 0; j < 13; j++) {
                tempHand.add(tileMountain.pollFirst());
                tileLeft--;
            }
            players.get(orientation).initHand(tempHand);
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

    public String getPlayerHand(Orientation orientation) {
        return players.get(orientation).toString();
    }

    public int getTileLeft() {
        return tileLeft;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            Graphics2D g2d = (Graphics2D) g;
            BufferedImage front = loadImage(PIC_SRC + "Front.svg", 60, 80);
            g.drawImage(front, 70, 30, 60, 80, null);
            g.drawImage(front, 130, 30, 60, 80, null);
            g.drawImage(front, 190, 30, 60, 80, null);
            g.drawImage(front, 250, 30, 60, 80, null);
            g.drawImage(front, 310, 30, 60, 80, null);
            g.drawImage(front, 370, 30, 60, 80, null);
            g.drawImage(front, 430, 30, 60, 80, null);
            g.drawImage(front, 490, 30, 60, 80, null);
            g.drawImage(front, 550, 30, 60, 80, null);
            g.drawImage(front, 610, 30, 60, 80, null);
            g.drawImage(front, 670, 30, 60, 80, null);
            g.drawImage(front, 730, 30, 60, 80, null);
            g.drawImage(front, 790, 30, 60, 80, null);
            drawWest(g);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(70, 30, 60, 80, 10, 10);
            g2d.drawRoundRect(130, 30, 60, 80, 10, 10);
            g2d.drawRoundRect(190, 30, 60, 80, 10, 10);
            g2d.drawRoundRect(250, 30, 60, 80, 10, 10);
            g2d.drawRoundRect(310, 30, 60, 80, 10, 10);
            g2d.drawRoundRect(370, 30, 60, 80, 10, 10);
            g2d.drawRoundRect(430, 30, 60, 80, 10, 10);
            g2d.drawRoundRect(490, 30, 60, 80, 10, 10);
            g2d.drawRoundRect(550, 30, 60, 80, 10, 10);
            g2d.drawRoundRect(610, 30, 60, 80, 10, 10);
            g2d.drawRoundRect(670, 30, 60, 80, 10, 10);
            g2d.drawRoundRect(730, 30, 60, 80, 10, 10);
            g2d.drawRoundRect(790, 30, 60, 80, 10, 10);
        } catch (TranscoderException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage loadImage(String svgFile, float width, float height) throws TranscoderException {
        BufferedImageTranscoder imageTranscoder = new BufferedImageTranscoder();

        imageTranscoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
        imageTranscoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height);

        TranscoderInput input = new TranscoderInput(svgFile);
        imageTranscoder.transcode(input, null);

        return imageTranscoder.getBufferedImage();
    }

    public void drawWest(Graphics g) throws TranscoderException {
        Graphics2D g2d = (Graphics2D) g;
        int[] revealedHand = players.get(Orientation.WEST).getRevealedHand();
        String fileName;
        for (int i : revealedHand) {
            if (revealedHand[i] == 3) {
                fileName = Hand.positionToTile(i).toFileName();
            } else if (revealedHand[i] == 4) {
                fileName = Hand.positionToTile(i).toFileName();
            }
        }
        BufferedImage i = loadImage(PIC_SRC + "Man1.svg", 60, 80);
        g2d.drawImage(i, 70, 30, 60, 80, null);
        g2d.drawImage(i, 130, 30, 60, 80, null);
        g2d.drawImage(i, 190, 30, 60, 80, null);
        g2d.drawImage(i, 250, 30, 60, 80, null);
        g2d.drawImage(i, 310, 30, 60, 80, null);
        g2d.drawImage(i, 370, 30, 60, 80, null);
        g2d.drawImage(i, 430, 30, 60, 80, null);
        g2d.drawImage(i, 490, 30, 60, 80, null);
        g2d.drawImage(i, 550, 30, 60, 80, null);
        g2d.drawImage(i, 610, 30, 60, 80, null);
        g2d.drawImage(i, 670, 30, 60, 80, null);
        g2d.drawImage(i, 730, 30, 60, 80, null);
        g2d.drawImage(i, 790, 30, 60, 80, null);
    }

}
