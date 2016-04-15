package launcherTest;

public class TestLauncher {
    protected static String validUser = "kwm";
    protected static String validPass = "kwm";
    protected static String invalidUser = "user";
    protected static String invalidPass = "pass";
    
    protected static String NOTE_RUN_LAUNCHER = "Run launcher for testing";
    protected static String NOTE_RUN_LAUNCHER_TIMEOUT = "Too late";
    protected static String NOTE_RUN_LAUNCHER_TEST_START = "Starting test...";
    protected static String NOTE_RUN_LAUNCHER_TEST_END = "Test ended.";
    
    protected static String ERROR_LAUNCHER_NOT_FOUND = "Launcher not launched, unable to test";
    protected static String ERROR_LOGOUT_CHECK = "Unable to logout for subsequent test";
    protected static String ERROR_LINK_LOGIN = "Unable to return to login page for subsequent test";
    protected static String ERROR_LINK_SIGNUP = "Unable to return to signup page for subsequent test";
    protected static String ERROR_CLEAR_INPUT = "Unable to clear text input before test";
    protected static String ERROR_CAPTCHA_INVALID = "Captcha entered is invalid.";
    protected static String ERROR_SERVER_OFFLINE = "Server is not online";
    protected static String ERROR_SERVER_ONLINE = "Server is not offline";
    
    protected static String PATH_IMAGE_SCREENSHOTS = "launcherTest/screenshots";
    protected static String PATH_IMAGE_LAUNCHER_LOGIN = "launcher_login";
    protected static String PATH_IMAGE_LAUNCHER_SIGNUP = "launcher_signup";
    protected static String PATH_IMAGE_SERVER_ONLINE = "status_online";
    protected static String PATH_IMAGE_SERVER_OFFLINE = "status_offline";

    protected static String PATH_IMAGE_LAUNCHER_HEADER_LOGIN = "launcher_header_login";
    protected static String PATH_IMAGE_LAUNCHER_HEADER_SIGNUP = "launcher_header_signup";
    protected static String PATH_IMAGE_LINK_LOGIN = "link_login";
    protected static String PATH_IMAGE_LINK_SIGNUP = "link_signup";
    protected static String PATH_IMAGE_INPUT_TEXTFIELD = "input";
    protected static String PATH_IMAGE_USERNAME_TEXTFIELD = "input_username";
    protected static String PATH_IMAGE_PASSWORD_TEXTFIELD = "input_password";
    protected static String PATH_IMAGE_CAPTCHA_TEXTFIELD = "input_captcha";
    protected static String PATH_IMAGE_EMAIL_TEXTFIELD = "input_email";
    protected static String PATH_IMAGE_USERNAME_EMPTY = "username_blank";
    protected static String PATH_IMAGE_PASSWORD_EMPTY = "password_blank";
    protected static String PATH_IMAGE_CAPTCHA_EMPTY = "captcha_blank";
    protected static String PATH_IMAGE_EMAIL_EMPTY = "email_blank";
    protected static String PATH_IMAGE_USERNAME_FILLED = "username_filled";
    protected static String PATH_IMAGE_PASSWORD_FILLED = "password_filled";
    protected static String PATH_IMAGE_CAPTCHA_FILLED = "captcha_filled";
    protected static String PATH_IMAGE_EMAIL_FILLED = "email_filled";
    protected static String PATH_IMAGE_CAPTCHA_INVALID = "captcha_invalid";
    protected static String PATH_IMAGE_EMAIL_INVALID = "email_invalid";
    
    protected static String PATH_IMAGE_BUTTON_LOGIN = "button_login";
    protected static String PATH_IMAGE_BUTTON_SIGNUP = "button_signup";
    protected static String PATH_IMAGE_BUTTON_LOGOUT = "button_logout";
    
    protected static String PATH_IMAGE_GAME_WELCOME = "game_welcome";
    protected static String PATH_IMAGE_INCORRECT_CREDENTIALS = "login_fail";
    
    protected static String REQUEST_ENTER_CAPTCHA = "Enter captcha";

    protected static double timeout = 10;
}
