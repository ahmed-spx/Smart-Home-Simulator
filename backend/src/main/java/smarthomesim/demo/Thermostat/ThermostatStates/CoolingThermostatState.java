package smarthomesim.demo.thermostat.thermostatstates;
import smarthomesim.demo.thermostat.IThermostatState;
import smarthomesim.demo.thermostat.Thermostat;
import smarthomesim.demo.thermostat.ThermostatState;
import smarthomesim.demo.thermostat.ThermostatMode;

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
    }

    @Override
    public void setTemperature(Thermostat thermostat, int temperature) {
        thermostat.setThermostatDesiredTemperature(temperature);
        setMode(thermostat, thermostat.getMode());
    }

    @Override
    public void tick(Thermostat thermostat) {
        if (thermostat.getAmbientTemperature() > thermostat.getDesiredTemperature()) {
            thermostat.decreaseAmbientTemperature();
        } else {
            thermostat.changeState(new IdleThermostatState());
        }
    }
}
