import java.util.Iterator;

// This class represents a cube inside 3D space, and the celestial Bodies within its boundaries
// Each tree may subdivide into 8 smaller children (=octant gates)
public class Octree implements Iterable<Body> {

    enum octreeType {                   // An Octree with this flag is...
        NULL,                           // ...empty
        LEAF,                           // ...is a leaf, holding a single celestial body
        GATE                            // ...acts as a gateway, holding no body
    }

    private octreeType flag;
    private Body leafBody;
    private Octree[] gates;
    private Vector3 origin;
    private double dimensions;
    private double mass;                // Total mass contained within the boundaries of this Octree
    private Vector3 centerMass;         // Center of mass inside this Octree-Space


    Octree(Vector3 origin, double dimensions) {
        flag = octreeType.NULL;
        leafBody = null;
        gates = null;
        this.origin = origin;
        this.dimensions = dimensions;
        mass = 0;
        centerMass = new Vector3(0,0,0);
    }

    // Collection of <getters>
    public octreeType getFlag() {
        return flag;
    }
    public Octree[] getGates() { return gates; }
    public double getDimensions() {
        return dimensions;
    }

    // Returns a Body if this Octree is of type 'Leaf', else returns null.
    public Body getLeafBody() {
        return flag == octreeType.LEAF ? leafBody : null;
    }


    // Add one celestial Body to this Octree-Space:
    // <leaf> If the Space is empty, assign it with a Body.
    // <gate> If the Space was already holding a single Body (=leaf), split it into 8 gates.
    //        Send both the leaf's Body and the new Body to their respective gates via .gate()
    public void add(Body body) {
        // Discard heavenly Bodies that move to be outside the boundaries of the canvas.
        double bodyX = Math.abs(body.getCenterMass().X());
        double bodyY = Math.abs(body.getCenterMass().Y());
        double bodyZ = Math.abs(body.getCenterMass().Z());

        double max = Math.max(Math.max(bodyX, bodyY), bodyZ);
        if(max > Simulation.D3/2) return;

    // Act according to which state the Octree's in
        switch(flag) {
            case NULL:
                leafBody = body;
                centerMass = body.getCenterMass();
                flag = octreeType.LEAF;
                break;
            case LEAF:
                split();
                gates[gate(leafBody)].add(leafBody);
                gates[gate(body)].add(body);
                newCenterMass(body);
                flag = octreeType.GATE;
                break;
            case GATE:
                gates[gate(body)].add(body);
                newCenterMass(body);
                break;
        }
        mass += body.getMass();
    }

    // Calculate the center of mass between 2 points in 3D space.
    // General center of mass formula: SUM( xi * mi)/M, i=index (1 to n), n=2 (calculate in pairs)
    // We need to do this separately for each dimension, i.e. another time for yi, and once again for zi.
    private void newCenterMass(Body b) {
        double bodyMass = b.getMass();
        double M = mass + bodyMass;         // Total sum of the masses involved

        double x = (centerMass.X()*mass + b.getCenterMass().X()*bodyMass) / M;
        double y = (centerMass.Y()*mass + b.getCenterMass().Y()*bodyMass) / M;
        double z = (centerMass.Z()*mass + b.getCenterMass().Z()*bodyMass) / M;

        centerMass = new Vector3(x, y, z);
    }

    // Gates 0-3 are on the lower level, 4-7 on an upper level (in relation to the point of origin).
    // Looking down the Z-axis, start in the upper right corner (northeast==NE) and continue counter-clockwise.
    // NE(0,4) => NW(1,5) => SW(2,6) => SE(3,7)
    private void split() {
        gates = new Octree[8];

        double newDimensions = dimensions / 2;  
        double Q = dimensions / 4;          // Shift the new point of origin

        gates[0] = new Octree(origin.plus(+Q, +Q, -Q), newDimensions);
        gates[1] = new Octree(origin.plus(-Q, +Q, -Q), newDimensions);
        gates[2] = new Octree(origin.plus(+Q, -Q, -Q), newDimensions);
        gates[3] = new Octree(origin.plus(-Q, -Q, -Q), newDimensions);
        gates[4] = new Octree(origin.plus(+Q, +Q, +Q), newDimensions);
        gates[5] = new Octree(origin.plus(-Q, +Q, +Q), newDimensions);
        gates[6] = new Octree(origin.plus(+Q, -Q, +Q), newDimensions);
        gates[7] = new Octree(origin.plus(-Q, -Q, +Q), newDimensions);
    }

    // Determine the gate a given celestial Body would go through.
    // Check the description in .split() for a manual on how the gates are organized.
    private int gate(Body body) {
        double x = body.getCenterMass().X();
        double y = body.getCenterMass().Y();
        double z = body.getCenterMass().Z();

        if(     x>=origin.X() && y>=origin.Y() && z< origin.Z()) return 0;
        else if(x< origin.X() && y>=origin.Y() && z< origin.Z()) return 1;
        else if(x>=origin.X() && y< origin.Y() && z< origin.Z()) return 2;
        else if(x< origin.X() && y< origin.Y() && z< origin.Z()) return 3;
        else if(x>=origin.X() && y>=origin.Y() && z>=origin.Z()) return 4;
        else if(x< origin.X() && y>=origin.Y() && z>=origin.Z()) return 5;
        else if(x>=origin.X() && y< origin.Y() && z>=origin.Z()) return 6;
        else return 7;
    }

    // Measure the distance from the origin of the Octree to a given Body.
    // <1>  If they're sufficiently far away from each other, or the Octree is a leaf itself:
    //      Calculate the gravitational forces between the two.
    // <2>  Otherwise, follow up by inspecting each octant separately.
    // The time complexity for the Barnes-Hut algorithm is in big O(n * log(n)).
    public void calcForceOn(Body leaf) {
        if(Double.compare(mass, 0) == 0) return;    // Ignore any empty octant, the bigger, the better
        else {
            double r = leaf.distanceTo(origin);
            double d = dimensions;

            if(r/d > Simulation.T || flag == octreeType.LEAF) {
                leaf.addForce(leaf.gravitationalForce(mass, centerMass));
            }
            else {
                for (int i = 0; i < 8; i++) {
                    gates[i].calcForceOn(leaf);
                }
            }
        }
    }

    // Iterate over the celestial bodies inside this Octree.
    @Override
    public Iterator<Body> iterator() {
        return new OctreeIterator(this);
    }
}
