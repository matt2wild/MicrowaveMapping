import javafx.geometry.Point3D;

public class Vector {
	private double X, Y, Z;

	// Constructors

	public Vector() {
		X = 0.0;
		Y = 0.0;
		Z = 0.0;
	}

	public Vector(double x, double y, double z) {
		X = x;
		Y = y;
		Z = z;
	}

	public Vector(double[] coord) {
		X = coord[0];
		Y = coord[1];
		Z = coord[2];
	}

	public Vector(double[] p1, double[] p2) {
		X = p2[0] - p1[0];
		Y = p2[1] - p1[1];
		Z = p2[2] - p1[2];
	}
	public boolean equals(Object obj){
		if(obj instanceof Vector){
			Vector otherVector = (Vector) obj;
			if(otherVector.X==X&&otherVector.Y==Y&&otherVector.Z==Z){
				return true;
			}
		}
		return false;
	}

	public double getX() {
		return X;
	}

	public double getY() {
		return Y;
	}

	public double getZ() {
		return Z;
	}

	public double Mag() {
		return Math.sqrt(X * X + Y * Y + Z * Z);
	}

	public void Normalize() {
		double M = Mag();
		if (M != 1) {
			X = X / M;
			Y = Y / M;
			Z = Z / M;
		}
	}

	public double[] GetCoordArray() {
		double[] v = new double[] { X, Y, Z };
		return v;
	}

	// Print Vector
	public String toString() {
		return "X: " + X + " Y: " + Y + " Z: " + Z;
	}

	// Vector Operations

	// Scalar Multiplication
	public Vector SM(double k) {
		Vector res = new Vector(X * k, Y * k, Z * k);
		return res;
	}

	// Cross Product
	public Vector CP(Vector oV) {
		Vector R = new Vector((Y * oV.Z - Z * oV.Y), -1 * (X * oV.Z - Z * oV.X),
				(X * oV.Y - Y * oV.Z));
		return R;
	}

	// Dot Product
	public double DP(Vector otherVector) {
		double R = X * otherVector.X + Y * otherVector.Y + Z * otherVector.Z;
		return R;
	}

	// Addition
	public Vector Add(Vector otherVector) {
		Vector res = new Vector(X + otherVector.X, Y + otherVector.Y, Z + otherVector.Z);
		return res;
	}
	public  Point3D toPoint3D(){
		Point3D pt = new Point3D(X,Y,Z);
		return pt;
	}

//	// Subtraction
	public Vector Sub(Vector otherVector) {
		Vector res = new Vector(X - otherVector.X, Y - otherVector.Y, Z - otherVector.Z);
		return res;
	}
}
