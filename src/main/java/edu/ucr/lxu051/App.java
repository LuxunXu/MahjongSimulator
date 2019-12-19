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
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                game = new Game(SCALE);
                game.initGame();
//                game.setSize(64 * SCALE, 64 * SCALE);
            }
        });
        JButton goButton = new JButton("Go");
        goButton.addActionListener(new ActionListener() {
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
        controlPanel.add(newGameButton);
        controlPanel.add(goButton);

        game = new Game(SCALE);
//        game.initGame();
        game.setSize(64 * SCALE, 64 * SCALE);

        contentPane.add(game, BorderLayout.CENTER);
        contentPane.add(controlPanel, BorderLayout.SOUTH);
    }

    public Game getGame() {
        return game;
    }
}
