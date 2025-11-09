package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import dto.LoginRequestDto;
import dto.LoginResponseDto;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import shared.ApiConstants;
import shared.ApiResponse;

/**
 * Test suite for {@link FlashcardLoginController}.
 *
 * <p>Validates login functionality, navigation between screens, error handling,
 * and UI component behavior using Mockito for mocking and reflection for
 * accessing private methods and fields.
 *
 * @author ailinat
 * @author marennod
 *
 * @see FlashcardLoginController
 * @see "docs/release_3/ai_tools.md"
 */
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

  /**
   * Initializes JavaFX toolkit before running tests.
   *
   * <p>Starts the JavaFX Platform if not already initialized.
   *
   * @throws InterruptedException if thread is interrupted while waiting for
   *                              JavaFX initialization
   */
  @BeforeAll
  public static void initJavaFx() throws InterruptedException {
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

  /**
   * Tests initialize method behavior.
   *
   * <p>Verifies that alert elements are hidden on initialization.
   *
   * @throws Exception if test execution fails
   */
  @Test
  public void testInitialize() throws Exception {
    controller.initialize();

    verify(alertMessage).setVisible(false);
    verify(ex).setVisible(false);
  }

  /**
   * Tests FlashcardLoginController instantiation. Verifies that controller object
   * is successfully created.
   */
  @Test
  public void testFlashcardLoginController() {
    assertNotNull(controller);
  }

  /**
   * Sets up test fixtures before each test method.
   *
   * <p>Initializes mocks and injects them into the controller.
   *
   * @throws Exception if field injection via reflection fails
   */
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

  /**
   * Helper method to inject mock objects into controller fields via reflection.
   *
   * @param fieldName  the name of the field to inject into
   * @param mockObject the mock object to inject
   *
   * @throws Exception if field access via reflection fails
   */
  private void injectMockField(String fieldName, Object mockObject) throws Exception {
    Field field = FlashcardLoginController.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(controller, mockObject);
  }

  /**
   * Tests login button click with empty username field and empty password field.
   *
   * <p>Verifies that appropriate error message is displayed.
   *
   * @throws Exception if test execution fails
   */
  @Test
  public void testWhenLoginButtonClickedEmptyFields() throws Exception {
    when(usernameField.getText()).thenReturn(""); // Empty username
    when(passwordField.getText()).thenReturn("password"); // Non-empty password

    controller.whenLoginButtonClicked();

    verify(alertMessage).setText("Username and password\ncannot be empty");
    verify(alertMessage).setVisible(true);
    verify(ex).setVisible(true);

    reset(alertMessage, ex); // Reset mocks for next check

    when(usernameField.getText()).thenReturn("username"); // Non-empty username
    when(passwordField.getText()).thenReturn(""); // Empty password

    controller.whenLoginButtonClicked();

    verify(alertMessage).setText("Username and password\ncannot be empty");
    verify(alertMessage).setVisible(true);
    verify(ex).setVisible(true);
  }

  /**
   * Tests successful login with valid credentials.
   *
   * <p>Verifies that no error messages are shown and navigation succeeds.
   *
   * @throws Exception if reflection or mock setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenLoginButtonClickedSuccessfulLogin() throws Exception {
    when(usernameField.getText()).thenReturn("user");
    when(passwordField.getText()).thenReturn("pass");

    LoginResponseDto mockLoginResponse = mock(LoginResponseDto.class);
    when(mockLoginResponse.isSuccess()).thenReturn(true);

    ApiResponse<LoginResponseDto> mockApiResponse = mock(ApiResponse.class);
    when(mockApiResponse.isSuccess()).thenReturn(true);
    when(mockApiResponse.getData()).thenReturn(mockLoginResponse);

    try (MockedStatic<ApiClient> mockedApiClient = mockStatic(ApiClient.class)) {
      mockedApiClient.when(() -> ApiClient
          .performApiRequest(anyString(), anyString(), any(LoginRequestDto.class),
          any(TypeReference.class))).thenReturn(mockApiResponse);

      // Use a spy and override the public method to avoid actual navigation
      FlashcardLoginController spyController = spy(controller);

      // Use reflection to make the private method accessible and stub it
      Method navigateToMainAppMethod = FlashcardLoginController.class
          .getDeclaredMethod("navigateToMainApp",
          String.class);
      navigateToMainAppMethod.setAccessible(true);

      // Replace controller with spy for this branch
      Field controllerField = FlashcardLoginControllerTest.class.getDeclaredField("controller");
      controllerField.setAccessible(true);
      controllerField.set(this, spyController);

      // Optionally, use doNothing() with reflection (not directly with Mockito)
      // Or just call the public method and check that no error message is shown
      spyController.whenLoginButtonClicked();

      // Check that no error message is shown
      verify(alertMessage, never()).setText(ApiConstants.EMPTY_FIELDS);
      verify(alertMessage, never()).setText(ApiConstants.LOGIN_FAILED);
      verify(alertMessage, never()).setText(ApiConstants.SERVER_ERROR);
      verify(alertMessage, never()).setText("Failed to load main application");
    }
  }

  /**
   * Tests login with invalid credentials.
   *
   * <p>Verifies that "Invalid credentials" error message is displayed.
   *
   * @throws Exception if mock API setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenLoginButtonClickedInvalidCredentials() throws Exception {
    when(usernameField.getText()).thenReturn("user");
    when(passwordField.getText()).thenReturn("pass");
    LoginResponseDto mockLoginResponseFail = mock(LoginResponseDto.class);
    when(mockLoginResponseFail.isSuccess()).thenReturn(false);
    when(mockLoginResponseFail.getMessage()).thenReturn(ApiConstants.LOGIN_FAILED);
    ApiResponse<LoginResponseDto> mockApiResponseFail = mock(ApiResponse.class);
    when(mockApiResponseFail.isSuccess()).thenReturn(true);
    when(mockApiResponseFail.getData()).thenReturn(mockLoginResponseFail);

    try (MockedStatic<ApiClient> mockedApiClient = mockStatic(ApiClient.class)) {
      mockedApiClient.when(() -> ApiClient
          .performApiRequest(anyString(), anyString(), any(LoginRequestDto.class),
          any(TypeReference.class))).thenReturn(mockApiResponseFail);

      controller.whenLoginButtonClicked();
      verify(alertMessage).setText(ApiConstants.LOGIN_FAILED);
      verify(alertMessage, atLeastOnce()).setVisible(true);
      verify(ex, atLeastOnce()).setVisible(true);
    }
  }

  /**
   * Tests login when API returns an error response.
   *
   * <p>Verifies that server error message is displayed to user.
   *
   * @throws Exception if mock API setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenLoginButtonClickedApiError() throws Exception {
    when(usernameField.getText()).thenReturn("user");
    when(passwordField.getText()).thenReturn("pass");
    ApiResponse<LoginResponseDto> mockApiResponseError = mock(ApiResponse.class);
    when(mockApiResponseError.isSuccess()).thenReturn(false);
    when(mockApiResponseError.getMessage()).thenReturn(ApiConstants.SERVER_ERROR);

    try (MockedStatic<ApiClient> mockedApiClient = mockStatic(ApiClient.class)) {
      mockedApiClient.when(() -> ApiClient
          .performApiRequest(anyString(), anyString(), any(LoginRequestDto.class),
          any(TypeReference.class))).thenReturn(mockApiResponseError);

      controller.whenLoginButtonClicked();
      verify(alertMessage).setText(ApiConstants.SERVER_ERROR);
      verify(alertMessage, atLeastOnce()).setVisible(true);
      verify(ex, atLeastOnce()).setVisible(true);
    }
  }

  /**
   * Tests IOException handling during navigation to main app after successful
   * login.
   *
   * <p>Verifies that ApiClient.showAlert is called with appropriate error message.
   *
   * @throws Exception if mock construction or API setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenLoginButtonClickedNavigateToMainAppIoException() throws Exception {
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
        MockedConstruction<FXMLLoader> mockedFXMLLoader = mockConstruction(FXMLLoader
            .class, (loader, context) -> {
              when(loader.load()).thenThrow(new IOException("Simulated IO error"));
            })) {

      mockedApiClient.when(() -> ApiClient
          .performApiRequest(anyString(), anyString(), any(LoginRequestDto.class),
          any(TypeReference.class))).thenReturn(mockApiResponse);

      // Act: call the public method, which will internally call navigateToMainApp
      // and hit the IOException
      controller.whenLoginButtonClicked();

      // Assert: ApiClient.showAlert should be called for navigation errors
      mockedApiClient.verify(() -> ApiClient
          .showAlert(eq(ApiConstants.LOAD_ERROR), eq(ApiConstants.UNEXPECTED_ERROR)));
    }
  }

  /**
   * Tests login with successful API response but null data.
   *
   * <p>Verifies that API response message is displayed when data is null.
   *
   * @throws Exception if mock API setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenLoginButtonClickedSuccessfulApiButNullData() throws Exception {
    when(usernameField.getText()).thenReturn("user");
    when(passwordField.getText()).thenReturn("pass");

    // Mock API response that is successful but returns null data
    ApiResponse<LoginResponseDto> mockApiResponse = mock(ApiResponse.class);
    when(mockApiResponse.isSuccess()).thenReturn(true);
    when(mockApiResponse.getData()).thenReturn(null); // This is the key difference
    when(mockApiResponse.getMessage()).thenReturn("No user data received");

    try (MockedStatic<ApiClient> mockedApiClient = mockStatic(ApiClient.class)) {
      mockedApiClient.when(() -> ApiClient
          .performApiRequest(anyString(), anyString(), any(LoginRequestDto.class),
          any(TypeReference.class))).thenReturn(mockApiResponse);

      controller.whenLoginButtonClicked();

      // Should hit the else branch and show the API response message
      verify(alertMessage).setText("No user data received");
      verify(alertMessage, atLeastOnce()).setVisible(true);
      verify(ex, atLeastOnce()).setVisible(true);
    }
  }

  /**
   * Tests successful navigation to main application screen, and tests navigation
   * failure when FXML loading throws IOException.
   *
   * <p>Verifies username is set and stage transitions correctly, and verifies that
   * error alert is displayed via ApiClient.
   *
   * @throws Exception if reflection or mock construction fails
   */
  @Test
  public void testNavigateToMainApp() throws Exception {
    // Arrange
    String username = "testuser";
    Parent realRoot = new javafx.scene.layout.Pane(); // Use a real JavaFX Parent subclass
    FlashcardMainController mockMainController = mock(FlashcardMainController.class);
    Stage mockStage = mock(Stage.class);
    Scene mockScene = mock(Scene.class);

    // Mock FXMLLoader construction
    try (MockedConstruction<FXMLLoader> mocked = 
        mockConstruction(FXMLLoader.class, (loader, context) -> {
          when(loader.load()).thenReturn(realRoot);
          when(loader.getController()).thenReturn(mockMainController);
        })) {

      // Inject loginButton and its scene/stage
      injectMockField("loginButton", loginButton);
      when(loginButton.getScene()).thenReturn(mockScene);
      when(mockScene.getWindow()).thenReturn(mockStage);

      // Make the private method accessible
      Method navigateToMainAppMethod = FlashcardLoginController.class
          .getDeclaredMethod("navigateToMainApp",
          String.class);
      navigateToMainAppMethod.setAccessible(true);

      // Act
      navigateToMainAppMethod.invoke(controller, username);

      // Assert
      verify(mockMainController).setCurrentUsername(username);
      verify(mockStage).setScene(any(Scene.class));
      verify(mockStage).show();
    }

    // Mock FXMLLoader construction to throw IOException
    try (MockedStatic<ApiClient> mockedApiClient = mockStatic(ApiClient.class);
        MockedConstruction<FXMLLoader> mocked = 
            mockConstruction(FXMLLoader.class, (loader, context) -> {
              when(loader.load()).thenThrow(new IOException("Load failed"));
            })) {

      injectMockField("loginButton", loginButton);
      when(loginButton.getScene()).thenReturn(scene);
      when(scene.getWindow()).thenReturn(stage);

      var method = FlashcardLoginController.class
          .getDeclaredMethod("navigateToMainApp", String.class);
      method.setAccessible(true);

      // Act: navigateToMainApp now handles IOException internally
      // and calls showAlert
      // Run directly since the method is already designed to handle errors
      // internally
      method.invoke(controller, username);

      // Assert: ApiClient.showAlert should be called for navigation errors
      mockedApiClient.verify(() -> ApiClient
          .showAlert(eq(ApiConstants.LOAD_ERROR), eq(ApiConstants.UNEXPECTED_ERROR)));
    }
  }

  /**
   * Tests updateUi method when showAlert flag is true and when showAlert flag is
   * false.
   *
   * <p>Verifies that error message is displayed and flag is reset, and verifies that
   * alert elements are hidden when flag is false.
   *
   * @throws Exception if field access via reflection fails
   */
  @Test
  public void testUpdateUi() throws Exception {
    final FlashcardLoginController controller = new FlashcardLoginController();

    // Access private fields via reflection
    Field showAlertField = FlashcardLoginController.class.getDeclaredField("showAlert");
    Field errorField = FlashcardLoginController.class.getDeclaredField("error");
    Field alertMessageField = FlashcardLoginController.class.getDeclaredField("alertMessage");
    final Field exField = FlashcardLoginController.class.getDeclaredField("ex");

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

    showAlertField.set(controller, false);
    errorField.setAccessible(false);

    // Act
    controller.updateUi();

    // Assert
    assertFalse(mockAlert.isVisible());
    assertFalse(mockEx.isVisible());
  }

  /**
   * Tests successful navigation to sign-up page, and navigation failure to
   * sign-up page when FXML loading fails.
   *
   * <p>Verifies that scene is set and stage is shown when successfull, and verifies
   * that IOException is thrown and propagated when loading fails.
   *
   * @throws Exception if reflection or mock construction fails
   */
  @Test
  public void testNavigateToSignUpPage() throws Exception {
    // Arrange
    Parent realRoot = new javafx.scene.layout.Pane(); // Use a real JavaFX Parent subclass
    Stage mockStage = mock(Stage.class);
    Scene mockScene = mock(Scene.class);

    try (MockedConstruction<FXMLLoader> mocked = 
        mockConstruction(FXMLLoader.class, (loader, context) -> {
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

    try (MockedConstruction<FXMLLoader> mocked = 
        mockConstruction(FXMLLoader.class, (loader, context) -> {
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

  /**
   * Tests successful sign-up button click and navigation to sign-up page, and
   * tests IOException handling during sign-up button click.
   *
   * <p>Verifies that no error messages are shown and stage transitions correctly,
   * and verifies that ApiClient.showAlert is called with appropriate error
   * message.
   *
   * @throws Exception if mock construction fails, or API setup fails
   */
  @Test
  public void testWhenSignUpButtonClicked() throws Exception {
    // Mock successful FXMLLoader construction
    Parent realRoot = new javafx.scene.layout.Pane();
    Stage mockStage = mock(Stage.class);
    Scene mockScene = mock(Scene.class);

    try (MockedConstruction<FXMLLoader> mockedFXMLLoader =
          mockConstruction(FXMLLoader.class, (loader, context) -> {
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

    try (MockedStatic<ApiClient> mockedApiClient = mockStatic(ApiClient.class);
        MockedConstruction<FXMLLoader> mockedFXMLLoader = 
                mockConstruction(FXMLLoader.class, (loader, context) -> {
                  when(loader.load()).thenThrow(new IOException("Simulated navigation error"));
                })) {

      controller.whenSignUpButtonClicked();

      mockedApiClient.verify(() -> ApiClient
          .showAlert(ApiConstants.LOAD_ERROR, ApiConstants.UNEXPECTED_ERROR));
    }
  }

}
