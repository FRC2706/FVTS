package ca.team2706.vision.trackerboxreloaded;

import org.opencv.core.Size;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ParamsSelector extends JFrame implements Runnable, ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField textField;
	private JTextField txtCamera;
	private JButton btnSave;
	private JSlider sMinHue; //Minimum Hue
	private JSlider sMaxHue; //Maximum Hue
	private JSlider sMinSat; //Minimum Saturation
	private JSlider sMaxSat; //Maximum Saturation
	private JSlider sMinVal; //Minimum Value
	private JSlider sMaxVal; //Maximum Value
	private JSlider slider;
	private JTextField txtIterations;
	private JTextField textField_1; //Erode Dilate Iterations
	private JTextField txtMinimumArea;
	private JTextField minArea; //Minimum Area
	private JTextField txtHue;
	private JTextField txtSaturation;
	private JTextField txtValue;
	private JTextField txtOutputPath;
	private JTextField textField_3;
	private JTextField txtTimeBetweenCaptures;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField txtDtci;
	private JSlider slider_1;
	private JTextField txtDoubleCubeDetection;
	private JTextField txtResolutionWidth;
	private JTextField textField_2;
	private JTextField txtHeight;
	
	
	public ParamsSelector() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		sMinHue = new JSlider();
		sMinHue.setToolTipText("Minimum Hue");
		sMinHue.setPaintTicks(true);
		sMinHue.setValue(0);
		sMinHue.setOrientation(SwingConstants.VERTICAL);
		sMinHue.setMaximum(255);
		sMinHue.setBounds(10, 0, 31, 95);
		contentPane.add(sMinHue);
		
		sMaxHue = new JSlider();
		sMaxHue.setValue(0);
		sMaxHue.setToolTipText("Maximum Hue");
		sMaxHue.setPaintTicks(true);
		sMaxHue.setOrientation(SwingConstants.VERTICAL);
		sMaxHue.setMaximum(255);
		sMaxHue.setBounds(10, 106, 31, 95);
		contentPane.add(sMaxHue);
		
		sMinSat = new JSlider();
		sMinSat.setValue(0);
		sMinSat.setToolTipText("Minimum Saturation");
		sMinSat.setPaintTicks(true);
		sMinSat.setOrientation(SwingConstants.VERTICAL);
		sMinSat.setMaximum(255);
		sMinSat.setBounds(42, 0, 31, 95);
		contentPane.add(sMinSat);
		
		sMaxSat = new JSlider();
		sMaxSat.setValue(0);
		sMaxSat.setToolTipText("Maximum Saturation");
		sMaxSat.setPaintTicks(true);
		sMaxSat.setOrientation(SwingConstants.VERTICAL);
		sMaxSat.setMaximum(255);
		sMaxSat.setBounds(42, 106, 31, 95);
		contentPane.add(sMaxSat);
		
		sMinVal = new JSlider();
		sMinVal.setValue(0);
		sMinVal.setToolTipText("Minimum Value");
		sMinVal.setPaintTicks(true);
		sMinVal.setOrientation(SwingConstants.VERTICAL);
		sMinVal.setMaximum(255);
		sMinVal.setBounds(74, 0, 31, 95);
		contentPane.add(sMinVal);
		
		sMaxVal = new JSlider();
		sMaxVal.setValue(0);
		sMaxVal.setToolTipText("Maximum Value");
		sMaxVal.setPaintTicks(true);
		sMaxVal.setOrientation(SwingConstants.VERTICAL);
		sMaxVal.setMaximum(255);
		sMaxVal.setBounds(74, 106, 31, 95);
		contentPane.add(sMaxVal);
		
		textField = new JTextField(String.valueOf(Main.visionParams.cameraSelect));
		textField.setBounds(115, 32, 86, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		txtCamera = new JTextField();
		txtCamera.setText("Camera #:");
		txtCamera.setBounds(115, 10, 86, 20);
		txtCamera.setEditable(false);
		contentPane.add(txtCamera);
		txtCamera.setColumns(10);
		
		btnSave = new JButton("Save");
		btnSave.setBounds(151, 178, 89, 23);
		btnSave.addActionListener(this);
		contentPane.add(btnSave);
		
		txtIterations = new JTextField();
		txtIterations.setText("Iterations:");
		txtIterations.setBounds(115, 102, 86, 20);
		txtIterations.setEditable(false);
		contentPane.add(txtIterations);
		txtIterations.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setText("0");
		textField_1.setBounds(115, 119, 86, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		txtMinimumArea = new JTextField();
		txtMinimumArea.setText("Minimum Area:");
		txtMinimumArea.setEditable(false);
		txtMinimumArea.setBounds(115, 55, 86, 20);
		contentPane.add(txtMinimumArea);
		txtMinimumArea.setColumns(10);
		
		minArea = new JTextField();
		minArea.setBounds(115, 75, 86, 20);
		contentPane.add(minArea);
		minArea.setColumns(10);
		
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File("visionParams.properties")));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		sMinHue.setValue(Integer.valueOf(properties.getProperty("minHue")));
		sMaxHue.setValue(Integer.valueOf(properties.getProperty("maxHue")));
		sMinSat.setValue(Integer.valueOf(properties.getProperty("minSaturation")));
		sMaxSat.setValue(Integer.valueOf(properties.getProperty("maxSaturation")));
		sMinVal.setValue(Integer.valueOf(properties.getProperty("minValue")));
		sMaxVal.setValue(Integer.valueOf(properties.getProperty("maxValue")));
		textField.setText(properties.getProperty("CameraSelect"));
		textField_1.setText(properties.getProperty("erodeDilateIterations"));
		minArea.setText(properties.getProperty("minArea"));
		
		txtHue = new JTextField();
		txtHue.setText("Hue: ");
		txtHue.setEditable(false);
		txtHue.setBounds(4, 212, 123, 20);
		contentPane.add(txtHue);
		txtHue.setColumns(10);
		
		txtSaturation = new JTextField();
		txtSaturation.setEditable(false);
		txtSaturation.setText("Saturation:");
		txtSaturation.setBounds(151, 212, 131, 20);
		contentPane.add(txtSaturation);
		txtSaturation.setColumns(10);
		
		txtValue = new JTextField();
		txtValue.setEditable(false);
		txtValue.setText("Value:");
		txtValue.setBounds(288, 212, 123, 20);
		contentPane.add(txtValue);
		txtValue.setColumns(10);
		
		txtOutputPath = new JTextField();
		txtOutputPath.setEditable(false);
		txtOutputPath.setText("Output Path:");
		txtOutputPath.setBounds(310, 24, 86, 20);
		contentPane.add(txtOutputPath);
		txtOutputPath.setColumns(10);
		
		textField_5 = new JTextField();
		textField_5.setBounds(310, 44, 86, 20);
		contentPane.add(textField_5);
		textField_5.setColumns(10);
		textField_5.setText(properties.getProperty("imgDumpPath"));
		
		txtTimeBetweenCaptures = new JTextField();
		txtTimeBetweenCaptures.setEditable(false);
		txtTimeBetweenCaptures.setText("Time Between Captures:");
		txtTimeBetweenCaptures.setBounds(431, 24, 131, 20);
		contentPane.add(txtTimeBetweenCaptures);
		txtTimeBetweenCaptures.setColumns(10);
		
		textField_4 = new JTextField();
		textField_4.setBounds(431, 44, 86, 20);
		contentPane.add(textField_4);
		textField_4.setColumns(10);
		textField_4.setText(properties.getProperty("imgDumpWait"));
		slider = new JSlider();
		slider.setValue((int) (Double.valueOf(properties.getProperty("distToCentreImportance"))*100));
		slider.setOrientation(SwingConstants.VERTICAL);
		slider.setToolTipText("Distance to center importance");
		slider.setBounds(211, 32, 31, 143);
		contentPane.add(slider);
		
		txtDtci = new JTextField();
		txtDtci.setText("Distance To Center Importance:");
		txtDtci.setBounds(4, 230, 278, 20);
		txtDtci.setEditable(false);
		contentPane.add(txtDtci);
		txtDtci.setColumns(10);
		
		slider_1 = new JSlider();
		slider_1.setValue(0);
		slider_1.setToolTipText("Double cube detection range");
		slider_1.setValue((int) (Double.valueOf(properties.getProperty("aspectRatioThresh"))*100));
		slider_1.setBounds(211, 0, 200, 26);
		contentPane.add(slider_1);
		
		txtDoubleCubeDetection = new JTextField();
		txtDoubleCubeDetection.setEditable(false);
		txtDoubleCubeDetection.setText("Double Cube Detection: "+slider_1.getValue()+"%");
		txtDoubleCubeDetection.setBounds(245, 179, 179, 20);
		contentPane.add(txtDoubleCubeDetection);
		txtDoubleCubeDetection.setColumns(10);
		
		txtResolutionWidth = new JTextField();
		txtResolutionWidth.setToolTipText("The width the program resizes the frame to");
		txtResolutionWidth.setEditable(false);
		txtResolutionWidth.setText("Width");
		txtResolutionWidth.setBounds(252, 37, 86, 20);
		contentPane.add(txtResolutionWidth);
		txtResolutionWidth.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.setBounds(252, 55, 86, 20);
		contentPane.add(textField_2);
		textField_2.setColumns(10);
		textField_2.setText(String.valueOf(Main.visionParams.width));
		txtHeight = new JTextField();
		txtHeight.setToolTipText("Sets the height for the frame in the program");
		txtHeight.setEditable(false);
		txtHeight.setText("Height");
		txtHeight.setBounds(252, 92, 86, 20);
		contentPane.add(txtHeight);
		txtHeight.setColumns(10);
		
		textField_3 = new JTextField();
		textField_3.setBounds(252, 119, 86, 20);
		contentPane.add(textField_3);
		textField_3.setColumns(10);
		textField_3.setText(String.valueOf(Main.visionParams.height));
		setVisible(true);
		
		new Thread(this).start();
	}

	@Override
	public void run() {
		while(true){
			try{
				if(isInt(textField.getText())){
					Main.visionParams.cameraSelect = Integer.valueOf(Integer.valueOf(textField.getText()));
				}
				if(isInt(textField_1.getText())){
					Main.visionParams.erodeDilateIterations = Integer.valueOf(textField_1.getText());
				}
				if(isDouble(minArea.getText())){
					Main.visionParams.minArea = Double.valueOf(minArea.getText());
				}
				if(!textField_5.getText().equals("")){
					Main.outputPath = textField_5.getText();
				}
				if(isInt(textField_4.getText())) {
					Main.seconds_between_img_dumps = Integer.valueOf(textField_4.getText());
				}
				if(isInt(textField_2.getText())){
					Main.visionParams.width = Integer.valueOf(textField_2.getText());
					Main.visionParams.sz = new Size(Main.visionParams.width,Main.visionParams.height);
				}
				if(isInt(textField_3.getText())){
					Main.visionParams.height = Integer.valueOf(textField_3.getText());
					Main.visionParams.sz = new Size(Main.visionParams.width,Main.visionParams.height);
				}
				Main.visionParams.minHue = sMinHue.getValue();
				Main.visionParams.maxHue = sMaxHue.getValue();
				Main.visionParams.minSaturation = sMinSat.getValue();
				Main.visionParams.maxSaturation = sMaxSat.getValue();
				Main.visionParams.minValue = sMinVal.getValue();
				Main.visionParams.maxValue = sMaxVal.getValue();
				Main.visionParams.distToCentreImportance = ((double) slider.getValue())/100;
				Main.visionParams.aspectRatioThresh = ((double) slider_1.getValue())/100;
				txtHue.setText("Hue: "+Main.visionParams.minHue+"-"+Main.visionParams.maxHue);
				txtSaturation.setText("Saturation: "+Main.visionParams.minSaturation+"-"+Main.visionParams.maxSaturation);
				txtValue.setText("Value: "+Main.visionParams.minValue+"-"+Main.visionParams.maxValue);
				txtDtci.setText("Distance To Center Importance: "+(Main.visionParams.distToCentreImportance*100)+"%");
				Thread.sleep(5);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == btnSave){
			Main.saveVisionParams();
		}
	}

	private boolean isInt(String s){
		try{
			Integer.valueOf(s);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}

	private boolean isDouble(String s){
		try{
			Double.valueOf(s);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
}
