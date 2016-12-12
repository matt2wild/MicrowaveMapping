import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Point3D;

public interface Constants {
	int c = 299792458; // Speed of light
	double h = 4.13566766225E-15; // Plank's
	double wgRadius = 0.1f;
	double width = 1000;
	double height = 700;
	double divergence = 4;
	double radius = 3;
	QuadricSurface sphere = new QuadricSurface(Math.pow(radius, 2) + "=x^2+y^2+z^2",
			new Point3D(0, -radius, -radius), new Point3D(radius, radius,
					radius), new Point3D(0, 0, 0));
	QuadricSurface cylinder = new QuadricSurface(sphere.getLeftSide() + "=y^2+z^2",
			new Point3D(-3 * radius, -radius, -radius), new Point3D(0,
					radius, radius), new Point3D(0, 0, 0));
	QuadricSurface plane = new QuadricSurface((-3 * radius) + "=x", new Point3D(-3
			* radius, -radius, -radius), new Point3D(-3 * radius, radius,
			radius), new Point3D(0, 0, 0));
	ArrayList<QuadricSurface> surface = new ArrayList<QuadricSurface>();

	ArrayList<Ray> parentRays = new ArrayList<Ray>();

	double scale = 100.0;
	Map<Point3D, Integer> heatMap = new HashMap<Point3D, Integer>();
}
