
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

public class CloudyLauncherRestructured extends Application {

    @FXML
    protected void handleSignIn(ActionEvent event) {
        System.out.println("handle sign in");
    }

    @FXML
    protected void handleLogin(ActionEvent event) {
        System.out.println("handle login");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("CloudyLauncher.fxml"));
        Scene scene = new Scene(root, 300, 275);

        primaryStage.setTitle("CloudyLauncher");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
