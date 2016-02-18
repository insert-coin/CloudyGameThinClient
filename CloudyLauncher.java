
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CloudyLauncher extends Application {

    private String baseurl = "http://127.0.0.1:8000";
    private String token = "";
    private String feedback = "";
    private List<Game> listOfGames = new ArrayList<Game>();
    private BorderPane rootBorder = new BorderPane();

    private void setToken(String newToken) {
        token = newToken;
    }

    private void setFeedback(String newFeedback) {
        feedback = newFeedback;
    }

    private void addLoginTab(TabPane parent) {

        GridPane loginInfo = new GridPane();
        loginInfo.setId("login-panel");

        Label username = new Label("Username");
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Enter username");
        Label password = new Label("Password");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Enter password");

        Text feedbackMessage = new Text();
        feedbackMessage.setId("login-feedback");
        Button loginButton = new Button("Login");
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                attemptAuthentication(usernameInput.getText(),
                        passwordInput.getText());

                feedbackMessage.setText(feedback);
            }
        });

        loginInfo.add(username, 0, 1);
        loginInfo.add(usernameInput, 1, 1);
        loginInfo.add(password, 0, 2);
        loginInfo.add(passwordInput, 1, 2);
        loginInfo.add(loginButton, 0, 3);
        loginInfo.add(feedbackMessage, 1, 3);

        Tab loginTab = new Tab("Login");
        loginTab.setClosable(false);
        parent.getTabs().add(loginTab);
        loginTab.setContent(loginInfo);
    }

    private void addSignupTab(TabPane parent) {

        GridPane loginInfo = new GridPane();
        loginInfo.setId("signup-panel");

        Label firstName = new Label("First Name");
        TextField firstNameInput = new TextField();
        firstNameInput.setPromptText("Enter first name");
        Label lastName = new Label("Last Name");
        TextField lastNameInput = new TextField();
        lastNameInput.setPromptText("Enter last name");
        Label username = new Label("Username");
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Enter username");
        Label email = new Label("Email");
        TextField emailInput = new TextField();
        emailInput.setPromptText("Enter email");
        Label password = new Label("Password");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Enter password");

        Text feedbackMessage = new Text();
        feedbackMessage.setId("signup-feedback");
        Button signupButton = new Button("Sign Up");
        signupButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                attemptUserRegistration(usernameInput.getText(),
                        passwordInput.getText(),
                        emailInput.getText(),
                        firstNameInput.getText(),
                        lastNameInput.getText());

                feedbackMessage.setText(feedback);
            }
        });

        loginInfo.add(firstName, 0, 0);
        loginInfo.add(firstNameInput, 1, 0);
        loginInfo.add(lastName, 0, 1);
        loginInfo.add(lastNameInput, 1, 1);
        loginInfo.add(email, 0, 2);
        loginInfo.add(emailInput, 1, 2);
        loginInfo.add(username, 0, 3);
        loginInfo.add(usernameInput, 1, 3);
        loginInfo.add(password, 0, 4);
        loginInfo.add(passwordInput, 1, 4);
        loginInfo.add(signupButton, 0, 5);
        loginInfo.add(feedbackMessage, 1, 5);

        Tab signupTab = new Tab("Sign Up");
        signupTab.setClosable(false);
        parent.getTabs().add(signupTab);
        signupTab.setContent(loginInfo);

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

            } else {

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));        
                String response = reader.readLine();
                reader.close();

                JSONObject tokenObj = new JSONObject(response);
                String responseToken = tokenObj.getString("token");                
                setToken(responseToken);

                setFeedback("User recognised.");

                initialiseGamePanel();
            }   

        } catch (IOException e) {
            setFeedback("Check server.");

        }
    }

    private void setErrorMessageFromConnection(HttpURLConnection connection) {
        String errorMessage = "";

        BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        JSONObject errorResponse;
        try {
            errorResponse = new JSONObject(errorReader.readLine());

            if (errorResponse.has("username")) {
                errorMessage = errorMessage + "\nUsername: " + errorResponse.getJSONArray("username").getString(0);
            }

            if (errorResponse.has("password")) {
                errorMessage = errorMessage + "\nPassword: " + errorResponse.getJSONArray("password").getString(0);
            }

            if (errorResponse.has("non_field_errors")) {
                errorMessage = errorMessage + "\n" + errorResponse.getJSONArray("non_field_errors").getString(0);
            }

            if (errorResponse.has("email")) {
                errorMessage = errorMessage + "\nEmail: " + errorResponse.getJSONArray("email").getString(0); 
            } 

            if (errorResponse.has("first_name")) {
                errorMessage = errorMessage + "\nFirst Name: " + errorResponse.getJSONArray("first_name").getString(0); 
            }

            if (errorResponse.has("last_name")) {
                errorMessage = errorMessage + "\nLast Name: " + errorResponse.getJSONArray("last_name").getString(0); 
            }

            if (errorResponse.has("detail")) {
                errorMessage = errorResponse.getJSONArray("username").getString(0);
            }

            if (errorResponse.has("game")) {
                errorMessage = errorMessage + "\nGame: " + errorResponse.getJSONArray("game").getString(0);
            }

            if (errorResponse.has("controller")) {
                errorMessage = errorMessage + "\nController: " + errorResponse.getJSONArray("controller").getString(0); 
            }

            if (errorResponse.has("player")) {
                errorMessage = errorMessage + "\nPlayer: " + errorResponse.getJSONArray("player").getString(0); 
            }

            setFeedback(errorMessage);
            errorReader.close();

        } catch (JSONException e) {
            setFeedback("Error parsing the response from server");

        } catch (IOException e) {
            setFeedback("Error accessing the error stream");
        }
    }

    private void attemptUserRegistration(String username, String password, String email, String firstName, String lastName) {

        try {
            URL url = new URL(baseurl + "/users/");        
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            String queryData = String.format("username=%s&password=%s&email=%s&first_name=%s&last_name=%s", username, password, email, firstName, lastName);

            connection.setDoOutput(true);
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());

            writer.writeBytes(queryData);
            writer.flush();
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {

                setErrorMessageFromConnection(connection);

            } else {

                setFeedback("User successfully registered.");
            }                

        } catch (IOException e) {
            setFeedback("Check server.");
        }
    }

    /**
     * Returns the Game object containing the given information.
     * 
     * @param gameObj  JSONObject containing game information
     * @return         Game object with information 
     */    
    private Game getGameFromJson(JSONObject gameObj) {

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

        Game newGame = new Game(gId, gName, gPublisher, Integer.parseInt(gLimit), gAddress, gUsers);
        return newGame;
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

            } else {
                setFeedback("token recognised.");

                JSONArray gameListString = new JSONArray(response);

                for (int i = 0; i < gameListString.length(); i++) {
                    JSONObject game = gameListString.getJSONObject(i);
                    listOfGames.add(getGameFromJson(game));
                }
            }

        } catch (IOException e) {
            setFeedback("Check server.");
        }
    }

    private String getControllerId(Game gameToJoin) {
        try {

            URL url = new URL(baseurl + "/game-session/");        
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            String auth = "Token " + token;

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", auth);
            connection.setRequestProperty("Accept", "application/json");
            String queryData = String.format("game=%s", gameToJoin.getId());            

            connection.setDoOutput(true);
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());

            writer.writeBytes(queryData);
            writer.flush();
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                // if system is working properly, should not reach here   

                setErrorMessageFromConnection(connection);

            } else {

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));        
                String response = reader.readLine();
                reader.close();

                JSONObject gameSession = new JSONObject(response);
                String controllerId = gameSession.getString("controller");

                return controllerId;
            }

        } catch (IOException e) {
            setFeedback("Check server.");
        }

        return null;
    }

    private void joinGame(Game selectedGame) {

        // controllerId is to be sent from the api,  
        // used to launch the thin_client
        // to be changed: api is not yet updated, value not correct 
        String controllerId = getControllerId(selectedGame);

        try {
            // launch the thin_client. 
            // to be changed: thin_client should receive the controllerId as input. 
            // default action: thin client assumes controllerId = 0
            Runtime.getRuntime().exec("python thin_client.py");

        } catch (IOException e) {
            setFeedback("Error joining game");
        }
    }

    private void initialiseGamePanel() {

        initialiseGameList();

        TilePane gameRoot = new TilePane();

        for (Game game : listOfGames) {
            //ImageView gameIcon = new ImageView("MeikyuuButterfly.jpg");
            //gameIcon.setFitHeight(100);
            //gameIcon.setFitWidth(100);

            Rectangle gameIcon = new Rectangle(100, 100);
            gameIcon.setUserData(game);

            gameIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    //ImageView selectedTile = (ImageView) event.getTarget();
                    Rectangle selectedTile = (Rectangle) event.getTarget();
                    Game selectedGame = (Game) selectedTile.getUserData();

                    VBox infoPanel = new VBox();

                    String baseGameInfo = "Name: %s\nPublisher: %s\nMaximum number of players: %s\nAvailability: %s";
                    Text gameInfo = new Text(
                            String.format(baseGameInfo, selectedGame.getName(), selectedGame.getPublisher(), selectedGame.getLimit(), "N.A."));
                    Text feedbackMessage = new Text("");
                    feedbackMessage.setId("join-feedback");
                    Button joinGameBtn = new Button("Join Game");
                    joinGameBtn.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                            joinGame(selectedGame);   

                            feedbackMessage.setText(feedback);
                        }
                    });

                    infoPanel.getChildren().add(gameInfo);
                    infoPanel.getChildren().add(feedbackMessage);
                    infoPanel.getChildren().add(joinGameBtn);

                    rootBorder.setBottom(infoPanel);
                }
            });
            gameRoot.getChildren().add(gameIcon);
        }

        gameRoot.setHgap(10);
        gameRoot.setVgap(10);
        gameRoot.setAlignment(Pos.CENTER);
        gameRoot.setPrefColumns(6);

        Button logoutButton = new Button("Log out");
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                initialiseLauncher();
            }
        });

        rootBorder.setTop(logoutButton);
        rootBorder.setCenter(gameRoot);
        rootBorder.setLeft(null);
    }

    private void initialiseLoginPanel() {
        TabPane userRoot = new TabPane();

        addLoginTab(userRoot);
        addSignupTab(userRoot);

        rootBorder.setLeft(userRoot);
    }

    private void initialiseLauncher() {
        rootBorder.setTop(null);
        rootBorder.setBottom(null);
        rootBorder.setLeft(null);
        rootBorder.setRight(null);
        rootBorder.setCenter(null);

        setFeedback("");
        setToken("");
        listOfGames.clear();
        initialiseLoginPanel();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        initialiseLauncher();

//      launcherScene.getStylesheets().add(CloudyLauncher.class.getResource("style.css").toExternalForm());        
        Scene launcherScene = new Scene(rootBorder, 500, 500);
        primaryStage.setScene(launcherScene);
        primaryStage.setTitle("CloudyLauncher");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
