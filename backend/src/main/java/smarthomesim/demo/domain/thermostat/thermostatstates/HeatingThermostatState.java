package smarthomesim.demo.domain.thermostat.thermostatstates;
import smarthomesim.demo.domain.thermostat.IThermostatState;
import smarthomesim.demo.domain.thermostat.Thermostat;
import smarthomesim.demo.domain.thermostat.ThermostatMode;
import smarthomesim.demo.domain.thermostat.ThermostatState;

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
    }

    @Override
    public void setTemperature(Thermostat thermostat, int temperature) {
        thermostat.setThermostatDesiredTemperature(temperature);
        setMode(thermostat, thermostat.getMode());
    }

    @Override
    public void tick(Thermostat thermostat) {
        if (thermostat.getAmbientTemperature() < thermostat.getDesiredTemperature()) {
            thermostat.increaseAmbientTemperature();
        } else {
            thermostat.changeState(new IdleThermostatState());
        }
    }
}
