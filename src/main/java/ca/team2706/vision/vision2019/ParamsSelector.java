package ca.team2706.vision.vision2019;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.opencv.core.Size;

import ca.team2706.vision.vision2019.Main.VisionData;
import ca.team2706.vision.vision2019.Main.VisionParams;

public class ParamsSelector extends JFrame implements Runnable, ActionListener {

	private static final long serialVersionUID = 1L;

	private VisionParams visionParams;
	/**
	 * The content panel
	 */
	private JPanel contentPane;
	/**
	 * The camera selecting field
	 */
	private JTextField textField;
	/**
	 * The text field that says "Camera:"
	 */
	private JTextField txtCamera;
	/**
	 * Save button
	 */
	private JButton btnSave;
	/**
	 * Minimum hue slider
	 */
	JSlider sMinHue; // Minimum Hue
	/**
	 * Maximum hue slider
	 */
	JSlider sMaxHue; // Maximum Hue
	/**
	 * Minimum saturation slider
	 */
	JSlider sMinSat; // Minimum Saturation
	/**
	 * Maximum saturation slider
	 */
	JSlider sMaxSat; // Maximum Saturation
	/**
	 * Minimum value slider
	 */
	JSlider sMinVal; // Minimum Value
	/**
	 * Maximum value slider
	 */
	JSlider sMaxVal; // Maximum Value
	/**
	 * Distance to center importance slider
	 */
	private JSlider slider;
	/**
	 * The Text Field that says "Iterations:"
	 */
	private JTextField txtIterations;
	/**
	 * The Text Field that holds the erode dilate iterations
	 */
	private JTextField textField_1; // Erode Dilate Iterations
	/**
	 * The Text Field that says "Minimum Area:"
	 */
	private JTextField txtMinimumArea;
	/**
	 * The Text Field that holds the minimum area
	 */
	JTextField minArea; // Minimum Area
	/**
	 * The Text Field for displaying the hue value
	 */
	private JTextField txtHue;
	/**
	 * The Text Field for displaying the saturation value
	 */
	private JTextField txtSaturation;
	/**
	 * The Text Field for displaying the value value
	 */
	private JTextField txtValue;
	/**
	 * The Text Field that says "Image Output Path:"
	 */
	private JTextField txtOutputPath;
	/**
	 * The Text Field for holding the height to resize the image to
	 */
	private JTextField textField_3;
	/**
	 * The Text Field that says "Time Between Captures:"
	 */
	private JTextField txtTimeBetweenCaptures;
	/**
	 * The Text Field to hold the time between image dumps
	 */
	private JTextField textField_4;
	/**
	 * The Text Field For holding the path to dump images to
	 */
	private JTextField textField_5;
	/**
	 * The Text Field that says "Distance To Center Importance:"
	 */
	private JTextField txtDtci;
	/**
	 * The double cube detection percentage slider
	 */
	private JSlider slider_1;
	/**
	 * The Text Field that prints the double cube detection percentage
	 */
	private JTextField txtDoubleCubeDetection;
	/**
	 * The Text Field that says "Width:"
	 */
	private JTextField txtResolutionWidth;
	/**
	 * The Text Fiekd that holds the width to resize the image to
	 */
	private JTextField textField_2;
	/**
	 * The Text Field that says "Height:"
	 */
	private JTextField txtHeight;
	private JTextField textField_6;
	private JTextField textField_7;
	
	private MainThread thread = null;
	private JButton btnNewButton_1;

	private JTextField textField_8;

	private JButton btnDistance;
	
	private VisionData frame;

	/**
	 * Creates a new Parameters Selector
	 */
	public ParamsSelector() {

		visionParams = new VisionParams();
		
		thread = new MainThread(visionParams);

		// Makes the program exit when the X button on the window is pressed
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Sets the size of the window
		setBounds(100, 100, 600, 300);

		// Initilizes the content panel
		contentPane = new JPanel();
		// Sets the window border
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Sets the layout to a abstract layout
		contentPane.setLayout(null);
		// Sets the content pane to the content pane
		setContentPane(contentPane);

		// Initilizes the minimum hue slider
		sMinHue = new JSlider();
		// Sets the tooltip
		sMinHue.setToolTipText("Minimum Hue");
		// Paints the tick marks
		sMinHue.setPaintTicks(true);
		// Sets the value
		sMinHue.setValue(0);
		// Sets the orientation
		sMinHue.setOrientation(SwingConstants.VERTICAL);
		// Sets the maximum value
		sMinHue.setMaximum(255);
		// Sets the size
		sMinHue.setBounds(10, 0, 31, 95);
		// Adds it to the window
		contentPane.add(sMinHue);

		// Initilizes the maximum hue slider
		sMaxHue = new JSlider();
		// Sets the value
		sMaxHue.setValue(0);
		// Sets the tooltip
		sMaxHue.setToolTipText("Maximum Hue");
		// Paints the tick marks
		sMaxHue.setPaintTicks(true);
		// Sets the orientation
		sMaxHue.setOrientation(SwingConstants.VERTICAL);
		// Sets the maximum value
		sMaxHue.setMaximum(255);
		// Sets the size
		sMaxHue.setBounds(10, 106, 31, 95);
		// Adds it to the window
		contentPane.add(sMaxHue);

		// Initilizes the minimum saturation slider
		sMinSat = new JSlider();
		// Sets the value
		sMinSat.setValue(0);
		// Sets the tooltip
		sMinSat.setToolTipText("Minimum Saturation");
		// Paints the tick marks
		sMinSat.setPaintTicks(true);
		// Sets the orientation
		sMinSat.setOrientation(SwingConstants.VERTICAL);
		// Sets the maximum value
		sMinSat.setMaximum(255);
		// Sets the size
		sMinSat.setBounds(42, 0, 31, 95);
		// Add it to the window
		contentPane.add(sMinSat);

		// Initilizes the maximum saturation slider
		sMaxSat = new JSlider();
		// Sets the value
		sMaxSat.setValue(0);
		// Sets the tooltip
		sMaxSat.setToolTipText("Maximum Saturation");
		// Paints the tick marks
		sMaxSat.setPaintTicks(true);
		// Sets the orientation
		sMaxSat.setOrientation(SwingConstants.VERTICAL);
		// Sets the maximum value
		sMaxSat.setMaximum(255);
		// Sets the size
		sMaxSat.setBounds(42, 106, 31, 95);
		// Adds it to the window
		contentPane.add(sMaxSat);

		// Initilizes the minimum value slider
		sMinVal = new JSlider();
		// Sets the value
		sMinVal.setValue(0);
		// Sets the tooltip
		sMinVal.setToolTipText("Minimum Value");
		// Paints the tick marks
		sMinVal.setPaintTicks(true);
		// Sets the orientation
		sMinVal.setOrientation(SwingConstants.VERTICAL);
		// Sets the maximum value
		sMinVal.setMaximum(255);
		// Sets the size
		sMinVal.setBounds(74, 0, 31, 95);
		// Adds it to the window
		contentPane.add(sMinVal);

		// Initilizes the maximum value slider
		sMaxVal = new JSlider();
		// Sets the value
		sMaxVal.setValue(0);
		// Sets the tooltip
		sMaxVal.setToolTipText("Maximum Value");
		// Paints the tick marks
		sMaxVal.setPaintTicks(true);
		// Sets the orientation
		sMaxVal.setOrientation(SwingConstants.VERTICAL);
		// Sets the maximum value
		sMaxVal.setMaximum(255);
		// Sets the size
		sMaxVal.setBounds(74, 106, 31, 95);
		// Add it to the window
		contentPane.add(sMaxVal);

		// Init the camera select text field
		textField = new JTextField();
		// Set the size
		textField.setBounds(115, 32, 86, 20);
		contentPane.add(textField);
		// Set the number of columns
		textField.setColumns(10);

		// Init the camera text field
		txtCamera = new JTextField();
		// Set the text
		txtCamera.setText("Camera #:");
		// Sets the size
		txtCamera.setBounds(115, 10, 86, 20);
		// Makes it not editable
		txtCamera.setEditable(false);
		// Add it to the window
		contentPane.add(txtCamera);
		// Set the number of columns
		txtCamera.setColumns(10);

		// Init the save button
		btnSave = new JButton("Save");
		// Set the size
		btnSave.setBounds(151, 178, 89, 23);
		// Add this as a listener
		btnSave.addActionListener(this);
		// Add it to the window
		contentPane.add(btnSave);

		// Init the iterations text field
		txtIterations = new JTextField();
		// Set the text
		txtIterations.setText("Iterations:");
		// Set the size
		txtIterations.setBounds(115, 102, 86, 20);
		// Makes it not editable
		txtIterations.setEditable(false);
		// Adds it to the window
		contentPane.add(txtIterations);
		// Sets the number of columns
		txtIterations.setColumns(10);

		// Inits the erode dilate iterations text field
		textField_1 = new JTextField();
		// Sets the text
		textField_1.setText("0");
		// Sets the size
		textField_1.setBounds(115, 119, 86, 20);
		// Add it to the window
		contentPane.add(textField_1);
		// Set the number of columns
		textField_1.setColumns(10);

		// Inits the minimum area text field
		txtMinimumArea = new JTextField();
		// Sets the text
		txtMinimumArea.setText("Minimum Area:");
		// Make it not editable
		txtMinimumArea.setEditable(false);
		// Set the size
		txtMinimumArea.setBounds(115, 55, 86, 20);
		// Add it to the window
		contentPane.add(txtMinimumArea);
		// Set the number of columns
		txtMinimumArea.setColumns(10);

		// Inits the minimum area text field
		minArea = new JTextField();
		// Set the size
		minArea.setBounds(115, 75, 86, 20);
		// Add it to the window
		contentPane.add(minArea);
		// Set the number of columns
		minArea.setColumns(10);

		// Inits the hue text field
		txtHue = new JTextField();
		// Sets the text
		txtHue.setText("Hue: ");
		// Make it not editable
		txtHue.setEditable(false);
		// Sets the size
		txtHue.setBounds(4, 212, 123, 20);
		// Add it to the window
		contentPane.add(txtHue);
		// Set the number of columns
		txtHue.setColumns(10);

		// Inits the saturation text field
		txtSaturation = new JTextField();
		// Make it not editable
		txtSaturation.setEditable(false);
		// Sets the text
		txtSaturation.setText("Saturation:");
		// Sets the size
		txtSaturation.setBounds(151, 212, 131, 20);
		// Add it to the window
		contentPane.add(txtSaturation);
		// Set the number of columns
		txtSaturation.setColumns(10);

		// Inits the value text field
		txtValue = new JTextField();
		// Makes it not editable
		txtValue.setEditable(false);
		// Sets the text
		txtValue.setText("Value:");
		// Sets the size
		txtValue.setBounds(288, 212, 123, 20);
		// Add it to the window
		contentPane.add(txtValue);
		// Sets the number of columns
		txtValue.setColumns(10);

		// Inits the output path text field
		txtOutputPath = new JTextField();
		// Make it not editable
		txtOutputPath.setEditable(false);
		// Sets the text
		txtOutputPath.setText("Output Path:");
		// Set the size
		txtOutputPath.setBounds(348, 102, 86, 20);
		// Add it to the window
		contentPane.add(txtOutputPath);
		// Set the number of columns
		txtOutputPath.setColumns(10);

		// Inits the image dump path text field
		textField_5 = new JTextField();
		// Sets the size
		textField_5.setBounds(348, 130, 214, 20);
		// Add it to the window
		contentPane.add(textField_5);
		// Set the number of columns
		textField_5.setColumns(10);
		
		// Inits the time between captures text field
		txtTimeBetweenCaptures = new JTextField();
		// Makes it not editable
		txtTimeBetweenCaptures.setEditable(false);
		// Sets the text
		txtTimeBetweenCaptures.setText("Time Between Captures:");
		// Sets the size
		txtTimeBetweenCaptures.setBounds(431, 24, 131, 20);
		// Add it to the window
		contentPane.add(txtTimeBetweenCaptures);
		// Sets the number of columns
		txtTimeBetweenCaptures.setColumns(10);

		// Inits the image dump wait text field
		textField_4 = new JTextField();
		// Sets the size
		textField_4.setBounds(431, 44, 86, 20);
		// Adds it to the window
		contentPane.add(textField_4);
		// Sets the number of columns
		textField_4.setColumns(10);
		
		// Inits the distance to center importance slider
		slider = new JSlider();
		// Sets the orientation
		slider.setOrientation(SwingConstants.VERTICAL);
		// Sets the tooltip
		slider.setToolTipText("Distance to center importance");
		// Sets the size
		slider.setBounds(211, 32, 31, 143);
		// Adds it to the window
		contentPane.add(slider);

		// Inits the distance to center importance text field
		txtDtci = new JTextField();
		// Sets the text
		txtDtci.setText("Distance To Center Importance:");
		// Sets the size
		txtDtci.setBounds(4, 230, 278, 20);
		// Makes it not editable
		txtDtci.setEditable(false);
		// Adds it to the window
		contentPane.add(txtDtci);
		// Sets the number of columns
		txtDtci.setColumns(10);

		// Inits the double cube detection range slider
		slider_1 = new JSlider();
		// Sets the value
		slider_1.setValue(0);
		// Sets the tooltip
		slider_1.setToolTipText("Double cube detection range");
		// Sets the size
		slider_1.setBounds(211, 0, 200, 26);
		// Adds it to the window
		contentPane.add(slider_1);

		// Inits the double cube detection text field
		txtDoubleCubeDetection = new JTextField();
		// Makes it not editable
		txtDoubleCubeDetection.setEditable(false);
		// Sets the text
		txtDoubleCubeDetection.setText("Double Cube Detection: " + slider_1.getValue() + "%");
		// Sets the size
		txtDoubleCubeDetection.setBounds(245, 179, 179, 20);
		// Adds it to the window
		contentPane.add(txtDoubleCubeDetection);
		// Sets the number of columns
		txtDoubleCubeDetection.setColumns(10);

		// Inits the width text field
		txtResolutionWidth = new JTextField();
		// Sets the tooltip
		txtResolutionWidth.setToolTipText("The width the program resizes the frame to");
		// Makes it not editable
		txtResolutionWidth.setEditable(false);
		// Sets the text
		txtResolutionWidth.setText("Width:");
		// Sets the size
		txtResolutionWidth.setBounds(252, 37, 86, 20);
		// Adds it to the window
		contentPane.add(txtResolutionWidth);
		// Sets the number of columns
		txtResolutionWidth.setColumns(10);

		// Inits the width text field
		textField_2 = new JTextField();
		// Sets the size
		textField_2.setBounds(252, 55, 86, 20);
		// Adds it to the window
		contentPane.add(textField_2);
		// Sets the number of columns
		textField_2.setColumns(10);
		// Sets the text
		textField_2.setText("");

		// Inits the height text field
		txtHeight = new JTextField();
		// Sets the tooltip
		txtHeight.setToolTipText("Sets the height for the frame in the program");
		// Makes it not editable
		txtHeight.setEditable(false);
		// Sets the text
		txtHeight.setText("Height:");
		// Sets the size
		txtHeight.setBounds(252, 92, 86, 20);
		// Adds it to the window
		contentPane.add(txtHeight);
		// Sets the number of columns
		txtHeight.setColumns(10);

		// Inits the height text field
		textField_3 = new JTextField();
		// Sets the size
		textField_3.setBounds(252, 119, 86, 20);
		// Adds it to the window
		contentPane.add(textField_3);
		// Sets the number of columns
		textField_3.setColumns(10);
		// Sets the text
		textField_3.setText("");

		textField_6 = new JTextField("Config Name:");
		textField_6.setBounds(298, 244, 114, 18);
		textField_6.setEditable(false);
		contentPane.add(textField_6);
		textField_6.setColumns(10);

		textField_7 = new JTextField();
		textField_7.setBounds(415, 244, 163, 18);
		contentPane.add(textField_7);
		textField_7.setColumns(10);

		JButton btnNewButton = new JButton("Load");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				Main.loadVisionParams();

				for (VisionParams p : Main.visionParamsList) {
					if (p.name.equals(textField_7.getText())) {

						visionParams = p;

						textField.setText("" + visionParams.cameraSelect);
						textField_1.setText("" + visionParams.erodeDilateIterations);
						minArea.setText("" + visionParams.minArea);
						textField_2.setText("" + visionParams.width);
						textField_3.setText("" + visionParams.height);

						sMinHue.setValue(visionParams.minHue);
						sMaxHue.setValue(visionParams.maxHue);
						sMinSat.setValue(visionParams.minSaturation);
						sMaxSat.setValue(visionParams.maxSaturation);
						sMinVal.setValue(visionParams.minValue);
						sMaxVal.setValue(visionParams.maxValue);

						textField_4.setText(""+visionParams.secondsBetweenImageDumps);
						
						textField_5.setText(visionParams.outputPath);
						
						slider.setValue((int) (visionParams.distToCentreImportance * 100));

						slider_1.setValue((int) (visionParams.aspectRatioThresh * 100));

						break;

					}
				}

			}
		});
		btnNewButton.setBounds(480, 210, 98, 24);
		contentPane.add(btnNewButton);
		
		btnNewButton_1 = new JButton("Start");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				thread.visionParams = visionParams;
				thread.start();
			}
		});
		btnNewButton_1.setBounds(480, 162, 98, 24);
		contentPane.add(btnNewButton_1);
		
		textField_8 = new JTextField("Place cube in intake");
		textField_8.setBounds(348, 244, 114, 18);
		contentPane.add(textField_6);
		textField_8.setColumns(10);
		textField_8.setEditable(false);

		btnDistance = new JButton("Distance");
		btnDistance.setBounds(245, 151, 98, 24);
		contentPane.add(btnDistance);
		btnDistance.addActionListener(this);

		// Makes the window visible
		setVisible(true);

		// Starts the update thread
		new Thread(this).start();
	}

	/**
	 * The method used for updating the values
	 */
	@Override
	public void run() {
		while (true) {
			try {
				// If the camera number is a number
				if (isInt(textField.getText())) {
					// Update the camera number
					visionParams.cameraSelect = Integer.valueOf(Integer.valueOf(textField.getText()));
				}
				// If the erode dilate iterations is a number
				if (isInt(textField_1.getText())) {
					// Update the erode dilate iterations
					visionParams.erodeDilateIterations = Integer.valueOf(textField_1.getText());
				}
				// If the minimum area is a number
				if (isDouble(minArea.getText())) {
					// Update the minimum area
					visionParams.minArea = Double.valueOf(minArea.getText());
				}

				// If the seconds between image dumps is a number
				if (isInt(textField_4.getText())) {
					// Update the seconds between image dumps
					visionParams.secondsBetweenImageDumps = Integer.valueOf(textField_4.getText());
				}
				// If the width is a number
				if (isInt(textField_2.getText())) {
					// Update the width
					visionParams.width = Integer.valueOf(textField_2.getText());
					// Update the size
					visionParams.sz = new Size(visionParams.width, visionParams.height);
				}
				// If the height is a number
				if (isInt(textField_3.getText())) {
					// Update the height
					visionParams.height = Integer.valueOf(textField_3.getText());
					// Update the size
					visionParams.sz = new Size(visionParams.width, visionParams.height);
				}
				visionParams.outputPath = textField_5.getText();
				// Update the minimum hue
				visionParams.minHue = sMinHue.getValue();
				// Update the maximum hue
				visionParams.maxHue = sMaxHue.getValue();
				// Update the minimum saturation
				visionParams.minSaturation = sMinSat.getValue();
				// Update the maximum saturation
				visionParams.maxSaturation = sMaxSat.getValue();
				// Update the minimum value
				visionParams.minValue = sMinVal.getValue();
				// Update the maximum value
				visionParams.maxValue = sMaxVal.getValue();
				// Update the distance to center importance
				visionParams.distToCentreImportance = ((double) slider.getValue()) / 100;
				// Update the aspect ratio threshold
				visionParams.aspectRatioThresh = ((double) slider_1.getValue()) / 100;
				// Update the hue text
				txtHue.setText("Hue: " + visionParams.minHue + "-" + visionParams.maxHue);
				// Update the saturation text
				txtSaturation.setText("Saturation: " + visionParams.minSaturation + "-" + visionParams.maxSaturation);
				// Update the value text
				txtValue.setText("Value: " + visionParams.minValue + "-" + visionParams.maxValue);
				// Update the distance to center importane text
				txtDtci.setText("Distance To Center Importance: " + (visionParams.distToCentreImportance * 100) + "%");

				visionParams.name = textField_7.getText();
				
				setTitle("ParamsSelector-"+visionParams.name);
				
				thread.updateParams(visionParams);
				
				// Sleep for 1ms
				Thread.sleep(5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		// Must be included!
		// Loads OpenCV
		System.loadLibrary("opencv_java310");
		CameraServer.startServer();
		new ParamsSelector();
	}

	/**
	 * The method used for detecting button presses
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// If save button pressed
		if (arg0.getSource() == btnSave) {
			// Save vision parameters
			try {
				Main.saveVisionParams(visionParams);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (arg0.getSource() == btnDistance) {

			if (textField_8.getText().equals("Place object 4m away")) {

				visionParams.slope = 1;

				frame = thread.forceProcess();
				
				textField_8.setText("Place object 1m away");

			} else {
				
				visionParams.slope = 1;

				VisionData frame2 = thread.forceProcess();
				
				double height1 = frame.preferredTarget.boundingBox.height;
				double height2 = frame2.preferredTarget.boundingBox.height;
				
				/*Calculate the slope using 
				*y2-y1
				*-----
				*x2-x1
				*
				*and x1 is 400 because the distance is 400cm from the camera
				*and x2 is always 100 because it is 100cm from the camera
				*then just measure the height of the cube in both images
				*and calculate the slope
				*
				*also the y intercept is equal to the height of the cube when it is in the intake because math
				*
				*/
				
				double slope = (height2-height1)/(100-400);
				double yIntercept = height1-slope*400;
				
				visionParams.slope = slope;
				visionParams.yIntercept = yIntercept;
				
				
				textField_8.setText("Place object 4m away");
				
			}
		}
	}

	/**
	 * Checks if a string is a valid integer
	 * 
	 * @param s the string
	 * @return if the string is a integer
	 */
	public static boolean isInt(String s) {
		try {
			// Test if it is a int
			Integer.valueOf(s);
			// Success
			return true;
		} catch (NumberFormatException e) {
			// Fail
			return false;
		}
	}

	/**
	 * Checks if a string is a valid double
	 * 
	 * @param s the string
	 * @return if the string is a double
	 */
	public static boolean isDouble(String s) {
		try {
			// Test if it is a double
			Double.valueOf(s);
			// Success
			return true;
		} catch (NumberFormatException e) {
			// Fail
			return false;
		}
	}
}
