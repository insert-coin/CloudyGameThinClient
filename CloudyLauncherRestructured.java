
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CloudyLauncherRestructured extends Application {

    @FXML private VBox rootLayout;
    @FXML private TabPane manageUserPanel;
    @FXML private VBox gameDisplayLayout;
    @FXML private Text gameInfo;

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
    @FXML private VBox gameInfoPanel;

    private String baseurl = "http://127.0.0.1:8000";
    private String token = "";
    private String feedback = "";
    private List<Game> listOfGames = new ArrayList<Game>();
    
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
        resetAllValues();
    }

    private void handleDisplayGameInfo(MouseEvent event) {
        if (rootLayout.getChildren().contains(gameInfoPanel)) {
            gameInfo.setText("");
        } else {
            rootLayout.getChildren().add(gameInfoPanel);
        }

//        ImageView selectedIcon = (ImageView) event.getTarget();
        Rectangle selectedIcon = (Rectangle) event.getTarget();
        Game selectedGame = (Game) selectedIcon.getUserData();

        String baseGameInfo = "Name: %s\nPublisher: %s\nMaximum number of players: %s\nAvailability: %s";
        gameInfo.setText(String.format(baseGameInfo, selectedGame.getName(),
                                       selectedGame.getPublisher(),
                                       selectedGame.getLimit(), "N.A."));
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

                initialiseGameDisplayPanel();

            } else {
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

    private Game getGameFromJson(JSONObject gameObj) {

        try {
            String gId = Integer.toString(gameObj.getInt("id"));
            String gName = gameObj.getString("name");
            String gPublisher = gameObj.getString("publisher");
            String gLimit = Integer.toString(gameObj.getInt("max_limit"));
            String gAddress = gameObj.getString("address");

            JSONArray users = gameObj.getJSONArray("users");
            List<String> gUsers = new ArrayList<String>();

            for (int i = 0; i < users.length(); i++) {
                String uUsername = users.getString(i);
                gUsers.add(uUsername);
            }

            Game newGame = new Game(gId, gName, gPublisher,
                                    Integer.parseInt(gLimit), gAddress, gUsers);
            return newGame;

        } catch (JSONException e) {
            gameInfo.setText("Error in parsing game information");

        }
        return null;
    }

    private void initialiseGameList() {
        try {
            URL url = new URL(baseurl + "/games/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            String auth = "Token " + token;

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", auth);
            connection.setRequestProperty("Accept", "application/json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.readLine();
            reader.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                // if login (and token retrieval) is successful, should never reach here.

                setErrorMessageFromConnection(connection);

            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                setFeedback("token recognised.");

                JSONArray gameListString = new JSONArray(response);

                for (int i = 0; i < gameListString.length(); i++) {
                    JSONObject gameJSON = gameListString.getJSONObject(i);
                    Game game = getGameFromJson(gameJSON);
                    listOfGames.add(game);
                }

            } else {
                setFeedback(connection.getHeaderField(0));
            }

        } catch (IOException e) {
            setFeedback("Check connection to server.");
        }
    }

    private void addGamesToDisplayList() {
        for (Game game : listOfGames) {
//            ImageView gameIcon = new ImageView("pix.jpg");
//            gameIcon.setFitHeight(100);
//            gameIcon.setFitWidth(100);

            Rectangle gameIcon = new Rectangle(100, 100);
            gameIcon.setUserData(game);
            gameIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    handleDisplayGameInfo(event);
                }
            });

            gameRoot.getChildren().add(gameIcon);
        }
    }

    private void initialiseGameDisplayPanel() {

        initialiseGameList();
        addGamesToDisplayList();

        rootLayout.getChildren().remove(manageUserPanel);
        rootLayout.getChildren().add(gameDisplayLayout);
    }

    private void resetAllValues() {

        token = "";
        feedback = "";
        listOfGames.clear();

        signupEmail.clear();
        signupFirstName.clear();
        signupLastName.clear();
        signupUsername.clear();
        signupPassword.clear();
        signupFeedback.setText("");

        loginUsername.clear();
        loginPassword.clear();
        loginFeedback.setText("");

        gameInfo.setText("");
        gameRoot.getChildren().clear();
        rootLayout.getChildren().clear();
        rootLayout.getChildren().add(manageUserPanel);
    }

    private void initialise() {
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
