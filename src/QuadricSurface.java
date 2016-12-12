import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class QuadricSurface implements Constants {

	private String function = "0=A*(x^2)+B*(y^2)+C*(z^2)+D*(x*y)+E*(x*z)+F*(y*z)+G*(x)+H*(y)+I*(z)+(J)";
	private double A, B, C, D, E, F, G, H, I, J;
	private char[] letters = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J' };
	private double[] coefficients = new double[10];

	Point3D min, max, offSet;

	// public static void main(String[] args) {
	// QuadricSurface test = new QuadricSurface("1=0.1x^2-y^2-z^2", new
	// Point3D(-1, -1, -1), new Point3D(1, 1, 1),
	// Point3D.ZERO);
	// System.out.println(test.toString());
	// }

	public String toString() {
		return getFunction().replaceAll("[\\(\\)]|\\.0|\\*|1.0\\*", "").replaceAll("0[x-z]([x-z]|\\^2)?\\+", "")
				.replaceAll("\\+-", "-");
	}

	/**
	 * Quadric function with upper and lower limits
	 * 
	 * @param f
	 *            String - Quadric function of the form
	 *            <p>
	 *            J=A*(x^2)+B*(y^2)+C*(z^2) +D*x*y+E*x*z+F*y*z+G*x+H*y+I*z
	 *            </p>
	 *            can take into account parentheses when squaring the A,B,and C
	 *            terms <b>The coefficients must be decimal numbers even if it
	 *            something like "4.0x" rather than "4x"
	 * @param LL
	 *            Point3D - lower limit that will render/interact
	 * @param UL
	 *            Point3D - upper limit that will render/interact
	 * @param OS
	 *            Point3D - offset value
	 */
	public QuadricSurface(String f, Point3D LL, Point3D UL, Point3D OS) {
		// change offset to effect the min/max here so it doesn't have to be
		// addressed elsewhere
		min = LL;
		max = UL;
		offSet = OS;
		f = f + "+0";
		f = f.replaceAll("(-|^\\d+)([x-z]\\*?[x-z]?)", "$1" + "1.0$2");
		f = f.replaceAll("(\\d+^\\.)([x-z]\\*?[x-z]?)", "$1" + ".0$2");
		System.out.println(f);
		Matcher p;
		p = Pattern.compile("((-?\\d+?\\.?\\d+?)?\\*?)?(\\(?(-?\\d+?\\.?\\d+?)?\\*?)x\\)?\\^2").matcher(f);
		if (p.find())
			A = Double.parseDouble((p.group(2) == null)
					? ((p.group(4) == null ? "1" : Math.pow(Double.parseDouble(p.group(4)), 2) + "")) : p.group(2));
		p = Pattern.compile("((-?\\d+?\\.?\\d+?)?\\*?)?(\\(?(-?\\d+?\\.?\\d+?)?\\*?)y\\)?\\^2").matcher(f);
		if (p.find())
			B = Double.parseDouble((p.group(2) == null)
					? ((p.group(4) == null ? "1" : Math.pow(Double.parseDouble(p.group(4)), 2) + "")) : p.group(2));
		p = Pattern.compile("((-?\\d+?\\.?\\d+?)?\\*?)?(\\(?(-?\\d+?\\.?\\d+?)?\\*?)z\\)?\\^2").matcher(f);
		if (p.find())
			C = Double.parseDouble((p.group(2) == null)
					? ((p.group(4) == null ? "1" : Math.pow(Double.parseDouble(p.group(4)), 2) + "")) : p.group(2));
		p = Pattern.compile("(-?\\d+?\\.?\\d+)?\\*?x\\*?y").matcher(f);
		if (p.find())
			D = Double.parseDouble((p.group(1) == null) ? "1" : p.group(1));
		p = Pattern.compile("(-?\\d+?\\.?\\d+)?\\*?x\\*?z").matcher(f);
		if (p.find())
			E = Double.parseDouble((p.group(1) == null) ? "1" : p.group(1));
		p = Pattern.compile("(-?\\d+?\\.?\\d+)?\\*?y\\*?z").matcher(f);
		if (p.find())
			F = Double.parseDouble((p.group(1) == null) ? "1" : p.group(1));
		p = Pattern.compile("[\\+|\\=](-?\\d+?\\.?\\d+)?\\*?x\\+").matcher(f);
		if (p.find())
			G = Double.parseDouble((p.group(1) == null) ? "1" : p.group(1));
		p = Pattern.compile("[\\+|\\=](-?\\d+?\\.?\\d+)?\\*?y\\+").matcher(f);
		if (p.find())
			H = Double.parseDouble((p.group(1) == null) ? "1" : p.group(1));
		p = Pattern.compile("[\\+|\\=](-?\\d+?\\.?\\d+)?\\*?z\\+").matcher(f);
		if (p.find())
			I = Double.parseDouble((p.group(1) == null) ? "1" : p.group(1));
		J = -Double.parseDouble(f.split("=")[0]);
		coefficients = new double[] { A, B, C, D, E, F, G, H, I, J };
		for (int i = 0; i < coefficients.length; i++) {
			function = function.replaceFirst("" + letters[i], "" + coefficients[i]);
		}
	}

	public String getFunction() {
		return function;
	}

	public Point3D getMin() {
		return min;
	}

	public void setMin(Point3D min) {
		this.min = min;
	}

	public Point3D getMax() {
		return max;
	}

	public void setMax(Point3D max) {
		this.max = max;
	}

	public Point3D getOffSet() {
		return offSet;
	}

	public void setOffSet(Point3D offSet) {
		this.offSet = offSet;
	}

	/**
	 * Determines if point is within the boundaries for the surface
	 * 
	 * @param Pos
	 *            Point3D - Position to test
	 * @param Sf
	 *            QuadricSurface - Surface to test
	 * @see QuadricSurface#getMin()
	 * @see QuadricSurface#getMax()
	 * @return
	 */
	private boolean outofbounds(Point3D Pos) {
		Point3D Ulim = getMax().add(getOffSet());
		Point3D Llim = getMin().add(getOffSet());

		boolean isOOB = false;
		if (Pos.getX() < Llim.getX() - 5E-10 || //
				Pos.getY() < Llim.getY() - 5E-10 || //
				Pos.getZ() < Llim.getZ() - 5E-10) {
			isOOB = true;
		}
		if (Pos.getX() > Ulim.getX() + 5E-10 || //
				Pos.getY() > Ulim.getY() + 5E-10 || //
				Pos.getZ() > Ulim.getZ() + 5E-10) {
			isOOB = true;
		}
		return isOOB;
	}

	/**
	 * Uses the quadratic formula combined with the general equation for quadric
	 * surfaces to solve for t in the equation <b> |EndPos| =
	 * |StartPos|+t*|v|</b>
	 * 
	 * @param xyz
	 *            Point3D the start position for the ray
	 * @param ijk
	 *            Point3D the vector of the ray
	 * @return t double the value that the vector must be multiplied by in order
	 *         to reach the intercept position
	 */
	public double get_t(Point3D xyz, Point3D ijk) {
		xyz = xyz.subtract(offSet);
		double x = xyz.getX(), y = xyz.getY(), z = xyz.getZ();
		double i = ijk.getX(), j = ijk.getY(), k = ijk.getZ();
		double t = 0;
		double a = A * Math.pow(i, 2) + B * Math.pow(j, 2) + C * Math.pow(k, 2) + D * i * j + E * i * k + F * j * k;
		double b = 2 * (A * x * i + B * y * j + C * z * k) + D * (x * j + y * i) + E * (x * k + z * i)
				+ F * (y * k + z * j) + G * i + H * j + I * k;
		double c = A * Math.pow(x, 2) + B * Math.pow(y, 2) + C * Math.pow(z, 2) + D * x * y + E * x * z + F * y * z
				+ G * x + H * y + I * z + J;

		double discrim = Math.pow(b, 2) - 4 * a * c;
		if (a == 0.0) {
			t = (b == 0.0) ? Double.NaN : (-c / b);
		} else {
			if (discrim >= 0) {
				double t_p = (-b + Math.sqrt(discrim)) / (2 * a);
				double t_n = (-b - Math.sqrt(discrim)) / (2 * a);
				t = Math.min(t_n, t_p);

				if (t < 5E-9 || outofbounds(ijk.multiply(t).add(xyz.add(offSet)))) {
					t = Math.max(t_n, t_p);
				}
			}
		}

		t = (t < 5E-9 || outofbounds(ijk.multiply(t).add(xyz.add(offSet)))) ? Double.NaN : t;
		return t;
	}

	/**
	 * Derives the normal vector at a given point
	 * 
	 * @param intercept
	 *            Point3D finds normal at this point
	 * @return Point3D normal vector
	 */
	public Point3D getGradient(Point3D intercept) {
		intercept = intercept.subtract(offSet);
		double Fx = 2 * A * intercept.getX() + D * intercept.getY() + E * intercept.getZ() + G;
		double Fy = 2 * B * intercept.getY() + D * intercept.getX() + F * intercept.getZ() + H;
		double Fz = 2 * C * intercept.getZ() + E * intercept.getX() + F * intercept.getY() + I;
		Point3D normal = new Point3D(Fx, Fy, Fz).normalize();
		return normal;
	}

	double getLeftSide() {
		return -J;
	}

	public Group plot() {
		Group result = new Group();
		double res = 0.05;
		PhongMaterial copper = new PhongMaterial();
		copper.setDiffuseColor(Color.rgb(200, 117, 51, 1));
		copper.setSpecularColor(Color.web("black", 1));
		if (A == 0 && D == 0 && E == 0 && G == 0) {
			for (double x = min.getX(); x <= max.getX(); x += (res)) {
				for (double z = min.getZ(); z <= max.getZ(); z += (res)) {
					double a = B;
					double b = F * z + H;
					double c = C * Math.pow(z, 2) + I * z + J;
					double discrim = Math.pow(b, 2) - 4 * a * c;

					double y1 = (B == 0) ? -c / b : (-b + Math.sqrt(discrim)) / (2 * a);
					double y2 = (B == 0) ? -c / b : (-b - Math.sqrt(discrim)) / (2 * a);

					if (!Double.isNaN(y1) && y1 >= min.getY() && y1 <= max.getY()) {
						Sphere s1 = new Sphere(0.02);
						s1.setLayoutY(-y1 - offSet.getY());
						s1.setTranslateZ(z + offSet.getZ());
						s1.setLayoutX(x + offSet.getX());
						s1.setMaterial(copper);
						result.getChildren().add(s1);
					}
					if (!Double.isNaN(y2) && y2 >= min.getY() && y2 <= max.getY()) {
						Sphere s2 = new Sphere(0.02);
						s2.setLayoutY(-y2 - offSet.getY());
						s2.setTranslateZ(z + offSet.getZ());
						s2.setLayoutX(x + offSet.getX());
						s2.setMaterial(copper);
						result.getChildren().add(s2);
					}
				}
			}
		} else {
			for (double y = min.getY(); y <= max.getY(); y += (res)) {
				for (double z = min.getZ(); z <= max.getZ(); z += (res)) {
					double a = A;
					double b = D * y + E * z + G;
					double c = B * Math.pow(y, 2) + C * Math.pow(z, 2) + F * y * z + H * y + I * z + J;
					double discrim = Math.pow(b, 2) - 4 * a * c;

					double x1 = (A == 0) ? -c / b : (-b + Math.sqrt(discrim)) / (2 * a);
					double x2 = (A == 0) ? -c / b : (-b - Math.sqrt(discrim)) / (2 * a);

					if (!Double.isNaN(x1) && x1 >= min.getX() && x1 <= max.getX()) {
						Sphere cir1 = new Sphere(0.02);
						cir1.setLayoutY(-y - offSet.getY());
						cir1.setTranslateZ(z + offSet.getZ());
						cir1.setLayoutX(x1 + offSet.getX());
						cir1.setMaterial(copper);
						result.getChildren().add(cir1);
					}
					if (!Double.isNaN(x2) && x2 >= min.getX() && x2 <= max.getX()) {
						Sphere cir2 = new Sphere(0.02);
						cir2.setLayoutY(-y - offSet.getY());
						cir2.setTranslateZ(z + offSet.getZ());
						cir2.setLayoutX(x2 + offSet.getX());
						cir2.setMaterial(copper);
						result.getChildren().add(cir2);
					}
				}
			}
		}
		return result;
	}
}
