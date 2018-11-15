package ca.team2706.vision.trackerboxreloaded;

import org.opencv.core.Size;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ParamsSelector extends JFrame implements Runnable, ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	
	private JButton btnOpenImage;
	private File f = null;
	private int index = 0;
	/**
	 * The content panel
	 */
	private JPanel contentPane;

	public JButton btnAutoCallibrate;
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
	JSlider sMinHue; //Minimum Hue
	/**
	 * Maximum hue slider
	 */
	JSlider sMaxHue; //Maximum Hue
	/**
	 * Minimum saturation slider
	 */
	JSlider sMinSat; //Minimum Saturation
	/**
	 * Maximum saturation slider
	 */
	JSlider sMaxSat; //Maximum Saturation
	/**
	 * Minimum value slider
	 */
	JSlider sMinVal; //Minimum Value
	/**
	 * Maximum value slider
	 */
	JSlider sMaxVal; //Maximum Value
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
	private JTextField textField_1; //Erode Dilate Iterations
	/**
	 * The Text Field that says "Minimum Area:"
	 */
	private JTextField txtMinimumArea;
	/**
	 * The Text Field that holds the minimum area
	 */
	JTextField minArea; //Minimum Area
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
	
	/**
	 * Creates a new Parameters Selector
	 */
	public ParamsSelector() {
		//Makes the program exit when the X button on the window is pressed
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Sets the size of the window
		setBounds(100, 100, 600, 300);
		
		//Initilizes the content panel
		contentPane = new JPanel();
		//Sets the window border
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//Sets the layout to a abstract layout
		contentPane.setLayout(null);
		//Sets the content pane to the content pane
		setContentPane(contentPane);
		
		//Initilizes the minimum hue slider
		sMinHue = new JSlider();
		//Sets the tooltip
		sMinHue.setToolTipText("Minimum Hue");
		//Paints the tick marks
		sMinHue.setPaintTicks(true);
		//Sets the value
		sMinHue.setValue(0);
		//Sets the orientation
		sMinHue.setOrientation(SwingConstants.VERTICAL);
		//Sets the maximum value
		sMinHue.setMaximum(255);
		//Sets the size
		sMinHue.setBounds(10, 0, 31, 95);
		//Adds it to the window
		contentPane.add(sMinHue);
		
		//Initilizes the maximum hue slider
		sMaxHue = new JSlider();
		//Sets the value
		sMaxHue.setValue(0);
		//Sets the tooltip
		sMaxHue.setToolTipText("Maximum Hue");
		//Paints the tick marks
		sMaxHue.setPaintTicks(true);
		//Sets the orientation
		sMaxHue.setOrientation(SwingConstants.VERTICAL);
		//Sets the maximum value
		sMaxHue.setMaximum(255);
		//Sets the size
		sMaxHue.setBounds(10, 106, 31, 95);
		//Adds it to the window
		contentPane.add(sMaxHue);
		
		//Initilizes the minimum saturation slider
		sMinSat = new JSlider();
		//Sets the value
		sMinSat.setValue(0);
		//Sets the tooltip
		sMinSat.setToolTipText("Minimum Saturation");
		//Paints the tick marks
		sMinSat.setPaintTicks(true);
		//Sets the orientation
		sMinSat.setOrientation(SwingConstants.VERTICAL);
		//Sets the maximum value
		sMinSat.setMaximum(255);
		//Sets the size
		sMinSat.setBounds(42, 0, 31, 95);
		//Add it to the window
		contentPane.add(sMinSat);
		
		//Initilizes the maximum saturation slider
		sMaxSat = new JSlider();
		//Sets the value
		sMaxSat.setValue(0);
		//Sets the tooltip
		sMaxSat.setToolTipText("Maximum Saturation");
		//Paints the tick marks
		sMaxSat.setPaintTicks(true);
		//Sets the orientation
		sMaxSat.setOrientation(SwingConstants.VERTICAL);
		//Sets the maximum value
		sMaxSat.setMaximum(255);
		//Sets the size
		sMaxSat.setBounds(42, 106, 31, 95);
		//Adds it to the window
		contentPane.add(sMaxSat);
		
		//Initilizes the minimum value slider
		sMinVal = new JSlider();
		//Sets the value
		sMinVal.setValue(0);
		//Sets the tooltip
		sMinVal.setToolTipText("Minimum Value");
		//Paints the tick marks
		sMinVal.setPaintTicks(true);
		//Sets the orientation
		sMinVal.setOrientation(SwingConstants.VERTICAL);
		//Sets the maximum value
		sMinVal.setMaximum(255);
		//Sets the size
		sMinVal.setBounds(74, 0, 31, 95);
		//Adds it to the window
		contentPane.add(sMinVal);
		
		//Initilizes the maximum value slider
		sMaxVal = new JSlider();
		//Sets the value
		sMaxVal.setValue(0);
		//Sets the tooltip
		sMaxVal.setToolTipText("Maximum Value");
		//Paints the tick marks
		sMaxVal.setPaintTicks(true);
		//Sets the orientation
		sMaxVal.setOrientation(SwingConstants.VERTICAL);
		//Sets the maximum value
		sMaxVal.setMaximum(255);
		//Sets the size
		sMaxVal.setBounds(74, 106, 31, 95);
		//Add it to the window
		contentPane.add(sMaxVal);
		
		//Init the camera select text field
		textField = new JTextField(String.valueOf(Main.visionParams.cameraSelect));
		//Set the size
		textField.setBounds(115, 32, 86, 20);
		//Add it to the window
		contentPane.add(textField);
		//Set the number of columns
		textField.setColumns(10);
		
		//Init the camera text field
		txtCamera = new JTextField();
		//Set the text
		txtCamera.setText("Camera #:");
		//Sets the size
		txtCamera.setBounds(115, 10, 86, 20);
		//Makes it not editable
		txtCamera.setEditable(false);
		//Add it to the window
		contentPane.add(txtCamera);
		//Set the number of columns
		txtCamera.setColumns(10);
		
		//Init the save button
		btnSave = new JButton("Save");
		//Set the size
		btnSave.setBounds(151, 178, 89, 23);
		//Add this as a listener 
		btnSave.addActionListener(this);
		//Add it to the window
		contentPane.add(btnSave);
		
		//Init the iterations text field
		txtIterations = new JTextField();
		//Set the text
		txtIterations.setText("Iterations:");
		//Set the size
		txtIterations.setBounds(115, 102, 86, 20);
		//Makes it not editable
		txtIterations.setEditable(false);
		//Adds it to the window
		contentPane.add(txtIterations);
		//Sets the number of columns
		txtIterations.setColumns(10);
		
		//Inits the erode dilate iterations text field
		textField_1 = new JTextField();
		//Sets the text
		textField_1.setText("0");
		//Sets the size
		textField_1.setBounds(115, 119, 86, 20);
		//Add it to the window
		contentPane.add(textField_1);
		//Set the number of columns
		textField_1.setColumns(10);
		
		//Inits the minimum area text field
		txtMinimumArea = new JTextField();
		//Sets the text
		txtMinimumArea.setText("Minimum Area:");
		//Make it not editable
		txtMinimumArea.setEditable(false);
		//Set the size
		txtMinimumArea.setBounds(115, 55, 86, 20);
		//Add it to the window
		contentPane.add(txtMinimumArea);
		//Set the number of columns
		txtMinimumArea.setColumns(10);
		
		//Inits the minimum area text field
		minArea = new JTextField();
		//Set the size
		minArea.setBounds(115, 75, 86, 20);
		//Add it to the window
		contentPane.add(minArea);
		//Set the number of columns
		minArea.setColumns(10);
		
		//Inits a Properties object;
		Properties properties = new Properties();
		try {
			//Loads the vision parameters
			properties.load(new FileInputStream(new File("visionParams.properties")));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		//Sets the minimum hue value
		sMinHue.setValue(Integer.valueOf(properties.getProperty("minHue")));
		//Sets the maximum hue value
		sMaxHue.setValue(Integer.valueOf(properties.getProperty("maxHue")));
		//Sets the minimum saturation value
		sMinSat.setValue(Integer.valueOf(properties.getProperty("minSaturation")));
		//Sets the maximum saturation value
		sMaxSat.setValue(Integer.valueOf(properties.getProperty("maxSaturation")));
		//Sets the minimum value value
		sMinVal.setValue(Integer.valueOf(properties.getProperty("minValue")));
		//Sets the maximum value value
		sMaxVal.setValue(Integer.valueOf(properties.getProperty("maxValue")));
		//Sets the camera select value
		textField.setText(properties.getProperty("CameraSelect"));
		//Sets the erode dilate iterations value
		textField_1.setText(properties.getProperty("erodeDilateIterations"));
		//Sets the minimum area value
		minArea.setText(properties.getProperty("minArea"));
		
		//Inits the hue text field
		txtHue = new JTextField();
		//Sets the text
		txtHue.setText("Hue: ");
		//Make it not editable
		txtHue.setEditable(false);
		//Sets the size
		txtHue.setBounds(4, 212, 123, 20);
		//Add it to the window
		contentPane.add(txtHue);
		//Set the number of columns
		txtHue.setColumns(10);
		
		//Inits the saturation text field
		txtSaturation = new JTextField();
		//Make it not editable
		txtSaturation.setEditable(false);
		//Sets the text
		txtSaturation.setText("Saturation:");
		//Sets the size
		txtSaturation.setBounds(151, 212, 131, 20);
		//Add it to the window
		contentPane.add(txtSaturation);
		//Set the number of columns
		txtSaturation.setColumns(10);
		
		//Inits the value text field
		txtValue = new JTextField();
		//Makes it not editable
		txtValue.setEditable(false);
		//Sets the text
		txtValue.setText("Value:");
		//Sets the size
		txtValue.setBounds(288, 212, 123, 20);
		//Add it to the window
		contentPane.add(txtValue);
		//Sets the number of columns
		txtValue.setColumns(10);
		
		//Inits the output path text field
		txtOutputPath = new JTextField();
		//Make it not editable
		txtOutputPath.setEditable(false);
		//Sets the text
		txtOutputPath.setText("Output Path:");
		//Set the size
		txtOutputPath.setBounds(348, 102, 86, 20);
		//Add it to the window
		contentPane.add(txtOutputPath);
		//Set the number of columns
		txtOutputPath.setColumns(10);
		
		//Inits the image dump path text field
		textField_5 = new JTextField();
		//Sets the size
		textField_5.setBounds(348, 130, 214, 20);
		//Add it to the window
		contentPane.add(textField_5);
		//Set the number of columns
		textField_5.setColumns(10);
		//Set the text
		textField_5.setText(properties.getProperty("imgDumpPath"));
		
		//Inits the time between captures text field
		txtTimeBetweenCaptures = new JTextField();
		//Makes it not editable
		txtTimeBetweenCaptures.setEditable(false);
		//Sets the text
		txtTimeBetweenCaptures.setText("Time Between Captures:");
		//Sets the size
		txtTimeBetweenCaptures.setBounds(431, 24, 131, 20);
		//Add it to the window
		contentPane.add(txtTimeBetweenCaptures);
		//Sets the number of columns
		txtTimeBetweenCaptures.setColumns(10);
		
		//Inits the image dump wait text field
		textField_4 = new JTextField();
		//Sets the size
		textField_4.setBounds(431, 44, 86, 20);
		//Adds it to the window
		contentPane.add(textField_4);
		//Sets the number of columns
		textField_4.setColumns(10);
		//Sets the text
		textField_4.setText(properties.getProperty("imgDumpWait"));
		
		//Inits the distance to center importance slider
		slider = new JSlider();
		//Sets the value
		slider.setValue((int) (Double.valueOf(properties.getProperty("distToCentreImportance"))*100));
		//Sets the orientation
		slider.setOrientation(SwingConstants.VERTICAL);
		//Sets the tooltip
		slider.setToolTipText("Distance to center importance");
		//Sets the size
		slider.setBounds(211, 32, 31, 143);
		//Adds it to the window
		contentPane.add(slider);
		
		//Inits the distance to center importance text field
		txtDtci = new JTextField();
		//Sets the text
		txtDtci.setText("Distance To Center Importance:");
		//Sets the size
		txtDtci.setBounds(4, 230, 278, 20);
		//Makes it not editable
		txtDtci.setEditable(false);
		//Adds it to the window
		contentPane.add(txtDtci);
		//Sets the number of columns
		txtDtci.setColumns(10);
		
		//Inits the double cube detection range slider
		slider_1 = new JSlider();
		//Sets the value
		slider_1.setValue(0);
		//Sets the tooltip
		slider_1.setToolTipText("Double cube detection range");
		//Sets the value
		slider_1.setValue((int) (Double.valueOf(properties.getProperty("aspectRatioThresh"))*100));
		//Sets the size
		slider_1.setBounds(211, 0, 200, 26);
		//Adds it to the window
		contentPane.add(slider_1);
		
		//Inits the double cube detection text field
		txtDoubleCubeDetection = new JTextField();
		//Makes it not editable
		txtDoubleCubeDetection.setEditable(false);
		//Sets the text
		txtDoubleCubeDetection.setText("Double Cube Detection: "+slider_1.getValue()+"%");
		//Sets the size
		txtDoubleCubeDetection.setBounds(245, 179, 179, 20);
		//Adds it to the window
		contentPane.add(txtDoubleCubeDetection);
		//Sets the number of columns
		txtDoubleCubeDetection.setColumns(10);
		
		//Inits the width text field
		txtResolutionWidth = new JTextField();
		//Sets the tooltip
		txtResolutionWidth.setToolTipText("The width the program resizes the frame to");
		//Makes it not editable
		txtResolutionWidth.setEditable(false);
		//Sets the text
		txtResolutionWidth.setText("Width:");
		//Sets the size
		txtResolutionWidth.setBounds(252, 37, 86, 20);
		//Adds it to the window
		contentPane.add(txtResolutionWidth);
		//Sets the number of columns
		txtResolutionWidth.setColumns(10);
		
		//Inits the width text field
		textField_2 = new JTextField();
		//Sets the size
		textField_2.setBounds(252, 55, 86, 20);
		//Adds it to the window
		contentPane.add(textField_2);
		//Sets the number of columns
		textField_2.setColumns(10);
		//Sets the text
		textField_2.setText(String.valueOf(Main.visionParams.width));
		
		//Inits the height text field
		txtHeight = new JTextField();
		//Sets the tooltip
		txtHeight.setToolTipText("Sets the height for the frame in the program");
		//Makes it not editable
		txtHeight.setEditable(false);
		//Sets the text
		txtHeight.setText("Height:");
		//Sets the size
		txtHeight.setBounds(252, 92, 86, 20);
		//Adds it to the window
		contentPane.add(txtHeight);
		//Sets the number of columns
		txtHeight.setColumns(10);
		
		//Inits the height text field
		textField_3 = new JTextField();
		//Sets the size
		textField_3.setBounds(252, 119, 86, 20);
		//Adds it to the window
		contentPane.add(textField_3);
		//Sets the number of columns
		textField_3.setColumns(10);
		//Sets the text
		textField_3.setText(String.valueOf(Main.visionParams.height));
		
		btnAutoCallibrate = new JButton("Auto Callibrate");
		btnAutoCallibrate.setToolTipText("Automaticcaly callibrates the vision paramaters, simply put the cube the maximum distance you want to be able to track away and allign it with the middle after pressing this button");
		btnAutoCallibrate.setBounds(345, 72, 131, 23);
		btnAutoCallibrate.addActionListener(this);
		contentPane.add(btnAutoCallibrate);
		
		btnOpenImage = new JButton("Open Image");
		btnOpenImage.setBounds(431, 178, 116, 23);
		btnOpenImage.addActionListener(this);
		contentPane.add(btnOpenImage);
		
		this.addKeyListener(this);
		
		//Makes the window visible
		setVisible(true);
		
		//Starts the update thread
		new Thread(this).start();
	}

	/**
	 * The method used for updating the values
	 */
	@Override
	public void run() {
		while(true){
			try{
				//If the camera number is a number
				if(isInt(textField.getText())){
					//Update the camera number
					Main.visionParams.cameraSelect = Integer.valueOf(Integer.valueOf(textField.getText()));
				}
				//If the erode dilate iterations is a number
				if(isInt(textField_1.getText())){
					//Update the erode dilate iterations
					Main.visionParams.erodeDilateIterations = Integer.valueOf(textField_1.getText());
				}
				//If the minimum area is a number
				if(isDouble(minArea.getText())){
					//Update the minimum area
					Main.visionParams.minArea = Double.valueOf(minArea.getText());
				}
				//If the output path is not blank
				if(!textField_5.getText().equals("")){
					//Update the output path
					Main.outputPath = textField_5.getText();
				}
				//If the seconds between image dumps is a number
				if(isInt(textField_4.getText())) {
					//Update the seconds between image dumps
					Main.seconds_between_img_dumps = Integer.valueOf(textField_4.getText());
				}
				//If the width is a number
				if(isInt(textField_2.getText())){
					//Update the width
					Main.visionParams.width = Integer.valueOf(textField_2.getText());
					//Update the size
					Main.visionParams.sz = new Size(Main.visionParams.width,Main.visionParams.height);
				}
				//If the height is a number
				if(isInt(textField_3.getText())){
					//Update the height
					Main.visionParams.height = Integer.valueOf(textField_3.getText());
					//Update the size
					Main.visionParams.sz = new Size(Main.visionParams.width,Main.visionParams.height);
				}
				//Update the minimum hue
				Main.visionParams.minHue = sMinHue.getValue();
				//Update the maximum hue
				Main.visionParams.maxHue = sMaxHue.getValue();
				//Update the minimum saturation
				Main.visionParams.minSaturation = sMinSat.getValue();
				//Update the maximum saturation
				Main.visionParams.maxSaturation = sMaxSat.getValue();
				//Update the minimum value
				Main.visionParams.minValue = sMinVal.getValue();
				//Update the maximum value
				Main.visionParams.maxValue = sMaxVal.getValue();
				//Update the distance to center importance
				Main.visionParams.distToCentreImportance = ((double) slider.getValue())/100;
				//Update the aspect ratio threshold
				Main.visionParams.aspectRatioThresh = ((double) slider_1.getValue())/100;
				//Update the hue text
				txtHue.setText("Hue: "+Main.visionParams.minHue+"-"+Main.visionParams.maxHue);
				//Update the saturation text
				txtSaturation.setText("Saturation: "+Main.visionParams.minSaturation+"-"+Main.visionParams.maxSaturation);
				//Update the value text
				txtValue.setText("Value: "+Main.visionParams.minValue+"-"+Main.visionParams.maxValue);
				//Update the distance to center importane text
				txtDtci.setText("Distance To Center Importance: "+(Main.visionParams.distToCentreImportance*100)+"%");
				
				//Sleep for 1ms
				Thread.sleep(5);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * The method used for detecting button presses
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//If save button pressed
		if(arg0.getSource() == btnSave){
			//Save vision parameters
			Main.saveVisionParams();
		}
		if(arg0.getSource() == btnAutoCallibrate){
			new AutoCallibrator();
		}
		if(arg0.getSource() == btnOpenImage){
			JFileChooser chooser = new JFileChooser();
			if(f != null){
				chooser.setCurrentDirectory(f.getParentFile());
			}
			chooser.showOpenDialog(contentPane);
			if(chooser.getSelectedFile() != null){
				f = chooser.getSelectedFile();
				Main.useCamera = false;
				Main.visionParams.imageFile= f.getAbsolutePath();
				for(int i = 0; i < f.getParentFile().listFiles().length;i++) {
					String path = f.getParentFile().listFiles()[i].getAbsolutePath();
					if(path.equals(f.getAbsolutePath())) {
						index = i;
						break;
					}
				}
			}
		}
	}
	/**
	 * Checks if a string is a valid integer
	 * @param s the string
	 * @return if the string is a integer
	 */
	private boolean isInt(String s){
		try{
			//Test if it is a int
			Integer.valueOf(s);
			//Success
			return true;
		}catch(NumberFormatException e){
			//Fail
			return false;
		}
	}
	/**
	 * Checks if a string is a valid double
	 * @param s the string
	 * @return if the string is a double
	 */
	private boolean isDouble(String s){
		try{
			//Test if it is a double
			Double.valueOf(s);
			//Success
			return true;
		}catch(NumberFormatException e){
			//Fail
			return false;
		}
	}

	@Override
	public void keyPressed(KeyEvent key) {
		System.out.println(key.getKeyCode());
		if(key.getKeyCode() == KeyEvent.VK_LEFT) {
			if(f != null && index != f.getParentFile().listFiles().length) {
				index++;
				f = f.getParentFile().listFiles()[index];
				Main.useCamera = false;
				Main.visionParams.imageFile= f.getAbsolutePath();
			}
		}else if(key.getKeyCode() == KeyEvent.VK_RIGHT) {
			if(f != null && index != 0) {
				index--;
				f = f.getParentFile().listFiles()[index];
				Main.useCamera = false;
				Main.visionParams.imageFile= f.getAbsolutePath();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
