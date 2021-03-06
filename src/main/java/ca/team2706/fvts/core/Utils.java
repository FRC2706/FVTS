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

import ca.team2706.fvts.core.image.AbstractImagePreprocessor;
import ca.team2706.fvts.core.interfaces.AbstractInterface;
import ca.team2706.fvts.core.math.AbstractMathProcessor;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;
import ca.team2706.fvts.core.pipelines.AbstractPipeline;
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

	public static List<AttributeOptions> getOptions(String pipelineName, String interfaceName, String mathNames,
			String preProcessors) {
		AttributeOptions name = new AttributeOptions("name", true);

		AttributeOptions type = new AttributeOptions("core/type", true);

		AttributeOptions identifier = new AttributeOptions("core/identifier", true);

		AttributeOptions enabled = new AttributeOptions("enabled", false);

		AttributeOptions csvLog = new AttributeOptions("core/csvLog", true);

		AttributeOptions imgDumpPath = new AttributeOptions("core/imgDumpPath", true);

		AttributeOptions imgDumpTime = new AttributeOptions("core/imgDumpTime", true);

		List<AttributeOptions> options = new ArrayList<AttributeOptions>();
		options.add(name);

		options.add(type);
		options.add(identifier);
		options.add(enabled);
		options.add(csvLog);
		options.add(imgDumpPath);
		options.add(imgDumpTime);

		AbstractPipeline pipeline = AbstractPipeline.getByName(pipelineName);
		if (pipeline == null) {
			Log.e("Failed to find pipeline by the name of " + pipelineName, true);
			System.exit(1);
		}
		options.addAll(pipeline.getOptions());

		AbstractInterface outputInterface = AbstractInterface.getByName(interfaceName);
		if (outputInterface == null) {
			Log.e("Failed to find interface by the name of " + interfaceName, true);
			System.exit(1);
		}
		options.addAll(outputInterface.getOptions());

		if (mathNames != null) {
			String[] maths = mathNames.split(",");
			for (String math : maths) {
				AbstractMathProcessor processor = AbstractMathProcessor.getByName(math);
				if (processor == null) {
					Log.e("Failed to find math processor by the name of " + math, true);
					System.exit(1);
				}
				options.addAll(processor.getOptions());
			}
		}

		if (preProcessors != null) {
			String[] preProcessorsA = preProcessors.split(",");
			for (String processor : preProcessorsA) {
				AbstractImagePreprocessor p = AbstractImagePreprocessor.getByName(processor);
				if (p == null) {
					Log.e("Failed to find image preprocessor by the name of " + processor, true);
					System.exit(1);
				}
				options.addAll(p.getOptions());
			}
		}

		return options;
	}

	/**
	 * Loads the visionTable params! :]
	 **/

	public static List<VisionParams> loadVisionParams() {
		try {
			List<String> lists = ConfigParser.listLists(Main.visionParamsFile);
			List<VisionParams> ret = new ArrayList<VisionParams>();
			for (String s : lists) {
				try {
					Map<String, Map<String, String>> data = ConfigParser.getProperties(Main.visionParamsFile, s);

					List<Attribute> attribs = new ArrayList<Attribute>();
					attribs.add(new Attribute("name", s));
					String pipelineName = null;
					String interfaceName = null;
					String mathNames = null;
					String imagePreprocessorNames = null;
					if (data.get("core") == null) {
						Log.e("Config " + s + " is missing the core section!", true);
						System.exit(1);
					}
					for (String s1 : data.keySet()) {
						for (String s2 : data.get(s1).keySet()) {
							if (s1.equals("core")) {
								if (s2.equals("pipeline")) {
									pipelineName = data.get("core").get(s2);
								} else if (s2.equals("interface")) {
									interfaceName = data.get("core").get(s2);
								} else if (s2.equals("maths")) {
									mathNames = data.get("core").get(s2);
								} else if (s2.equals("preprocessors")) {
									imagePreprocessorNames = data.get("core").get(s2);
								}
							}
							attribs.add(new Attribute(s1 + "/" + s2, data.get(s1).get(s2)));
						}
					}
					if (interfaceName == null || pipelineName == null) {
						Log.e("Missing pipeline or interface in config " + s, true);
						System.exit(1);
					}
					List<AttributeOptions> options = getOptions(pipelineName, interfaceName, mathNames,
							imagePreprocessorNames);
					VisionParams params = new VisionParams(attribs, options);

					// The parameters are now valid, because it didn't throw an error
					ret.add(params);
				} catch (Exception e) {
					Log.e("Error in config " + s, true);
					throw new Exception();
				}
			}
			return ret;

		} catch (Exception e1) {
			Log.e("\n\nError reading the params file, check if the file is corrupt?", true);
			System.exit(1);
		}
		return null;
	}

	public static void sendVisionParams(VisionParams params) {
		for (Attribute a : params.getAttribs()) {
			if (a.getName().equals("name")) {
				NetworkTable visionTable = NetworkTablesManager.tables.get(params.getByName("name").getValue());
				visionTable.putString(a.getName(), a.getValue());
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
		Map<String, Map<String,String>> data = new HashMap<String, Map<String,String>>();

		for (Attribute a : params.getAttribs()) {
			if (a.getName().contains("/")) {
				String key = a.getName().split("\\/")[0];
				Map<String,String> map = data.get(key);
				if(map == null)
					map = new HashMap<String,String>();
				map.put(a.getName().split("\\/",2)[1], a.getValue());
				data.put(key, map);
			}
		}

		ConfigParser.saveList(Main.visionParamsFile, params.getByName("name").getValue(), data);
	}

	public static int findFirstAvailable(String pattern) {
		if (!pattern.contains("$1"))
			return 0;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			File f = new File(pattern.replaceAll("\\$1", "" + i));
			if (!f.exists())
				return i;
		}
		return 0;
	}
}
