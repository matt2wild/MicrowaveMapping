import java.util.*;
import java.util.stream.Stream;

import javafx.geometry.Point3D;

public class LineMapper extends Thread implements Constants {

	private Thread t;
	private String threadName;
	private final static double resolution = 5;
	private static double k;
	private static double j;

	LineMapper(String name) {
		threadName = name;
		System.out.println("Creating " + threadName);
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		Map<K, V> result = new LinkedHashMap<>();
		Stream<Map.Entry<K, V>> st = map.entrySet().stream();

		st.sorted(Map.Entry.comparingByValue()).forEachOrdered(
				e -> result.put(e.getKey(), e.getValue()));

		return result;
	}

	public void run() {
		try {
			for (double ci = -2 * radius; ci < radius; ci += resolution / scale) {
				Point3D[] intercepts = new Point3D[] { new Point3D(ci, j, k),
						new Point3D(ci, j, -k), new Point3D(ci, -j, k),
						new Point3D(ci, -j, -k) };

				for (Ray pRay : parentRays) {
					for (Ray aRay : pRay.reflections) {
						for (Point3D intercept : intercepts) {
//							if (CoordValidation.isValid(aRay)) {
								if (isClose(aRay, intercept)
										&& CoordValidation.isValid(new Point3D(
												ci, j, k))) {
									int count = 1;
									if (heatMap.containsKey(intercept)) {
										count = Integer.parseInt(heatMap.get(
												intercept).toString()) + 1;
									}
									heatMap.put(intercept, count);
//								}
							}
						}
					}
				}
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
			System.out.println("Thread " + threadName + " interrupted.");
		}
		t.interrupt();
		System.out.println(heatMap.size());
		System.out.println("Thread " + threadName + " exiting.");

	}

	public void start() {
		// System.out.println("Starting " + threadName);
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

	public static void main(String args[]) {

		ArrayList<LineMapper> threads = new ArrayList<LineMapper>();
		for (double cj = 0; cj < radius; cj += resolution / scale)
			for (double ck = 0; ck < radius; ck += resolution / scale) {
				threads.add(new LineMapper("Thread-" + threads.size()));
				k = ck;
				j = cj;
				if (threads.size() > 5000) {
					System.err.println("Over 5000");
					System.exit(0);
				}
			}
		for (LineMapper aThread : threads) {
			aThread.start();
		}
	}

	private static boolean isClose(Ray R, Point3D P) {
		Point3D str = R.getStartPos();
		Point3D end = R.getEndPos();
		Point3D v = end.subtract(str);
		Point3D w = P.subtract(str);
		double c1 = w.dotProduct(v);
		double c2 = v.dotProduct(v);
		double t = c1 / c2;
		Point3D Pos = str.add(v.multiply(t));
		if (c1 <= 0)
			Pos = str;
		if (c2 <= c1)
			Pos = end;

		double d = (P.subtract(Pos)).magnitude();

		if (d < 1)
			return true;

		return false;
	}
}
