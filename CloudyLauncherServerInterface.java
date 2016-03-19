import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CloudyLauncherServerInterface {
    private String baseUrl = "";
    private String serverResponse = "";
    private String errorResponse = "";

    private final static String URL_USERS = "users/";
    private final static String URL_LOGIN = "api-token-auth/";
    private final static String URL_GAMES = "games/";
    private final static String URL_GAMES_OWNED = "game-ownership/?user=%s";
    private final static String URL_GAME_SESSION = "game-session/";
    private final static String URL_GAME_SESSION_CURRENT = "game-session/?game=%s&user=%s";

    private final static String DATA_SIGNUP = "username=%s&password=%s&email=%s";
    private final static String DATA_LOGIN = "username=%s&password=%s";
    private final static String DATA_GAME_ACCOUNT = "game=%s&user=%s";

    private final static String FEEDBACK_SUCCESS_SIGNUP = "User successfully registered";
    private final static String FEEDBACK_SUCCESS_LOGIN = "User recognised";
    private final static String FEEDBACK_SUCCESS_JOIN_GAME = "Game session successfully created";
    private final static String FEEDBACK_SUCCESS_GET_PORT = "Streaming port number retrieved";
    private final static String FEEDBACK_SUCCESS_GET_GAMES = "Games list successfully retrieved";

    final static String ERROR_FEEDBACK = "INVALID REQUEST";
    final static String ERROR_CONNECTION = "SERVER CONNECTION ERROR";

    public CloudyLauncherServerInterface(String serverUrl) {
        baseUrl = serverUrl;
    }

    public String postSignupRequest(String signupUsername, String signupPassword, String signupEmail) {

        try {
            URL url = new URL(baseUrl + URL_USERS);
            String queryData = String.format(DATA_SIGNUP, signupUsername,
                                             signupPassword, signupEmail);

            HttpURLConnection connection = openConnectionAndPost(url, queryData);

            String returnMessage;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED){
                setServerResponse(connection);
                returnMessage = FEEDBACK_SUCCESS_SIGNUP;

            } else {
                setErrorResponse(connection);
                returnMessage = ERROR_FEEDBACK;
            }

            connection.disconnect();
            return returnMessage;

        } catch (IOException e) {
            return ERROR_CONNECTION;
        }
    }

    public String postAuthenticationRequest(String loginUsername, String loginPassword) {

        try {
            URL url = new URL(baseUrl + URL_LOGIN);
            String queryData = String.format(DATA_LOGIN, loginUsername, loginPassword);

            HttpURLConnection connection = openConnectionAndPost(url, queryData);

            String returnMessage;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                setServerResponse(connection);
                returnMessage = FEEDBACK_SUCCESS_LOGIN;

            } else {
                setErrorResponse(connection);
                returnMessage = ERROR_FEEDBACK;
            }
            
            connection.disconnect();
            return returnMessage;

        } catch (IOException e) {
            return ERROR_CONNECTION;
        }
    }

    public String postCurrentGameSessionQuery(Game gameToJoin, String username, String token) {
        try {
            URL url = new URL(baseUrl + String.format(URL_GAME_SESSION_CURRENT,
                                                      gameToJoin.getId(),
                                                      username));
            String tokenAuthorization = "Token " + token;
            HttpURLConnection connection = openGetConnection(url, tokenAuthorization);

            String returnMessage;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                setServerResponse(connection);
                returnMessage = FEEDBACK_SUCCESS_GET_PORT;

            } else {
                setErrorResponse(connection);
                returnMessage = ERROR_FEEDBACK;
            }

            connection.disconnect();
            return returnMessage;

        } catch (IOException e) {
            return ERROR_CONNECTION;
        }
    }

    public String postGameSessionRequest(Game gameToJoin, String username, String token) {
        try {
            URL url = new URL(baseUrl + URL_GAME_SESSION);
            String tokenAuthorization = "Token " + token;
            String queryData = String.format(DATA_GAME_ACCOUNT,
                                             gameToJoin.getId(),
                                             username);

            HttpURLConnection connection = openConnectionAndPost(url, queryData, tokenAuthorization);

            String returnMessage;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                setServerResponse(connection);
                returnMessage = FEEDBACK_SUCCESS_JOIN_GAME;

            } else {
                setErrorResponse(connection);
                returnMessage = ERROR_FEEDBACK;
            }

            connection.disconnect();
            return returnMessage;

        } catch (IOException e) {
            return ERROR_CONNECTION;
        }
    }

    public String postAllGamesQuery(String token) {
        try {
            URL url = new URL(baseUrl + URL_GAMES);
            String tokenAuthorization = "Token " + token;
            HttpURLConnection connection = openGetConnection(url, tokenAuthorization);

            String returnMessage;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                setServerResponse(connection);
                returnMessage = FEEDBACK_SUCCESS_GET_GAMES;

            } else {
                setErrorResponse(connection);
                returnMessage = ERROR_FEEDBACK;
            }

            connection.disconnect();
            return returnMessage;

        } catch (IOException e) {
            return ERROR_CONNECTION;
        }
    }

    public String postOwnedGamesQuery(String username, String token) {
        try {
            URL url = new URL(baseUrl + String.format(URL_GAMES_OWNED, username));
            String tokenAuthorization = "Token " + token;
            HttpURLConnection connection = openGetConnection(url, tokenAuthorization);

            String returnMessage;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                setServerResponse(connection);
                returnMessage = FEEDBACK_SUCCESS_GET_GAMES;

            } else {
                setErrorResponse(connection);
                returnMessage = ERROR_FEEDBACK;
            }

            connection.disconnect();
            return returnMessage;

        } catch (IOException e) {
            return ERROR_CONNECTION;
        }
    }

    private HttpURLConnection openGetConnection(URL url, String tokenAuthorization) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", tokenAuthorization);
        connection.setRequestProperty("Accept", "application/json");

        return connection;
    }

    private HttpURLConnection openConnectionAndPost(URL url, String data) throws IOException {
        return openConnectionAndPost(url, data, "");
    }

    private HttpURLConnection openConnectionAndPost(URL url, String data, String tokenAuthorization) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", tokenAuthorization);
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
        writer.writeBytes(data);
        writer.flush();
        writer.close();

        return connection;
    }

    private void setErrorResponse(HttpURLConnection connection) throws IOException {
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        errorResponse = errorReader.readLine();
        errorReader.close();
    }

    public String getErrorResponse() {
        return errorResponse;
    }

    private void setServerResponse(HttpURLConnection connection) throws IOException {
        BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        serverResponse = responseReader.readLine();
        responseReader.close();
    }

    public String getServerResponse() {
        return serverResponse;
    }
}
