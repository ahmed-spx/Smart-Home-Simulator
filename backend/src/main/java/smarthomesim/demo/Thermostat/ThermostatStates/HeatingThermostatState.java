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
    
    @Override
    public void setMode(Thermostat thermostat, ThermostatMode mode) {
        int ambientTemp = thermostat.getAmbientTemperature();
        int desiredTemp = thermostat.getDesiredTemperature();

        if (ambientTemp < desiredTemp) {
            Thread.sleep(5000);
            ambientTemp++;
        } else {
            thermostat.changeState(new IdleThermostatState());
        }
    }
}
