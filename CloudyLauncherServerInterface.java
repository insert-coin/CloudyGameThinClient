import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

public class CloudyLauncherServerInterface {
    private String baseUrl = "";
    private String serverResponse = "";
    private String errorResponse = "";

    private final static String URL_USERS = "users/";
    private final static String URL_REGISTRATION = "api-token-auth/registrations/";
    private final static String URL_LOGIN = "api-token-auth/tokens/";
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

    /**
     * Constructor for a CloudyLauncherServerInterface object. Url will default
     * to the deployed CloudyWeb if not specified.
     */
    public CloudyLauncherServerInterface() {
        baseUrl = "http://cloudyweb.gixs.work/";
    }

    /**
     * Constructor for a CloudyLauncherServerInterface object. Format of the url
     * should be given as http://ip:port/ or http://ip/
     *
     * @param serverUrl the base url of the server
     *
     */
    public CloudyLauncherServerInterface(String serverUrl) {
        baseUrl = serverUrl;
    }

    /**
     * Checks if the server is currently online and reachable. Method retrieves
     * the host name from the baseUrl and checks connection
     *
     * @return  whether server is reachable
     */
    public boolean isOnline() {

        String httpStr = "http://";
        String hostName = baseUrl.substring(httpStr.length(),
                                            baseUrl.length() - 1);

        if (hostName.contains(":")) {
            hostName = baseUrl.substring(httpStr.length(),
                                         baseUrl.indexOf(":", httpStr.length()));

        } else {
            hostName = baseUrl.substring(httpStr.length(), baseUrl.length() - 1);
        }

        try {
            InetAddress inetAddress = InetAddress.getByName(hostName);
            return inetAddress.isReachable(1000);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Post request to the CloudyWeb server to create a new user.
     *
     * Method returns a feedback string and not the server response. Use
     * methods getErrorResponse and getServerResponse to get the corresponding
     * information.
     *
     * @param signupUsername  username of the new user
     * @param signupPassword  password of the new user
     * @param signupEmail     email of the new user
     * @return  feedback string indicating success or error
     */
    public String postSignupRequest(String signupUsername, String signupPassword, String signupEmail) {
        resetResponses();

        try {
            URL url = new URL(baseUrl + URL_REGISTRATION);
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

    /**
     * Post request to the CloudyWeb server to login as a registered user.
     *
     * Method returns a feedback string and not the server response. Use
     * methods getErrorResponse and getServerResponse to get the corresponding
     * information.
     *
     * @param loginUsername  username of the registered user
     * @param loginPassword  password of the registered user
     * @return  feedback string indicating success or error
     */
    public String postAuthenticationRequest(String loginUsername, String loginPassword) {
        resetResponses();

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

    /**
     * Get request to the CloudyWeb server to query a current game session
     * using username and game id.
     *
     * Method returns a feedback string and not the server response. Use
     * methods getErrorResponse and getServerResponse to get the corresponding
     * information.
     *
     * @param gameToJoin  the Game object of the game to be joined
     * @param username    the username of the user
     * @param token       the authorization token of the user
     * @return  feedback string indicating success or error
     */
    public String postCurrentGameSessionQuery(Game gameToJoin, String username, String token) {
        resetResponses();

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

    /**
     * Get request to the CloudyWeb server to query a current game session.
     *
     * Method returns a feedback string and not the server response. Use
     * methods getErrorResponse and getServerResponse to get the corresponding
     * information.
     *
     * @param gameToJoin  the Game object of the game to be joined
     * @param username    the username of the user
     * @param token       the authorization token of the user
     * @return  feedback string indicating success or error
     */
    public String postGameSessionRequest(Game gameToJoin, String username, String token) {
        resetResponses();

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

    /**
     * Get request to the CloudyWeb server to retrieve information on all games.
     *
     * Method returns a feedback string and not the server response. Use
     * methods getErrorResponse and getServerResponse to get the corresponding
     * information.
     *
     * @param token  the authorization token of the user
     * @return  feedback string indicating success or error
     */
    public String postAllGamesQuery(String token) {
        resetResponses();

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

    /**
     * Get request to the CloudyWeb server to retrieve information on user-owned
     * games.
     *
     * Method returns a feedback string and not the server response. Use
     * methods getErrorResponse and getServerResponse to get the corresponding
     * information.
     *
     * @param username  the username of the user
     * @param token     the authorization token of the user
     * @return  feedback string indicating success or error
     */
    public String postOwnedGamesQuery(String username, String token) {
        resetResponses();

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

    /**
     * Opens a connection to the specified url, and set the corresponding
     * properties for the get request.
     *
     * @param url                 the url of the server
     * @param tokenAuthorization  the value of the authorization header
     * @return  the opened connection
     * @throws IOException error connecting to server
     */
    private HttpURLConnection openGetConnection(URL url, String tokenAuthorization) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", tokenAuthorization);
        connection.setRequestProperty("Accept", "application/json");

        return connection;
    }

    /**
     * Opens a connection to the specified url, and set the corresponding
     * properties for the post request. This method is the same as
     * openConnectionAndPost, but with an empty token.
     *
     * @param url   the url of the server
     * @param data  the data to be posted to the server
     * @return  the opened connection
     * @throws IOException  error connecting to server
     */
    private HttpURLConnection openConnectionAndPost(URL url, String data) throws IOException {
        return openConnectionAndPost(url, data, "");
    }

    /**
     * Opens a connection to the specified url, and set the corresponding
     * properties for the post request.
     *
     * @param url    the url of the server
     * @param data   the data to be posted to the server
     * @param token  the token of the user
     * @return  the opened connection
     * @throws IOException  error connecting to server
     */
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

    /**
     * Set method for the error response from server.
     *
     * @param connection  the connection to read the error stream from
     * @throws IOException  error connecting to server
     */
    private void setErrorResponse(HttpURLConnection connection) throws IOException {
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        errorResponse = errorReader.readLine();
        errorReader.close();
    }

    /**
     * Get method for the error response from server.
     *
     * @return  String containing the error response from server
     */
    public String getErrorResponse() {
        return errorResponse;
    }

    /**
     * Set method for reply from server.
     *
     * @param connection  the connection to read the input stream from
     * @throws IOException  error connecting to server
     */
    private void setServerResponse(HttpURLConnection connection) throws IOException {
        BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        serverResponse = responseReader.readLine();
        responseReader.close();
    }

    /**
     * Get method for the reply from server.
     *
     * @return  String containing the reply from server
     */
    public String getServerResponse() {
        return serverResponse;
    }

    /**
     * Reset the responses before each server request to prevent outdated
     * responses.
     */
    private void resetResponses() {
        serverResponse = "";
        errorResponse = "";
    }
}
