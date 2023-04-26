// This class represents vectors in a 3D vector space.

public class Vector3 {
    private double X;
    private double Y;
    private double Z;

    public Vector3(double x, double y, double z) {
        X=x; Y=y; Z=z;
    }

    // <get> Retrieve single coordinates of this 3D vector.
    public double X() { return X; }
    public double Y() { return Y; }
    public double Z() { return Z; }


    // Add another vector to this vector.
    public Vector3 plus(Vector3 v) {
        return new Vector3(X+v.X, Y+v.Y, Z+v.Z);
    }
    public Vector3 plus(double x, double y, double z) {
        return new Vector3(X+x, Y+y, Z+z);
    }

    // Multiply this vector with a constant.
    public Vector3 times(double lambda) {
        return new Vector3(X*lambda, Y*lambda, Z*lambda);
    }

    // Return the cross product of this vector and another vector.
    public Vector3 cross(Vector3 v) {
        Vector3 result = new Vector3(0,0,0);

        result.X = Y*v.Z - Z*v.Y;
        result.Y = Z*v.X - X*v.Z;
        result.Z = X*v.Y - Y*v.X;

        return result;
    }

    // Subtract another vector from this vector.
    public Vector3 minus(Vector3 v) {
        return new Vector3(X-v.X, Y-v.Y, Z-v.Z);
    }

    // Returns the Euclidean distance of this vector to another vector.
    public double distanceTo(Vector3 v) {
        double dX = X - v.X;
        double dY = Y - v.Y;
        double dZ = Z - v.Z;

        return Math.sqrt(dX*dX + dY*dY + dZ*dZ);
    }

    // Returns the magnitude (length) of this vector.
    public double length() {
        return distanceTo(new Vector3(0, 0, 0));
    }

    // Turn this vector into a unit vector with a magnitude (length) of 1.
    // The orientation of the vector is not affected.
    public void normalize() {
        if (length() != 0){
            double length = this.length();
            this.X/=length;
            this.Y/=length;
            this.Z/=length;
        }
    }

    // Returns the coordinates of this vector in a "[x,y,z]" format, i.e.: "[1.48E11,0.0,0.0]".
    public String toString() {
        return "["+X+","+Y+","+Z+"]";
    }
}