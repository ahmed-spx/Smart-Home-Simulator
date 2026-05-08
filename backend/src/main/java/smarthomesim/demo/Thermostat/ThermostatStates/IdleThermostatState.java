package smarthomesim.demo.thermostat.thermostatstates;
import smarthomesim.demo.thermostat.IThermostatState;
import smarthomesim.demo.thermostat.Thermostat;
import smarthomesim.demo.thermostat.ThermostatState;
import smarthomesim.demo.thermostat.ThermostatMode;

public class IdleThermostatState implements IThermostatState {

    @Override
    public ThermostatState getState() {
        return ThermostatState.Idle;
    }

    @Override
    public void togglePower(Thermostat thermostat) {
        thermostat.changeState(new OffThermostatState());
    }

    // Idle is central that decides to go to cooling or heating because ambient != desired
    @Override
    public void setMode(Thermostat thermostat, ThermostatMode mode) {
        thermostat.setThermostatMode(mode);
        thermostat.currentModeStrategy().evaluate(thermostat);
    }

    @Override
    public void setTemperature(Thermostat thermostat, int temperature) {
        thermostat.setThermostatDesiredTemperature(temperature);
        thermostat.currentModeStrategy().evaluate(thermostat);
    }

    // doesnt need tick because ambient = desired
    @Override
    public void tick(Thermostat thermostat) {
    }
    
}
