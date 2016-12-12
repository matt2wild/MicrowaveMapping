import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import javafx.geometry.Point3D;

public class Ray implements Constants {
	private Point3D v = new Point3D(0, 0, 0);
	private Point3D startPos = new Point3D(0, 0, 0);
	private Point3D endPos = new Point3D(0, 0, 0);
	private double t = Double.NaN;
	public ArrayList<Ray> reflections = new ArrayList<Ray>();
	public String color = "cyan";

	/**
	 * Rays are individual beams that can scatter and interact with Quadric
	 * Surfaces
	 * 
	 * @param iPos
	 *            Point3D - starting position of the ray
	 * @param initialPoint3D
	 *            Point3D - initial Vector
	 */
	public Ray(Point3D iPos, Point3D initialPoint3D) {
		v = initialPoint3D;
		startPos = iPos;
	}

	/**
	 * @return Start: {@link Point3D#toString()} Vector:
	 *         {@link Point3D#toString()}
	 */
	public String toString() {
		String str = "Start: " + startPos.toString() + "\n\tVector: " + v.toString() + "\n\tEnd: " + endPos.toString();
		return str;
	}

	public Point3D getStartPos() {
		return startPos;
	}

	public void setStartPos(Point3D p) {
		startPos = new Point3D(p.getX(), p.getY(), p.getZ());
	}

	public Point3D getEndPos() {
		return endPos;
	}

	public void setEndPos(Point3D p) {
		endPos = new Point3D(p.getX(), p.getY(), p.getZ());
	}

	Point3D getVector() {
		return v;
	}

	public void setVector(Point3D aVect) {
		v = aVect;
	}

	public double get_t() {
		return t;
	}

	/**
	 * Returns the reflection vector if reflecting off of a valid surface,
	 * otherwise returns the zero vector
	 * 
	 * @return r = i - 2(i<b>&middot;</b>N)N<text-decoration: overline;>
	 */
	public Point3D reflect() {
		int s = closestSurface();
		Point3D r = Point3D.ZERO;
		if (s != -1) {
			Point3D Normal = surface.get(s).getGradient(endPos);
			v = v.add((Normal.multiply(v.dotProduct(Normal))).multiply(-2));
			r = v.normalize();
		} else {
			System.err.println("\nRay does not intersect\n");

			setStartPos(endPos);
			System.out.println(toString());
		}
		return r;
	}

	/**
	 * Sorts a Map by values rather than keys and returns this as a sorted set
	 * 
	 * @param map
	 *            Map - map to be sorted
	 * @return SortedSet - sortedEntries
	 * @see #closestSurface()
	 */
	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> valSorted(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				int res = e1.getValue().compareTo(e2.getValue());
				return res != 0 ? res : 1;
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	/**
	 * Derives the index of the closest valid surface as well as setting the end
	 * position of the ray
	 * 
	 * @return s - the index in the surface ArrayList for the closest valid
	 *         surface.
	 * @see QuadricSurface#get_t(Point3D xyz, Point3D ijk)
	 */
	public int closestSurface() {
		int s;
		Map<Integer, Double> surface_ts = new HashMap<Integer, Double>();
		for (int i = 0; i < surface.size(); i++) {
			double tmp = surface.get(i).get_t(startPos, v);
			if (!Double.isNaN(tmp))
				surface_ts.put(i, tmp);
		}
		if (surface_ts.isEmpty()) {
			System.err.println("Empty Surface Set\n");
			return -1;
		}
		SortedSet<Entry<Integer, Double>> sorted = valSorted(surface_ts);
		t = sorted.first().getValue();
		s = sorted.first().getKey();
		
		setEndPos(v.multiply(t).add(startPos));

		return s;
	}
}