import javafx.geometry.Point3D;

public class CoordValidation implements Constants {
	public CoordValidation() {
	}

	/**
	 * Tests whether or not the line is contained within the reflection chamber
	 * 
	 * @param s
	 *            Starting position
	 * @param e
	 *            End position
	 * @return Returns true only if the magnitude is less than the threshold and
	 *         both points pass the single point validation test.
	 */
	public static boolean isValid(Ray aRay) {
		Point3D s = aRay.getStartPos();
		Point3D e = aRay.getEndPos();
		Point3D v = new Point3D(s.getX() - e.getX(), s.getY() - e.getY(), s.getZ() - e.getZ());
		if (!v.equals(Point3D.ZERO) && !aRay.getVector().equals(Point3D.ZERO))
			if (isValid(s) && isValid(e)) {
				return true;
			}
		return false;
	}

	/**
	 * Determines whether or not the provided coordinate can be found inside the
	 * reflection chamber
	 * 
	 * @param v
	 *            coordinate to test
	 * @return Returns true only if the point is inside the boundary conditions.
	 */
	public static boolean isValid(Point3D v) {
		if (v.getX() <= radius && v.getX() >= plane.getLeftSide()) {
			if (Math.abs(v.getY()) <= radius) {
				if (Math.abs(v.getZ()) <= radius) {
					return true;
				}
			}
		}
		return false;
	}
}