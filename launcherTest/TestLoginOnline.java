package launcherTest;

import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import org.sikuli.basics.Settings;
import org.sikuli.script.FindFailed;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class TestLoginOnline extends TestLauncher {

    static Region launcher;

    /**
     * Waits until Cloudy Launcher is running and in the login page before
     * proceeding with testing; requires user to manually fire up the launcher.
     * otherwise if timeout is reached, testing will fail. Function will set
     * testing area region to current area of the launcher.
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Settings.MinSimilarity = 0.8;
        System.out.println(NOTE_RUN_LAUNCHER);
        ImagePath.add(PATH_IMAGE_SCREENSHOTS);

        Screen s = new Screen();
        try {
            Match m = s.wait(PATH_IMAGE_LAUNCHER_LOGIN, timeout);
            launcher = new Region(m.x, m.y, m.w, m.h);
            System.out.println(NOTE_RUN_LAUNCHER_TEST_START);

        } catch (FindFailed e) {
            System.out.println(NOTE_RUN_LAUNCHER_TIMEOUT);
            fail(ERROR_LAUNCHER_NOT_FOUND);
        }
    }

    /**
     * Resets the testing area to initial view of launcher. All text fields are
     * emptied; if currently logged in, user will be logged out; if sign up page
     * is seen, return to login page.
     */
    @Before
    public void setUp() {
        Settings.MinSimilarity = 0.8;

        if (launcher.exists(PATH_IMAGE_BUTTON_LOGOUT) != null) {
            try {
                launcher.click(PATH_IMAGE_BUTTON_LOGOUT);
            } catch (FindFailed e) {
                fail(ERROR_LOGOUT_CHECK);
            }
        }

        if (launcher.exists(PATH_IMAGE_LAUNCHER_HEADER_SIGNUP) != null) {
            try {
                launcher.click(PATH_IMAGE_LINK_LOGIN);
            } catch (FindFailed e) {
                fail(ERROR_LINK_LOGIN);
            }
        }

        if (launcher.exists(PATH_IMAGE_INPUT_TEXTFIELD) != null) {
            try {
                Iterator<Match> iter = launcher.findAll(PATH_IMAGE_INPUT_TEXTFIELD);
                while (iter.hasNext()) {
                    Match next = iter.next();
                    launcher.click(next);
                    launcher.type("a", KeyModifier.CMD);
                    launcher.type(Key.BACKSPACE);
                }

            } catch (FindFailed e) {
                fail(ERROR_CLEAR_INPUT);
            }
        }
    }

    @Test
    public void LoginBlankInputTest() {
        try {
            launcher.wait(PATH_IMAGE_SERVER_ONLINE, timeout);
        } catch (FindFailed e) {
            fail(ERROR_SERVER_OFFLINE);
        }

        try {
            launcher.click(PATH_IMAGE_BUTTON_LOGIN);
            launcher.wait(PATH_IMAGE_USERNAME_EMPTY);
            launcher.wait(PATH_IMAGE_PASSWORD_EMPTY);

        } catch (FindFailed e) {
            fail();
        }
    }

    @Test
    public void LoginBlankUsernameTest() {
        try {
            launcher.wait(PATH_IMAGE_SERVER_ONLINE, timeout);
        } catch (FindFailed e) {
            fail(ERROR_SERVER_OFFLINE);
        }

        try {
            launcher.click(PATH_IMAGE_PASSWORD_TEXTFIELD);
            launcher.type(validPass);
            launcher.click(PATH_IMAGE_BUTTON_LOGIN);
            launcher.wait(PATH_IMAGE_USERNAME_EMPTY);
            launcher.wait(PATH_IMAGE_PASSWORD_FILLED);

        } catch (FindFailed e) {
            fail();
        }
    }

    @Test
    public void LoginBlankPasswordTest() {
        try {
            launcher.wait(PATH_IMAGE_SERVER_ONLINE, timeout);
        } catch (FindFailed e) {
            fail(ERROR_SERVER_OFFLINE);
        }

        try {
            launcher.click(PATH_IMAGE_USERNAME_TEXTFIELD);
            launcher.type(validUser);
            launcher.click(PATH_IMAGE_BUTTON_LOGIN);
            launcher.wait(PATH_IMAGE_USERNAME_FILLED);
            launcher.wait(PATH_IMAGE_PASSWORD_EMPTY);

        } catch (FindFailed e) {
            fail();
        }
    }

    @Test
    public void LoginInvalidUsernameTest() {
        try {
            launcher.wait(PATH_IMAGE_SERVER_ONLINE, timeout);
        } catch (FindFailed e) {
            fail(ERROR_SERVER_OFFLINE);
        }

        try {
            launcher.click(PATH_IMAGE_USERNAME_TEXTFIELD);
            launcher.type(invalidUser);
            launcher.click(PATH_IMAGE_PASSWORD_TEXTFIELD);
            launcher.type(validPass);
            launcher.click(PATH_IMAGE_BUTTON_LOGIN);
            launcher.wait(PATH_IMAGE_INCORRECT_CREDENTIALS);

        } catch (FindFailed e) {
            fail();
        }
    }

    @Test
    public void LoginInvalidPasswordTest() {
        try {
            launcher.wait(PATH_IMAGE_SERVER_ONLINE, timeout);
        } catch (FindFailed e) {
            fail(ERROR_SERVER_OFFLINE);
        }

        try {
            launcher.click(PATH_IMAGE_USERNAME_TEXTFIELD);
            launcher.type(validUser);
            launcher.click(PATH_IMAGE_PASSWORD_TEXTFIELD);
            launcher.type(invalidPass);
            launcher.click(PATH_IMAGE_BUTTON_LOGIN);
            launcher.wait(PATH_IMAGE_INCORRECT_CREDENTIALS);

        } catch (FindFailed e) {
            fail();
        }
    }

    @Test
    public void LoginSuccessTest() {
        Settings.MinSimilarity = 0.7;
        try {
            launcher.wait(PATH_IMAGE_SERVER_ONLINE, timeout);
        } catch (FindFailed e) {
            fail(ERROR_SERVER_OFFLINE);
        }

        try {
            launcher.click(PATH_IMAGE_USERNAME_TEXTFIELD);
            launcher.type(validUser);
            launcher.click(PATH_IMAGE_PASSWORD_TEXTFIELD);
            launcher.type(validPass);
            launcher.click(PATH_IMAGE_BUTTON_LOGIN);
            launcher.wait(PATH_IMAGE_GAME_WELCOME);

        } catch (FindFailed e) {
            fail();
        }
    }

    @Test
    public void LinkToSignupTest() {
        try {
            launcher.wait(PATH_IMAGE_SERVER_ONLINE, timeout);
        } catch (FindFailed e) {
            fail(ERROR_SERVER_OFFLINE);
        }

        try {
            launcher.click(PATH_IMAGE_LINK_SIGNUP);
            launcher.wait(PATH_IMAGE_LAUNCHER_HEADER_SIGNUP);

        } catch (FindFailed e) {
            fail();
        }
    }
    
    @AfterClass
    public static void tearDownAfterClass() {
        System.out.println(NOTE_RUN_LAUNCHER_TEST_END);
    }
}
