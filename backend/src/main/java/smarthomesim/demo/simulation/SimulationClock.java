package smarthomesim.demo.simulation;

import java.util.Map;
import org.springframework.stereotype.Component;

import smarthomesim.demo.device.Device;
import smarthomesim.demo.thermostat.Multiplier;
import smarthomesim.demo.thermostat.Thermostat;

/**
 * Global simulation clock. Tracks the simulation speed multiplier
 * (1×/2×/5×/10×) and the elapsed simulated time.
 *
 * When the multiplier changes, the elapsed time accumulated at the OLD rate
 * is folded into a running total before the new rate kicks in, so changing
 * speed mid-flight does not retroactively rewrite history.
 *
 * The thermostat tick logic still reads its own per-thermostat multiplier;
 * we keep them in sync by pushing the global value into each thermostat
 * whenever the global multiplier changes.
 */
@Component
public class SimulationClock {

  private final Map<String, Device> devices;

  private Multiplier multiplier = Multiplier.x1;
  private long startRealMillis = System.currentTimeMillis();
  private long accumulatedSimSeconds = 0L;

  public SimulationClock(Map<String, Device> devices) {
    this.devices = devices;
  }

  public synchronized Multiplier getMultiplier() {
    return multiplier;
  }

  public synchronized void setMultiplier(Multiplier next) {
    long now = System.currentTimeMillis();
    accumulatedSimSeconds += realSecondsSinceStart(now) * multiplier.getMultiplierValue();
    startRealMillis = now;
    this.multiplier = next;

    // Keep per-thermostat multiplier in lockstep so the existing
    // ThermostatTicker counter math stays correct.
    for (Device d : devices.values()) {
      if (d instanceof Thermostat t) {
        t.setMultiplier(next);
      }
    }
  }

  public synchronized long getElapsedSimulatedSeconds() {
    long now = System.currentTimeMillis();
    return accumulatedSimSeconds + realSecondsSinceStart(now) * multiplier.getMultiplierValue();
  }

  public synchronized void reset() {
    accumulatedSimSeconds = 0L;
    startRealMillis = System.currentTimeMillis();
  }

  private long realSecondsSinceStart(long now) {
    return (now - startRealMillis) / 1000L;
  }
}
