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
        thermostat.changeState(new CoolingThermostatState());
    }

    private void heatingMode(Thermostat thermostat) {
        thermostat.changeState(new HeatingThermostatState());
    }
    
}
