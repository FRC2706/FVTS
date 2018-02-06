package ca.team2706.vision.trackerboxreloaded;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

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
	
	
	public ParamsSelector() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		sMinHue = new JSlider();
		sMinHue.setToolTipText("Minimum Hue");
		sMinHue.setPaintTicks(true);
		sMinHue.setValue(0);
		sMinHue.setOrientation(SwingConstants.VERTICAL);
		sMinHue.setMaximum(180);
		sMinHue.setBounds(10, 0, 31, 95);
		contentPane.add(sMinHue);
		
		sMaxHue = new JSlider();
		sMaxHue.setValue(0);
		sMaxHue.setToolTipText("Maximum Hue");
		sMaxHue.setPaintTicks(true);
		sMaxHue.setOrientation(SwingConstants.VERTICAL);
		sMaxHue.setMaximum(180);
		sMaxHue.setBounds(10, 106, 31, 95);
		contentPane.add(sMaxHue);
		
		sMinSat = new JSlider();
		sMinSat.setValue(0);
		sMinSat.setToolTipText("Minimum Saturation");
		sMinSat.setPaintTicks(true);
		sMinSat.setOrientation(SwingConstants.VERTICAL);
		sMinSat.setMaximum(255);
		sMinSat.setBounds(59, 0, 31, 95);
		contentPane.add(sMinSat);
		
		sMaxSat = new JSlider();
		sMaxSat.setValue(0);
		sMaxSat.setToolTipText("Maximum Saturation");
		sMaxSat.setPaintTicks(true);
		sMaxSat.setOrientation(SwingConstants.VERTICAL);
		sMaxSat.setMaximum(255);
		sMaxSat.setBounds(59, 106, 31, 95);
		contentPane.add(sMaxSat);
		
		sMinVal = new JSlider();
		sMinVal.setValue(0);
		sMinVal.setToolTipText("Minimum Value");
		sMinVal.setPaintTicks(true);
		sMinVal.setOrientation(SwingConstants.VERTICAL);
		sMinVal.setMaximum(255);
		sMinVal.setBounds(110, 0, 31, 95);
		contentPane.add(sMinVal);
		
		sMaxVal = new JSlider();
		sMaxVal.setValue(0);
		sMaxVal.setToolTipText("Maximum Value");
		sMaxVal.setPaintTicks(true);
		sMaxVal.setOrientation(SwingConstants.VERTICAL);
		sMaxVal.setMaximum(255);
		sMaxVal.setBounds(110, 106, 31, 95);
		contentPane.add(sMaxVal);
		
		textField = new JTextField(String.valueOf(Main.visionParams.CameraSelect));
		textField.setBounds(196, 49, 86, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		txtCamera = new JTextField();
		txtCamera.setText("Camera #:");
		txtCamera.setBounds(196, 24, 86, 20);
		txtCamera.setEditable(false);
		contentPane.add(txtCamera);
		txtCamera.setColumns(10);
		
		btnSave = new JButton("Save");
		btnSave.setBounds(196, 178, 89, 23);
		btnSave.addActionListener(this);
		contentPane.add(btnSave);
		
		txtIterations = new JTextField();
		txtIterations.setText("Iterations:");
		txtIterations.setBounds(288, 75, 86, 20);
		txtIterations.setEditable(false);
		contentPane.add(txtIterations);
		txtIterations.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setText("0");
		textField_1.setBounds(288, 102, 86, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		txtMinimumArea = new JTextField();
		txtMinimumArea.setText("Minimum Area:");
		txtMinimumArea.setEditable(false);
		txtMinimumArea.setBounds(151, 92, 86, 20);
		contentPane.add(txtMinimumArea);
		txtMinimumArea.setColumns(10);
		
		minArea = new JTextField();
		minArea.setBounds(151, 114, 86, 20);
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
		
		slider = new JSlider();
		slider.setValue((int) (Double.valueOf(properties.getProperty("distToCentreImportance"))*100));
		slider.setOrientation(SwingConstants.VERTICAL);
		slider.setToolTipText("Distance to center importance");
		slider.setBounds(400, 24, 24, 143);
		contentPane.add(slider);
		setVisible(true);
		
		new Thread(this).start();
	}

	@Override
	public void run() {
		while(true){
			try{
				if(isInt(textField.getText())){
					Main.visionParams.CameraSelect = Integer.valueOf(Integer.valueOf(textField.getText()));
				}
				if(isInt(textField_1.getText())){
					Main.visionParams.erodeDilateIterations = Integer.valueOf(textField_1.getText());
				}
				if(isDouble(minArea.getText())){
					Main.visionParams.minArea = Double.valueOf(minArea.getText());
				}
				Main.visionParams.minHue = sMinHue.getValue();
				Main.visionParams.maxHue = sMaxHue.getValue();
				Main.visionParams.minSaturation = sMinSat.getValue();
				Main.visionParams.maxSaturation = sMaxSat.getValue();
				Main.visionParams.minValue = sMinVal.getValue();
				Main.visionParams.maxValue = sMaxVal.getValue();
				Main.visionParams.distToCentreImportance = slider.getValue()/100;
				txtHue.setText("Hue: "+Main.visionParams.minHue+"-"+Main.visionParams.maxHue);
				txtSaturation.setText("Saturation: "+Main.visionParams.minSaturation+"-"+Main.visionParams.maxSaturation);
				txtValue.setText("Value: "+Main.visionParams.minValue+"-"+Main.visionParams.maxValue);
				
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
