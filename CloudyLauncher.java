
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

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
    private String feedback = "";

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
            String queryData = String.format("username=%s&password=%s", username, password);
            
            DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
    
            writer.writeBytes(queryData);
            writer.flush();
            writer.close();
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));        
            String response = reader.readLine();
            setToken(response);
            reader.close();
            
            setFeedback("User recognised.");
            
        } catch (IOException e) {
            setFeedback("Incorrect login details");
        }
    }

    private String getValidationResponse(String username, String password, String email, String firstName, String lastName) {
        
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._-]*@[a-zA-Z][a-zA-Z-.]*[.][a-zA-Z]+");
        Pattern namePattern = Pattern.compile("[a-zA-Z][a-zA-Z.,-]*");
        
        if (username.isEmpty()) {
            return "Username cannot be empty.";
        } else if (password.isEmpty()) {
            return "Password cannot be empty.";
        } else if (!email.isEmpty() && !emailPattern.matcher(email).matches()) {
            return "Email format: example@com.sg.";
        } else if (!firstName.isEmpty() && !namePattern.matcher(firstName).matches()) {
            return "First name must be alphabets";
        } else if (!lastName.isEmpty() && !namePattern.matcher(lastName).matches()) {
            return "Last name must be alphabets";
        } else {
            return "";
        }
    }

    private void attemptUserRegistration(String username, String password, String email, String firstName, String lastName) {

        String validationResult = getValidationResponse(username, password, email, firstName, lastName);
        
        if (validationResult.isEmpty()) {
            try {
                URL url = new URL(baseurl + "/users/");        
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                String queryData = String.format("username=%s&password=%s&email=%s&first_name=%s&last_name=%s", username, password, email, firstName, lastName);

                // send post request
                connection.setDoOutput(true);
                DataOutputStream writer = new DataOutputStream(connection.getOutputStream());

                writer.writeBytes(queryData);
                writer.flush();
                writer.close();
                
                setFeedback("User successfully registered.");

            } catch (IOException e) {
                setFeedback("Username is already taken.");
            }
            
        } else {
            setFeedback(validationResult);
        }
    }
 
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        TabPane userRoot = new TabPane();
        
        addLoginTab(userRoot);
        addSignupTab(userRoot);
        
        Scene loginScene = new Scene(userRoot, 300, 400);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("CloudyLauncher");
        primaryStage.show();
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
