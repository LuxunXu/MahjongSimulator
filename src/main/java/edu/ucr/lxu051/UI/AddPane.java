package edu.ucr.lxu051.UI;

import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class AddPane {

    public static HBox addMasterControl() {

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");

        Button buttonNewGame = new Button("New Game");
        buttonNewGame.setPrefSize(100, 20);
        buttonNewGame.setOnAction(e -> {
            ConfirmBox.display("Are you sure?", "Are you sure to start a new game?");
        });

        Button buttonProjected = new Button("Last Step");
        buttonProjected.setPrefSize(100, 20);

        hbox.getChildren().addAll(buttonNewGame, buttonProjected);

        return hbox;
    }

    public static GridPane addGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        HBox hBox = new HBox();
        ImageView imageHouse = new ImageView(
            new Image(AddPane.class.getResourceAsStream("/Pic/Export/Regular/Man4.png"), 60, 80, true, true));
        hBox.getChildren().add(addBorder(imageHouse));
        grid.add(hBox,1, 0);

        return grid;
    }

    private static HBox addBorder(ImageView imageView) {
        HBox hBox = new HBox();
        String style = "-fx-border-color: black;"
                    + "-fx-border-radius: 5 5 5 5;"
                    + "-fx-border-width: 2;";
        hBox.setStyle(style);
        hBox.getChildren().add(imageView);
        return hBox;
    }
}
