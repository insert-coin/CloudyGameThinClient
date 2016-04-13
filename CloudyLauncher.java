import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.FlatColorBackgroundProducer;

public class CloudyLauncher extends Application {

    private CloudyLauncherServerInterface server;
    private CloudyLauncherJsonParser parser = CloudyLauncherJsonParser.getParser();
    private Stage currentStage;

    private int captchaResult;
    private int numOfIncorrectLogins = 0;
    private String token;
    private List<Game> listOfGames = new ArrayList<Game>();
    private List<Game> listOfOwnedGames = new ArrayList<Game>();
    private Game selectedGame;

    @FXML private Text date;
    @FXML private Text time;
    @FXML private Label status;
    @FXML private BorderPane mainContent;
    @FXML private Pagination pagination;
    @FXML private VBox gameInfoPanel;
    @FXML private VBox captchaBox;

    @FXML private Button mainButton;
    @FXML private Captcha captcha;
    @FXML private VBox captchaLabel;
    @FXML private ImageView captchaImage;
    @FXML private TextField captchaInput;
    @FXML private TextField email;
    @FXML private TextField username;
    @FXML private TextField password;

    @FXML private Text captchaFeedback;
    @FXML private Text usernameFeedback;
    @FXML private Text passwordFeedback;
    @FXML private Text emailFeedback;
    @FXML private Text accountsFeedback;

    @FXML private Text welcomeText;
    @FXML private Text gameTitle;
    @FXML private ImageView gameImage;
    @FXML private Text gameTextInformation;
    @FXML private Button gameButton;
    @FXML private Text gameFeedback;
    @FXML private Text settingsFeedback;
    @FXML private Button settingsButton;

    final private String CLOCK_DATE_PATTERN = "dd MMMM y";
    final private String CLOCK_TIME_PATTERN = "kk : mm : ss";
    final private String STATUS_ONLINE = "Online";
    final private String STATUS_OFFLINE = "Offline";
    final private String WELCOME_TEXT = "Welcome, %s";

    final private String PATH_LAUNCHER_BASE = "design/CL.fxml";
    final private String PATH_LAUNCHER_BASE_ACCOUNTS = "design/CL_accounts.fxml";
    final private String PATH_LAUNCHER_BASE_GAME = "design/CL_game.fxml";
    final private String PATH_ACCOUNTS_LOGIN = "design/Accounts_login.fxml";
    final private String PATH_ACCOUNTS_SIGNUP = "design/Accounts_signup.fxml";
    final private String PATH_GAME_DISPLAY_BASE = "design/GameDisplay.fxml";
    final private String PATH_GAME_DISPLAY_INITIAL = "design/GameDisplay_initial.fxml";
    final private String PATH_GAME_DISPLAY_GAME = "design/GameDisplay_game.fxml";
    final private String PATH_SETTINGS = "design/CL_settings.fxml";

    final private String ERROR_CAPTCHA_EMPTY = "Enter Captcha";
    final private String ERROR_CAPTCHA_INCORRECT = "Unrecognised Captcha";
    final private String ERROR_FXML_CONFIG = "ERROR: Cannot load settings/config.xml";
    final private String ERROR_FXML_SIGNUP = "ERROR: Cannot load sign up page";
    final private String ERROR_FXML_LOGIN = "ERROR: Cannot load login page";
    final private String ERROR_FXML_GAME_DISPLAY = "ERROR: Cannot load game display";
    final private String ERROR_FXML_SETTINGS = "ERROR: Cannot load settings";
    final private String ERROR_GAME_JOIN = "ERROR: Cannot join game";

    final private String URL_OWNED_BADGE = "images/orangeribbon.png";
    final private String GAME_INFORMATION = "Publisher: %s\n\n%s";

    final private String COMMAND_RUN_THINCLIENT = "python thin_client/main.py --session %s %s %s %s";

    final private Integer GAME_DISPLAY_WELCOME = 0;
    final private Integer GAME_DISPLAY_GAME_INFO = 1;
    final private Integer PAGINATION_ALL_GAMES = 2;
    final private Integer PAGINATION_MY_GAMES = 3;
    final private Integer CAPTCHA_CORRECT = 4;
    final private Integer CAPTCHA_EMPTY = 5;
    final private Integer CAPTCHA_INCORRECT = 6;
    final private Integer TILES_PER_PAGE = 12;

    private void setupClock() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(CLOCK_DATE_PATTERN,
                                                           Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat(CLOCK_TIME_PATTERN,
                                                           Locale.US);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1),
                new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    final Calendar cal = Calendar.getInstance();
                    date.setText(dateFormat.format(cal.getTime()));
                    time.setText(timeFormat.format(cal.getTime()));
                }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void setupStatus() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5),
                new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {

                    if (server.isOnline()) {
                        status.setText(STATUS_ONLINE);
                        status.getStyleClass().clear();
                        status.getStyleClass().add("status-online");

                    } else {
                        status.setText(STATUS_OFFLINE);
                        status.getStyleClass().clear();
                        status.getStyleClass().add("status-offline");
                    }
                }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void setupServer() {
        server = new CloudyLauncherServerInterface();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        clearFeedback();

        checkCaptchaResult();
        if (captchaResult == CAPTCHA_CORRECT) {
            String serverFeedback = server.postAuthenticationRequest(username.getText(),
                                                                     password.getText());
            accountsFeedback.setText(serverFeedback);
            String error = server.getErrorResponse();
            String response = server.getServerResponse();

            if (serverFeedback.equals(CloudyLauncherServerInterface.ERROR_CONNECTION)) {
            } else if (!error.isEmpty()) {
                Map<CloudyLauncherJsonParser.ErrorHeaders, String> errorStrings = parser.parseRespectiveErrors(error);

                if (errorStrings.isEmpty()) {
                    accountsFeedback.setText(CloudyLauncherJsonParser.ERROR_PARSER_FEEDBACK);

                } else {
                    usernameFeedback.setText(errorStrings.get(CloudyLauncherJsonParser.ErrorHeaders.USERNAME));
                    passwordFeedback.setText(errorStrings.get(CloudyLauncherJsonParser.ErrorHeaders.PASSWORD));
                    emailFeedback.setText(errorStrings.get(CloudyLauncherJsonParser.ErrorHeaders.EMAIL));
                    accountsFeedback.setText(errorStrings.get(CloudyLauncherJsonParser.ErrorHeaders.OTHERS));

                    if (!accountsFeedback.getText().isEmpty()) {
                        numOfIncorrectLogins++;
                        setCaptchaAsNeeded();
                        clearInput();
                    }
                }
            } else {
                token = parser.parseToken(response);
                setGameDisplayPage();
                numOfIncorrectLogins = 0;
            }
        } else if (captchaResult == CAPTCHA_EMPTY) {
            captchaFeedback.setText(ERROR_CAPTCHA_EMPTY);
        } else {
            refreshCaptcha();
            captchaFeedback.setText(ERROR_CAPTCHA_INCORRECT);
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        clearFeedback();

        checkCaptchaResult();
        if (captchaResult == CAPTCHA_CORRECT) {
            String serverFeedback = server.postSignupRequest(username.getText(),
                                                             password.getText(),
                                                             email.getText());

            accountsFeedback.setText(serverFeedback);
            String error = server.getErrorResponse();

            if (serverFeedback.equals(CloudyLauncherServerInterface.ERROR_CONNECTION)) {
            } else if (!error.isEmpty()) {
                Map<CloudyLauncherJsonParser.ErrorHeaders, String> errorStrings = parser.parseRespectiveErrors(error);

                if (errorStrings.isEmpty()) {
                    accountsFeedback.setText(CloudyLauncherJsonParser.ERROR_PARSER_FEEDBACK);

                } else {
                    usernameFeedback.setText(errorStrings.get(CloudyLauncherJsonParser.ErrorHeaders.USERNAME));
                    passwordFeedback.setText(errorStrings.get(CloudyLauncherJsonParser.ErrorHeaders.PASSWORD));
                    emailFeedback.setText(errorStrings.get(CloudyLauncherJsonParser.ErrorHeaders.EMAIL));
                    accountsFeedback.setText(errorStrings.get(CloudyLauncherJsonParser.ErrorHeaders.OTHERS));
                }
            } else {
            }
        } else if (captchaResult == CAPTCHA_EMPTY) {
            captchaFeedback.setText(ERROR_CAPTCHA_EMPTY);
        } else {
            refreshCaptcha();
            captchaFeedback.setText(ERROR_CAPTCHA_INCORRECT);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        token = "";
        initialiseLauncher();
    }

    @FXML
    private void handleGame(ActionEvent event) {
        if (listOfOwnedGames.contains(selectedGame)) {
            joinGame(selectedGame);
        } else {
            gameFeedback.setText("Obtain game first");
        }
    }

    @FXML
    private void displayAllGames(ActionEvent event) {
        setGamePanel(GAME_DISPLAY_WELCOME);
        setPaginationSettings(PAGINATION_ALL_GAMES);
    }

    @FXML
    private void displayMyCollection(ActionEvent event) {
        setGamePanel(GAME_DISPLAY_WELCOME);
        setPaginationSettings(PAGINATION_MY_GAMES);
    }

    @FXML
    private void setCaptchaAsNeeded() {
        if (numOfIncorrectLogins > 2) {
            captchaLabel.setVisible(true);
            captchaBox.setVisible(true);
            refreshCaptcha();
        } else {
            captchaLabel.setVisible(false);
            captchaBox.setVisible(false);
        }
    }

    @FXML
    private void refreshCaptcha() {
        captchaResult = CAPTCHA_EMPTY;
        Color lightBlue = new Color(0, 105, 166);
        captcha = new Captcha.Builder(200, 40).addText()
                .addBackground(new FlatColorBackgroundProducer(lightBlue))
                .addBorder()
                .addNoise()
                .addNoise()
                .build();

        Image javafxImage = SwingFXUtils.toFXImage(captcha.getImage(), null);
        captchaImage.setImage(javafxImage);
    }

    @FXML
    private void setSignupPage(MouseEvent event) {
        if (!mainButton.isDisabled()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_ACCOUNTS_SIGNUP));
                loader.setController(this);
                mainContent.setCenter(loader.load());

                refreshCaptcha();

            } catch (IOException e) {
                accountsFeedback.setText(ERROR_FXML_SIGNUP);
            }
        }
    }

    @FXML
    private void setSignupPageKeyboard(KeyEvent event) {
        String key = event.getCode().toString();
        if ((key.equals("ENTER")) || (key.equals("SPACE"))) {
            setSignupPage(null);
        }
    }

    @FXML
    private void setLoginPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_ACCOUNTS_LOGIN));
            loader.setController(this);
            mainContent.setCenter(loader.load());

            setCaptchaAsNeeded();

        } catch (IOException e) {
            accountsFeedback.setText(ERROR_FXML_LOGIN);
            mainButton.setDisable(true);
        }
    }

    @FXML
    private void setLoginPageKeyboard(KeyEvent event) {
        String key = event.getCode().toString();
        if ((key.equals("ENTER")) || (key.equals("SPACE"))) {
            setLoginPage(null);
        }
    }

    @FXML
    private void setGameDisplayPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_LAUNCHER_BASE_GAME));
            loader.setController(this);
            currentStage.getScene().setRoot(loader.load());

            loader = new FXMLLoader(getClass().getResource(PATH_GAME_DISPLAY_BASE));
            loader.setController(this);
            mainContent.setCenter(loader.load());

            setGamePanel(GAME_DISPLAY_WELCOME);
            welcomeText.setText(String.format(WELCOME_TEXT, username.getText()));

            clearGamePage();

            initialiseGameList();
            initialiseOwnedGameList();
            displayAllGames(null);

        } catch (IOException e) {
            gameFeedback.setText(ERROR_FXML_GAME_DISPLAY);
            gameButton.setDisable(true);
        }
    }

    @FXML
    private void setSettingsPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_SETTINGS));
            loader.setController(this);
            currentStage.getScene().setRoot(loader.load());

            welcomeText.setText(String.format(WELCOME_TEXT, username.getText()));

        } catch (IOException e) {
            e.printStackTrace();
            settingsFeedback.setText(ERROR_FXML_SETTINGS);
            settingsButton.setDisable(true);
        }

    }

    @FXML
    private void changeTheme(ActionEvent event) {
        String theme = ((Button) event.getSource()).getText().toLowerCase();
        String themePath = String.format("styles/%s.css", theme);
        currentStage.getScene().getStylesheets().clear();
        currentStage.getScene().getStylesheets().add("styles/stylebase.css");
        currentStage.getScene().getStylesheets().add(themePath);
    }

    private void initialiseStage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_LAUNCHER_BASE));
            loader.setController(this);

            currentStage = loader.load();
            currentStage.getScene().getStylesheets().add("styles/stylebase.css");
            currentStage.getScene().getStylesheets().add("styles/cloudy.css");

            currentStage.show();

        } catch (IOException e) {
            System.out.println("ERROR_FXML_BASE_STAGE");
        }
    }

    private void initialiseLauncher() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PATH_LAUNCHER_BASE_ACCOUNTS));
            loader.setController(this);
            currentStage.getScene().setRoot(loader.load());

            loader = new FXMLLoader(getClass().getResource(PATH_ACCOUNTS_LOGIN));
            loader.setController(this);
            mainContent.setCenter(loader.load());

            setupServer();
            setupClock();
            setupStatus();
            setCaptchaAsNeeded();

        } catch (IOException e) {
            accountsFeedback.setText(ERROR_FXML_LOGIN);
            mainButton.setDisable(true);
        }
    }

    private void initialiseGameList() {
        String serverFeedback = server.postAllGamesQuery(token);
        gameFeedback.setText(serverFeedback);
        String error = server.getErrorResponse();
        String response = server.getServerResponse();

        if (serverFeedback.equals(CloudyLauncherServerInterface.ERROR_CONNECTION)) {
        } else if (!error.isEmpty()) {
            gameFeedback.setText(parser.parseErrorResponse(error));
        } else {
            initialiseGameListFromList(parser.parseGameList(response));
        }
    }

    private void initialiseOwnedGameList() {
        String serverFeedback = server.postOwnedGamesQuery(username.getText(),
                                                           token);
        gameFeedback.setText(serverFeedback);
        String error = server.getErrorResponse();
        String response = server.getServerResponse();
        List<String> listOfOwnedIds;

        if (serverFeedback.equals(CloudyLauncherServerInterface.ERROR_CONNECTION)) {
        } else if (!error.isEmpty()) {
            gameFeedback.setText(parser.parseErrorResponse(error));
        } else {
            listOfOwnedIds = parser.parseOwnedIdList(response);

            for (Game game : listOfGames) {
                if (listOfOwnedIds.contains(game.getId())) {
                    listOfOwnedGames.add(game);
                }
            }
        }
    }

    private void initialiseGameListFromList(List<Map<CloudyLauncherJsonParser.GameInformation, String>> gameList) {
        if (gameList.isEmpty()) {
            gameFeedback.setText("game list error");

        } else {
            for (Map<CloudyLauncherJsonParser.GameInformation, String> game : gameList) {
                Game newGame = new Game(game.get(CloudyLauncherJsonParser.GameInformation.ID),
                                        game.get(CloudyLauncherJsonParser.GameInformation.NAME),
                                        game.get(CloudyLauncherJsonParser.GameInformation.DESCRIPTION),
                                        game.get(CloudyLauncherJsonParser.GameInformation.PUBLISHER),
                                        Integer.parseInt(game.get(CloudyLauncherJsonParser.GameInformation.LIMIT)),
                                        game.get(CloudyLauncherJsonParser.GameInformation.ADDRESS),
                                        game.get(CloudyLauncherJsonParser.GameInformation.THUMBNAIL));
                listOfGames.add(newGame);
            }
        }
    }

    private void setGamePanel(Integer gameListType) {
        try {
            FXMLLoader loader;

            if (gameListType.equals(GAME_DISPLAY_WELCOME)) {
                loader = new FXMLLoader(getClass().getResource(PATH_GAME_DISPLAY_INITIAL));
                gameButton.setDisable(true);
            } else {
                loader = new FXMLLoader(getClass().getResource(PATH_GAME_DISPLAY_GAME));
                gameButton.setDisable(false);
            }

            loader.setController(this);
            VBox gamePanelContent = loader.load();

            gameInfoPanel.getChildren().clear();
            gameInfoPanel.getChildren().addAll(gamePanelContent.getChildren());

        } catch (IOException e) {
            gameFeedback.setText(ERROR_FXML_GAME_DISPLAY);
            gameButton.setDisable(true);
        }
    }

    private void setGameInformation(InputEvent event) {

        setGamePanel(GAME_DISPLAY_GAME_INFO);

        StackPane selectedIconStack = (StackPane) event.getSource();
        ImageView selectedIcon = (ImageView) selectedIconStack.getChildren().get(0);
        selectedGame = (Game) selectedIcon.getUserData();

        gameTitle.setText(selectedGame.getName());
        gameImage.setImage(selectedIcon.getImage());
        gameTextInformation.setText(String.format(GAME_INFORMATION,
                                                  selectedGame.getPublisher(),
                                                  selectedGame.getDescription()));

    }

    private void setPaginationSettings(Integer gameListType) {

        int numPages;
        List<Game> gameList;

        if (gameListType.equals(PAGINATION_ALL_GAMES)) {
            numPages = listOfGames.size() / TILES_PER_PAGE + 1;
            gameList = listOfGames;
        } else {
            numPages = listOfOwnedGames.size() / TILES_PER_PAGE + 1;
            gameList = listOfOwnedGames;
        }

        pagination.setPageCount(numPages);
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
                TilePane gameRoot = new TilePane();
                gameRoot.getStyleClass().add("game-root");

                int startIndex = pageIndex * TILES_PER_PAGE;
                for (int i = startIndex; i < startIndex + TILES_PER_PAGE; i++) {
                    if (i < gameList.size()) {
                        StackPane gameIcon = createNewGameThumbnail(gameList.get(i));
                        gameRoot.getChildren().add(gameIcon);
                    } else {
                        break;
                    }
                }
                return gameRoot;
            }
        });
    }

    private StackPane createNewGameThumbnail(Game gameInfo) {
        ImageView gameIcon = new ImageView(gameInfo.getThumbnail());
        gameIcon.setFitHeight(100);
        gameIcon.setFitWidth(100);

        gameIcon.setUserData(gameInfo);

        ImageView icon = new ImageView(URL_OWNED_BADGE);
        StackPane img = new StackPane();
        img.getStyleClass().add("game-icon");

        img.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setGameInformation(event);
            }
        });

        img.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String key = event.getCode().toString();
                if ((key.equals("ENTER")) || (key.equals("SPACE"))) {
                    setGameInformation(event);
                }
            }
        });

        if (listOfOwnedGames.contains(gameInfo)) {
            img.getChildren().addAll(gameIcon, icon);
        } else {
            img.getChildren().addAll(gameIcon);
        }

        return img;
    }

    private void checkCaptchaResult() {
        if (!captchaBox.isVisible()) {
            captchaResult = CAPTCHA_CORRECT;
        } else if (captchaInput.getText().trim().isEmpty()) {
            captchaResult = CAPTCHA_EMPTY;
        } else if (captchaInput.getText().trim().equals(captcha.getAnswer())){
            captchaResult = CAPTCHA_CORRECT;
        } else {
            captchaResult = CAPTCHA_INCORRECT;
        }
    }

    private void clearGamePage() {
        listOfGames.clear();
        listOfOwnedGames.clear();
    }

    private void clearInput() {
        password.setText("");
        captchaInput.setText("");
    }

    private void clearFeedback() {
        emailFeedback.setText("");
        usernameFeedback.setText("");
        passwordFeedback.setText("");
        accountsFeedback.setText("");
        captchaFeedback.setText("");
    }

    private void joinGame(Game gameToJoin) {

        String serverFeedback = server.postGameSessionRequest(gameToJoin,
                                                              username.getText(),
                                                              token);

        gameFeedback.setText(serverFeedback);
        String error = server.getErrorResponse();
        String response = server.getServerResponse();

        if (serverFeedback.equals(CloudyLauncherServerInterface.ERROR_CONNECTION)) {
        } else if (!error.isEmpty()) {
            gameFeedback.setText(parser.parseErrorResponse(error));
        } else {
            Map<CloudyLauncherJsonParser.GameSession, String> gameSession = parser.parseGameSession(response);
            String controllerId = gameSession.get(CloudyLauncherJsonParser.GameSession.CONTROLLER);
            String port = gameSession.get(CloudyLauncherJsonParser.GameSession.PORT);
            String sessionId = gameSession.get(CloudyLauncherJsonParser.GameSession.ID);

            try {
                Runtime.getRuntime()
                       .exec(String.format(COMMAND_RUN_THINCLIENT,
                                           sessionId, gameToJoin.getAddress(),
                                           port, controllerId));
            } catch (IOException e) {
                gameFeedback.setText(ERROR_GAME_JOIN);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        CloudyLauncher cl = new CloudyLauncher();
        cl.initialiseStage();
        cl.initialiseLauncher();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
