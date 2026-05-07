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

    @Override
    public void setMode(Thermostat thermostat, ThermostatMode mode) {
        thermostat.setThermostatMode(mode);
        switch (mode) {
            case Auto:
                autoMode(thermostat);
                break;
            case Cooling:
                coolingMode(thermostat);
                break;
            case Heating:
                heatingMode(thermostat);
                break;
        }
    }

    @Override
    public void setTemperature(Thermostat thermostat, int temperature) {
        thermostat.setThermostatDesiredTemperature(temperature);
        setMode(thermostat, thermostat.getMode());
    }

    private void autoMode(Thermostat thermostat) {
        int ambientTemp = thermostat.getAmbientTemperature();
        int desiredTemp = thermostat.getDesiredTemperature();

        if (ambientTemp < desiredTemp) {
            thermostat.changeState(new HeatingThermostatState());
        } else if (ambientTemp > desiredTemp) {
            thermostat.changeState(new CoolingThermostatState());
        }
    }

    private void coolingMode(Thermostat thermostat) {
        int ambientTemp = thermostat.getAmbientTemperature();
        int desiredTemp = thermostat.getDesiredTemperature();

        if (ambientTemp > desiredTemp) {
            thermostat.changeState(new CoolingThermostatState());
        }
    }

    private void heatingMode(Thermostat thermostat) {
        int ambientTemp = thermostat.getAmbientTemperature();
        int desiredTemp = thermostat.getDesiredTemperature();
        
        if (ambientTemp < desiredTemp) {
            thermostat.changeState(new HeatingThermostatState());
        }
    }
    
}
