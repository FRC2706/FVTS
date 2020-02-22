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

import ca.team2706.fvts.core.interfaces.AbstractInterface;
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

	public static List<AttributeOptions> getOptions(String pipelineName, String interfaceName){
		AttributeOptions name = new AttributeOptions("name", true);

		AttributeOptions type = new AttributeOptions("type", true);

		AttributeOptions identifier = new AttributeOptions("identifier", true);

		AttributeOptions enabled = new AttributeOptions("enabled", false);
		
		AttributeOptions csvLog = new AttributeOptions("csvLog", true);

		List<AttributeOptions> options = new ArrayList<AttributeOptions>();
		options.add(name);
		
		options.add(type);
		options.add(identifier);
		options.add(enabled);
		options.add(csvLog);
		
		AbstractPipeline pipeline = AbstractPipeline.getByName(pipelineName);
		options.addAll(pipeline.getOptions());
		
		AbstractInterface outputInterface = AbstractInterface.getByName(interfaceName);
		options.addAll(outputInterface.getOptions());
		
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

				Map<String, String> data = ConfigParser.getProperties(Main.visionParamsFile, s);

				List<Attribute> attribs = new ArrayList<Attribute>();
				attribs.add(new Attribute("name", s));
				String pipelineName = null;
				String interfaceName = null;
				for (String s1 : data.keySet()) {
					if(s1.equals("pipeline")) {
						pipelineName = data.get(s1);
					}else if(s1.equals("interface")){
						interfaceName = data.get(s1);
					}
					attribs.add(new Attribute(s1, data.get(s1)));
				}
				if(interfaceName == null || pipelineName == null) {
					Log.e("Missing pipeline or interface in config "+s, true);
					System.exit(1);
				}
				List<AttributeOptions> options = getOptions(pipelineName, interfaceName);
				VisionParams params = new VisionParams(attribs, options);
				String resolution1 = params.getByName("resolution").getValue();
				int width = Integer.valueOf(resolution1.split("x")[0]);
				int height = Integer.valueOf(resolution1.split("x")[1]);
				params.getAttribs().add(new Attribute("width", width + ""));
				params.getAttribs().add(new Attribute("height", height + ""));
				NetworkTable visionTable = NetworkTable
						.getTable("vision-" + params.getByName("name").getValue() + "/");
				NetworkTablesManager.tables.put(s, visionTable);
				// The parameters are now valid, because it didn't throw an error
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
			if (!a.getName().equals("name") && !a.getName().equals("enabled") && !a.getName().equals("width") && !a.getName().equals("height")) {
				data.put(a.getName(), a.getValue());
			}
		}
	
		ConfigParser.saveList(Main.visionParamsFile, params.getByName("name").getValue(), data);
	}
	public static int findFirstAvailable(String pattern) {
		if(!pattern.contains("$1"))
			return 0;
		for(int i = 0; i < Integer.MAX_VALUE; i++) {
			File f = new File(pattern.replaceAll("\\$1", ""+i));
			if(!f.exists())
				return i;
		}
		return 0;
	}
}
