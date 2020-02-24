package ca.team2706.fvts.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

import ca.team2706.fvts.core.image.AbstractImagePreprocessor;
import ca.team2706.fvts.core.input.AbstractInputDevice;
import ca.team2706.fvts.core.interfaces.AbstractInterface;
import ca.team2706.fvts.core.math.AbstractMathProcessor;
import ca.team2706.fvts.core.params.VisionParams;
import ca.team2706.fvts.core.pipelines.AbstractPipeline;
import ca.team2706.fvts.core.pipelines.BlobDetectPipeline;
import ca.team2706.fvts.main.Main;

public class MainThread extends Thread {

	public VisionParams visionParams;
	public ParamsSelector selector;

	public MainThread(VisionParams params) {
		this.visionParams = params;
		String interfaceN = visionParams.getByName("core/interface").getValue();
		outputInterface = AbstractInterface.getByName(interfaceN);
		if (outputInterface == null) {
			Log.e("No interface found for profile " + visionParams.getByName("name").getValue(), true);
			System.exit(1);
		}
		outputInterface.init(this);

		String pipelineN = visionParams.getByName("core/pipeline").getValue();
		pipeline = AbstractPipeline.getByName(pipelineN);
		if (pipeline == null) {
			Log.e("No pipeline found for profile " + visionParams.getByName("name").getValue(), true);
			System.exit(1);
		}
		pipeline.init(this);

		this.maths = new ArrayList<AbstractMathProcessor>();
		if (visionParams.getByName("core/maths") != null) {
			String mathNames = visionParams.getByName("core/maths").getValue();
			String[] maths = mathNames.split(",");
			for (String math : maths) {
				AbstractMathProcessor processor = AbstractMathProcessor.getByName(math);
				if (processor == null) {
					Log.e("No math processor found for profile " + visionParams.getByName("name").getValue()
							+ " by the name of " + math, true);
					System.exit(1);
				}
				processor.init(this);
				this.maths.add(processor);
			}
		}

		this.processors = new ArrayList<AbstractImagePreprocessor>();
		if (visionParams.getByName("core/preprocessors") != null) {
			String preProcessorNames = visionParams.getByName("core/preprocessors").getValue();
			String[] processors = preProcessorNames.split(",");
			for (String p : processors) {
				AbstractImagePreprocessor processor = AbstractImagePreprocessor.getByName(p);
				if (processor == null) {
					Log.e("No image preprocessor found for profile " + visionParams.getByName("name").getValue()
							+ " by the name of " + p, true);
					System.exit(1);
				}
				processor.init(this);
				this.processors.add(processor);
			}
		}
	}

	public VisionParams getVisionParams() {
		return visionParams;
	}

	public void setOutputInterface(AbstractInterface outputInterface) {
		this.outputInterface = outputInterface;
	}

	public Mat frame;
	public double current_time_seconds;
	public boolean useCamera;
	public static int timestamp = 0;
	public double lastDist = 0;
	private AbstractInterface outputInterface;
	private AbstractPipeline pipeline;
	private List<AbstractMathProcessor> maths;
	private List<AbstractImagePreprocessor> processors;

	@Override
	public void run() {
		// Setup the camera server for this camera
		try {
			VisionCameraServer.initCamera(this.visionParams.getByName("core/type").getValue(),
					this.visionParams.getByName("core/identifier").getValue());
		} catch (Exception e2) {
			Log.e(e2.getMessage(), true);
			e2.printStackTrace();
		}

		// Initializes a Matrix to hold the frame

		frame = new Mat();

		AbstractInputDevice input = AbstractInputDevice.getByName(visionParams.getByName("core/type").getValue());
		useCamera = !input.isStaticFrame();
		try {
			VisionCameraServer.initCamera(visionParams.getByName("core/type").getValue(),
					visionParams.getByName("core/identifier").getValue());
			VisionCameraServer.update();
		} catch (Exception e) {
			Log.e(e.getMessage(), true);
		}
		// The window to display the raw image
		DisplayGui guiRawImg = null;
		// The window to display the processed image
		DisplayGui guiProcessedImg = null;
		// Wether to open the guis
		boolean use_GUI = Main.developmentMode;

		frame = VisionCameraServer.getFrame(visionParams.getByName("core/type").getValue(),
				visionParams.getByName("core/identifier").getValue());

		// Set up the GUI display windows
		if (use_GUI) {
			// Initializes the window to display the raw image
			guiRawImg = new DisplayGui(1, 1, "Raw-" + visionParams.getByName("name").getValue(), true);
			// Initializes the window to display the processed image
			guiProcessedImg = new DisplayGui(1, 1, "Processed-" + visionParams.getByName("name").getValue(), true);
			// Initializes the parameters selector window
			try {
				selector = new ParamsSelector(visionParams);
			} catch (Exception e) {
				Log.e(e.getMessage(), true);
				e.printStackTrace();
			}
		}

		File csvFile = new File(visionParams.getByName("core/csvLog").getValue().replaceAll("\\$1", "" + Main.runID));

		long lastTime = System.currentTimeMillis();
		boolean first = true;

		Log.i("Initialized profile " + visionParams.getByName("name").getValue(), true);

		// Main video processing loop
		while (true) {
			try {

				if (!visionParams.getByName("enabled").getValueB() && use_GUI) {

					guiRawImg.b = false;
					guiProcessedImg.b = false;

					guiRawImg.dispose();
					guiProcessedImg.dispose();

					break;

				} else if (!visionParams.getByName("enabled").getValueB()) {
					break;
				}

				// Read the frame
				frame = VisionCameraServer.getFrame(visionParams.getByName("core/type").getValue(),
						visionParams.getByName("core/identifier").getValue());
				if (useCamera) {
					for (AbstractImagePreprocessor processor : processors) {
						Mat newFrame = processor.process(frame, this);
						frame.release();
						frame = newFrame;
					}
				}

				// Process the frame!
				// Log when the pipeline starts
				long pipelineStart = System.nanoTime();
				// Process the frame
				VisionData visionData = pipeline.process(frame, visionParams);
				// Log when the pipeline stops
				long pipelineEnd = System.nanoTime();
				for (AbstractMathProcessor processor : maths) {
					processor.process(visionData, this);
				}
				// Creates the raw output image object
				Mat rawOutputImg;
				if (use_GUI) {
					// If use gui then draw the prefered target
					// Sets the raw image to the frame
					rawOutputImg = frame.clone();

					// Draws the preffered target
					pipeline.drawPreferredTarget(rawOutputImg, visionData);
				} else {
					// Sets the raw image to the frame
					rawOutputImg = frame.clone();
				}

				if (visionData.preferredTarget != null)
					lastDist = visionData.preferredTarget.distance;

				outputInterface.publishData(visionData, this);

				// display the processed frame in the GUI
				if (use_GUI) {
					try {
						// May throw a NullPointerException if initializing
						// the window failed
						BufferedImage raw = Utils.matToBufferedImage(rawOutputImg);
						if (visionData.preferredTarget != null) {
							double dist = visionData.preferredTarget.distance;
							Graphics g = raw.createGraphics();
							g.setColor(Color.GREEN);
							g.drawString("dist: " + dist, 50, 50);
							g.dispose();
						}
						guiRawImg.updateImage(raw);
						guiProcessedImg.updateImage(Utils.matToBufferedImage(visionData.binMask.clone()));
					} catch (IOException e) {
						// means mat2BufferedImage broke
						// non-fatal error, let the program continue
						Log.e(e.getMessage(), true);
						continue;
					} catch (NullPointerException e) {
						Log.e(e.getMessage(), true);
						Log.i("Window closed", true);
						Runtime.getRuntime().halt(0);
					} catch (Exception e) {
						// just in case
						Log.e(e.getMessage(), true);
						continue;
					}
				}
				if (useCamera) {
					// log images to file once every seconds_between_img_dumps
					double elapsedTime = ((double) System.currentTimeMillis() / 1000) - current_time_seconds;
					// If the elapsed time is more that the seconds between image
					// dumps

					// then dump images asynchronously
					if (elapsedTime >= visionParams.getByName("core/imgDumpTime").getValueD()
							&& visionParams.getByName("core/imgDumpTime").getValueD() != -1) {
						// Sets the current number of seconds
						current_time_seconds = (((double) System.currentTimeMillis()) / 1000);
						try {
							Mat draw = frame.clone();
							pipeline.drawPreferredTarget(draw, visionData);
							Bundle b = new Bundle(Utils.matToBufferedImage(frame.clone()),
									Utils.matToBufferedImage(visionData.binMask), Utils.matToBufferedImage(draw),
									timestamp, visionParams);
							ImageDumpScheduler.schedule(b);
							timestamp++;
						} catch (IOException e) {
							Log.e(e.getMessage(), true);
							return;
						}
					}
				}
				if (csvFile.getParentFile().exists()) {
					List<String> data = new ArrayList<String>();
					if (first) {
						data.add("Elapsed Time");
						data.add("FPS");
						data.add("Number of Targets");
						data.add("Preffered Target X");
						data.add("Preffered Target Y");
						data.add("Preffered Target Area");
						data.add("Preffered Target Distance");
						try {
							Log.logData(csvFile, data);
						} catch (Exception e) {
							Log.e("Error while logging vision data to csv file!", true);
							Log.e(e.getMessage(), true);
						}
						data.clear();
						first = false;
					}
					data.add("" + (System.currentTimeMillis() - lastTime));
					data.add("" + visionData.fps);
					data.add("" + visionData.targetsFound.size());
					if (visionData.preferredTarget != null) {
						data.add(visionData.preferredTarget.xCentreNorm + "");
						data.add(visionData.preferredTarget.yCentreNorm + "");
						data.add(visionData.preferredTarget.areaNorm + "");
						data.add(visionData.preferredTarget.distance + "");
					}
					try {
						Log.logData(csvFile, data);
					} catch (Exception e) {
						Log.e("Error while logging vision data to csv file!", true);
						Log.e(e.getMessage(), true);
					}
				}

				// Display the frame rate onto the console
				double pipelineTime = (((double) (pipelineEnd - pipelineStart))
						/ BlobDetectPipeline.NANOSECONDS_PER_SECOND) * 1000;
				Log.i("Vision FPS: " + visionData.fps + ", pipeline took: " + pipelineTime + " ms\n", false);

				lastTime = System.currentTimeMillis();
			} catch (Exception e) {
				Log.e(e.getMessage(), true);
				e.printStackTrace();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					Log.e(e1.getMessage(), true);
					e.printStackTrace();
				}
			}
		}

	}

	public void updateParams(VisionParams params) {
		this.visionParams = params;
	}

}
