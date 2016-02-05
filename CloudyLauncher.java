
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CloudyLauncher extends Application {
    
    private String baseurl = "http://127.0.0.1:8000";
    private String token = "";

    private void setToken(String newToken) {
        token = newToken;
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
        
        Text feedback = new Text();
        feedback.setId("login-feedback");
        Button loginButton = new Button("Login");
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                try {
                    attemptAuthentication(usernameInput.getText(),
                                          passwordInput.getText());
                    feedback.setText("User recognised.");
                    
                } catch (IOException e) {
                    feedback.setText("No such user registered");
                    
                }
            }
        });
        
        loginInfo.add(username, 0, 1);
        loginInfo.add(usernameInput, 1, 1);
        loginInfo.add(password, 0, 2);
        loginInfo.add(passwordInput, 1, 2);
        loginInfo.add(loginButton, 0, 3);
        loginInfo.add(feedback, 1, 3);

        Tab loginTab = new Tab("Login");
        loginTab.setClosable(false);
        parent.getTabs().add(loginTab);
        loginTab.setContent(loginInfo);
    }
    
    private void addSignupTab(TabPane parent) {
        
        GridPane loginInfo = new GridPane();
        loginInfo.setId("signup-panel");

        Label username = new Label("Username");
        TextField usernameInput = new TextField();
        Label email = new Label("Email");
        TextField emailInput = new TextField();
        Label password = new Label("Password");
        PasswordField passwordInput = new PasswordField();

        Button loginButton = new Button("Sign Up");
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            }
        });
        
        loginInfo.add(email, 0, 0);
        loginInfo.add(emailInput, 1, 0);
        loginInfo.add(username, 0, 1);
        loginInfo.add(usernameInput, 1, 1);
        loginInfo.add(password, 0, 2);
        loginInfo.add(passwordInput, 1, 2);
        loginInfo.add(loginButton, 0, 3);

        Tab signupTab = new Tab("Sign Up");
        signupTab.setClosable(false);
        parent.getTabs().add(signupTab);
        signupTab.setContent(loginInfo);

    }    

           
    private void attemptAuthentication(String username, String password) throws IOException {

        URL url = new URL(baseurl + "/api-token-auth/");        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("POST");
        String queryData = String.format("username=%s&password=%s", username, password);
        
        // send post request
        connection.setDoOutput(true);
        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());

        writer.writeBytes(queryData);
        writer.flush();
        writer.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));        
        String response = reader.readLine();
        setToken(response);
        reader.close();

    }
    
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        TabPane userRoot = new TabPane();
        
        addLoginTab(userRoot);
        addSignupTab(userRoot);
        
        Scene loginScene = new Scene(userRoot);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("CloudyLauncher");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

    }

}
