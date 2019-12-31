package edu.ucr.lxu051;

import edu.ucr.lxu051.UI.ImageProcessor;
import edu.ucr.lxu051.Util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class Game extends JPanel {
    private int scale;
    private final String PIC_SRC = "src/main/resources/Pic/Regular/";
    private final String PIC_FORMAT = ".svg";
    private LinkedList<Tile> tileMountain;
    private Map<Orientation, LinkedList<Tile>> discardedPiles;
    private int tileLeft;
    private Map<Orientation, Player> players;
    private long seed;
    private BufferedImage FRONT;
    private Orientation whosTurn;
    private LinkedList<Orientation> playersLeft;

    public Game(int scale) {
        this(scale, 0);
    }

    public Game(int scale, long seed) {
        this.seed = seed;
        this.scale = scale;
        FRONT = ImageProcessor.loadImage(PIC_SRC + "Front" + PIC_FORMAT, 3 * scale, 4 * scale);
        playersLeft = new LinkedList<>();
    }

    public void initGame() {
        tileMountain = new LinkedList<>();
        discardedPiles = new HashMap<>();
        players = new HashMap<>();
        genMountain();
        Random rnd = new Random();
        if (seed == 0) {
            seed = System.currentTimeMillis();
        }
        rnd.setSeed(seed);
        System.out.println("Seed: " + seed);
        Collections.shuffle(tileMountain, rnd);
        for (Orientation orientation : Orientation.values()) {
            players.put(orientation, new Player(orientation));
            discardedPiles.put(orientation, new LinkedList<>());
        }
        distributeStartingTiles();
        whosTurn = Orientation.EAST;
        playersLeft.addAll(Arrays.asList(Orientation.values()));
        offer(whosTurn);
        for (Orientation orientation : Orientation.values()) {
            System.out.println(players.get(orientation).toString());
            players.get(orientation).decideForfeitedSimple();
        }
        repaint();
    }

    public void offer(Orientation orientation) {
        Tile tile = tileMountain.pollFirst();
        System.out.println(orientation + " drew " + tile.toString());
        players.get(orientation).drawTile(tile);
        tileLeft--;
        repaint();
    }

    public int discard(Orientation orientation) {
        int discardedTile = players.get(orientation).discardAI();
        players.get(orientation).discardTile(discardedTile);
        // has issue: only in discarded pile when no one wants
//        discardedPiles.get(orientation).add(Tool.positionToTile(discardedTile));
        repaint();
        Tile tile = Tool.positionToTile(discardedTile);
        System.out.println(orientation + " discarded " + tile.toString());
        return discardedTile;
    }

    public void discard(Orientation orientation, int i) {
        players.get(orientation).discardTile(i);
        // has issue: only in discarded pile when no one wants
//        discardedPiles.get(orientation).add(Tool.positionToTile(i));
        Tile tile = Tool.positionToTile(i);
        System.out.println(orientation + " discarded " + tile.toString());
        repaint();
    }

    public void peng(Orientation orientation, int i) {
        System.out.println(orientation + " peng " + Tool.positionToTile(i).toString());
        players.get(orientation).peng(i);
        whosTurn = orientation;
        repaint();
    }

    public void gangRevealed(Orientation orientation, int i) {
        players.get(orientation).gangRevealed(i);
        whosTurn = orientation;
        repaint();
    }

    public void gangAttached(Orientation orientation, int i) {
        players.get(orientation).gangAttached(i);
        whosTurn = orientation;
        repaint();
    }

    public void hu(Orientation orientation, int i) {
        System.out.println(orientation + " declared hu.");
        players.get(orientation).drawTile(i);
        players.get(orientation).hu();
        whosTurn = orientation;
        System.out.println(players.get(orientation).toString());
        repaint();
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

    public Orientation getWhosTurn() {
        return whosTurn;
    }

    public void setWhosTurn(Orientation whosTurn) {
        this.whosTurn = whosTurn;
    }

    public void autoExecute() throws IOException {
        while (!tileMountain.isEmpty()) {
//            for (Orientation o : playersLeft) {
//                System.out.println(players.get(o).toString());
//            }
//            System.out.println();
            int decision = players.get(whosTurn).decideAction();

            while (decision == 300) {
                offer(whosTurn);
                decision = players.get(whosTurn).decideAction();
            }
            while (decision >= 400) { // handle qiangGang case
                Orientation origin = whosTurn;
                int tempTile = decision - 400;
                LinkedList<Orientation> someoneHued = new LinkedList<>();
                for (Orientation orientation : playersLeft) {
                    if (!orientation.equals(whosTurn)) {
                        Player p = players.get(orientation);
                        if (p.isReadyHelper(p.getConcealedHand(), tempTile)) {
                            if (p.wantHu(tempTile)) {
                                hu(orientation, tempTile);
                                someoneHued.add(orientation);
                            }
                        }
                    }
                }
                if (playersLeft.size() == 1) {
                    return;
                }
                if (!someoneHued.isEmpty()) {
                    for (Orientation orientation : someoneHued) {
                        playersLeft.remove(orientation);
                    }
                    discard(origin, tempTile);
                    break;
                } else {
                    gangAttached(whosTurn, tempTile);
                    offer(whosTurn);
                    decision = players.get(whosTurn).decideAction();
                }
            }
            boolean ifGanged = false;
            if (decision == 200) {
                playersLeft.remove(whosTurn);
            } else if (decision >= 0 && decision < 27) {
                discard(whosTurn, decision);
                boolean someoneDiscarded = true;
                while (someoneDiscarded) {
                    someoneDiscarded = false;
                    LinkedList<Orientation> someoneHued = new LinkedList<>();
                    for (Orientation orientation : playersLeft) {
                        if (!orientation.equals(whosTurn)) {
                            Player p = players.get(orientation);
                            if (p.isReadyHelper(p.getConcealedHand(), decision)) {
                                if (p.wantHu(decision)) {
                                    hu(orientation, decision);
                                    someoneHued.add(orientation);
                                }
                            }
                        }
                    }
                    if (playersLeft.size() == 1) {
                        return;
                    }
                    if (!someoneHued.isEmpty()) {
                        for (Orientation orientation : someoneHued) {
                            playersLeft.remove(orientation);
                        }
                        break;
                    }
                    for (Orientation orientation : playersLeft) {
                        if (!orientation.equals(whosTurn)) {
                            Player p = players.get(orientation);
                            if (p.canGangRevealed(decision)) {
                                if (p.wantGang(decision)) {
                                    gangRevealed(orientation, decision);
                                    ifGanged = true;
                                    break;
                                }
                            } else if (p.canPeng(decision)) {
                                if (p.wantPeng(decision)) {
                                    peng(orientation, decision);
                                    decision = discard(orientation);
                                    someoneDiscarded = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (!ifGanged) {
                discardedPiles.get(whosTurn).add(Tool.positionToTile(decision));
                whosTurn = getNextTurn();
            }
            offer(whosTurn);
        }
        // last round
        int decision = players.get(whosTurn).decideActionLastRound();
        if (decision == 200) {
            playersLeft.remove(whosTurn);
        } else {
            discard(whosTurn, decision);
            boolean someoneHued = false;
            for (Orientation orientation : playersLeft) {
                if (!orientation.equals(whosTurn)) {
                    Player p = players.get(orientation);
                    if (p.isReadyHelper(p.getConcealedHand(), decision)) {
                        if (p.wantHu(decision)) {
                            hu(orientation, decision);
                            someoneHued = true;
                        }
                    }
                }
            }
            if (someoneHued) {
                return;
            }
            // check one last round if someone can still peng
            while (decision != -1) {
                boolean changeMade = false;
                for (Orientation orientation : playersLeft) {
                    if (!orientation.equals(whosTurn)) {
                        Player p = players.get(orientation);
                        if (p.canPeng(decision)) {
                            if (p.wantPeng(decision)) {
                                peng(orientation, decision);
                                decision = discard(orientation);
                                changeMade = true;
                                break;
                            }
                        }
                    }
                }
                if (!changeMade) {
                    decision = -1;
                }
            }
        }
    }

    public Orientation getNextTurn() {
        int i = playersLeft.indexOf(whosTurn);
        if (i == playersLeft.size() - 1) {
            return playersLeft.get(0);
        }
        return playersLeft.get(i + 1);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCenter(g);
        if (whosTurn == null) {
            return;
        }
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
        g2d.drawRect(23 * scale, 30 * scale, 4 * scale, 4 * scale);
        g2d.drawRect(37 * scale, 30 * scale, 4 * scale, 4 * scale);
        g2d.drawRect(30 * scale, 37 * scale, 4 * scale, 4 * scale);
        g2d.drawRect(30 * scale, 30 * scale, 4 * scale, 4 * scale);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("DengXian", Font.PLAIN, 2 * scale));
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if (whosTurn != null) {
            g2d.drawString("" + String.format("%02d", tileLeft), 31 * scale, 32 * scale);
        }
        if (players != null && players.get(Orientation.EAST).isFinished()) {
            g2d.setColor(Color.RED);
            g2d.drawString("东", 31 * scale, 39 * scale);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.drawString("东", 31 * scale, 39 * scale);
        }
        if (players != null && players.get(Orientation.WEST).isFinished()) {
            g2d.setColor(Color.RED);
            g2d.drawString("西", 31 * scale, 25 * scale);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.drawString("西", 31 * scale, 25 * scale);
        }
        if (players != null && players.get(Orientation.SOUTH).isFinished()) {
            g2d.setColor(Color.RED);
            g2d.drawString("南", 38 * scale, 32 * scale);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.drawString("南", 38 * scale, 32 * scale);
        }
        if (players != null && players.get(Orientation.NORTH).isFinished()) {
            g2d.setColor(Color.RED);
            g2d.drawString("北", 24 * scale, 32 * scale);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.drawString("北", 24 * scale, 32 * scale);
        }
    }

    private void drawWest(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int[] revealedHand = players.get(Orientation.WEST).getRevealedHand();
        int[] concealedHand = players.get(Orientation.WEST).getConcealedHand();
        LinkedList<Tile> discardedPile = discardedPiles.get(Orientation.WEST);
        String fileName; BufferedImage image;
        int x = 7 * scale, revealedCount = 0, y = 3 * scale;
        for (int pos = 0; pos < revealedHand.length; pos++) {
            if (revealedHand[pos] == 3) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                for (int i = 0; i < 3; i++) {
                    drawTile(image, g2d, x, y, false);
                    x += 3 * scale;
                }
                x += scale;
                revealedCount++;
            } else if (revealedHand[pos] == 4) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                BufferedImage imageRotated = ImageProcessor.rotate(image, false);
                drawTile(image, g2d, x, y, false);
                drawTile(imageRotated, g2d, x + 3 * scale, y, true);
                drawTile(imageRotated, g2d, x + 3 * scale, y + 3 * scale, true);
                x += 7 * scale;
                drawTile(image, g2d, x, y, false);
                x += 4 * scale;
                revealedCount++;
            }
        }
        int tileLeft = 13 - 3 * revealedCount;
        x = 53 * scale  - 3 * scale * tileLeft;
        for (int pos = 0; pos < concealedHand.length; pos++) {
            int numTiles = concealedHand[pos];
            if (numTiles > 0) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                for (int i = 0; i < numTiles; i++) {
                    drawTile(image, g2d, x, y, false);
                    x += 3 * scale;
                }
            }
        }

        int discardedCount = 0;
        x = 41 * scale;
        y = 19 * scale;
        for (Tile tile : discardedPile) {
            if (discardedCount == 6) {
                x = 41 * scale;
                y = 15 * scale;
            } else if (discardedCount == 12) {
                x = 41 * scale;
                y = 11 * scale;
            }
            x -= 3 * scale;
            fileName = tile.toFileName();
            image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
            drawTile(image, g2d, x, y, false);
            discardedCount++;
        }
    }

    private void drawEast(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int[] revealedHand = players.get(Orientation.EAST).getRevealedHand();
        int[] concealedHand = players.get(Orientation.EAST).getConcealedHand();
        LinkedList<Tile> discardedPile = discardedPiles.get(Orientation.EAST);
        String fileName; BufferedImage image;
        int x = 11 * scale, y = 57 * scale;
        for (int pos = 0; pos < concealedHand.length; pos++) {
            int numTiles = concealedHand[pos];
            if (numTiles > 0) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                for (int i = 0; i < numTiles; i++) {
                    drawTile(image, g2d, x, y, false);
                    x += 3 * scale;
                }
            }
        }

        x = 54 * scale;
        for (int pos = 0; pos < revealedHand.length; pos++) {
            if (revealedHand[pos] == 3) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                for (int i = 0; i < 3; i++) {
                    drawTile(image, g2d, x, y, false);
                    x -= 3 * scale;
                }
                x -= scale;
            } else if (revealedHand[pos] == 4) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                BufferedImage imageRotated = ImageProcessor.rotate(image, false);
                drawTile(image, g2d, x, y, false);
                drawTile(imageRotated, g2d, x - 4 * scale, y - 2 * scale, true);
                drawTile(imageRotated, g2d, x - 4 * scale, y + scale, true);
                x -= 7 * scale;
                drawTile(image, g2d, x, y, false);
                x -= 4 * scale;
            }
        }

        int discardedCount = 0;
        x = 20 * scale;
        y = 41 * scale;
        for (Tile tile : discardedPile) {
            if (discardedCount == 6) {
                x = 20 * scale;
                y = 45 * scale;
            } else if (discardedCount == 12) {
                x = 20 * scale;
                y = 49 * scale;
            }
            x += 3 * scale;
            fileName = tile.toFileName();
            image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
            drawTile(image, g2d, x, y, false);
            discardedCount++;
        }

    }

    private void drawNorth(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int[] revealedHand = players.get(Orientation.NORTH).getRevealedHand();
        int[] concealedHand = players.get(Orientation.NORTH).getConcealedHand();
        LinkedList<Tile> discardedPile = discardedPiles.get(Orientation.NORTH);
        String fileName; BufferedImage image, imageRotated;
        int x = 3 * scale, y = 11 * scale;
        for (int pos = 0; pos < concealedHand.length; pos++) {
            int numTiles = concealedHand[pos];
            if (numTiles > 0) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                imageRotated = ImageProcessor.rotate(image, true);
                for (int i = 0; i < numTiles; i++) {
                    drawTile(imageRotated, g2d, x, y, true);
                    y += 3 * scale;
                }
            }
        }

        y = 54 * scale;
        for (int pos = 0; pos < revealedHand.length; pos++) {
            if (revealedHand[pos] == 3) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                imageRotated = ImageProcessor.rotate(image, true);
                for (int i = 0; i < 3; i++) {
                    drawTile(imageRotated, g2d, x, y, true);
                    y -= 3 * scale;
                }
                y -= scale;
            } else if (revealedHand[pos] == 4) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                imageRotated = ImageProcessor.rotate(image, true);
                drawTile(imageRotated, g2d, x, y, true);
                drawTile(image, g2d, x, y - 4 * scale, false);
                drawTile(image, g2d, x + 3 * scale, y - 4 * scale, false);
                y -= 7 * scale;
                drawTile(imageRotated, g2d, x, y, true);
                y -= 4 * scale;
            }
        }

        int discardedCount = 0;
        x = 19 * scale;
        y = 20 * scale;
        for (Tile tile : discardedPile) {
            if (discardedCount == 6) {
                x = 15 * scale;
                y = 20 * scale;
            } else if (discardedCount == 12) {
                x = 11 * scale;
                y = 20 * scale;
            }
            y += 3 * scale;
            fileName = tile.toFileName();
            image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
            imageRotated = ImageProcessor.rotate(image, true);
            drawTile(imageRotated, g2d, x, y, true);
            discardedCount++;
        }
    }

    private void drawSouth(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int[] revealedHand = players.get(Orientation.SOUTH).getRevealedHand();
        int[] concealedHand = players.get(Orientation.SOUTH).getConcealedHand();
        LinkedList<Tile> discardedPile = discardedPiles.get(Orientation.SOUTH);
        String fileName; BufferedImage image, imageRotated;
        int x = 57 * scale, y = 50 * scale;
        for (int pos = 0; pos < concealedHand.length; pos++) {
            int numTiles = concealedHand[pos];
            if (numTiles > 0) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                imageRotated = ImageProcessor.rotate(image, false);
                for (int i = 0; i < numTiles; i++) {
                    drawTile(imageRotated, g2d, x, y, true);
                    y -= 3 * scale;
                }
            }
        }

        y = 7 * scale;
        for (int pos = 0; pos < revealedHand.length; pos++) {
            if (revealedHand[pos] == 3) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                imageRotated = ImageProcessor.rotate(image, false);
                for (int i = 0; i < 3; i++) {
                    drawTile(imageRotated, g2d, x, y, true);
                    y += 3 * scale;
                }
                y += scale;
            } else if (revealedHand[pos] == 4) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                imageRotated = ImageProcessor.rotate(image, false);
                drawTile(imageRotated, g2d, x, y, true);
                drawTile(image, g2d, x + scale, y + 3 * scale, false);
                drawTile(image, g2d, x - 2 * scale, y + 3 * scale, false);
                y += 7 * scale;
                drawTile(imageRotated, g2d, x, y, true);
                y += 4 * scale;
            }
        }

        int discardedCount = 0;
        x = 41 * scale;
        y = 41 * scale;
        for (Tile tile : discardedPile) {
            if (discardedCount == 6) {
                x = 45 * scale;
                y = 41 * scale;
            } else if (discardedCount == 12) {
                x = 49 * scale;
                y = 41 * scale;
            }
            y -= 3 * scale;
            fileName = tile.toFileName();
            image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
            imageRotated = ImageProcessor.rotate(image, false);
            drawTile(imageRotated, g2d, x, y, true);
            discardedCount++;
        }
    }

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
