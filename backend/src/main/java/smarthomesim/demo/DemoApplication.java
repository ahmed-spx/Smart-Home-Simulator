package smarthomesim.demo;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import smarthomesim.demo.device.Device;
import smarthomesim.demo.fan.Fan;
import smarthomesim.demo.light.Light;
import smarthomesim.demo.thermostat.Thermostat;
import smarthomesim.demo.doorlock.DoorLock;

@SpringBootApplication
@EnableScheduling
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
		DoorLock doorLock = new DoorLock("doorlock-1", "Door Lock 1", "Front Door");

		devices.put(light.getId(), light);
		devices.put(fan.getId(), fan);
		devices.put(thermostat.getId(), thermostat);
		devices.put(doorLock.getId(), doorLock);

		return devices;
	}

}
