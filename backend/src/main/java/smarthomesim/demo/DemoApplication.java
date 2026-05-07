package smarthomesim.demo;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import smarthomesim.demo.device.Device;
import smarthomesim.demo.fan.Fan;
import smarthomesim.demo.light.Light;
import smarthomesim.demo.thermostat.Thermostat;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@Bean
	public Map<String, Device> deviceHub() { // main device hub

		Map<String, Device> devices = new HashMap<>();

		Light light = new Light("light-1", "Light 1", "Living Room");
		Fan fan = new Fan("fan-1", "Fan 1", "Living Room");
		Thermostat thermostat = new Thermostat("thermo-1", "Thermostat 1", "Living Room");

		devices.put(light.getId(), light);
		devices.put(fan.getId(), fan);
		devices.put(thermostat.getId(), thermostat);

		return devices;
	}

}
