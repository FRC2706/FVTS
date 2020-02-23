package ca.team2706.fvts.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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
		String interfaceN = visionParams.getByName("interface").getValue();
		outputInterface = AbstractInterface.getByName(interfaceN);
		if(outputInterface == null) {
			Log.e("No interface found for profile "+visionParams.getByName("name").getValue(),true);
			System.exit(1);
		}
		outputInterface.init(this);
		
		String pipelineN = visionParams.getByName("pipeline").getValue();
		pipeline = AbstractPipeline.getByName(pipelineN);
		if(pipeline == null) {
			Log.e("No pipeline found for profile "+visionParams.getByName("name").getValue(), true);
			System.exit(1);
		}
		pipeline.init(this);
		
		String mathNames = visionParams.getByName("maths").getValue();
		String[] maths = mathNames.split(",");
		this.maths = new ArrayList<AbstractMathProcessor>();
		for(String math : maths) {
			AbstractMathProcessor processor = AbstractMathProcessor.getByName(math);
			if(processor == null) {
				Log.e("No math processor found for profile "+visionParams.getByName("name").getValue()+" by the name of "+math, true);
				System.exit(1);
			}
			processor.init(this);
			this.maths.add(processor);
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
	public boolean useCamera = true;
	public static int timestamp = 0;
	public double lastDist = 0;
	private AbstractInterface outputInterface;
	private AbstractPipeline pipeline;
	private List<AbstractMathProcessor> maths;
	
	@Override
	public void run() {
		// Setup the camera server for this camera
		try {
			VisionCameraServer.initCamera(this.visionParams.getByName("type").getValue(),
					this.visionParams.getByName("identifier").getValue());
		} catch (Exception e2) {
			Log.e(e2.getMessage(), true);
			e2.printStackTrace();
		}
		
		// Initializes a Matrix to hold the frame

		frame = new Mat();

		// Whether to use a camera, or load an image file from disk.
		if (visionParams.getByName("type").getValue().equals("usb") && visionParams.getByName("identifier").getValueI() == -1) {
			useCamera = false;
		}

		if (useCamera) {
			try {
				VisionCameraServer.initCamera(visionParams.getByName("type").getValue(),visionParams.getByName("identifier").getValue());
				VisionCameraServer.update();
			} catch (Exception e) {
				Log.e(e.getMessage(), true);
			}

		} else {
			// load the image from file.
			try {
				frame = Utils.bufferedImageToMat(ImageIO.read(new File(visionParams.getByName("imageFile").getValue())));
			} catch (IOException e) {
				Log.e(e.getMessage(), true);
				frame = new Mat();
			}
		}
		// The window to display the raw image
		DisplayGui guiRawImg = null;
		// The window to display the processed image
		DisplayGui guiProcessedImg = null;
		// Wether to open the guis
		boolean use_GUI = Main.developmentMode;

		if (useCamera) {

			frame = VisionCameraServer.getFrame(visionParams.getByName("type").getValue(),visionParams.getByName("identifier").getValue());

		} else {
			try {
				frame = Utils.bufferedImageToMat(ImageIO.read(new File(visionParams.getByName("imageFile").getValue())));
			} catch (IOException e) {
				Log.e(e.getMessage(), true);
				System.exit(1);
			}
		}

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
		
		File csvFile = new File(visionParams.getByName("csvLog").getValue().replaceAll("\\$1", ""+Main.runID));
		
		long lastTime = System.currentTimeMillis();
		boolean first = true;
		
		Log.i("Initialized profile "+visionParams.getByName("name").getValue(), true);
		
		// Main video processing loop
		while (true) {
			try {
				
				if(!visionParams.getByName("enabled").getValueB() && use_GUI) {
					
					guiRawImg.b = false;
					guiProcessedImg.b = false;
					
					guiRawImg.dispose();
					guiProcessedImg.dispose();
					
					break;
					
				}else if(!visionParams.getByName("enabled").getValueB()) {
					break;
				}
				
				if (useCamera) {
					// Read the frame from the camera, if it fails try again
					frame = VisionCameraServer.getFrame(visionParams.getByName("type").getValue(),visionParams.getByName("identifier").getValue());
				} // else use the image from disk that we loaded above
				// Resize the frame
				Size sz = new Size(visionParams.getByName("width").getValueI(),visionParams.getByName("height").getValueI());
				Imgproc.resize(frame, frame, sz);
				// Process the frame!
				// Log when the pipeline starts
				long pipelineStart = System.nanoTime();
				// Process the frame
				VisionData visionData = pipeline.process(frame, visionParams);
				// Log when the pipeline stops
				long pipelineEnd = System.nanoTime();
				for(AbstractMathProcessor processor : maths) {
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
						if(visionData.preferredTarget != null) {
							double dist = visionData.preferredTarget.distance;
							Graphics g = raw.createGraphics();
							g.setColor(Color.GREEN);
							g.drawString("dist: "+dist, 50, 50);
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
						Log.i("Window closed",true);
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
					if (elapsedTime >= visionParams.getByName("imgDumpTime").getValueD() && visionParams.getByName("imgDumpTime").getValueD() != -1) {
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
				if(csvFile.getParentFile().exists()) {
					List<String> data = new ArrayList<String>();
					if(first) {
						data.add("Elapsed Time");
						data.add("FPS");
						data.add("Number of Targets");
						data.add("Preffered Target X");
						data.add("Preffered Target Y");
						data.add("Preffered Target Area");
						data.add("Preffered Target Distance");
						try {
							Log.logData(csvFile, data);
						}catch(Exception e) {
							Log.e("Error while logging vision data to csv file!", true);
							Log.e(e.getMessage(), true);
						}
						data.clear();
						first = false;
					}
					data.add(""+(System.currentTimeMillis()-lastTime));
					data.add(""+visionData.fps);
					data.add(""+visionData.targetsFound.size());
					if(visionData.preferredTarget != null) {
						data.add(visionData.preferredTarget.xCentreNorm+"");
						data.add(visionData.preferredTarget.yCentreNorm+"");
						data.add(visionData.preferredTarget.areaNorm+"");
						data.add(visionData.preferredTarget.distance+"");
					}
					try {
						Log.logData(csvFile, data);
					}catch(Exception e) {
						Log.e("Error while logging vision data to csv file!", true);
						Log.e(e.getMessage(), true);
					}
				}
				
				// Display the frame rate onto the console
				double pipelineTime = (((double) (pipelineEnd - pipelineStart)) / BlobDetectPipeline.NANOSECONDS_PER_SECOND)
						* 1000;
				Log.i("Vision FPS: "+visionData.fps+", pipeline took: "+pipelineTime+" ms\n",false);
				
				lastTime = System.currentTimeMillis();
			} catch (Exception e) {
				Log.e(e.getMessage(), true);e.printStackTrace();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					Log.e(e1.getMessage(), true);e.printStackTrace();
				}
			}
		}

	}
	public void updateParams(VisionParams params) {
		this.visionParams = params;
	}

}
