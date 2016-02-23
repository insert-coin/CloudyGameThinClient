
import java.io.IOException;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CloudyLauncherRestructured extends Application {

    private VBox rootLayout;
    private Node manageUserPanel;
    private VBox gameDisplayLayout;
    private Node gameInfo;

    @FXML TilePane gameRoot;

    @FXML
    protected void handleSignIn(ActionEvent event) {
        System.out.println("handle sign in");
    }

    @FXML
    protected void handleLogin(ActionEvent event) {
        System.out.println("handle login");
    }

    @FXML
    protected void handleJoinGame(ActionEvent event) {
        System.out.println("handle join game");
    }

    @FXML
    protected void handleLogOut(ActionEvent event) {
        System.out.println("handle logout");
    }

    void initialise() {
        try {
            rootLayout = (VBox) FXMLLoader.load(getClass().getResource("CloudyLauncher.fxml"));
            ObservableList<Node> rChildren = rootLayout.getChildren();
            System.out.println(rChildren);
            manageUserPanel = rChildren.get(0);
            gameDisplayLayout = (VBox) rChildren.get(1);
            gameInfo = rChildren.get(2);

            rChildren.remove(1, rChildren.size());

        } catch (IOException e) {
            System.out.println("Error in loading fxml file");
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        initialise();
        Scene scene = new Scene(rootLayout, 300, 275);

        primaryStage.setTitle("CloudyLauncher");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
