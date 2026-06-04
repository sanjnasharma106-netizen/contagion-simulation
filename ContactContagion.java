public class ContactContagion extends Contagion {

    public ContactContagion() {

        super(
                0.60,   // infection rate
                0.0,   // death rate
                1,      // spread distance
                5       // damage
        );
    }

    @Override
    public String getType() {
        return "Contact";
    }
}
