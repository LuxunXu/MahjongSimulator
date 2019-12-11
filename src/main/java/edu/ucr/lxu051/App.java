package edu.ucr.lxu051;

import edu.ucr.lxu051.UI.AddPane;
import edu.ucr.lxu051.UI.ConfirmBox;
import edu.ucr.lxu051.Util.Hand;
import edu.ucr.lxu051.Util.HandUtil;
import edu.ucr.lxu051.Util.Simple;
import edu.ucr.lxu051.Util.Tile;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App extends Application {

    private Stage window;

    public static void main( String[] args ) throws IOException {
        launch(args);

//        Hand hand2 = new Hand("WEST");
//        hand2.initHand("5W 5W 5W 6W 7W 7W 7W 8B 9B 7B 3T 4T 2T");
//        System.out.println(hand2);
//        System.out.println(hand2.isReady());

//        HandUtil handUtil = new HandUtil("1133");
//        System.out.println(handUtil.reduce());

//        Game game = new Game();
//        game.initGame();
//        System.out.println(game.getPlayerHand(0));
//        System.out.println(game.getPlayerHand(1));
//        System.out.println(game.getPlayerHand(2));
//        System.out.println(game.getPlayerHand(3));



    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        BorderPane border = new BorderPane();

        HBox masterControl = AddPane.addMasterControl();
        border.setTop(masterControl);

        GridPane mainFrame = AddPane.addGridPane();
        border.setCenter(mainFrame);

        Scene scene = new Scene(border, 1280, 960);
        window.setScene(scene);
        window.setTitle("Mahjong Simulator");
        window.show();
    }

}
