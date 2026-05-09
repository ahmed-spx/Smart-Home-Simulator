package smarthomesim.demo.controller;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration: this class contains setup that runs at startup.
// CORS = "Cross-Origin Resource Sharing". Browsers block JS on one origin
// (your React app at localhost:5173) from calling a server on another origin
// (Spring Boot at localhost:8080) unless the server explicitly allows it.

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:5173")
            .allowedMethods("GET", "POST", "PUT", "DELETE");
  }
}