import javafx.scene.AmbientLight;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class ReflectionChamber extends Group {

	public boolean selfLightEnabled = true;
	public AmbientLight selfLight = new AmbientLight(Color.WHITE);
	public float radius;
	public float length;
	public Color color;
	public int granularity;
	public boolean ambient;
	public boolean fill;

	public TriangleMesh mesh;
	public MeshView meshView;

	/**
	 * I really have no idea how this works. <b>DO NOT EDIT</b>
	 * 
	 * @param radius
	 *            radius of the sphere segment
	 * @param color
	 *            The sphere segment color.
	 * @param granularity
	 *            The number of segments of curves approximations.
	 * @param ambient
	 *            Whether to have an ambient light or not
	 * @param fill
	 *            whether to show filled with the color param or as wire mesh
	 */
	public ReflectionChamber(float radius, float length, Color color,
			int granularity) {

		this.radius = radius;
		this.length = length;
		this.color = color;
		this.granularity = granularity;
		setDepthTest(DepthTest.ENABLE);

		mesh = new TriangleMesh();
		granularity = granularity + 3;

		final int halfDivisions = granularity / 2;
		final float fDivisions = 1.f / granularity;

		final int numPoints = granularity * (halfDivisions - 1) + 2;
		final int numTexCoords = (granularity + 1) * (halfDivisions - 1)
				+ granularity * 2;
		final int numFaces = granularity * 2 * (halfDivisions - 1);

		float points[] = new float[numPoints * mesh.getPointElementSize()];
		float texCoords[] = new float[numTexCoords
				* mesh.getTexCoordElementSize()];
		int faces[] = new int[numFaces * mesh.getFaceElementSize()];

		int pointIndex = 0, texIndex = 0;

		for (int i = 0; i < halfDivisions - 1; i++) {
			float va = fDivisions * (i + 1 - halfDivisions / 2) * 2
					* (float) Math.PI;
			float hdY = (float) Math.sin(va);
			float hdX = (float) Math.cos(va);

			float thetaY = 0.5f + hdY * 0.5f;

			for (int point = 0; point < granularity; point++) {
				double localTheta = fDivisions * point * 2 * (float) Math.PI;
				float ly = (float) Math.sin(localTheta);
				float lx = (float) Math.cos(localTheta);

				if (i >= (halfDivisions - 1) / 2) {
					// the length creates the joining cylinder
					points[pointIndex + 0] = ly * radius; // X
					points[pointIndex + 1] = length;// Y
					points[pointIndex + 2] = lx * radius; // Z
				} else {
					points[pointIndex + 0] = ly * hdX * radius; // X
					points[pointIndex + 1] = hdY * radius; // Y
					points[pointIndex + 2] = lx * hdX * radius; // Z
				}
				texCoords[texIndex + 0] = 1 - fDivisions * point;
				texCoords[texIndex + 1] = thetaY;
				pointIndex += 3;
				texIndex += 2;

			}
			texCoords[texIndex + 0] = 0;
			texCoords[texIndex + 1] = thetaY;
			texIndex += 2;
		}
		points[pointIndex + 0] = 0;
		points[pointIndex + 1] = -radius;
		points[pointIndex + 2] = 0;

		pointIndex += 3;

		int pS = (halfDivisions - 1) * granularity;

		float textureDelta = 1.f / 256;
		for (int i = 0; i < granularity; i++) {
			texCoords[texIndex + 0] = fDivisions * (0.5f + i);
			texCoords[texIndex + 1] = textureDelta;
			texIndex += 2;
		}

		for (int i = 0; i < granularity; i++) {
			texCoords[texIndex + 0] = fDivisions * (0.5f + i);
			texCoords[texIndex + 1] = 1 - textureDelta;
			texIndex += 2;
		}

		int faceIndex = 0;
		for (int i = 0; i < halfDivisions - 2; i++) {
			for (int j = 0; j < granularity; j++) {
				int p0 = i * granularity + j;
				int p1 = p0 + 1;
				int p2 = p0 + granularity;
				int p3 = p1 + granularity;

				int t0 = p0 + i;
				int t1 = t0 + 1;
				int t2 = t0 + (granularity + 1);
				int t3 = t1 + (granularity + 1);

				// add p0, p1, p2
				faces[faceIndex + 0] = p0;
				faces[faceIndex + 1] = t0;
				faces[faceIndex + 2] = p1 % granularity == 0 ? p1 - granularity
						: p1;
				faces[faceIndex + 3] = t1;
				faces[faceIndex + 4] = p2;
				faces[faceIndex + 5] = t2;
				faceIndex += 6;

				// add p3, p2, p1
				faces[faceIndex + 0] = p3 % granularity == 0 ? p3 - granularity
						: p3;
				faces[faceIndex + 1] = t3;
				faces[faceIndex + 2] = p2;
				faces[faceIndex + 3] = t2;
				faces[faceIndex + 4] = p1 % granularity == 0 ? p1 - granularity
						: p1;
				faces[faceIndex + 5] = t1;
				faceIndex += 6;
			}
		}
		// cap of dome
		int p0 = pS;
		int tB = (halfDivisions - 1) * (granularity + 1);
		for (int i = 0; i < granularity; i++) {
			int p2 = i, p1 = i + 1, t0 = tB + i;
			faces[faceIndex + 0] = p0;
			faces[faceIndex + 1] = t0;
			faces[faceIndex + 2] = p1 == granularity ? 0 : p1;
			faces[faceIndex + 3] = p1;
			faces[faceIndex + 4] = p2;
			faces[faceIndex + 5] = p2;
			faceIndex += 6;
		}

		mesh.getPoints().setAll(points);
		mesh.getTexCoords().setAll(texCoords);
		mesh.getFaces().setAll(faces);
		PhongMaterial maxPhong = new PhongMaterial();
		maxPhong.setSpecularColor(color);
		maxPhong.setDiffuseColor(Color.web("gray", 0.001));

		// Create a viewable MeshView to be added to the scene
		// To add a TriangleMesh to a 3D scene you need a MeshView container
		// object

		meshView = new MeshView(mesh);
		// The MeshView allows you to control how the TriangleMesh is rendered

		meshView.setDrawMode(DrawMode.FILL);
		meshView.setDrawMode(DrawMode.LINE);
		// show lines only by default

		meshView.setCullFace(CullFace.FRONT); // Removing culling to show back
												// lines

		getChildren().add(meshView);
		meshView.setMaterial(maxPhong);
		// if (ambient) {
		// AmbientLight light = new AmbientLight(Color.WHITE);
		// light.getScope().add(meshView);
		// getChildren().add(light);
		// }
	}
}