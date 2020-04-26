package edu.ucr.lxu051;

import edu.ucr.lxu051.UI.ImageProcessor;
import edu.ucr.lxu051.Util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Game extends JPanel {
    private boolean totalRandom;
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
    private Status currentStatus;
    private Tile currentTileOfInterest;
    private Tile offeredTile;

    public Game(int scale) {
        this(scale, 0);
    }

    public Game(int scale, long seed) {
        this.totalRandom = seed == 0;
        this.seed = seed;
        this.scale = scale;
        FRONT = ImageProcessor.loadImage(PIC_SRC + "Front" + PIC_FORMAT, 3 * scale, 4 * scale);
    }

    public long initGame() {
        return initGame(0);
    }

    public long initGame(long lastSeed) {
        tileMountain = new LinkedList<>();
        discardedPiles = new HashMap<>();
        players = new HashMap<>();
        playersLeft = new LinkedList<>();
        genMountain();
        Random rnd = new Random();
        if (lastSeed != 0) {
            seed = lastSeed;
        } else if (totalRandom) {
            seed = System.currentTimeMillis();
        }
        rnd.setSeed(seed);
        System.out.println("Seed: " + seed + "L");
        Collections.shuffle(tileMountain, rnd);
        for (Orientation orientation : Orientation.values()) {
            if (orientation.equals(Orientation.EAST)) {
                players.put(orientation, new PlayerAIV1(orientation));
            } else {
                players.put(orientation, new Player(orientation));
            }
            discardedPiles.put(orientation, new LinkedList<>());
        }
        distributeStartingTiles();
        whosTurn = Orientation.EAST;
        currentStatus = Status.OFFER;
        currentTileOfInterest = null;
        offeredTile = null;
        playersLeft.addAll(Arrays.asList(Orientation.values()));
//        offer(whosTurn);
        for (Orientation orientation : Orientation.values()) {
            System.out.println(players.get(orientation).toString());
            players.get(orientation).decideForfeitedSimple();
        }
        repaint();
        return seed;
    }

    public void offer(Orientation orientation) {
        Tile tile = tileMountain.pollFirst();
        offeredTile = tile;
        System.out.println(orientation + " drew " + tile.toString());
        players.get(orientation).drawTile(tile);
        tileLeft--;
        repaint();
    }

    public int discard(Orientation orientation) throws IOException {
        int discardedTile = players.get(orientation).discardAI();
        players.get(orientation).discardTile(discardedTile);
        repaint();
        Tile tile = Tool.positionToTile(discardedTile);
        System.out.println(orientation + " discarded " + tile.toString());
        return discardedTile;
    }

    public void discard(Orientation orientation, int i) {
        players.get(orientation).discardTile(i);
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

    public void hu(Orientation orientation, int i, String type) {
        System.out.println(orientation + " declared hu.");
        players.get(orientation).drawTile(i);
        players.get(orientation).hu(type);
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


    /*
    * decision == 300 暗杠
    * decision == 400 加杠
    * decision == 200 胡
    * */
    public void autoExecute() throws IOException {
        while (!tileMountain.isEmpty() && playersLeft.size() > 1) {
            offer(whosTurn);
            int decision = players.get(whosTurn).decideAction(!tileMountain.isEmpty());

            while (decision == 300) {
                offer(whosTurn);
                decision = players.get(whosTurn).decideAction(!tileMountain.isEmpty());
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
                                hu(orientation, tempTile, "QiangGang");
                                someoneHued.add(orientation);
                            }
                        }
                    }
                }
                if (!someoneHued.isEmpty()) {
                    discard(origin, tempTile);
                    for (Orientation orientation : someoneHued) {
                        whosTurn = orientation;
                        whosTurn = getNextTurn();
                        playersLeft.remove(orientation);
                        if (playersLeft.size() == 1) {
                            System.out.println("Game over: three player wins.");
                            return;
                        }
                    }
                    break;
                } else {
                    gangAttached(whosTurn, tempTile);
                    offer(whosTurn);
                    decision = players.get(whosTurn).decideAction(!tileMountain.isEmpty());
                }
            }
            if (decision == 200) {
                Orientation temp = whosTurn;
                whosTurn = getNextTurn();
                playersLeft.remove(temp);
                if (playersLeft.size() == 1) {
                    System.out.println("Game over: three player wins.");
                    return;
                }
            } else if (decision >= 0 && decision < 27) {
                discard(whosTurn, decision);
                boolean someoneDiscarded = true;
                boolean getNext = true;
                while (someoneDiscarded) {
                    getNext = true;
                    someoneDiscarded = false;
                    LinkedList<Orientation> someoneHued = new LinkedList<>();
                    for (Orientation orientation : playersLeft) {
                        if (!orientation.equals(whosTurn)) {
                            Player p = players.get(orientation);
                            if (p.isReadyHelper(p.getConcealedHand(), decision)) {
                                if (p.wantHu(decision)) {
                                    someoneHued.add(orientation);
                                }
                            }
                        }
                    }
                    if (!someoneHued.isEmpty()) {
                        for (Orientation orientation : someoneHued) {
                            if (someoneHued.size() > 1) {
                                hu(orientation, decision, "Double");
                            } else {
                                hu(orientation, decision, "Hu");
                            }
                            whosTurn = orientation;
                            whosTurn = getNextTurn();
                            playersLeft.remove(orientation);
                            if (playersLeft.size() == 1) {
                                System.out.println("Game over: three player wins.");
                                return;
                            }
                        }
                        getNext = false;
                        break;
                    }
                    for (Orientation orientation : playersLeft) {
                        if (!orientation.equals(whosTurn)) {
                            Player p = players.get(orientation);
                            if (p.canGangRevealed(decision) && !tileMountain.isEmpty()) {
                                if (p.wantGang(decision)) {
                                    gangRevealed(orientation, decision);
                                    getNext = false;
                                    break;
                                }
                            } else if (p.canPeng(decision)) {
                                if (p.wantPeng(decision)) {
                                    peng(orientation, decision);
                                    decision = discard(orientation);
                                    someoneDiscarded = true;
                                    getNext = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (getNext) {
                    discardedPiles.get(whosTurn).add(Tool.positionToTile(decision));
                    if (!tileMountain.isEmpty()) {
                        whosTurn = getNextTurn();
                    }
                }
            }
        }
        System.out.println("Game already over.");
    }

    /*
    * decision == 300 暗杠
    * decision == 400 加杠
    * decision == 200 胡
    * */
    public void step() throws IOException {
        if (playersLeft.size() == 1) {
            System.out.println("Game over: three player wins.");
            return;
        } else if (currentStatus.equals(Status.OFFER)) {
            if (tileMountain.isEmpty()) {
                System.out.println("Game over: no more tiles.");
                return;
            }
            offer(whosTurn);
            currentTileOfInterest = null;
            currentStatus = Status.DECISION;
        } else if (currentStatus.equals(Status.DECISION)) {
            int decision = players.get(whosTurn).decideAction(!tileMountain.isEmpty());
            if (decision == 300) {
                currentStatus = Status.OFFER;
            } else if (decision == 200) {
                Orientation temp = whosTurn;
                whosTurn = getNextTurn();
                playersLeft.remove(temp);
            } else if (decision >= 0 && decision < 27) {
                discard(whosTurn, decision);
                discardedPiles.get(whosTurn).add(Tool.positionToTile(decision));
                currentTileOfInterest = Tool.positionToTile(decision);
                currentStatus = Status.WAIT;
            } else if (decision >= 400) {
                int pos = decision - 400;
                currentTileOfInterest = Tool.positionToTile(pos);
                currentStatus = Status.WAITFORQIANG;
            } else {
                throw new IllegalStateException("Wrong decision ID.");
            }
        } else if (currentStatus.equals(Status.WAITFORQIANG)) {
            if (currentTileOfInterest == null) {
                throw new IllegalStateException("No tile of interest.");
            } else {
                int curTile = Tool.tileToPosition(currentTileOfInterest);
                Orientation origin = whosTurn;
                // Start to ask for move
                LinkedList<Orientation> someoneHued = new LinkedList<>();
                for (Orientation player : playersLeft) {
                    if (!player.equals(origin)) {
                        Player p = players.get(player);
                        if (p.isReadyHelper(p.getConcealedHand(), curTile)) {
                            if (p.wantHu(curTile)) {
                                someoneHued.add(player);
                            }
                        }
                    }
                }
                if (!someoneHued.isEmpty()) {
                    discardedPiles.get(origin).removeLast();
                    for (Orientation orientation : someoneHued) {
                        if (someoneHued.size() > 1) {
                            hu(orientation, curTile, "Double");
                        } else {
                            hu(orientation, curTile, "Hu");
                        }
                        whosTurn = getNextTurn();
                        playersLeft.remove(orientation);
                        if (playersLeft.size() == 1) {
                            System.out.println("Game over: three player wins.");
                            return;
                        }
                    }
                    currentStatus = Status.OFFER;
                } else {
                    gangAttached(origin, curTile);
                    currentStatus = Status.OFFER;
                }
            }
        } else if (currentStatus.equals(Status.WAIT)) {
            if (currentTileOfInterest == null) {
                throw new IllegalStateException("No tile of interest.");
            } else {
//                    whosTurn = getNextTurn();
//                    currentStatus = Status.OFFER;
                int curTile = Tool.tileToPosition(currentTileOfInterest);
                Orientation origin = whosTurn;
                // Start to ask for move
                LinkedList<Orientation> someoneHued = new LinkedList<>();
                for (Orientation player : playersLeft) {
                    if (!player.equals(origin)) {
                        Player p = players.get(player);
                        if (p.isReadyHelper(p.getConcealedHand(), curTile)) {
                            if (p.wantHu(curTile)) {
                                someoneHued.add(player);
                            }
                        }
                    }
                }
                if (!someoneHued.isEmpty()) {
                    discardedPiles.get(origin).removeLast();
                    for (Orientation orientation : someoneHued) {
                        if (someoneHued.size() > 1) {
                            hu(orientation, curTile, "Double");
                        } else {
                            hu(orientation, curTile, "Hu");
                        }
                        whosTurn = orientation;
                        whosTurn = getNextTurn();
                        playersLeft.remove(orientation);
                        if (playersLeft.size() == 1) {
                            System.out.println("Game over: three player wins.");
                            return;
                        }
                    }
                    currentStatus = Status.OFFER;
                } else {
                    for (Orientation orientation : playersLeft) {
                        if (!orientation.equals(whosTurn)) {
                            Player p = players.get(orientation);
                            if (p.canGangRevealed(curTile) && !tileMountain.isEmpty()) {
                                if (p.wantGang(curTile)) {
                                    discardedPiles.get(origin).removeLast();
                                    gangRevealed(orientation, curTile);
                                    currentStatus = Status.OFFER;
                                    return;
                                }
                            } else if (p.canPeng(curTile)) {
                                if (p.wantPeng(curTile)) {
                                    discardedPiles.get(origin).removeLast();
                                    peng(orientation, curTile);
                                    currentStatus = Status.DISCARD;
                                    return;
                                }
                            }
                        }
                    }
                    whosTurn = getNextTurn();
                    currentStatus = Status.OFFER;
                }
            }
        } else if (currentStatus.equals(Status.DISCARD)) {
            int decision = discard(whosTurn);
            discardedPiles.get(whosTurn).add(Tool.positionToTile(decision));
            currentTileOfInterest = Tool.positionToTile(decision);
            currentStatus = Status.WAIT;
        } else {
            throw new IllegalStateException("Wrong Status.");
        }
    }

    public Orientation getNextTurn() {
//        List<Orientation> seating = Arrays.asList(Orientation.EAST, Orientation.SOUTH, Orientation.WEST, Orientation.NORTH);
        int i = playersLeft.indexOf(whosTurn);
        if (i + 1 == playersLeft.size()) {
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
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(4f));
        g2d.drawLine(0, 11 * scale, 53 * scale, 11 * scale);
        g2d.drawLine(53 * scale, 0, 53 * scale, 53 * scale);
        g2d.drawLine(11 * scale, 53 * scale, 64 * scale, 53 * scale);
        g2d.drawLine(11 * scale, 11 * scale, 11 * scale, 64 * scale);
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
            g2d.setColor(Color.GREEN);
            g2d.drawString("东" + players.get(Orientation.EAST).getHuType(), 31 * scale, 39 * scale);
        } else if (whosTurn != null && whosTurn.equals(Orientation.EAST)) {
            g2d.setColor(Color.RED);
            g2d.drawString("东", 31 * scale, 39 * scale);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.drawString("东", 31 * scale, 39 * scale);
        }
        if (players != null && players.get(Orientation.WEST).isFinished()) {
            g2d.setColor(Color.GREEN);
            g2d.drawString("西" + players.get(Orientation.WEST).getHuType(), 31 * scale, 25 * scale);
        } else if (whosTurn != null && whosTurn.equals(Orientation.WEST)) {
            g2d.setColor(Color.RED);
            g2d.drawString("西", 31 * scale, 25 * scale);
        }  else {
            g2d.setColor(Color.BLACK);
            g2d.drawString("西", 31 * scale, 25 * scale);
        }
        if (players != null && players.get(Orientation.SOUTH).isFinished()) {
            g2d.setColor(Color.GREEN);
            g2d.drawString("南" + players.get(Orientation.SOUTH).getHuType(), 38 * scale, 32 * scale);
        } else if (whosTurn != null && whosTurn.equals(Orientation.SOUTH)) {
            g2d.setColor(Color.RED);
            g2d.drawString("南", 38 * scale, 32 * scale);
        }  else {
            g2d.setColor(Color.BLACK);
            g2d.drawString("南", 38 * scale, 32 * scale);
        }
        if (players != null && players.get(Orientation.NORTH).isFinished()) {
            g2d.setColor(Color.GREEN);
            g2d.drawString("北" + players.get(Orientation.NORTH).getHuType(), 24 * scale, 32 * scale);
        } else if (whosTurn != null && whosTurn.equals(Orientation.NORTH)) {
            g2d.setColor(Color.RED);
            g2d.drawString("北", 24 * scale, 32 * scale);
        }  else {
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
        int x = 3 * scale, revealedCount = 0, y = 3 * scale;
        for (int pos = 0; pos < revealedHand.length; pos++) {
            if (revealedHand[pos] == 3) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                for (int i = 0; i < 3; i++) {
                    drawTile(image, g2d, x, y, false, null);
                    x += 3 * scale;
                }
                x += scale;
                revealedCount++;
            } else if (revealedHand[pos] == 4) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                BufferedImage imageRotated = ImageProcessor.rotate(image, false);
                drawTile(image, g2d, x, y, false, null);
                drawTile(imageRotated, g2d, x + 3 * scale, y, true, null);
                drawTile(imageRotated, g2d, x + 3 * scale, y + 3 * scale, true, null);
                x += 7 * scale;
                drawTile(image, g2d, x, y, false, null);
                x += 4 * scale;
                revealedCount++;
            }
        }
        int tileLeft = 0;
        for (int i = 0; i < concealedHand.length; i++) {
            tileLeft += concealedHand[i];
        }
        x = 53 * scale - 3 * scale * tileLeft;
        boolean turnedRed = false;
        for (int pos = 0; pos < concealedHand.length; pos++) {
            int numTiles = concealedHand[pos];
            if (numTiles > 0) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                for (int i = 0; i < numTiles; i++) {
                    if (offeredTile != null && whosTurn.equals(Orientation.WEST) && currentStatus.equals(Status.DECISION)
                            && Tool.tileToPosition(offeredTile) == pos && !turnedRed) {
                        drawTile(image, g2d, x, y, false, Color.red);
                        turnedRed = true;
                    } else {
                        drawTile(image, g2d, x, y, false, null);
                    }
                    x += 3 * scale;
                }
            }
        }

        int discardedCount = 0;
        x = 41 * scale;
        y = 19 * scale;
        for (int i = 0; i < discardedPile.size(); i++) {
            Tile tile = discardedPile.get(i);
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
            if (i == discardedPile.size() - 1 && whosTurn.equals(Orientation.WEST) && currentStatus.equals(Status.WAIT)) {
                drawTile(image, g2d, x, y, false, Color.RED);
            } else {
                drawTile(image, g2d, x, y, false, null);
            }
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
        boolean turnedRed = false;
        for (int pos = 0; pos < concealedHand.length; pos++) {
            int numTiles = concealedHand[pos];
            if (numTiles > 0) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                for (int i = 0; i < numTiles; i++) {
                    if (offeredTile != null && whosTurn.equals(Orientation.EAST) && currentStatus.equals(Status.DECISION)
                            && Tool.tileToPosition(offeredTile) == pos && !turnedRed) {
                        drawTile(image, g2d, x, y, false, Color.RED);
                        turnedRed = true;
                    } else {
                        drawTile(image, g2d, x, y, false, null);
                    }
                    x += 3 * scale;
                }
            }
        }

        x = 58 * scale;
        for (int pos = 0; pos < revealedHand.length; pos++) {
            if (revealedHand[pos] == 3) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                for (int i = 0; i < 3; i++) {
                    drawTile(image, g2d, x, y, false, null);
                    x -= 3 * scale;
                }
                x -= scale;
            } else if (revealedHand[pos] == 4) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                BufferedImage imageRotated = ImageProcessor.rotate(image, false);
                drawTile(image, g2d, x, y, false, null);
                drawTile(imageRotated, g2d, x - 4 * scale, y - 2 * scale, true, null);
                drawTile(imageRotated, g2d, x - 4 * scale, y + scale, true, null);
                x -= 7 * scale;
                drawTile(image, g2d, x, y, false, null);
                x -= 4 * scale;
            }
        }

        int discardedCount = 0;
        x = 20 * scale;
        y = 41 * scale;
        for (int i = 0; i < discardedPile.size(); i++) {
            Tile tile = discardedPile.get(i);
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
            if (i == discardedPile.size() - 1 && whosTurn.equals(Orientation.EAST) && currentStatus.equals(Status.WAIT)) {
                drawTile(image, g2d, x, y, false, Color.RED);
            } else {
                drawTile(image, g2d, x, y, false, null);
            }
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
        boolean turnedRed = false;
        for (int pos = 0; pos < concealedHand.length; pos++) {
            int numTiles = concealedHand[pos];
            if (numTiles > 0) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                imageRotated = ImageProcessor.rotate(image, true);
                for (int i = 0; i < numTiles; i++) {
                    if (offeredTile != null && whosTurn.equals(Orientation.NORTH) && currentStatus.equals(Status.DECISION)
                            && Tool.tileToPosition(offeredTile) == pos && !turnedRed) {
                        drawTile(imageRotated, g2d, x, y, true, Color.red);
                        turnedRed = true;
                    } else {
                        drawTile(imageRotated, g2d, x, y, true, null);
                    }
                    y += 3 * scale;
                }
            }
        }

        y = 58 * scale;
        for (int pos = 0; pos < revealedHand.length; pos++) {
            if (revealedHand[pos] == 3) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                imageRotated = ImageProcessor.rotate(image, true);
                for (int i = 0; i < 3; i++) {
                    drawTile(imageRotated, g2d, x, y, true, null);
                    y -= 3 * scale;
                }
                y -= scale;
            } else if (revealedHand[pos] == 4) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                imageRotated = ImageProcessor.rotate(image, true);
                drawTile(imageRotated, g2d, x, y, true, null);
                drawTile(image, g2d, x, y - 4 * scale, false, null);
                drawTile(image, g2d, x + 3 * scale, y - 4 * scale, false, null);
                y -= 7 * scale;
                drawTile(imageRotated, g2d, x, y, true, null);
                y -= 4 * scale;
            }
        }

        int discardedCount = 0;
        x = 19 * scale;
        y = 20 * scale;
        for (int i = 0; i < discardedPile.size(); i++) {
            Tile tile = discardedPile.get(i);
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
            if (i == discardedPile.size() - 1 && whosTurn.equals(Orientation.NORTH) && currentStatus.equals(Status.WAIT)) {
                drawTile(imageRotated, g2d, x, y, true, Color.red);
            } else {
                drawTile(imageRotated, g2d, x, y, true, null);
            }
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
        boolean turnedRed = false;
        for (int pos = 0; pos < concealedHand.length; pos++) {
            int numTiles = concealedHand[pos];
            if (numTiles > 0) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                imageRotated = ImageProcessor.rotate(image, false);
                for (int i = 0; i < numTiles; i++) {
                    if (offeredTile != null && whosTurn.equals(Orientation.SOUTH) && currentStatus.equals(Status.DECISION)
                            && Tool.tileToPosition(offeredTile) == pos && !turnedRed) {
                        drawTile(imageRotated, g2d, x, y, true, Color.RED);
                        turnedRed = true;
                    } else {
                        drawTile(imageRotated, g2d, x, y, true, null);
                    }
                    y -= 3 * scale;
                }
            }
        }

        y = 3 * scale;
        for (int pos = 0; pos < revealedHand.length; pos++) {
            if (revealedHand[pos] == 3) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                imageRotated = ImageProcessor.rotate(image, false);
                for (int i = 0; i < 3; i++) {
                    drawTile(imageRotated, g2d, x, y, true, null);
                    y += 3 * scale;
                }
                y += scale;
            } else if (revealedHand[pos] == 4) {
                fileName = Tool.positionToTile(pos).toFileName();
                image = ImageProcessor.loadImage(PIC_SRC + fileName + PIC_FORMAT, 3 * scale, 4 * scale);
                imageRotated = ImageProcessor.rotate(image, false);
                drawTile(imageRotated, g2d, x, y, true, null);
                drawTile(image, g2d, x + scale, y + 3 * scale, false, null);
                drawTile(image, g2d, x - 2 * scale, y + 3 * scale, false, null);
                y += 7 * scale;
                drawTile(imageRotated, g2d, x, y, true, null);
                y += 4 * scale;
            }
        }

        int discardedCount = 0;
        x = 41 * scale;
        y = 41 * scale;
        for (int i = 0; i < discardedPile.size(); i++) {
            Tile tile = discardedPile.get(i);
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
            if (i == discardedPile.size() - 1 && whosTurn.equals(Orientation.SOUTH) && currentStatus.equals(Status.WAIT)) {
                drawTile(imageRotated, g2d, x, y, true, Color.RED);
            } else {
                drawTile(imageRotated, g2d, x, y, true, null);
            }
            discardedCount++;
        }
    }

    private void drawTile(BufferedImage image, Graphics2D g2d, int X, int Y, boolean sideway, Color outline) {
        if (!sideway) {
            g2d.drawImage(FRONT, X, Y, 3 * scale, 4 * scale, null);
            g2d.drawImage(image, X, Y, 3 * scale, 4 * scale, null);
            if (outline == null) {
                g2d.setColor(Color.LIGHT_GRAY);
            } else {
                g2d.setColor(outline);
            }
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(X, Y, 3 * scale, 4 * scale, scale / 2, scale / 2);
        } else {
            g2d.drawImage(FRONT, X, Y, 4 * scale, 3 * scale, null);
            g2d.drawImage(image, X, Y, 4 * scale, 3 * scale, null);
            if (outline == null) {
                g2d.setColor(Color.LIGHT_GRAY);
            } else {
                g2d.setColor(outline);
            }
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(X, Y, 4 * scale, 3 * scale, scale / 2, scale / 2);
        }
    }



}
