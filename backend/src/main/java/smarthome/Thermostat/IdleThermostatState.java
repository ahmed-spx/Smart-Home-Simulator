package main.java.smarthome.Thermostat;

public class IdleThermostatState implements IThermostatState {
    private Thermostat thermostat;

    @Override
    public ThermostatState getState() {
        return ThermostatState.Idle;
    }

    @Override
    public void togglePower(Thermostat thermostat) {
    }

    @Override
    public void setMode(Thermostat thermostat, ThermostatState mode) {
    }

    @Override
    public void setTemperature(Thermostat thermostat, int temperature) {
    }
    
}
