import java.awt.*;

// This class fills a provided Octree with any number or randomized celestial bodies
public class Generator {

    // Generate random celestial bodies and fill the Octree
    public void fillOctree(Octree root, int numberOfBodies) {
        for(int i = 0; i < numberOfBodies; i++) {
            String name = "";
            double mass = genRandomNumber(1e23, 1e27);
            double radius = 6;
            Vector3 position = genRandomPosition(root);
            Vector3 currentVelocity = genRandomVelocity(position, Math.random()*3e8);
            Color color = Color.WHITE;

            root.add(new Body(name, mass, radius, position, currentVelocity, color));
        }
    }

    // Generate a random Double within a given range
    private double genRandomNumber(double min, double max) {
        return min + Math.random() * (max-min);
    }

    // Generate a random position within the Octree-space (its boundaries)
    private Vector3 genRandomPosition(Octree tree) {
        double halfCanvas = tree.getDimensions() /2;

        double positionX = genRandomNumber(-halfCanvas, halfCanvas);
        double positionY = genRandomNumber(-halfCanvas, halfCanvas);
        double positionZ = genRandomNumber(-halfCanvas, halfCanvas);

        return new Vector3(positionX, positionY, positionZ);
    }

    // Initiates an orbit by calculating the cross product of the position vector with its Z-vector
    // (since the coordinate system of the canvas is in X,Y)
    // It is then normalized and finally, scaled
    private Vector3 genRandomVelocity(Vector3 position, double scale) {
        Vector3 orthogonal = position.cross(new Vector3(0,0,position.Z()));
        orthogonal.normalize();

        return orthogonal.times(scale);
    }
}
