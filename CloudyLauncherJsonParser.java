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
    final static String[] FIELDS_NON_HEADER = { "non_field_errors", "detail",
                                                "message" };

    static enum GameInformation {
        ID, NAME, PUBLISHER, LIMIT, ADDRESS, THUMBNAIL
    };

    static enum GameSession {
        ID, USER, GAME, CONTROLLER, PORT
    }

    private CloudyLauncherJsonParser() {
        parser = this;
    }

    public static CloudyLauncherJsonParser getParser() {
        if (parser == null) {
            parser = new CloudyLauncherJsonParser();
        }
        return parser;
    }

    public String parseToken(String tokenString) {
        try {
            JSONObject tokenObj = new JSONObject(tokenString);
            String responseToken = tokenObj.getString("token");
            return responseToken;

        } catch (JSONException e) {
            return ERROR_PARSER_OBJECT;
        }
    }

    public String parseErrorResponse(String errorString) {
        String errorMessage = "";
        JSONObject errorResponse;
        try {
            errorResponse = new JSONObject(errorString);
            for (String header : FIELDS_HEADER) {
                if (errorResponse.has(header)) {
                    errorMessage = errorMessage
                                   + "\n"
                                   + header
                                   + ": "
                                   + errorResponse.getJSONArray(header)
                                                  .getString(0);
                }
            }

            for (String header : FIELDS_NON_HEADER) {
                if (errorResponse.has(header)) {
                    errorMessage = errorMessage
                                   + "\n"
                                   + errorResponse.getJSONArray(header)
                                                  .getString(0);
                }
            }
            return errorMessage;

        } catch (JSONException e) {
            return ERROR_PARSER_FEEDBACK;
        }
    }

    public List<Map<GameInformation, String>> parseGameList(String gameListString) {
        JSONArray gameArray = new JSONArray(gameListString);
        List<Map<GameInformation, String>> gameList = new ArrayList<Map<GameInformation, String>>();

        for (int i = 0; i < gameArray.length(); i++) {
            JSONObject gameObject = gameArray.getJSONObject(i);
            gameList.add(parseGameDetailString(gameObject));
        }

        return gameList;
    }

    public Map<GameInformation, String> parseGameDetailString(JSONObject gameObj) {

        try {
            Map<GameInformation, String> gameInformation = new HashMap<GameInformation, String>();

            String gId = Integer.toString(gameObj.getInt("id"));
            String gName = gameObj.getString("name");
            String gPublisher = gameObj.getString("publisher");
            String gLimit = Integer.toString(gameObj.getInt("max_limit"));
            String gAddress = gameObj.getString("address");
            String gThumbnail = gameObj.getString("thumbnail");

            gameInformation.put(GameInformation.ID, gId);
            gameInformation.put(GameInformation.NAME, gName);
            gameInformation.put(GameInformation.PUBLISHER, gPublisher);
            gameInformation.put(GameInformation.LIMIT, gLimit);
            gameInformation.put(GameInformation.ADDRESS, gAddress);
            gameInformation.put(GameInformation.THUMBNAIL, gThumbnail);

            return gameInformation;

        } catch (JSONException e) {
            return null;
        }
    }

    public List<String> parseOwnedIdList(String ownedIdListString) {
        JSONArray ownedIdArray = new JSONArray(ownedIdListString);
        List<String> ownedIdList = new ArrayList<String>();

        for (int i = 0; i < ownedIdArray.length(); i++) {
            JSONObject ownedIdObject = ownedIdArray.getJSONObject(i);
            ownedIdList.add(Integer.toString(ownedIdObject.getInt("game")));
        }

        return ownedIdList;
    }

    public Map<GameSession, String> parseGameSession(String gameSessionString) {
        Map<GameSession, String> gameSession = new HashMap<>(5);
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
                        Integer.toString(gameSessionObject.getInt("port")));

        return gameSession;
    }
}
