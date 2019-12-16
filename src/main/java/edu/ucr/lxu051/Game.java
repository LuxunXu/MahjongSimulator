package edu.ucr.lxu051;

import edu.ucr.lxu051.UI.ImageProcessor;
import edu.ucr.lxu051.Util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class Game extends JPanel {
    private int scale = 20;
    private final String PIC_SRC = "src/main/resources/Pic/Regular/";
    private final String PIC_FORMAT = ".svg";
    private LinkedList<Tile> tileMountain;
    private Map<Orientation, LinkedList<Tile>> discardedPiles;
    private int tileLeft;
    private Map<Orientation, Hand> players;
    private long seed;
    private final BufferedImage FRONT = ImageProcessor.loadImage(PIC_SRC + "Front" + PIC_FORMAT, 3 * scale, 4 * scale);

    public Game(int scale) {
        this(scale, 0);
    }

    public Game(int scale, long seed) {
        tileMountain = new LinkedList<>();
        discardedPiles = new HashMap<>();
        players = new HashMap<>();
        this.seed = seed;
        this.scale = scale;
    }

    public void initGame() {
        genMountain();
        Random rnd = new Random();
        if (seed > 0) {
            rnd.setSeed(seed);
        }
        Collections.shuffle(tileMountain, rnd);
        for (Orientation orientation : Orientation.values()) {
            players.put(orientation, new Hand(orientation.name()));
            discardedPiles.put(orientation, new LinkedList<>());
        }
        distributeStartingTiles();
        repaint();
    }

    public void offer(Orientation orientation) {
        Tile tile = tileMountain.pollFirst();
        players.get(orientation).drawTile(tile);
        tileLeft--;
    }

    public void discard(Orientation orientation) {
        Tile discardedTile = players.get(orientation).discardAI();
        players.get(orientation).discardTile(discardedTile);
        discardedPiles.get(orientation).add(discardedTile);
        repaint();
    }

    public void gang(Orientation orientation) {
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

    public Hand getPlayerHand(Orientation orientation) {
        return players.get(orientation);
    }

    public int getTileLeft() {
        return tileLeft;
    }

    public boolean isFinish() {
        return tileMountain.isEmpty();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCenter(g);
        drawWest(g);
        drawEast(g);
        drawNorth(g);
        drawSouth(g);
    }

    private void drawCenter(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRect(23 * scale, 23 * scale, 18 * scale, 18 * scale);
        g2d.drawRect(30 * scale, 23 * scale, 4 * scale, 4 * scale);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("DengXian", Font.PLAIN, 2 * scale));
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.drawString("" + tileLeft, 31 * scale, 25 * scale);
//        g2d.drawRect(23 * scale, 23 * scale, 18 * scale, 18 * scale);
//        g2d.drawRect(23 * scale, 23 * scale, 18 * scale, 18 * scale);
//        g2d.drawRect(23 * scale, 23 * scale, 18 * scale, 18 * scale);
    }

    private void drawWest(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int[] revealedHand = players.get(Orientation.WEST).getRevealedHand();
        int[] concealedHand = players.get(Orientation.WEST).getConcealedHand();
        LinkedList<Tile> discardedPile = discardedPiles.get(Orientation.WEST);
        String fileName; BufferedImage image;
        int curX = 7 * scale, revealedCount = 0, y = 3 * scale;
        for (int pos = 0; pos < revealedHand.length; pos++) {
            if (revealedHand[pos] == 3) {
                fileName = Hand.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                for (int i = 0; i < 3; i++) {
                    drawTile(image, g2d, curX, y, false);
                    curX += 3 * scale;
                }
                curX += 2 * scale;
                revealedCount++;
            } else if (revealedHand[pos] == 4) {
                fileName = Hand.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                BufferedImage imageRotated = ImageProcessor.rotate(image);
                drawTile(image, g2d, curX, y, false);
                drawTile(imageRotated, g2d, curX + 3 * scale, y, true);
                drawTile(imageRotated, g2d, curX + 3 * scale, y + 3 * scale, true);
                curX += 7 * scale;
                drawTile(image, g2d, curX, y, false);
                curX += 2 * scale;
                revealedCount++;
            }
        }
        int tileLeft = 13 - 3 * revealedCount;
        curX = 53 * scale  - 3 * scale * tileLeft;
        for (int pos = 0; pos < concealedHand.length; pos++) {
            int numTiles = concealedHand[pos];
            if (numTiles > 0) {
                fileName = Hand.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                for (int i = 0; i < numTiles; i++) {
                    drawTile(image, g2d, curX, y, false);
                    curX += 3 * scale;
                }
            }
        }
        int discardedCount = 0;
        curX = 41 * scale;
        y = 19 * scale;
        for (Tile tile : discardedPile) {
            if (discardedCount == 6) {
                curX = 41 * scale;
                y = 15 * scale;
            } else if (discardedCount == 12) {
                curX = 41 * scale;
                y = 11 * scale;
            }
            curX -= 3 * scale;
            fileName = tile.toFileName();
            image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
            drawTile(image, g2d, curX, y, false);
            discardedCount++;
        }
    }

    private void drawEast(Graphics g) {}

    private void drawNorth(Graphics g) {}

    private void drawSouth(Graphics g) {}

    private void drawTile(BufferedImage image, Graphics2D g2d, int X, int Y, boolean sideway) {
        if (!sideway) {
            g2d.drawImage(FRONT, X, Y, 3 * scale, 4 * scale, null);
            g2d.drawImage(image, X, Y, 3 * scale, 4 * scale, null);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(X, Y, 3 * scale, 4 * scale, scale / 2, scale / 2);
        } else {
            g2d.drawImage(FRONT, X, Y, 4 * scale, 3 * scale, null);
            g2d.drawImage(image, X, Y, 4 * scale, 3 * scale, null);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(X, Y, 4 * scale, 3 * scale, scale / 2, scale / 2);
        }
    }
}
