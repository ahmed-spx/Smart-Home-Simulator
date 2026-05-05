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

    public void autoMode(Thermostat thermostat, ThermostatMode mode, int ambientTemp, int desiredTemp) {
        if (mode == ThermostatMode.Auto) {
            if (ambientTemp < desiredTemp) {
                thermostat.changeState(new HeatingThermostatState());
            } else if (ambientTemp > desiredTemp) {
                thermostat.changeState(new CoolingThermostatState());
            }
        }
    }

    public void coolingMode(Thermostat thermostat, ThermostatMode mode, int ambientTemp, int desiredTemp) {
        if (mode == ThermostatMode.Cooling) {
            if (ambientTemp > desiredTemp) {
                thermostat.changeState(new CoolingThermostatState());
            }
        }
    }

    public void heatingMode(Thermostat thermostat, ThermostatMode mode, int ambientTemp, int desiredTemp) {
        if (mode == ThermostatMode.Heating) {
            if (ambientTemp < desiredTemp) {
                thermostat.changeState(new HeatingThermostatState());
            }
        }
    }
    
}
