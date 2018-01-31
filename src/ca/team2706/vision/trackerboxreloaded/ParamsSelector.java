package ca.team2706.vision.trackerboxreloaded;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class ParamsSelector extends JFrame implements Runnable, ActionListener {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField textField;
	private JTextField txtCamera;
	private JButton btnSave;
	private JSlider slider; //Minimum Hue
	private JSlider slider_1; //Maximum Hue
	private JSlider slider_2; //Minimum Saturation
	private JSlider slider_3; //Maximum Saturation
	private JSlider slider_4; //Minimum Value
	private JSlider slider_5; //Maximum Value
	private JTextField txtIterations;
	private JTextField textField_1;
	public ParamsSelector() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		slider = new JSlider();
		slider.setToolTipText("Minimum Hue");
		slider.setPaintTicks(true);
		slider.setValue(0);
		slider.setOrientation(SwingConstants.VERTICAL);
		slider.setMaximum(256);
		slider.setBounds(10, 0, 31, 95);
		contentPane.add(slider);
		
		slider_1 = new JSlider();
		slider_1.setValue(0);
		slider_1.setToolTipText("Maximum Hue");
		slider_1.setPaintTicks(true);
		slider_1.setOrientation(SwingConstants.VERTICAL);
		slider_1.setMaximum(256);
		slider_1.setBounds(10, 106, 31, 95);
		contentPane.add(slider_1);
		
		slider_2 = new JSlider();
		slider_2.setValue(0);
		slider_2.setToolTipText("Minimum Saturation");
		slider_2.setPaintTicks(true);
		slider_2.setOrientation(SwingConstants.VERTICAL);
		slider_2.setMaximum(256);
		slider_2.setBounds(59, 0, 31, 95);
		contentPane.add(slider_2);
		
		slider_3 = new JSlider();
		slider_3.setValue(0);
		slider_3.setToolTipText("Maximum Saturation");
		slider_3.setPaintTicks(true);
		slider_3.setOrientation(SwingConstants.VERTICAL);
		slider_3.setMaximum(256);
		slider_3.setBounds(59, 106, 31, 95);
		contentPane.add(slider_3);
		
		slider_4 = new JSlider();
		slider_4.setValue(0);
		slider_4.setToolTipText("Minimum Value");
		slider_4.setPaintTicks(true);
		slider_4.setOrientation(SwingConstants.VERTICAL);
		slider_4.setMaximum(256);
		slider_4.setBounds(110, 0, 31, 95);
		contentPane.add(slider_4);
		
		slider_5 = new JSlider();
		slider_5.setValue(0);
		slider_5.setToolTipText("Maximum Value");
		slider_5.setPaintTicks(true);
		slider_5.setOrientation(SwingConstants.VERTICAL);
		slider_5.setMaximum(256);
		slider_5.setBounds(110, 106, 31, 95);
		contentPane.add(slider_5);
		
		textField = new JTextField(String.valueOf(Main.visionParams.CameraSelect));
		textField.setBounds(196, 44, 86, 20);
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
		
		setVisible(true);
		
		new Thread(this).start();
	}

	@Override
	public void run() {
		while(true){
			if(textField.getText() != ""){
				Main.visionParams.CameraSelect = Integer.valueOf(Integer.valueOf(textField.getText()));
			}
			if(textField_1.getText() != ""){
				Main.visionParams.erodeDilateIterations = slider.getValue();
			}
			Main.visionParams.minHue = slider.getValue();
			Main.visionParams.maxHue = slider_1.getValue();
			Main.visionParams.minSaturation = slider_2.getValue();
			Main.visionParams.maxSaturation = slider_3.getValue();
			Main.visionParams.minValue = slider_4.getValue();
			Main.visionParams.maxValue = slider_5.getValue();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == btnSave){
			Main.save();
		}
	}
}
