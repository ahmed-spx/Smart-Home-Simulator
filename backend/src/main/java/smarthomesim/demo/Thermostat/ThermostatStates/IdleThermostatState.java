package smarthomesim.demo.Thermostat.ThermostatStates;
import smarthomesim.demo.Thermostat.IThermostatState;
import smarthomesim.demo.Thermostat.Thermostat;
import smarthomesim.demo.Thermostat.ThermostatState;

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
    public void setMode(Thermostat thermostat, ThermostatMode mode) {
        thermostat.setMode(mode);
        
    }

    @Override
    public void setTemperature(Thermostat thermostat, int temperature) {
        thermostat.setTemperature(temperature);
    }
    
}
