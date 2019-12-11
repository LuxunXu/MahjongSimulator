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

        // Category in column 2, row 1
        Text category = new Text("Sales:");
        category.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        grid.add(category, 1, 0);

        // Title in column 3, row 1
        Text chartTitle = new Text("Current Year");
        chartTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        grid.add(chartTitle, 2, 0);

        // Subtitle in columns 2-3, row 2
        Text chartSubtitle = new Text("Goods and Services");
        grid.add(chartSubtitle, 1, 1, 2, 1);

        // House icon in column 1, rows 1-2
        ImageView imageHouse = new ImageView(
          new Image(AddPane.class.getResourceAsStream("/Pic/Export/Black/Man4.png"), 300, 400, true, true));
        grid.add(addBoarder(imageHouse), 0, 0, 1, 2);

        // Left label in column 1 (bottom), row 3
        Text goodsPercent = new Text("Goods\n80%");
        GridPane.setValignment(goodsPercent, VPos.BOTTOM);
        grid.add(goodsPercent, 0, 2);

        // Chart in columns 2-3, row 3
        ImageView imageChart = new ImageView(
         new Image(AddPane.class.getResourceAsStream("/Pic/Export/Regular/Man4.png"), 60, 80, true, true));
        grid.add(addBoarder(imageChart), 1, 2, 1, 1);

        // Right label in column 4 (top), row 3
        Text servicesPercent = new Text("Services\n20%");
        GridPane.setValignment(servicesPercent, VPos.TOP);
        grid.add(servicesPercent, 3, 2);

        return grid;
    }

    private static HBox addBoarder(ImageView imageView) {
        HBox hBox = new HBox();
        String style = "-fx-border-color: black;"
                    + "-fx-border-radius: 5 5 5 5;"
                    + "-fx-border-width: 2;";
        hBox.setStyle(style);
        hBox.getChildren().add(imageView);
        return hBox;
    }
}
