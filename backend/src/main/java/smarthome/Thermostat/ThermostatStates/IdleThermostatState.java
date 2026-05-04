package main.java.smarthome.Thermostat.ThermostatStates;
import main.java.smarthome.Thermostat.IThermostatState;
import main.java.smarthome.Thermostat.Thermostat;
import main.java.smarthome.Thermostat.ThermostatState;

public class IdleThermostatState implements IThermostatState {

    @Override
    public ThermostatState getState() {
        return ThermostatState.Idle;
    }

    @Override
    public void togglePower(Thermostat thermostat) {
        thermostat.changeState(new OffThermostatState());
    }

    @Override
    public void setMode(Thermostat thermostat, ThermostatState mode) {
    }

    @Override
    public void setTemperature(Thermostat thermostat, int temperature) {
    }
    
}
