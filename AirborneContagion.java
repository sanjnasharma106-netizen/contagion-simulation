public class AirborneContagion extends Contagion {

    public AirborneContagion() {

        super(
                0.35,   // infection rate
                0.0,   // death rate
                4,      // spread distance
                2       // damage
        );
    }

    @Override
    public String getType() {
        return "Airborne";
    }
}
