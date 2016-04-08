import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CloudyLauncherJsonParser {
    private static CloudyLauncherJsonParser parser;

    final static String ERROR_PARSER_OBJECT = "PARSING OBJECT ERROR";
    final static String ERROR_PARSER_GAME = "PARSING GAME ERROR";
    final static String ERROR_PARSER_FEEDBACK = "PARSING FEEDBACK ERROR";

    final static String[] FIELDS_HEADER = { "username", "password", "email" };
    final static String[] FIELDS_NON_HEADER_JSONARRAY = { "non_field_errors" };
    final static String[] FIELDS_NON_HEADER_JSONOBJECT = { "detail", "message" };

    static enum GameInformation {
        ID, NAME, DESCRIPTION, PUBLISHER, LIMIT, ADDRESS, THUMBNAIL
    };

    static enum GameSession {
        ID, USER, GAME, CONTROLLER, PORT
    }

    /**
     * Private constructor for a CloudyLauncherJsonParser object. Use the static
     * method getParser to get the parser instance instead.
     */
    private CloudyLauncherJsonParser() {
        parser = this;
    }

    /**
     * Retrieve the instance of the CloudyLauncherJsonParser. Create one if not
     * existing.
     *
     * @return CloudyLauncherJsonParser instance
     */
    public static CloudyLauncherJsonParser getParser() {
        if (parser == null) {
            parser = new CloudyLauncherJsonParser();
        }
        return parser;
    }

    /**
     * Parse json string to retrieve the token.
     *
     * Return error message if error encountered.
     *
     * @param tokenString json string containing the token
     * @return parsed token
     */
    public String parseToken(String tokenString) {
        try {
            JSONObject tokenObj = new JSONObject(tokenString);
            String responseToken = tokenObj.getString("token");
            return responseToken;

        } catch (JSONException e) {
            return ERROR_PARSER_OBJECT;
        }
    }

    /**
     * Parse json string to retrieve error string.
     *
     * Return error message if error encountered.
     *
     * @param errorString json string containing any errors
     * @return parsed error string
     */
    public String parseErrorResponse(String errorString) {
        String errorMessage = "";
        JSONObject errorResponse;
        try {
            errorResponse = new JSONObject(errorString);
            for (String header : FIELDS_HEADER) {
                if (errorResponse.has(header)) {
                    errorMessage = errorMessage
                                   + "\n"
                                   + header.toUpperCase()
                                   + ": "
                                   + errorResponse.getJSONArray(header)
                                                  .getString(0);
                }
            }

            for (String header : FIELDS_NON_HEADER_JSONARRAY) {
                if (errorResponse.has(header)) {
                    errorMessage = errorMessage
                                   + "\n"
                                   + errorResponse.getJSONArray(header)
                                                  .getString(0);
                }
            }

            for (String header : FIELDS_NON_HEADER_JSONOBJECT) {
                if (errorResponse.has(header)) {
                    errorMessage = errorMessage + "\n"
                                   + errorResponse.getString(header);
                }
            }

            return errorMessage.trim();

        } catch (JSONException e) {
            return ERROR_PARSER_FEEDBACK;
        }
    }

    /**
     * Parse json string to retrieve a list of games. Use enum GameInformation
     * to get individual properties of each game.
     *
     * Return an empty list if error encountered.
     *
     * @param gameListString json string containing games' information
     * @return list of games with parsed information
     */
    public List<Map<GameInformation, String>> parseGameList(String gameListString) {
        try {
            JSONArray gameArray = new JSONArray(gameListString);
            List<Map<GameInformation, String>> gameList = new ArrayList<Map<GameInformation, String>>();

            for (int i = 0; i < gameArray.length(); i++) {
                JSONObject gameObject = gameArray.getJSONObject(i);
                gameList.add(parseGameDetailString(gameObject));
            }

            return gameList;

        } catch (JSONException e) {
            return new ArrayList<Map<GameInformation, String>>();
        }
    }

    /**
     * Parse json object to retrieve game details into Map using enum
     * GameInformation. Used by method parseGameList; throws an exception if
     * error encountered.
     *
     * @param gameObject JSONObject containing game details
     * @return map with game key-value information
     * @throws JSONException error parsing game detail
     */
    private Map<GameInformation, String> parseGameDetailString(JSONObject gameObject)
            throws JSONException {
        Map<GameInformation, String> gameInformation = new HashMap<GameInformation, String>(6);
        String gId = Integer.toString(gameObject.getInt("id"));
        String gName = gameObject.getString("name");
        String gDescription = gameObject.getString("description");
        String gPublisher = gameObject.getString("publisher");
        String gLimit = Integer.toString(gameObject.getInt("max_limit"));
        String gAddress = gameObject.getString("address");
        String gThumbnail = gameObject.getString("thumbnail");

        gameInformation.put(GameInformation.ID, gId);
        gameInformation.put(GameInformation.NAME, gName);
        gameInformation.put(GameInformation.DESCRIPTION, gDescription);
        gameInformation.put(GameInformation.PUBLISHER, gPublisher);
        gameInformation.put(GameInformation.LIMIT, gLimit);
        gameInformation.put(GameInformation.ADDRESS, gAddress);
        gameInformation.put(GameInformation.THUMBNAIL, gThumbnail);

        return gameInformation;
    }

    /**
     * Parse json string to retrieve list of game ids.
     *
     * Return an empty list if error encountered.
     *
     * @param ownedIdListString json string containing game ownership
     *            information
     * @return list of game ids
     */
    public List<String> parseOwnedIdList(String ownedIdListString) {
        try {
            JSONArray ownedIdArray = new JSONArray(ownedIdListString);
            List<String> ownedIdList = new ArrayList<String>();

            for (int i = 0; i < ownedIdArray.length(); i++) {
                JSONObject ownedIdObject = ownedIdArray.getJSONObject(i);
                ownedIdList.add(Integer.toString(ownedIdObject.getInt("game")));
            }

            return ownedIdList;

        } catch (JSONException e) {
            return new ArrayList<String>();
        }
    }

    /**
     * Parse json string to retrieve game session information into Map using
     * enum GameSession. Get individual properties of game session using
     * GameSession.
     *
     * Return an empty map if error encountered.
     *
     * @param gameSessionString json string containing game session information
     * @return map with game session key-value information
     */
    public Map<GameSession, String> parseGameSession(String gameSessionString) {
        try {
            Map<GameSession, String> gameSession = new HashMap<GameSession, String>(5);
            JSONObject gameSessionObject = new JSONObject(gameSessionString);
            gameSession.put(GameSession.ID,
                            Integer.toString(gameSessionObject.getInt("id")));
            gameSession.put(GameSession.USER,
                            gameSessionObject.getString("user"));
            gameSession.put(GameSession.GAME,
                            Integer.toString(gameSessionObject.getInt("game")));
            gameSession.put(GameSession.CONTROLLER,
                            Integer.toString(gameSessionObject.getInt("controller")));
            gameSession.put(GameSession.PORT,
                            Integer.toString(gameSessionObject.getInt("streaming_port")));

            return gameSession;

        } catch (JSONException e) {
            return new HashMap<GameSession, String>();
        }
    }
}
