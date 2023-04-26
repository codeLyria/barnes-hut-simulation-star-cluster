import java.awt.*;

// This class represents celestial bodies like stars, planets, asteroids, etc..
public class Body {

    public final String name;
    private final double mass;
    private final double radius;
    private Vector3 centerMass;                 // Position of this Body in 3D space
    private Vector3 velocity;                   // Vector quantity of speed
    public Vector3 forceOnBody;                 // Gravitational forces impacting this Body
    public static final double G = 6.6743e-11;  // Gravitational constant
    private final Color color;

    public Body(String name, double mass, double radius, Vector3 centerMass, Vector3 velocity, Color color) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.centerMass = centerMass;
        this.velocity = velocity;
        forceOnBody = new Vector3(0,0,0);
        this.color = color;
    }

    // Collection of <getters>
    public double getMass() { return mass; }
    public Vector3 getCenterMass() { return centerMass; }

    // Reset the gravitational forces pulling on this Body to 0.
    public void resetForce() { forceOnBody = new Vector3(0,0,0); }
    // Add a gravitational force pulling on this body.
    public void addForce(Vector3 forceOnBody) { this.forceOnBody = this.forceOnBody.plus(forceOnBody); }

    // Calculate the Euclidian distance between this celestial Body and another point in space.
    public double distanceTo(Vector3 coordinate) { return centerMass.distanceTo(coordinate); }


    // Returns a Vector representing the gravitational force exerted by a cluster of mass on this Body.
    // That force F is calculated as: F = G*(m1*m2)/(d^2),  m1m2...masses of the objects
    //                                                      d...distance between the centers of the masses
    // To calculate the force exerted on b1, multiply the normalized vector pointing from b1 to b2 with the calculated force.
    public Vector3 gravitationalForce(double mass, Vector3 centerMass) {
        Vector3 direction = centerMass.minus(this.centerMass);
        direction.normalize();
        double d = this.centerMass.distanceTo(centerMass);
        double force = 0;

        if(Double.compare(d, 0) != 0) {
            force = (G* this.mass*mass)/(Math.pow(d, 2)); }

        return direction.times(force);
    }

    // Updates the position of this Body after accounting for all the forces exerted on it.
    // The (force) vectors are: Position + Acceleration (a=F/m) + Velocity
    public void move() {
        Vector3 newPosition = centerMass.plus(forceOnBody.times(1/mass)).plus(velocity);
        Vector3 newVelocity = newPosition.minus(centerMass);

        centerMass = newPosition;
        velocity = newVelocity;
    }


    // Draws the Body to the current StdDraw canvas as a dot.
    public void draw() {
        StdDraw.setPenColor(color);

        // Smooth the radius with log10 because of the large variation of radii.
        StdDraw.filledCircle(centerMass.X(), centerMass.Y(),1e9*Math.log10(radius));
    }

    // Returns a String with select information about this Body.
    public String toString() {
        String data =
                "Name: "+name+"\r\n"+
                "Mass: "+mass+"\r\n"+
                "Position: "+centerMass.toString()+"\r\n";

        return data;
    }
}