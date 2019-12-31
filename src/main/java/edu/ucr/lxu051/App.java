package edu.ucr.lxu051;

import edu.ucr.lxu051.Util.Hand;
import edu.ucr.lxu051.Util.Orientation;
import edu.ucr.lxu051.Util.Simple;
import edu.ucr.lxu051.Util.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class App extends JFrame {

    private final int SCALE = 13;
    private Game game;
    private JPanel contentPane;

    public static void main( String[] args ) throws IOException {

//        Hand hand2 = new Hand(Orientation.WEST);
//        hand2.initHand("1B 3B 3B 8B 8B 1W 1W");
//        System.out.println(hand2);
//        System.out.println(hand2.isReady());

//        HandUtil handUtil = new HandUtil("1133");
//        System.out.println(handUtil.reduce());

//        Game testGame = new Game(13, 5);
//        testGame.initGame();
//        testGame.autoExecute();

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
                game.initGame();
            }
        });
        JButton goButton = new JButton("Go");
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    game.autoExecute();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        controlPanel.add(newGameButton);
        controlPanel.add(goButton);

        game = new Game(SCALE,1577802091939L);
        game.setSize(64 * SCALE, 64 * SCALE);

        contentPane.add(game, BorderLayout.CENTER);
        contentPane.add(controlPanel, BorderLayout.SOUTH);
    }

    public Game getGame() {
        return game;
    }
}
