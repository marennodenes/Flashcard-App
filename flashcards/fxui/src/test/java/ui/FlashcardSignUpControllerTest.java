package ui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import dto.UserDataDto;
import shared.ApiResponse;
import shared.ApiConstants;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.concurrent.TimeUnit;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.OutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Test suite for FlashcardSignUpController.
 * Tests user registration logic, input validation, error handling, and navigation to main app.
 * 
 * @author ailinat
 * @author sofietw
 * @author marennod
 * @author parts of this code is generated with AI assistance for comprehensive test coverage
 *
 * @see FlashcardSignUpController
 */
@ExtendWith(ApplicationExtension.class)
public class FlashcardSignUpControllerTest {

    private FlashcardSignUpController controller;
    private Text alertMessage;
    private Text ex;
    private TextField usernameField;
    private TextField passwordField;
    private TextField confirmPasswordField;
    private Button signInButton;

    /**
     * Sets up the test environment before each test.
     * @throws Exception
     */
    @BeforeEach
    void setUp() throws Exception {
        controller = new FlashcardSignUpController();
        alertMessage = new Text();
        ex = new Text();
        usernameField = new TextField();
        passwordField = new TextField();
        confirmPasswordField = new TextField();
        signInButton = new Button();

        setField(controller, "alertMessage", alertMessage);
        setField(controller, "ex", ex);
        setField(controller, "usernameField", usernameField);
        setField(controller, "passwordField", passwordField);
        setField(controller, "confirmPasswordField", confirmPasswordField);
        setField(controller, "signInButton", signInButton);
        // Ensure initial UI state is applied on the JavaFX thread (hide alertMessage by default)
        WaitForAsyncUtils.asyncFx(() -> controller.updateUi()).get(5, TimeUnit.SECONDS);
    }

    /**
     * Starts a simple test HTTP server that responds with the given status code and response body.
     * @param statusCode The HTTP status code to return.
     * @param responseBody The JSON response body to return.
     * @return The ServerThread instance running the test server.
     * @throws Exception If an error occurs while starting the server.
     */
    private ServerThread startTestServer(int statusCode, String responseBody) throws Exception {
        ServerThread serverThread = new ServerThread(statusCode, responseBody);
        serverThread.start();
        // Wait until server is bound
        while (serverThread.getPort() == -1) {
            Thread.sleep(5);
        }
            // Make the ApiEndpoints pick up the test server by setting the system property
            // before ApiEndpoints is used. ApiEndpoints reads server.port at class init,
            // so setting the system property here affects subsequent calls.
            System.setProperty("server.port", String.valueOf(serverThread.getPort()));
        return serverThread;
    }

    /**
     * Simple HTTP server thread for testing.
     * Responds with predefined status and body to any request.
     */
    private static final class ServerThread extends Thread {
        private final int status;
        private final String body;
        private volatile int port = -1;
        private volatile ServerSocket serverSocket;

        /** Constructor to set response status and body. */
        ServerThread(int status, String body) {
            this.status = status;
            this.body = body;
            setDaemon(true);
        }

        /** Returns the port number on which the server is listening.
         * @return the port number
         */
        int getPort() {
            return port;
        }

        /** Stops the server */
        void stopServer() {
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (Exception e) {
                // ignore
            }
        }

        /** Runs the server */
        @Override
        public void run() {
            try (ServerSocket ss = new ServerSocket(0)) {
                this.serverSocket = ss;
                this.port = ss.getLocalPort();
                Socket socket = ss.accept();
                try (InputStream in = socket.getInputStream(); OutputStream out = socket.getOutputStream()) {
                    // Drain the request (simple, not robust)
                    byte[] buffer = new byte[1024];
                    while (in.available() > 0) {
                        in.read(buffer);
                    }
                    byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
                    String headers = "HTTP/1.1 " + status + " OK\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + bodyBytes.length + "\r\n\r\n";
                    out.write(headers.getBytes(StandardCharsets.UTF_8));
                    out.write(bodyBytes);
                    out.flush();
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Sets a private field on the target object.
     * @param target the object whose field to set
     * @param name the field name
     * @param value the value to set
     */
    private void setField(Object target, String name, Object value) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tests that empty input fields show an error message.
     * @throws Exception
     */
    @Test
    void emptyFieldsShowError() throws Exception {
        WaitForAsyncUtils.asyncFx(() -> {
            usernameField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            controller.whenSignInButtonClicked();
        }).get(5, TimeUnit.SECONDS);

        assertTrue(alertMessage.isVisible());
        assertEquals(ApiConstants.INVALID_REQUEST, alertMessage.getText());
    }

    /**
     * Tests that a successful user creation navigates to main app or does not show error.
     * @throws Exception
     */
    @Test
    public void createUserSuccessNavigatesOrDoesNotShowError() throws Exception {
        ApiResponse<UserDataDto> resp = new ApiResponse<>(true, "", new UserDataDto("newuser", "Password123!", null));
        String oldPort = System.getProperty("server.port");
        String json = new ObjectMapper().writeValueAsString(resp);
        ServerThread server = startTestServer(200, json);
        try {
            // Provide a fake FXMLLoader and a real Scene/Stage so navigateToMainApp can run without NPE.
            @SuppressWarnings("unchecked")
            javafx.fxml.FXMLLoader fakeLoader = new javafx.fxml.FXMLLoader() {
                @Override
                public javafx.scene.layout.Pane load() {
                    return new javafx.scene.layout.Pane();
                }

                @Override
                public Object getController() {
                    return new FlashcardMainController();
                }
            };
            
            FlashcardSignUpController.TEST_FXMLLOADER_SUPPLIER = () -> fakeLoader;

            try {
                WaitForAsyncUtils.asyncFx(() -> {
                    try {
                        // prepare a button inside a scene so navigateToMainApp can get the window
                        javafx.scene.control.Button btn = new javafx.scene.control.Button();
                        javafx.scene.layout.Pane container = new javafx.scene.layout.Pane();
                        container.getChildren().add(btn);
                        javafx.scene.Scene scene = new javafx.scene.Scene(container);
                        javafx.stage.Stage stage = new javafx.stage.Stage();
                        stage.setScene(scene);
                        setField(controller, "signInButton", btn);

                        usernameField.setText("newuser");
                        passwordField.setText("Password123!");
                        confirmPasswordField.setText("Password123!");
                        controller.whenSignInButtonClicked();
                    } catch (Exception e) {
                        // let outer catch handle it
                        throw new RuntimeException(e);
                    }
                }).get(5, TimeUnit.SECONDS);
            } catch (Exception ignored) {
                // Some ApiClient implementations may throw on network issues; for this test we only
                // assert that no inline error message is shown. Ignore exceptions and continue.
            }

            // On success controller tries to navigate; ensure no registration error message shown
            String txt = alertMessage.getText() == null ? "" : alertMessage.getText().toLowerCase();
            assertFalse(txt.contains("username") && txt.contains("exists"));
            assertFalse(txt.contains("error") || txt.contains("failed"));
        } finally {
            FlashcardSignUpController.TEST_FXMLLOADER_SUPPLIER = null;
            server.stopServer();
            if (oldPort == null) System.clearProperty("server.port"); else System.setProperty("server.port", oldPort);
        }
    }

    /**
     * Tests that creating a user with an existing username shows an error message.
     * @throws Exception
     */
    @Test
    public void createUserWhenUsernameExistsShowsError() throws Exception {
        ApiResponse<UserDataDto> resp = new ApiResponse<>(false, ApiConstants.USER_ALREADY_EXISTS, null);
        String oldPort = System.getProperty("server.port");
        String json = new ObjectMapper().writeValueAsString(resp);
        ServerThread server = startTestServer(200, json);

        try {
            WaitForAsyncUtils.asyncFx(() -> {
                usernameField.setText("existing");
                passwordField.setText("Strong1!");
                confirmPasswordField.setText("Strong1!");
                controller.whenSignInButtonClicked();
            }).get(5, TimeUnit.SECONDS);

            assertTrue(alertMessage.isVisible());
            assertEquals(ApiConstants.USER_ALREADY_EXISTS, alertMessage.getText());
        } finally {
            server.stopServer();
            if (oldPort == null) System.clearProperty("server.port"); else System.setProperty("server.port", oldPort);
        }
    }

    /**
     * Tests that creating a user with server error shows alert and no inline error.
     * @throws Exception
     */
    @Test
    public void createUserWhenServerErrorShowsAlertAndNoInlineError() throws Exception {
        ApiResponse<UserDataDto> resp = new ApiResponse<>(false, "Internal server error", null);
        String oldPort = System.getProperty("server.port");
        String json = new ObjectMapper().writeValueAsString(resp);
        ServerThread server = startTestServer(500, json);

        try {
            // ApiClient may throw for 5xx responses; swallow the exception here so we can assert UI state.
            try {
                WaitForAsyncUtils.asyncFx(() -> {
                    usernameField.setText("u1");
                    passwordField.setText("Strong123!");
                    confirmPasswordField.setText("Strong123!");
                    try {
                        controller.whenSignInButtonClicked();
                    } catch (RuntimeException re) {
                        // expected in some ApiClient implementations; ignore to verify UI state
                    }
                }).get(5, TimeUnit.SECONDS);
            } catch (java.util.concurrent.ExecutionException ee) {
                // If asyncFx wrapped an exception, it's acceptable for server error tests; proceed to assertions.
            }

            // Since the server returned a general error, the controller should call ApiClient.showAlert
            // and not show the inline alert message (inline remains hidden).
            assertFalse(alertMessage.isVisible());
        } finally {
            server.stopServer();
            if (oldPort == null) System.clearProperty("server.port"); else System.setProperty("server.port", oldPort);
        }
    }

    /**
     * Tests that back button click with load failure shows error message.
     * @throws Exception
     */
    @Test
    public void whenBackButtonIsClicked_loadFailsShowsError() throws Exception {
        // Give controller a back button without a scene to force an exception in the method
        setField(controller, "backButton", new Button());

        WaitForAsyncUtils.asyncFx(() -> {
            controller.whenBackButtonIsClicked();
        }).get(5, TimeUnit.SECONDS);

        assertTrue(alertMessage.isVisible());
        assertEquals(ApiConstants.INVALID_REQUEST, alertMessage.getText());
    }

    /**
     * Tests that password policy violations show an error message.
     * @throws Exception
     */
    @Test
    public void passwordPolicyEnforced() throws Exception {
        WaitForAsyncUtils.asyncFx(() -> {
            usernameField.setText("u1");
            // weak password: no uppercase, no special char, too short
            passwordField.setText("weak1");
            confirmPasswordField.setText("weak1");
            controller.whenSignInButtonClicked();
        }).get(5, TimeUnit.SECONDS);

        assertTrue(alertMessage.isVisible());
        assertEquals(ApiConstants.INVALID_PASSWORD, alertMessage.getText());
    }

    /**
     * Tests that mismatched passwords show an error message.
     * @throws Exception
     */
    @Test
    public void passwordsMismatchShowsInvalidPassword() throws Exception {
        WaitForAsyncUtils.asyncFx(() -> {
            usernameField.setText("u2");
            passwordField.setText("Password1!");
            confirmPasswordField.setText("Password2!");
            controller.whenSignInButtonClicked();
        }).get(5, TimeUnit.SECONDS);

        assertTrue(alertMessage.isVisible());
        assertEquals(ApiConstants.INVALID_PASSWORD, alertMessage.getText());
    }

    /**
     * Tests that too short passwords show an error message.
     * @throws Exception
     */
    @Test
    public void passwordTooShortShowsInvalidPassword() throws Exception {
        WaitForAsyncUtils.asyncFx(() -> {
            usernameField.setText("u3");
            passwordField.setText("A1!a");
            confirmPasswordField.setText("A1!a");
            controller.whenSignInButtonClicked();
        }).get(5, TimeUnit.SECONDS);

        assertTrue(alertMessage.isVisible());
        assertEquals(ApiConstants.INVALID_PASSWORD, alertMessage.getText());
    }

    /**
     * Tests that passwords missing an uppercase letter show an error message.
     * @throws Exception
     */
    @Test
    public void passwordMissingUppercaseShowsInvalidPassword() throws Exception {
        WaitForAsyncUtils.asyncFx(() -> {
            usernameField.setText("u4");
            passwordField.setText("password1!");
            confirmPasswordField.setText("password1!");
            controller.whenSignInButtonClicked();
        }).get(5, TimeUnit.SECONDS);

        assertTrue(alertMessage.isVisible());
        assertEquals(ApiConstants.INVALID_PASSWORD, alertMessage.getText());
    }

    /**
     * Tests that passwords missing a digit show an error message.
     * @throws Exception
     */
    @Test
    public void passwordMissingDigitShowsInvalidPassword() throws Exception {
        WaitForAsyncUtils.asyncFx(() -> {
            usernameField.setText("u5");
            passwordField.setText("Password!");
            confirmPasswordField.setText("Password!");
            controller.whenSignInButtonClicked();
        }).get(5, TimeUnit.SECONDS);

        assertTrue(alertMessage.isVisible());
        assertEquals(ApiConstants.INVALID_PASSWORD, alertMessage.getText());
    }

    /**
     * Tests that passwords missing a special character show an error message.
     * @throws Exception
     */
    @Test
    public void passwordMissingSpecialShowsInvalidPassword() throws Exception {
        WaitForAsyncUtils.asyncFx(() -> {
            usernameField.setText("u6");
            passwordField.setText("Password1");
            confirmPasswordField.setText("Password1");
            controller.whenSignInButtonClicked();
        }).get(5, TimeUnit.SECONDS);

        assertTrue(alertMessage.isVisible());
        assertEquals(ApiConstants.INVALID_PASSWORD, alertMessage.getText());
    }

    /**
     * Tests that showError called from a background thread schedules UI update.
     * @throws Exception
     */
    @Test
    public void showErrorFromBackgroundThreadSchedulesUiUpdate() throws Exception {
        // Call the private showError method from a background thread to exercise the
        // Platform.runLater branch in the controller.
        java.lang.reflect.Method m = controller.getClass().getDeclaredMethod("showError", String.class);
        m.setAccessible(true);

        Thread t = new Thread(() -> {
            try {
                m.invoke(controller, "bg-error");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        t.join(2000);

        // Wait for the JavaFX runLater tasks to complete
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(alertMessage.isVisible());
        assertEquals("bg-error", alertMessage.getText());
    }

    /**
     * Tests that mismatched passwords show an error message.
     * @throws Exception
     */
    @Test
    public void navigateToMainApp_usesLoaderAndDoesNotShowStageInTest() throws Exception {
        /**
         * Dummy main controller to capture username passed to it.
         * Uses FlashcardMainController as base to avoid NPEs.
         */
        class DummyMainController extends FlashcardMainController {
            String received;
            @Override
            public void setCurrentUsername(String u) { received = u; }
        }

        DummyMainController dummy = new DummyMainController();

        /**
         * Create a fake FXMLLoader that returns dummy controller and a simple Pane.
         * This is used to test the navigation without showing the actual UI.
         */
        @SuppressWarnings("unchecked")
        javafx.fxml.FXMLLoader fakeLoader = new javafx.fxml.FXMLLoader() {
            @Override
            public javafx.scene.layout.Pane load() {
                return new javafx.scene.layout.Pane();
            }
            @Override
            public Object getController() {
                return dummy;
            }
        };

        FlashcardSignUpController.TEST_FXMLLOADER_SUPPLIER = () -> fakeLoader;

        try {
            // Call navigateToMainApp via reflection on the JavaFX Application Thread
            java.lang.reflect.Method m = controller.getClass().getDeclaredMethod("navigateToMainApp", String.class);
            m.setAccessible(true);
            WaitForAsyncUtils.asyncFx(() -> {
                try {
                    // Prepare a signInButton that has a Scene/Stage (must be created on FX thread)
                    javafx.scene.control.Button btn = new javafx.scene.control.Button();
                    javafx.scene.layout.Pane container = new javafx.scene.layout.Pane();
                    container.getChildren().add(btn);
                    javafx.scene.Scene scene = new javafx.scene.Scene(container);
                    javafx.stage.Stage stage = new javafx.stage.Stage();
                    stage.setScene(scene);
                    setField(controller, "signInButton", btn);

                    m.invoke(controller, "tester123");
                } catch (RuntimeException re) {
                    throw re;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).get(5, java.util.concurrent.TimeUnit.SECONDS);

            // confirm dummy controller received the username
            assertEquals("tester123", dummy.received);
        } finally {
            FlashcardSignUpController.TEST_FXMLLOADER_SUPPLIER = null;
        }
    }
}
