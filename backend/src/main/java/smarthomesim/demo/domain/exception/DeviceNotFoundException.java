package smarthomesim.demo.domain.exception;

public class DeviceNotFoundException extends RuntimeException {
  public DeviceNotFoundException(String id) {
    super("No device with id: " + id);
  }
}