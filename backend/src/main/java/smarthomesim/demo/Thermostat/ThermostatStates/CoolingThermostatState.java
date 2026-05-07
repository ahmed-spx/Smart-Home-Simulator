package smarthomesim.demo.Thermostat.ThermostatStates;
import smarthomesim.demo.Thermostat.IThermostatState;
import smarthomesim.demo.Thermostat.Thermostat;
import smarthomesim.demo.Thermostat.ThermostatState;
import smarthomesim.demo.Thermostat.ThermostatMode;

public class CoolingThermostatState implements IThermostatState{
        
    @Override
    public ThermostatState getState() {
        return ThermostatState.Cooling;
    }

    @Override
    public void togglePower(Thermostat thermostat) {
        thermostat.changeState(new OffThermostatState());
    }
    
    @Override
    public void setMode(Thermostat thermostat, ThermostatMode mode) {
        // a Do while loop to continue comparing desired and ambient till they equal?
        // int ambientTemp = thermostat.getAmbientTemperature();
        // int desiredTemp = thermostat.getDesiredTemperature();

        // if (ambientTemp == desiredTemp) {
        //     thermostat.changeState(new IdleThermostatState());
        // }
    }

    @Override
    public void setTemperature(Thermostat thermostat, int temperature) {
        thermostat.setThermostatDesiredTemperature(temperature);
        setMode(thermostat, thermostat.getMode());
    }
}
