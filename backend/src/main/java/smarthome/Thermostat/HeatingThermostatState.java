package main.java.smarthome.Thermostat;

public class HeatingThermostatState implements IThermostatState {

    @Override
    public ThermostatState getState() {
        return ThermostatState.Heating;
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
