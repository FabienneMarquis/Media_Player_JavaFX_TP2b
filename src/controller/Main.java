package controller;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
    @FXML
    private GridPane root;

    @FXML
    private Scene scene;

    @FXML
    private HBox hboxEverything;

    @FXML
    private VBox vboxInfo;


    @Override
    public void start(Stage primaryStage) throws Exception  {

        String path = "/vue/vueMusique.fxml";
        System.out.print(getClass().getResource("/Super Neko World.mp3"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(path));
        root = fxmlLoader.load();
        scene = new Scene(root);

        primaryStage.setTitle("Cours multimédia TP2 - Musique - Fabienne Marquis");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest((WindowEvent event)->
                ((Controller_musique)fxmlLoader.getController()).quit()
        );

    }


    public static void main(String[] args) {
        launch(args);
    }
}
