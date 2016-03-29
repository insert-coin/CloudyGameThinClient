import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class CloudyLauncherRestructured extends Application {

    private CloudyLauncherServerInterface server;
    private CloudyLauncherJsonParser parser = CloudyLauncherJsonParser.getParser();

    private String token;
    private List<Game> listOfGames = new ArrayList<Game>();
    private List<Game> listOfOwnedGames = new ArrayList<Game>();

    @FXML private BorderPane mainContent;
    @FXML private Pagination pagination;
    @FXML private VBox gameInfoPanel;

    @FXML private TextField email;
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private Text accountsFeedback;

    @FXML private Text gameTitle;
    @FXML private ImageView gameImage;
    @FXML private Text gameTextInformation;
    @FXML private Button gameButton;
    @FXML private Text gameFeedback;

    final private String ERROR_FXML = "Error in loading %s page";
    final private String URL_OWNED_BADGE = "images/orangeribbon.png";
    final private String GAME_INFORMATION = "%s\n%s\n%s";

    final private Integer GAME_DISPLAY_WELCOME = 0;
    final private Integer GAME_DISPLAY_GAME_INFO = 1;
    final private Integer PAGINATION_ALL_GAMES = 2;
    final private Integer PAGINATION_MY_GAMES = 3;
    final private Integer TILES_PER_PAGE = 12;

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
        accountsFeedback.setText(serverFeedback);
        String error = server.getErrorResponse();
        String response = server.getServerResponse();

        if (serverFeedback.equals(CloudyLauncherServerInterface.ERROR_CONNECTION)) {
        } else if (!error.isEmpty()) {
            accountsFeedback.setText(parser.parseErrorResponse(error));
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

        accountsFeedback.setText(serverFeedback);
        String error = server.getErrorResponse();

        if (serverFeedback.equals(CloudyLauncherServerInterface.ERROR_CONNECTION)) {
        } else if (!error.isEmpty()) {
            accountsFeedback.setText(parser.parseErrorResponse(error));
        } else {
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        token = "";
        clearGamePage();
        setLoginPage(null);
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
    private void refreshGameList(ActionEvent event) {
        clearGamePage();
        setGameDisplayPage();
    }

    @FXML
    private void setSignupPage(MouseEvent event) {
        try {
            FXMLLoader vloader = new FXMLLoader(getClass().getResource("design/Signup.fxml"));
            vloader.setController(this);
            mainContent.setCenter(vloader.load());

        } catch (IOException e) {
            System.out.println(String.format(ERROR_FXML, "signup"));
        }
    }

    @FXML
    private void setLoginPage(MouseEvent event) {
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

            setGamePanel(GAME_DISPLAY_WELCOME);

            initialiseGameList();
            initialiseOwnedGameList();
            displayAllGames(null);

        } catch (IOException e) {
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
            System.out.println("game list error");

        } else {
            for (Map<CloudyLauncherJsonParser.GameInformation, String> game : gameList) {
                Game newGame = new Game(game.get(CloudyLauncherJsonParser.GameInformation.ID),
                                        game.get(CloudyLauncherJsonParser.GameInformation.NAME),
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
                loader = new FXMLLoader(getClass().getResource("design/GameDisplay_initial.fxml"));
                gameButton.setDisable(true);
            } else {
                loader = new FXMLLoader(getClass().getResource("design/GameDisplay_game.fxml"));
                gameButton.setDisable(false);
            }

            loader.setController(this);
            VBox gamePanelContent = loader.load();

            gameInfoPanel.getChildren().clear();
            gameInfoPanel.getChildren().addAll(gamePanelContent.getChildren());

        } catch (IOException e) {
            System.out.println(String.format(ERROR_FXML, "game information"));
        }
    }

    private void setGameInformation(MouseEvent event) {

        setGamePanel(GAME_DISPLAY_GAME_INFO);

        ImageView selectedIcon = (ImageView) event.getTarget();
        Game selectedGame = (Game) selectedIcon.getUserData();

        gameTitle.setText(selectedGame.getName());
        gameImage.setImage(selectedIcon.getImage());
        gameTextInformation.setText(String.format(GAME_INFORMATION,
                                                  selectedGame.getId(),
                                                  selectedGame.getPublisher(),
                                                  selectedGame.getLimit()));

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

        TilePane gameRoot = new TilePane();
        gameRoot.getStyleClass().add("game-root");

        pagination.setPageCount(numPages);
        pagination.setPageFactory(new Callback<Integer, Node>() {

            @Override
            public Node call(Integer pageIndex) {
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
        gameIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setGameInformation(event);
            }
        });

        ImageView icon = new ImageView(URL_OWNED_BADGE);
        StackPane img = new StackPane();
        img.getStyleClass().add("game-icon");

        if (listOfOwnedGames.contains(gameInfo)) {
            img.getChildren().addAll(gameIcon, icon);
        } else {
            img.getChildren().addAll(gameIcon);
        }

        return img;
    }

    private void clearGamePage() {
        listOfGames.clear();
        listOfOwnedGames.clear();
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
