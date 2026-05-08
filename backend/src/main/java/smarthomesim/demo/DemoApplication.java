package smarthomesim.demo;

import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.persistence.DeviceStore;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @Bean
  public Map<String, Device> deviceHub(DeviceStore store) {
    return store.load();
  }
}