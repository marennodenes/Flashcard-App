package ui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;
import dto.LoginRequestDto;
import dto.LoginResponseDto;
import dto.UserDataDto;
import shared.ApiResponse;
import shared.ApiEndpoints;
import com.fasterxml.jackson.core.type.TypeReference;

public class FlashcardLoginControllerTest {
    
    private FlashcardLoginController controller;
    
    @Mock
    private Text alertMessage;
    
    @Mock 
    private Text ex;
    
    @Mock
    private Button loginButton;
    
    @Mock
    private TextField usernameField;
    
    @Mock
    private TextField passwordField;
    
    @Mock
    private Button signUpButton;
    
    @Mock
    private Stage stage;
    
    @Mock
    private Scene scene;

    @BeforeAll
    public static void initJavaFX() throws InterruptedException {
        // Initialize JavaFX toolkit
        if (!Platform.isFxApplicationThread()) {
            try {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.startup(() -> latch.countDown());
                latch.await();
            } catch (IllegalStateException e) {
                // Toolkit already initialized
            }
        }
    }

    @AfterAll
    public static void tearDown() {
        if (Platform.isFxApplicationThread()) {
            Platform.exit();
        }
    }



    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new FlashcardLoginController();
        
        // Inject mocked FXML fields using reflection
        injectMockField("alertMessage", alertMessage);
        injectMockField("ex", ex);
        injectMockField("loginButton", loginButton);
        injectMockField("usernameField", usernameField);
        injectMockField("passwordField", passwordField);
        injectMockField("signUpButton", signUpButton);
        
        // Set up common mock behaviors
        when(loginButton.getScene()).thenReturn(scene);
        when(signUpButton.getScene()).thenReturn(scene);
        when(scene.getWindow()).thenReturn(stage);
    }
    
    private void injectMockField(String fieldName, Object mockObject) throws Exception {
        Field field = FlashcardLoginController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, mockObject);
    }


@SuppressWarnings("unchecked")
@Test
public void testWhenLoginButtonClicked_emptyUsername() throws Exception {
    when(usernameField.getText()).thenReturn(""); // Empty username
    when(passwordField.getText()).thenReturn("password"); // Non-empty password
    
    controller.whenLoginButtonClicked();
    
    verify(alertMessage).setText("Username and password\ncannot be empty");
    verify(alertMessage).setVisible(true);
    verify(ex).setVisible(true);
}

@SuppressWarnings("unchecked")
@Test
public void testWhenLoginButtonClicked_emptyPassword() throws Exception {
    when(usernameField.getText()).thenReturn("username"); // Non-empty username
    when(passwordField.getText()).thenReturn(""); // Empty password
    
    controller.whenLoginButtonClicked();
    
    verify(alertMessage).setText("Username and password\ncannot be empty");
    verify(alertMessage).setVisible(true);
    verify(ex).setVisible(true);
}

@SuppressWarnings("unchecked")
@Test
public void testWhenLoginButtonClicked_successfulLogin() throws Exception {
    when(usernameField.getText()).thenReturn("user");
    when(passwordField.getText()).thenReturn("pass");
    LoginResponseDto mockLoginResponse = mock(LoginResponseDto.class);
    when(mockLoginResponse.isSuccess()).thenReturn(true);
    ApiResponse<LoginResponseDto> mockApiResponse = mock(ApiResponse.class);
    when(mockApiResponse.isSuccess()).thenReturn(true);
    when(mockApiResponse.getData()).thenReturn(mockLoginResponse);

    try (MockedStatic<ApiClient> mockedApiClient = mockStatic(ApiClient.class)) {
        mockedApiClient.when(() -> ApiClient.performApiRequest(
            anyString(), anyString(), any(LoginRequestDto.class), any(TypeReference.class)))
            .thenReturn(mockApiResponse);

        // Use a spy and override the public method to avoid actual navigation
        FlashcardLoginController spyController = spy(controller);

        // Use reflection to make the private method accessible and stub it
        Method navigateToMainAppMethod = FlashcardLoginController.class.getDeclaredMethod("navigateToMainApp", String.class);
        navigateToMainAppMethod.setAccessible(true);

        // Replace controller with spy for this branch
        Field controllerField = FlashcardLoginControllerTest.class.getDeclaredField("controller");
        controllerField.setAccessible(true);
        controllerField.set(this, spyController);

        // Optionally, use doNothing() with reflection (not directly with Mockito)
        // Or just call the public method and check that no error message is shown
        spyController.whenLoginButtonClicked();

        // Check that no error message is shown
        verify(alertMessage, never()).setText("Username and password\ncannot be empty");
        verify(alertMessage, never()).setText("Invalid credentials");
        verify(alertMessage, never()).setText("Server error");
        verify(alertMessage, never()).setText("Failed to load main application");
    }
}

@SuppressWarnings("unchecked")
@Test
public void testWhenLoginButtonClicked_invalidCredentials() throws Exception {
    when(usernameField.getText()).thenReturn("user");
    when(passwordField.getText()).thenReturn("pass");
    LoginResponseDto mockLoginResponseFail = mock(LoginResponseDto.class);
    when(mockLoginResponseFail.isSuccess()).thenReturn(false);
    when(mockLoginResponseFail.getMessage()).thenReturn("Invalid credentials");
    ApiResponse<LoginResponseDto> mockApiResponseFail = mock(ApiResponse.class);
    when(mockApiResponseFail.isSuccess()).thenReturn(true);
    when(mockApiResponseFail.getData()).thenReturn(mockLoginResponseFail);

    try (MockedStatic<ApiClient> mockedApiClient = mockStatic(ApiClient.class)) {
        mockedApiClient.when(() -> ApiClient.performApiRequest(
            anyString(), anyString(), any(LoginRequestDto.class), any(TypeReference.class)))
            .thenReturn(mockApiResponseFail);

        controller.whenLoginButtonClicked();
        verify(alertMessage).setText("Invalid credentials");
        verify(alertMessage, atLeastOnce()).setVisible(true);
        verify(ex, atLeastOnce()).setVisible(true);
    }
}


@Test
public void testWhenLoginButtonClicked_apiError() throws Exception {
    when(usernameField.getText()).thenReturn("user");
    when(passwordField.getText()).thenReturn("pass");
    ApiResponse<LoginResponseDto> mockApiResponseError = mock(ApiResponse.class);
    when(mockApiResponseError.isSuccess()).thenReturn(false);
    when(mockApiResponseError.getMessage()).thenReturn("Server error");

    try (MockedStatic<ApiClient> mockedApiClient = mockStatic(ApiClient.class)) {
        mockedApiClient.when(() -> ApiClient.performApiRequest(
            anyString(), anyString(), any(LoginRequestDto.class), any(TypeReference.class)))
            .thenReturn(mockApiResponseError);

        controller.whenLoginButtonClicked();
        verify(alertMessage).setText("Server error");
        verify(alertMessage, atLeastOnce()).setVisible(true);
        verify(ex, atLeastOnce()).setVisible(true);
    }
}


@SuppressWarnings("unchecked")
@Test
public void testWhenLoginButtonClicked_navigateToMainAppIOException() throws Exception {
    // Arrange: valid username/password
    when(usernameField.getText()).thenReturn("user");
    when(passwordField.getText()).thenReturn("pass");

    // Mock successful login response
    LoginResponseDto mockLoginResponse = mock(LoginResponseDto.class);
    when(mockLoginResponse.isSuccess()).thenReturn(true);
    ApiResponse<LoginResponseDto> mockApiResponse = mock(ApiResponse.class);
    when(mockApiResponse.isSuccess()).thenReturn(true);
    when(mockApiResponse.getData()).thenReturn(mockLoginResponse);

    try (MockedStatic<ApiClient> mockedApiClient = mockStatic(ApiClient.class);
         MockedConstruction<FXMLLoader> mockedFXMLLoader = mockConstruction(FXMLLoader.class,
             (loader, context) -> {
                 when(loader.load()).thenThrow(new IOException("Simulated IO error"));
             })) {

        mockedApiClient.when(() -> ApiClient.performApiRequest(
            anyString(), anyString(), any(LoginRequestDto.class), any(TypeReference.class)))
            .thenReturn(mockApiResponse);

        // Act: call the public method, which will internally call navigateToMainApp and hit the IOException
        controller.whenLoginButtonClicked();

        // Assert: error message shown
        verify(alertMessage).setText("Failed to load main application");
        verify(alertMessage, atLeastOnce()).setVisible(true);
        verify(ex, atLeastOnce()).setVisible(true);
    }
}
    
@Test
public void testNavigateToMainApp_success() throws Exception {
    // Arrange
    String username = "testuser";
    FXMLLoader mockLoader = mock(FXMLLoader.class);
    Parent realRoot = new javafx.scene.layout.Pane(); // Use a real JavaFX Parent subclass
    FlashcardMainController mockMainController = mock(FlashcardMainController.class);
    Stage mockStage = mock(Stage.class);
    Scene mockScene = mock(Scene.class);

    // Mock FXMLLoader construction
    try (MockedConstruction<FXMLLoader> mocked = mockConstruction(FXMLLoader.class,
        (loader, context) -> {
            when(loader.load()).thenReturn(realRoot);
            when(loader.getController()).thenReturn(mockMainController);
        })) {

        // Inject loginButton and its scene/stage
        injectMockField("loginButton", loginButton);
        when(loginButton.getScene()).thenReturn(mockScene);
        when(mockScene.getWindow()).thenReturn(mockStage);

        // Make the private method accessible
        Method navigateToMainAppMethod = FlashcardLoginController.class.getDeclaredMethod("navigateToMainApp", String.class);
        navigateToMainAppMethod.setAccessible(true);

        // Act
        navigateToMainAppMethod.invoke(controller, username);

        // Assert
        verify(mockMainController).setCurrentUsername(username);
        verify(mockStage).setScene(any(Scene.class));
        verify(mockStage).show();
    }
}

@Test
public void testNavigateToMainApp_failure() throws Exception {
    // Arrange
    String username = "testuser";

    // Mock FXMLLoader construction
    try (MockedConstruction<FXMLLoader> mocked = mockConstruction(FXMLLoader.class,
        (loader, context) -> {
            when(loader.load()).thenThrow(new IOException("Load failed"));
        })) {

        injectMockField("loginButton", loginButton);
        when(loginButton.getScene()).thenReturn(scene);
        when(scene.getWindow()).thenReturn(stage);

        var method = FlashcardLoginController.class.getDeclaredMethod("navigateToMainApp", String.class);
        method.setAccessible(true);

        // Act & Assert
        Exception thrown = assertThrows(Exception.class, () -> method.invoke(controller, username));
        // Unwrap InvocationTargetException
        Throwable cause = thrown.getCause();
        assertTrue(cause instanceof IOException);
        assertEquals("Load failed", cause.getMessage());
    }
}

   @Test
    public void testUpdateUiShowAlertTrue() throws Exception {
        FlashcardLoginController controller = new FlashcardLoginController();

        // Access private fields via reflection
        Field showAlertField = FlashcardLoginController.class.getDeclaredField("showAlert");
        Field errorField = FlashcardLoginController.class.getDeclaredField("error");
        Field alertMessageField = FlashcardLoginController.class.getDeclaredField("alertMessage");
        Field exField = FlashcardLoginController.class.getDeclaredField("ex");

        showAlertField.setAccessible(true);
        errorField.setAccessible(true);
        alertMessageField.setAccessible(true);
        exField.setAccessible(true);

        // Mock UI components
        Text mockAlert = new Text();
        Text mockEx = new Text();

        alertMessageField.set(controller, mockAlert);
        exField.set(controller, mockEx);

        // Set up state for the 'true' branch
        showAlertField.set(controller, true);
        errorField.set(controller, "Invalid credentials");

        // Act
        controller.updateUi();

        // Assert (verify side effects)
        assertFalse((boolean) showAlertField.get(controller)); // should reset to false
        assertEquals("Invalid credentials", mockAlert.getText());
        assertTrue(mockAlert.isVisible());
        assertTrue(mockEx.isVisible());
    }

    @Test
    public void testUpdateUiShowAlertFalse() throws Exception {
        FlashcardLoginController controller = new FlashcardLoginController();

        Field showAlertField = FlashcardLoginController.class.getDeclaredField("showAlert");
        Field alertMessageField = FlashcardLoginController.class.getDeclaredField("alertMessage");
        Field exField = FlashcardLoginController.class.getDeclaredField("ex");

        showAlertField.setAccessible(true);
        alertMessageField.setAccessible(true);
        exField.setAccessible(true);

        // Mock UI components
        Text mockAlert = new Text();
        Text mockEx = new Text();

        mockAlert.setVisible(true);
        mockEx.setVisible(true);

        alertMessageField.set(controller, mockAlert);
        exField.set(controller, mockEx);

        showAlertField.set(controller, false);

        // Act
        controller.updateUi();

        // Assert
        assertFalse(mockAlert.isVisible());
        assertFalse(mockEx.isVisible());
    }


    @Test
public void testNavigateToSignUpPageSuccess() throws Exception {
    // Arrange
    FXMLLoader mockLoader = mock(FXMLLoader.class);
    Parent realRoot = new javafx.scene.layout.Pane(); // Use a real JavaFX Parent subclass
    Stage mockStage = mock(Stage.class);
    Scene mockScene = mock(Scene.class);

    try (MockedConstruction<FXMLLoader> mocked = mockConstruction(FXMLLoader.class,
        (loader, context) -> {
            when(loader.load()).thenReturn(realRoot);
        })) {

        injectMockField("signUpButton", signUpButton);
        when(signUpButton.getScene()).thenReturn(mockScene);
        when(mockScene.getWindow()).thenReturn(mockStage);

        // Make the private method accessible
        Method method = FlashcardLoginController.class.getDeclaredMethod("navigateToSignUpPage");
        method.setAccessible(true);

        // Act
        method.invoke(controller);

        // Assert
        verify(mockStage).setScene(any(Scene.class));
        verify(mockStage).show();
    }
}

@Test
public void testNavigateToSignUpPageFailure() throws Exception {
    // Arrange
    try (MockedConstruction<FXMLLoader> mocked = mockConstruction(FXMLLoader.class,
        (loader, context) -> {
            when(loader.load()).thenThrow(new IOException("Load failed"));
        })) {

        injectMockField("signUpButton", signUpButton);
        when(signUpButton.getScene()).thenReturn(scene);
        when(scene.getWindow()).thenReturn(stage);

        Method method = FlashcardLoginController.class.getDeclaredMethod("navigateToSignUpPage");
        method.setAccessible(true);

        // Act & Assert
        Exception thrown = assertThrows(Exception.class, () -> method.invoke(controller));
        Throwable cause = thrown.getCause();
        assertTrue(cause instanceof IOException);
        assertEquals("Load failed", cause.getMessage());
    }
}

    @Test
    public void testWhenSignUpButtonClicked_success() throws Exception {
        // Mock successful FXMLLoader construction (same pattern as your other success tests)
        Parent realRoot = new javafx.scene.layout.Pane();
        Stage mockStage = mock(Stage.class);
        Scene mockScene = mock(Scene.class);

        try (MockedConstruction<FXMLLoader> mockedFXMLLoader = mockConstruction(FXMLLoader.class,
             (loader, context) -> {
                 when(loader.load()).thenReturn(realRoot); // Make load() succeed
             })) {
            
            // Set up mocks for the navigation
            injectMockField("signUpButton", signUpButton);
            when(signUpButton.getScene()).thenReturn(mockScene);
            when(mockScene.getWindow()).thenReturn(mockStage);
            
            // Act
            controller.whenSignUpButtonClicked();
            
            // Assert: no error should be shown and navigation should succeed
            verify(alertMessage, never()).setText("Failed to load signup page");
            verify(mockStage).setScene(any(Scene.class));
            verify(mockStage).show();
        }
    }

    @Test
    public void testWhenSignUpButtonClicked_ioException() throws Exception {
        // Use MockedConstruction to simulate IOException (same pattern as your other working tests)
        try (MockedConstruction<FXMLLoader> mockedFXMLLoader = mockConstruction(FXMLLoader.class,
             (loader, context) -> {
                 when(loader.load()).thenThrow(new IOException("Simulated navigation error"));
             })) {
            
            // Act
            controller.whenSignUpButtonClicked();
            
            // Assert: error message should be shown
            verify(alertMessage).setText("Failed to load signup page");
            verify(alertMessage, atLeastOnce()).setVisible(true);
            verify(ex, atLeastOnce()).setVisible(true);
        }
    }

    @Test
    public void testInitialize() throws Exception {
        // Since showAlert defaults to false, initialize should hide the alert elements
        
        // Act
        controller.initialize();
        
        // Assert: verify that alert elements are hidden (default state)
        verify(alertMessage).setVisible(false);
        verify(ex).setVisible(false);
    }

    @Test
    public void testFlashcardLoginController() {
        // TODO: Test constructor
        assertNotNull(controller);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testWhenLoginButtonClicked_successfulApiButNullData() throws Exception {
        when(usernameField.getText()).thenReturn("user");
        when(passwordField.getText()).thenReturn("pass");
        
        // Mock API response that is successful but returns null data
        ApiResponse<LoginResponseDto> mockApiResponse = mock(ApiResponse.class);
        when(mockApiResponse.isSuccess()).thenReturn(true);
        when(mockApiResponse.getData()).thenReturn(null); // This is the key difference
        when(mockApiResponse.getMessage()).thenReturn("No user data received");

        try (MockedStatic<ApiClient> mockedApiClient = mockStatic(ApiClient.class)) {
            mockedApiClient.when(() -> ApiClient.performApiRequest(
                anyString(), anyString(), any(LoginRequestDto.class), any(TypeReference.class)))
                .thenReturn(mockApiResponse);

            controller.whenLoginButtonClicked();
            
            // Should hit the else branch and show the API response message
            verify(alertMessage).setText("No user data received");
            verify(alertMessage, atLeastOnce()).setVisible(true);
            verify(ex, atLeastOnce()).setVisible(true);
        }
    }
}
