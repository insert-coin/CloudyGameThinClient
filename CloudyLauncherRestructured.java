import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javafx.application.Application;
import javafx.stage.Stage;

public class CloudyLauncherRestructured extends Application {
    private CloudyLauncherServerInterface server;

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

    @Override
    public void start(Stage primaryStage) throws Exception {

        CloudyLauncherRestructured cl = new CloudyLauncherRestructured();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
