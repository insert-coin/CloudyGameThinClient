import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class CloudyLauncherRestructured extends Application {
    private CloudyLauncherServerInterface server;

    @FXML private BorderPane mainContent;

    final private String ERROR_FXML = "Error in loading %s page";

    public CloudyLauncherRestructured() {
        try {

            FileInputStream configFile = new FileInputStream("settings/config.xml");
            Properties props = new Properties();
            props.loadFromXML(configFile);
            configFile.close();

            String url = "http://%s:%s/";
            String serverIp = props.getProperty("serverIp");
            String serverPort = props.getProperty("serverPort");
            server = new CloudyLauncherServerInterface(String.format(url,
                                                                     serverIp,
                                                                     serverPort));

        } catch (IOException e) {
            System.out.println(String.format(ERROR_FXML, "config"));
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        System.out.println("handle login");
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        System.out.println("handle signup");

    }

    @FXML
    private void setSignupPage(MouseEvent event) throws IOException {
        try {
            FXMLLoader vloader = new FXMLLoader(getClass().getResource("design/Signup.fxml"));
            vloader.setController(this);
            mainContent.setCenter(vloader.load());

        } catch (IOException e) {
            System.out.println(String.format(ERROR_FXML, "signup"));
        }
    }

    @FXML
    private void setLoginPage(MouseEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("design/Login.fxml"));
            loader.setController(this);
            mainContent.setCenter(loader.load());

        } catch (IOException e) {
            System.out.println(String.format(ERROR_FXML, "login"));
        }
    }

    private void initialiseLauncher(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("design/CL.fxml"));
            loader.setController(this);
            stage = loader.load();

            loader = new FXMLLoader(getClass().getResource("design/Login.fxml"));
            loader.setController(this);
            mainContent.setCenter(loader.load());

            stage.show();
        } catch (IOException e) {
            System.out.println(String.format(ERROR_FXML, "login"));
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        CloudyLauncherRestructured cl = new CloudyLauncherRestructured();
        cl.initialiseLauncher(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
