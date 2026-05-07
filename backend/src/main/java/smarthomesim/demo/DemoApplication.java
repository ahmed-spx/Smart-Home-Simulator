package smarthomesim.demo;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import smarthomesim.demo.Fan.Fan;
import smarthomesim.demo.Light.Light;
import smarthomesim.demo.Thermostat.Thermostat;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@Bean
	public Map<String, Device> deviceHub() { // main device hub

		Map<String, Device> devices = new HashMap<>();

		Light Light = new Light("light 1");
		Fan Fan = new Fan("fan 1");
		Thermostat Thermostat = new Thermostat("thermo 1");

		devices.put(Light.getId(), Light);
		devices.put(Fan.getId(), Fan);
		devices.put(Thermostat.getId(), Thermostat);

		return devices;
	}

}
