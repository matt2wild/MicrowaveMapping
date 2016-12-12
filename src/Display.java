import java.util.ArrayList;

import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

public class Display implements Constants {
	String windowTitle = "Ray Tracer";

	public Display() {

	}

	Group getSurface(ArrayList<QuadricSurface> extraSurfaces) {
		Group surface = new Group();
		ReflectionChamber rf1 = new ReflectionChamber((float) radius, 3 * (float) radius, Color.rgb(173, 178, 189), 80);
		ReflectionChamber rf2 = new ReflectionChamber((float) radius, 3 * (float) radius, Color.rgb(173, 178, 189), 80);
		ReflectionChamber rf3 = new ReflectionChamber((float) radius, 3 * (float) radius, Color.rgb(173, 178, 189), 80);

		rf1.getTransforms().add(new Rotate(90f));
		rf2.getTransforms().add(new Rotate(90f));
		rf3.getTransforms().add(new Rotate(90f));
		Xform side = new Xform();
		for (QuadricSurface p : extraSurfaces) {
			side.getChildren().add(p.plot());
		}
		side.getChildren().add(rf1);
		side.setLayoutX(-7);

		Xform top = new Xform();
		for (QuadricSurface p : extraSurfaces) {
			top.getChildren().add(p.plot());
		}
		top.getChildren().add(rf2);
		top.setLayoutX(-7);
		top.setLayoutY(-7);
		top.setRotateX(90f);
		Xform front = new Xform();
		for (QuadricSurface p : extraSurfaces) {
			Xform tmp = new Xform();
			tmp.getChildren().add(p.plot());
			front.getChildren().add(tmp);
		}
		front.getChildren().add(rf3);
		front.setRotateY(90);

		Line topLine = new Line(plane.getLeftSide(), -radius, 0, -radius);
		topLine.setStroke(Color.web("white", 0.7));
		topLine.setStrokeWidth(0.1);
		Line bottomLine = new Line(plane.getLeftSide(), radius, 0, radius);
		bottomLine.setStroke(Color.web("white", 0.7));
		bottomLine.setStrokeWidth(0.1);
		Arc arc = new Arc();
		arc.setCenterX(0);
		arc.setCenterY(0);
		arc.setRadiusY(radius);
		arc.setRadiusX(radius);
		arc.setStroke(Color.web("white", 0.7));
		arc.setFill(null);
		arc.setStrokeWidth(0.1);
		arc.setStartAngle(-90f);
		arc.setType(ArcType.OPEN);
		arc.setLength(180f);
		// surface.getChildren().addAll(arc, topLine, bottomLine);

		surface.getChildren().addAll(side, top, front);
		return surface;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}

}
