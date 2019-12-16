package edu.ucr.lxu051;

import edu.ucr.lxu051.Util.Orientation;
import edu.ucr.lxu051.Util.Simple;
import edu.ucr.lxu051.Util.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App extends JFrame {

    private final int SCALE = 15;
    private Game game;
    private JPanel contentPane;

    public static void main( String[] args ) {

//        Hand hand2 = new Hand("WEST");
//        hand2.initHand("5W 5W 5W 6W 7W 7W 7W 8B 9B 7B 3T 4T 2T");
//        System.out.println(hand2);
//        System.out.println(hand2.isReady());

//        HandUtil handUtil = new HandUtil("1133");
//        System.out.println(handUtil.reduce());

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    App demo = new App();
                    demo.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public App() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 64 * SCALE, 68 * SCALE);
//        setSize(64 * SCALE, 64 * SCALE);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);
        setResizable(false);

        JPanel controlPanel = new JPanel();
        controlPanel.setSize(64 * SCALE, 4 * SCALE);
        JButton jButton = new JButton("Go");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Orientation whosTurn = game.getWhosTurn();
                game.offer(whosTurn);
                game.discard(whosTurn);
                if (whosTurn.equals(Orientation.EAST)) {
                    game.setWhosTurn(Orientation.SOUTH);
                } else if (whosTurn.equals(Orientation.SOUTH)) {
                    game.setWhosTurn(Orientation.WEST);
                } else if (whosTurn.equals(Orientation.WEST)) {
                    game.setWhosTurn(Orientation.NORTH);
                } else {
                    game.setWhosTurn(Orientation.EAST);
                }
            }
        });
        controlPanel.add(jButton);

        game = new Game(SCALE, 576456);
        game.initGame();
        game.setSize(64 * SCALE, 64 * SCALE);

//        game.getPlayerHand(Orientation.SOUTH).gangConcealed(new Tile(Simple.B, 2));
//        game.getPlayerHand(Orientation.SOUTH).peng(new Tile(Simple.T, 1));
//        game.getPlayerHand(Orientation.SOUTH).discardTile(new Tile(Simple.B, 1));
//        game.getPlayerHand(Orientation.NORTH).gangConcealed(new Tile(Simple.W, 3));
//        game.getPlayerHand(Orientation.NORTH).peng(new Tile(Simple.T, 1));
//        game.getPlayerHand(Orientation.NORTH).discardTile(new Tile(Simple.W, 1));

        contentPane.add(game, BorderLayout.CENTER);
        contentPane.add(controlPanel, BorderLayout.SOUTH);
    }

    public Game getGame() {
        return game;
    }
}
