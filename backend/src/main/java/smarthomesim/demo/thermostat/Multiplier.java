package smarthomesim.demo.thermostat;

public enum Multiplier {
    x1(1),
    x2(2),
    x5(5),
    x10(10);

    private final int multiplierValue;

    Multiplier(int multiplierValue) {
        this.multiplierValue = multiplierValue;
    }

    public int getMultiplierValue() {
        return multiplierValue;
    }
}
