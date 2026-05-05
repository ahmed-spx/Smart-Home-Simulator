package smarthomesim.demo.Thermostat.ThermostatStates;
import smarthomesim.demo.Thermostat.IThermostatState;
import smarthomesim.demo.Thermostat.Thermostat;
import smarthomesim.demo.Thermostat.ThermostatState;

public class HeatingThermostatState implements IThermostatState {

    @Override
    public ThermostatState getState() {
        return ThermostatState.Heating;
    }

    @Override
    public void togglePower(Thermostat thermostat) {
        thermostat.changeState(new OffThermostatState());
    }
    
    public void heatingMode(Thermostat thermostat, int ambientTemp, int desiredTemp) {
        if (ambientTemp < desiredTemp) {
            Thread.sleep(5000);
            ambientTemp++;
        } else {
            thermostat.changeState(new IdleThermostatState());
        }
    }
}
