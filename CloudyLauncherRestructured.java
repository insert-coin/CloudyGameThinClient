import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CloudyLauncherRestructured extends Application {

    private CloudyLauncherServerInterface server;
    private CloudyLauncherJsonParser parser = CloudyLauncherJsonParser.getParser();
    private String token;

    @FXML private BorderPane mainContent;
    @FXML private Pagination pagination;

    @FXML private TextField email;
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private Text feedback;

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
        String serverFeedback = server.postAuthenticationRequest(username.getText(),
                                                                 password.getText());
        feedback.setText(serverFeedback);
        String error = server.getErrorResponse();
        String response = server.getServerResponse();

        if (serverFeedback.equals(CloudyLauncherServerInterface.ERROR_CONNECTION)) {
        } else if (!error.isEmpty()) {
            feedback.setText(parser.parseErrorResponse(error));
        } else {
            token = parser.parseToken(response);
            setGameDisplayPage();
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        String serverFeedback = server.postSignupRequest(username.getText(),
                                                         password.getText(),
                                                         email.getText());

        feedback.setText(serverFeedback);
        String error = server.getErrorResponse();

        if (serverFeedback.equals(CloudyLauncherServerInterface.ERROR_CONNECTION)) {
        } else if (!error.isEmpty()) {
            feedback.setText(parser.parseErrorResponse(error));
        } else {
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("logout");
    }

    @FXML
    private void displayAllGames(ActionEvent event) {
        System.out.println("display all games");
    }

    @FXML
    private void displayMyCollection(ActionEvent event) {
        System.out.println("display my collection");
    }

    @FXML
    private void refreshGameList(ActionEvent event) {
        System.out.println("refresh");
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

    private void setGameDisplayPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("design/GameDisplay.fxml"));
            loader.setController(this);
            mainContent.setCenter(loader.load());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(String.format(ERROR_FXML, "game display"));
        }
    }

    @FXML
    private void setSettingsPage(ActionEvent event) {
        System.out.println("settings");
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
