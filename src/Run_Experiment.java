import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

import java.util.concurrent.ThreadLocalRandom;

public class Run_Experiment extends Application implements Constants {
	private Group root = new Group();
	private final Xform chamberGroup = new Xform();
	private final Xform reflectionsGroup = new Xform();
	private final Xform world = new Xform();
	private final PerspectiveCamera camera = new PerspectiveCamera(false);
	private final Xform cameraXform = new Xform();
	private final Xform cameraXform2 = new Xform();
	private final Xform cameraXform3 = new Xform();
	private static PointLight light = new PointLight();
	private static AmbientLight light2 = new AmbientLight();
	private static Display maincanv = new Display();
	private static final double CAMERA_INITIAL_DISTANCE = -16 * scale;
	private static final double CAMERA_NEAR_CLIP = 0.01;
	private static final double CAMERA_FAR_CLIP = 5000.0;
	private static final double SHIFT_MULTIPLIER = 10.0;
	private static final double MOUSE_SPEED = 0.1;
	private static final double ROTATION_SPEED = 2.0;
	private static final double TRACK_SPEED = 0.3;

	private double mousePosX;
	private double mousePosY;
	private double mouseOldX;
	private double mouseOldY;
	private double mouseDeltaX;
	private double mouseDeltaY;

	private int lnCount = 0;
	private double absorbProb = 0;
	private int absorbCount = 0;
	private double lnwidth = 0.1;
	private Xform pastLinesSide = new Xform();
	private Xform pastLinesTop = new Xform();
	private Xform pastLinesFront = new Xform();

	public static void main(String[] args) {
		launch(args);
	}

	private Group buildCamera() {
		Group cams = new Group();
		System.out.println("buildCamera()");

		cameraXform.getChildren().add(cameraXform2);
		cameraXform2.getChildren().add(cameraXform3);
		cameraXform3.getChildren().add(camera);

		camera.setNearClip(CAMERA_NEAR_CLIP);
		camera.setFarClip(CAMERA_FAR_CLIP);
		camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
		light.setColor(Color.WHITE);
		light2.setColor(Color.WHITE);

		cams.getChildren().addAll(cameraXform, light2, light);
		return cams;

	}

	private void handleZoom(Scene scene, final Node root) {
		scene.setOnZoom(new EventHandler<ZoomEvent>() {
			@Override
			public void handle(ZoomEvent me) {
				double z = camera.getTranslateZ();
				double newZ = z + mouseDeltaX * me.getZoomFactor();
				camera.setTranslateZ(newZ);
				me.consume();
			}
		});
	}

	private void handleMouse(Scene scene, final Node root) {

		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				reflectionsGroup.setVisible(false);
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseOldX = me.getSceneX();
				mouseOldY = me.getSceneY();
			}
		});
		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mouseOldX = mousePosX;
				mouseOldY = mousePosY;
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseDeltaX = (mousePosX - mouseOldX);
				mouseDeltaY = (mousePosY - mouseOldY);

				double modifier = 1.0;
				if (me.isControlDown() && me.isShiftDown()) {
					modifier = SHIFT_MULTIPLIER;
					cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
					cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
				} else {
					if (me.isShiftDown()) {
						modifier = SHIFT_MULTIPLIER;
					}
					if (me.isPrimaryButtonDown()) {
						cameraXform.ry.setAngle(
								cameraXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
						cameraXform.rx.setAngle(
								cameraXform.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
					} else if (me.isSecondaryButtonDown()) {
						modifier += SHIFT_MULTIPLIER;
						double z = camera.getTranslateZ();
						double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
						camera.setTranslateZ(newZ);
					}
				}
			}
		});
		scene.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				reflectionsGroup.setVisible(true);
			}
		});
	}

	private void handleKeyboard(Scene scene, final Node root) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				reflectionsGroup.setVisible(false);
				switch (event.getCode()) {
				case SPACE:
					cameraXform2.t.setX(0.0);
					cameraXform2.t.setY(0.0);
					camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
					cameraXform.ry.setAngle(0);
					cameraXform.rx.setAngle(0);
					break;
				case X:
					chamberGroup.setVisible(!chamberGroup.isVisible());
					break;
				case V:
					reflectionsGroup.setVisible(!reflectionsGroup.isVisible());
					break;
				case UP:
					cameraXform2.t.setY(cameraXform2.t.getY() - 10);
					break;
				case DOWN:
					cameraXform2.t.setY(cameraXform2.t.getY() + 10);
					break;
				case RIGHT:
					cameraXform2.t.setX(cameraXform2.t.getX() + 10);
					break;
				case LEFT:
					cameraXform2.t.setX(cameraXform2.t.getX() - 10);
					break;
				default:
					break;
				}
			}
		});
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent me) {
				reflectionsGroup.setVisible(true);
			}
		});

	}

	/**
	 * Calculates the interaction of a given parent ray (which are the initial
	 * rays emerging from the waveguide) for a given number of times Adds the
	 * resulting (reflected) ray to the end of the parent ray's subsequent
	 * reflected rays. There is a chance that the ray will be absorbed and
	 * ceases to propagate.
	 * 
	 * @see Constants
	 * 
	 * @param parentRay
	 *            The initial ray that all subsequent bounces are dependent on
	 * @param n
	 *            Number of intercepts (NOT bounces)
	 * @throws Exception
	 *             Required to call the reflect method and calculate the
	 *             reflected vector
	 */
	private void bounce(Ray parentRay, int n) {
		for (int i = 0; i < n; i++) {
			if (parentRay.reflections.isEmpty() && i != 0) {
				break;
			}

			Ray incidentRay = (parentRay.reflections.isEmpty()) ? parentRay : parentRay.reflections.get(i - 1);
			int y = (i < 7) ? i : ((i % 7) - 1) * (i / 7);
			switch (y) {
			case 0:
				incidentRay.color = "red";
				break;
			case 1:
				incidentRay.color = "darkorange";
				break;
			case 2:
				incidentRay.color = "yellow";
				break;
			case 3:
				incidentRay.color = "lime";
				break;
			case 4:
				incidentRay.color = "blue";
				break;
			case 5:
				incidentRay.color = "purple";
				break;
			case 6:
				incidentRay.color = "violet";
				break;
			}

			if (incidentRay.getVector().equals(Point3D.ZERO)) {
				System.err.println("\nRay Terminated\n");
				parentRay.color = "white";
				for (Ray ray : parentRay.reflections) {
					ray.color = "white";
				}
				incidentRay.color = "white";
				break;
			}
			int absorb = ThreadLocalRandom.current().nextInt(1, 101);
			Point3D r = Point3D.ZERO;
			Point3D start = Point3D.ZERO;
			if (absorb > absorbProb) {
				r = incidentRay.reflect();
				start = incidentRay.getEndPos();
				lnCount++;
				parentRay.reflections.add(new Ray(start, r));
			} else {
				absorbCount++;
				break;
			}
		}
	}

	/**
	 * Adds an array of randomly created beams with a maximum possible
	 * divergence provided in the Constants interface. Determines the color of
	 * the parent ray and all of its subsequent reflected rays based on the
	 * parent rays starting location with respect to the center of the waveguide
	 * 
	 * @param iPos
	 *            Origin point of the beam
	 * @param v
	 *            Initial directional vector (as Point3D) that the beam is
	 *            pointed
	 * @param waveGuideRadius
	 *            Half of the initial width of the beam emerging from the
	 *            waveguide
	 */
	@SuppressWarnings("unused")
	private void createBeam(Point3D iPos, Point3D v, double wgR) {
		lnwidth = 1 / scale;
		double dr = ThreadLocalRandom.current().nextDouble(0, wgR / 2);
		for (double r = 0; r < wgR; r += dr) {
			for (Point3D p : ptsOnCircle(iPos, r)) {
				float divergeY = (float) ThreadLocalRandom.current().nextDouble(-1, 1);
				float divergeZ = (float) ThreadLocalRandom.current().nextDouble(-1, 1);
				Point3D offset = new Point3D(0, divergeY, divergeZ);

				Point3D altIncident = v.add(offset.multiply(Math.tan(Math.toRadians(divergence)) * v.getX()));
				Ray R = new Ray(p, altIncident);
				double dZ = iPos.getZ() - p.getZ();
				if (CoordValidation.isValid(R))
					parentRays.add(R);
			}
			dr = (float) ThreadLocalRandom.current().nextDouble(0, wgR / 2);
		}
	}

	/**
	 * Given a circle's location and radius, return points along the perimeter
	 * with a random separation between 10 and 20 degrees
	 * 
	 * @param iPos
	 *            Center position of the circle
	 * @param radius
	 *            Radius of the circle
	 * @return An ArrayList of Point3D's located around the perimeter of the
	 *         circle
	 */
	private ArrayList<Point3D> ptsOnCircle(Point3D iPos, double radius) {
		ArrayList<Point3D> pts = new ArrayList<Point3D>();
		int[] dsRng = new int[]{10,20};
		double ds = ThreadLocalRandom.current().nextDouble(dsRng[0], dsRng[1]);
		for (double degree = 0; degree < 360; degree += ds) {
			ds = ThreadLocalRandom.current().nextDouble(dsRng[0], dsRng[1]);
			double radians = Math.toRadians(degree);
			Point3D pt = iPos.add(0, radius * Math.sin(radians), radius * Math.cos(radians));
			pts.add(pt);
		}
		return pts;
	}

	private Line createLine(double i1, double j1, double i2, double j2, String color) {
		Line ln = new Line(i1, -j1, i2, -j2);
		ln.setStroke(Color.web(color));
		ln.setStrokeWidth(lnwidth);
//		ln.setRotationAxis(Rotate.Y_AXIS);
//		Rotate r=new Rotate();
//		r.setPivotX(start.getX());
//		r.setPivotY(start.getY());
//		r.setPivotZ(start.getZ());
//		r.setAngle(30);
//		r.setAxis(Rotate.Y_AXIS);
//		ln.getTransforms().add(r);
		return ln;
	}

	@Override
	public void start(Stage primaryStage) throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("start()");
		surface.add(sphere);
		surface.add(cylinder);
		surface.add(plane);

		QuadricSurface hyperbaloid2 = new QuadricSurface("1=0.16x^2+1.5y^2+0.19z^2", new Point3D(0,0,-1), new Point3D(2, 2, 1),
				new Point3D(-0.5, 1, 0));
		QuadricSurface hyperbaloid1 = new QuadricSurface("1=-x+y", new Point3D(0,0,-1), new Point3D(1, 2, 1),
				new Point3D(0, -2.5, 0));
		surface.add(hyperbaloid2);
		surface.add(hyperbaloid1);
		System.out.println("Added the 3 base parametric"
				+ ((surface.size() > 3) ? (" and " + (surface.size() - 3) + " reflective") : "") + " surfaces");

		Point3D incident = new Point3D(1, 0, 0);
		Point3D emmitPos = new Point3D(-4, -1, 0);
//		Point3D emmitPos = new Point3D(-1, -1, 0);
		int n = 3;
		absorbProb = 0;
		System.out
				.println("Beam parameters:\n\tEmmitPos: " + emmitPos.toString() + "\n\tIncident: " + incident.toString()
						+ "\n\tBounces#: " + (n - 1) + "\n\tAbsorbtion: " + absorbProb + "% per reflection\n");
		incident = incident.normalize();
		parentRays.add(new Ray(emmitPos, incident));
		 createBeam(emmitPos, incident, wgRadius);
		for (Ray pRay : parentRays) {
			bounce(pRay, n);
		}
		lnCount = parentRays.size();
		System.out.println("done bouncing with " + lnCount + " lines " + (n - 1) + " times with "
				+ (100 * absorbCount / lnCount) + " % absorbed");
		PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
		for (Ray pRay : parentRays) {
			pRay.reflections.add(0, pRay);
			for (Ray aRay : pRay.reflections) {
				int idx = pRay.reflections.indexOf(aRay);
				if (idx < n) {
					DecimalFormat df = new DecimalFormat("#0.0000000000");
					Point3D start = aRay.getStartPos();
					Point3D end = aRay.getEndPos();
					if (!end.equals(Point3D.ZERO)) {
						writer.println(df.format(start.getX()) + "," + df.format(start.getY()) + ","
								+ df.format(start.getZ()) + "|" + df.format(end.getX()) + "," + df.format(end.getY())
								+ "," + df.format(end.getZ()));
						Line ln;
						ln = createLine(start.getX(), start.getY(), end.getX(), end.getY(), aRay.color);
						pastLinesSide.getChildren().add(ln);

						ln = createLine(start.getX(), start.getZ(), end.getX(), end.getZ(), aRay.color);
						pastLinesTop.getChildren().add(ln);

						ln = createLine(start.getZ(), start.getY(), end.getZ(), end.getY(), aRay.color);
						pastLinesFront.getChildren().add(ln);
					}
				}
			}
			writer.println();
		}
		writer.close();
		
		double w = wgRadius*2;
		Box emmit = new Box(w, w, w);
		emmit.setLayoutX(emmitPos.getX()-7);
		emmit.setLayoutY(-emmitPos.getY());
		 emmit.setTranslateZ(emmitPos.getZ());
		emmit.setMaterial(new PhongMaterial(Color.LIGHTGRAY));
		
		
		reflectionsGroup.getChildren().add(emmit);
		Scene scene = new Scene(root, width, height, Color.BLACK);
		primaryStage.setTitle(maincanv.getWindowTitle());
		root.getChildren().add(world);
		buildCamera();
		root.getChildren().addAll(cameraXform, light2, light);
		ArrayList<QuadricSurface> otherSurfs = new ArrayList<QuadricSurface>(surface.subList(3, surface.size()));
		world.getChildren().addAll(chamberGroup);
		chamberGroup.getChildren().addAll(maincanv.getSurface(otherSurfs));

		handleKeyboard(scene, world);
		handleMouse(scene, world);
		handleZoom(scene, world);

		Xform reflectors = new Xform();
		for (QuadricSurface p : otherSurfs) {
			reflectors.getChildren().add(p.plot());
		}
		reflectors.setTranslate(1, -8);
		handleMouse(scene, reflectors);
		chamberGroup.getChildren().add(reflectors);
		pastLinesTop.setTranslateY(-7);
		pastLinesTop.setTranslateX(-7);
		pastLinesSide.setTranslateX(-7);
		reflectionsGroup.getChildren().addAll(pastLinesFront, pastLinesTop, pastLinesSide);
		world.getChildren().addAll(reflectionsGroup);
		world.setScale(scale);
		world.setTranslateY(maincanv.getHeight());
		world.setTranslateX(maincanv.getWidth() + 100);
		primaryStage.setScene(scene);
		scene.setCamera(camera);
		primaryStage.show();
	}

}