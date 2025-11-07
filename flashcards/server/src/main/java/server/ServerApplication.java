package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Flashcards REST API server.
 * This class bootstraps the Spring Boot application and starts the embedded Tomcat server.
 * 
 * @author ailinat
 * @author sofietw
 * 
 */
@SpringBootApplication
public class ServerApplication {

  /**
   * Main method that starts the Spring Boot application.
   * 
   * @param args command line arguments
   * 
   */
  public static void main(String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }
}