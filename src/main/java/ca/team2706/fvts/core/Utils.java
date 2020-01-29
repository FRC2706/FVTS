package ca.team2706.fvts.core;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;
import ca.team2706.fvts.main.Main;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Utils {

	/**
	 * 
	 * @param The    image to dump to a file
	 * @param image  the image to be dumped
	 * @param suffix the suffix to put on the file name
	 * @throws IOException
	 */
	
	public static void imgDump(BufferedImage image, String suffix, int timestamp, String outputPath)
			throws IOException {
		// prepend the file name with the tamestamp integer, left-padded with
		// zeros so it sorts properly
		@SuppressWarnings("deprecation")
		String match = Main.loggingTable.getString("match");
		if (match.equals("")) {
			match = "practice";
		}
	
		File output = new File(outputPath + match + "-" + String.format("%05d", timestamp) + "_" + suffix + ".png");
		ImageIO.write(image, "png", output);
	}

	/**
	 * Converts a Buffered Image to a OpenCV Matrix
	 * 
	 * @param Buffered Image to convert to matrix
	 * @return The matrix from the buffered image
	 */
	
	public static Mat bufferedImageToMat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
	}

	/**
	 * Converts a OpenCV Matrix to a BufferedImage :)
	 * 
	 * @param matrix Matrix to be converted
	 * @return Generated from the matrix
	 * @throws IOException
	 * @throws Exception
	 */
	public static BufferedImage matToBufferedImage(Mat matrix) throws IOException {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		byte ba[] = mob.toArray();
	
		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
		matrix.release();
		return bi;
	}

	/**
	 * Loads the visionTable params! :]
	 **/

	public static List<VisionParams> loadVisionParams() {
		try {
			AttributeOptions name = new AttributeOptions("name", true);

			AttributeOptions minHue = new AttributeOptions("minHue", true);
			AttributeOptions maxHue = new AttributeOptions("maxHue", true);
			AttributeOptions minSat = new AttributeOptions("minSaturation", true);
			AttributeOptions maxSat = new AttributeOptions("maxSaturation", true);
			AttributeOptions minVal = new AttributeOptions("minValue", true);
			AttributeOptions maxVal = new AttributeOptions("maxValue", true);

			AttributeOptions distToCentreImportance = new AttributeOptions("distToCentreImportance", true);

			AttributeOptions imageFile = new AttributeOptions("imageFile", true);

			AttributeOptions minArea = new AttributeOptions("minArea", true);

			AttributeOptions erodeDilateIterations = new AttributeOptions("erodeDilateIterations", true);

			AttributeOptions resolution = new AttributeOptions("resolution", true);

			AttributeOptions imgDumpPath = new AttributeOptions("imgDumpPath", true);

			AttributeOptions imgDumpTime = new AttributeOptions("imgDumpTime", true);

			AttributeOptions slope = new AttributeOptions("slope", true);

			AttributeOptions yIntercept = new AttributeOptions("yIntercept", true);

			AttributeOptions group = new AttributeOptions("group", true);
			AttributeOptions angle = new AttributeOptions("groupAngle",true);

			AttributeOptions type = new AttributeOptions("type", true);

			AttributeOptions identifier = new AttributeOptions("identifier", true);

			AttributeOptions enabled = new AttributeOptions("enabled", false);

			Main.options = new ArrayList<AttributeOptions>();
			Main.options.add(name);
			Main.options.add(minHue);
			Main.options.add(maxHue);
			Main.options.add(minSat);
			Main.options.add(maxSat);
			Main.options.add(minVal);
			Main.options.add(maxVal);
			Main.options.add(distToCentreImportance);
			Main.options.add(imageFile);
			Main.options.add(minArea);
			Main.options.add(erodeDilateIterations);
			Main.options.add(resolution);
			Main.options.add(imgDumpPath);
			Main.options.add(imgDumpTime);
			Main.options.add(slope);
			Main.options.add(yIntercept);
			Main.options.add(group);
			Main.options.add(angle);
			Main.options.add(type);
			Main.options.add(identifier);
			Main.options.add(enabled);
			List<String> lists = ConfigParser.listLists(Main.visionParamsFile);
			List<VisionParams> ret = new ArrayList<VisionParams>();

			for (String s : lists) {

				Map<String, String> data = ConfigParser.getProperties(Main.visionParamsFile, s);

				List<Attribute> attribs = new ArrayList<Attribute>();
				attribs.add(new Attribute("name", s));
				for (String s1 : data.keySet()) {
					attribs.add(new Attribute(s1, data.get(s1)));
				}
				VisionParams params = new VisionParams(attribs, Main.options);
				String resolution1 = params.getByName("resolution").getValue();
				int width = Integer.valueOf(resolution1.split("x")[0]);
				int height = Integer.valueOf(resolution1.split("x")[1]);
				params.getAttribs().add(new Attribute("width", width + ""));
				params.getAttribs().add(new Attribute("height", height + ""));
				NetworkTable visionTable = NetworkTable
						.getTable("vision-" + params.getByName("name").getValue() + "/");
				NetworkTablesManager.tables.put(s, visionTable);
				// The parameters are now valid, because it didnt throw an error
				ret.add(params);
			}

			sendVisionParams(ret);
			return ret;

		} catch (Exception e1) {
			Log.e(e1.getMessage(), true);
			Log.e("\n\nError reading the params file, check if the file is corrupt?", true);
			System.exit(1);
		}
		return null;
	}
	public static void sendVisionParams(List<VisionParams> visionParamsList) {

		for (VisionParams params : visionParamsList) {

			for (Attribute a : params.getAttribs()) {
				if (a.getName().equals("name")) {
					NetworkTable visionTable = NetworkTablesManager.tables.get(params.getByName("name").getValue());
					visionTable.putString(a.getName(), a.getValue());
				}
			}
		}
	}

	/**
	 * Saves the vision parameters to a file
	 * 
	 **/
	public static void saveVisionParams() {
		try {
	
			for (VisionParams params : Main.visionParamsList) {
				Utils.saveVisionParams(params);
			}
	
		} catch (Exception e1) {
			Log.e(e1.getMessage(), true);
		}
	}

	public static void saveVisionParams(VisionParams params) throws Exception {
		Map<String, String> data = new HashMap<String, String>();
	
		for (Attribute a : params.getAttribs()) {
			if (!a.getName().equals("name")) {
				data.put(a.getName(), a.getValue());
			}
		}
	
		ConfigParser.saveList(Main.visionParamsFile, params.getByName("name").getValue(), data);
	}
	public static File findFirstAvailable(String pattern) {
		if(!pattern.contains("$1"))
			return new File(pattern);
		for(int i = 0; i < Integer.MAX_VALUE; i++) {
			File f = new File(pattern.replaceAll("\\$1", ""+i));
			if(!f.exists())
				return f;
		}
		return null;
	}
}
