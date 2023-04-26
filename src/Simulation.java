import java.awt.*;

// This is the Main class
// It generates an Octree (3D) and fills it with randomized celestial Bodies
// The Bodies move by calculating their gravitational forces, and...
// ... are painted on a 2D canvas, courtesy of the StdDraw library @Princeton University
// The implementation of the Barnes-Hut Algorithm ensures that the time complexity of the Simulation is O(n*log(n))
public class Simulation {

    public static final double AU = 150e9;              // 1 astronomical unit (AU): average distance between earth and sun
    public static final double D3 = AU *4;              // Dimensions of the Octree-Space
    private static final int numberOfBodies = 10000;    // Number of random planets to be added to the simulation
    public static final double T = 1;                   // Accuracy threshold for the Barnes-Hut simulation
                                                        // (higher = faster, but also less accurate)


    public static void main(String[] args) {
        StdDraw.setCanvasSize(800, 800);
        StdDraw.setXscale(-D3/2, D3/2);
        StdDraw.setYscale(-D3/2, D3/2);
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.show();

        Octree WorldTree = new Octree(new Vector3(0,0,0), D3);
        Generator random = new Generator();

        random.fillOctree(WorldTree, numberOfBodies);
        Body sun = new Body("sun", 1e38, 15, new Vector3(0, 0, 0), new Vector3(0,0,0), Color.RED);
        WorldTree.add(sun);


        // Movement loop. This is where all the magic comes together
        while(true){
            // Set the gravitational forces of every Body back to 0
            for(Body b: WorldTree) {
                b.resetForce();
            }

            // Calculate the gravity that each Body exerts on each other Body. Barnes-Hut simulation:
            // Use a shortcut (octant in the Octree) to look at large, far-away clusters instead of inspecting each Body
            // This turns a would-be time complexity of O(n^2) into O(n* log(n))
            for(Body b: WorldTree) {
                WorldTree.calcForceOn(b);
            }

            // Update the positions of all Bodies
            Octree newWorldTree = new Octree(new Vector3(0,0,0), D3);
            for(Body b: WorldTree) {
                b.move();
                newWorldTree.add(b);
            }
            WorldTree = newWorldTree;

            // Draw into canvas. It is possible to adjust the display frequency
            StdDraw.clear(StdDraw.BLACK);
            for(Body b: WorldTree) {
                b.draw();
            }
            StdDraw.show();
        }
    }
}
