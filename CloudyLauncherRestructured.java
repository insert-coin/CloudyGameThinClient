
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CloudyLauncherRestructured extends Application {

    @FXML private VBox rootLayout;
    @FXML private TabPane manageUserPanel;
    @FXML private VBox gameDisplayLayout;
    @FXML private Node gameInfo;

    @FXML private TextField signupEmail;
    @FXML private TextField signupFirstName;
    @FXML private TextField signupLastName;
    @FXML private TextField signupUsername;
    @FXML private Text signupFeedback;

    @FXML private TextField signupPassword;
    @FXML private TextField loginUsername;
    @FXML private TextField loginPassword;
    @FXML private Text loginFeedback;

    @FXML private TilePane gameRoot;

    private String baseurl = "http://127.0.0.1:8000";
    private String token = "";
    private String feedback = "";
    
    @FXML
    protected void handleSignUp(ActionEvent event) {
        attemptUserRegistration(signupUsername.getText(),
                                signupPassword.getText(),
                                signupEmail.getText(),
                                signupFirstName.getText(),
                                signupLastName.getText());
        signupFeedback.setText(feedback);
    }

    @FXML
    protected void handleLogin(ActionEvent event) {
        attemptAuthentication(loginUsername.getText(), loginPassword.getText());
        loginFeedback.setText(feedback);
    }

    @FXML
    protected void handleJoinGame(ActionEvent event) {
        System.out.println("handle join game");
    }

    @FXML
    protected void handleLogOut(ActionEvent event) {
        System.out.println("handle logout");
    }

    private void setToken(String newToken) {
        token = newToken;
    }

    private void setFeedback(String newFeedback) {
        feedback = newFeedback;
    }

    private void attemptAuthentication(String username, String password) {

        try {
            URL url = new URL(baseurl + "/api-token-auth/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            String queryData = String.format("username=%s&password=%s", username, password);

            connection.setDoOutput(true);
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());

            writer.writeBytes(queryData);
            writer.flush();
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                setErrorMessageFromConnection(connection);

            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.readLine();
                reader.close();

                JSONObject tokenObj = new JSONObject(response);
                String responseToken = tokenObj.getString("token");
                setToken(responseToken);

                setFeedback("User recognised.");

            } else{
                setFeedback(connection.getHeaderField(0));
            }

        } catch (IOException e) {
            setFeedback("Check connection to server.");
        }
    }

    private void attemptUserRegistration(String username, String password,
                                         String email, String firstName,
                                         String lastName) {

        try {
            URL url = new URL(baseurl + "/users/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            String queryData = String.format("username=%s&password=%s&email=%s&first_name=%s&last_name=%s",
                                             username, password, email,
                                             firstName, lastName);

            connection.setDoOutput(true);
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());

            writer.writeBytes(queryData);
            writer.flush();
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                setErrorMessageFromConnection(connection);

            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED){
                setFeedback("User successfully registered.");

            } else {
                setFeedback(connection.getHeaderField(0));
            }

        } catch (IOException e) {
            setFeedback("Check connection to server.");
        }
    }

    private void setErrorMessageFromConnection(HttpURLConnection connection) {
        String errorMessage = "";

        BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        JSONObject errorResponse;
        try {
            errorResponse = new JSONObject(errorReader.readLine());

            if (errorResponse.has("username")) {
                errorMessage = errorMessage
                               + "\nUsername: "
                               + errorResponse.getJSONArray("username")
                                              .getString(0);
            }

            if (errorResponse.has("password")) {
                errorMessage = errorMessage
                               + "\nPassword: "
                               + errorResponse.getJSONArray("password")
                                              .getString(0);
            }

            if (errorResponse.has("non_field_errors")) {
                errorMessage = errorMessage
                               + "\n"
                               + errorResponse.getJSONArray("non_field_errors")
                                              .getString(0);
            }

            if (errorResponse.has("email")) {
                errorMessage = errorMessage
                               + "\nEmail: "
                               + errorResponse.getJSONArray("email")
                                              .getString(0);
            }

            if (errorResponse.has("first_name")) {
                errorMessage = errorMessage
                               + "\nFirst Name: "
                               + errorResponse.getJSONArray("first_name")
                                              .getString(0);
            }

            if (errorResponse.has("last_name")) {
                errorMessage = errorMessage
                               + "\nLast Name: "
                               + errorResponse.getJSONArray("last_name")
                                              .getString(0);
            }

            if (errorResponse.has("detail")) {
                errorMessage = errorResponse.getJSONArray("username")
                                            .getString(0);
            }

            if (errorResponse.has("game")) {
                errorMessage = errorMessage
                               + "\nGame: "
                               + errorResponse.getJSONArray("game")
                                              .getString(0);
            }

            if (errorResponse.has("controller")) {
                errorMessage = errorMessage
                               + "\nController: "
                               + errorResponse.getJSONArray("controller")
                                              .getString(0);
            }

            if (errorResponse.has("player")) {
                errorMessage = errorMessage
                               + "\nPlayer: "
                               + errorResponse.getJSONArray("player")
                                              .getString(0);
            }

            setFeedback(errorMessage);
            errorReader.close();

        } catch (JSONException e) {
            setFeedback("Error parsing the response from server");

        } catch (IOException e) {
            setFeedback("Error accessing the error stream");
        }
    }

    void initialise() {
        try {
            rootLayout = (VBox) FXMLLoader.load(getClass().getResource("CloudyLauncher.fxml"));
            ObservableList<Node> rChildren = rootLayout.getChildren();

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
