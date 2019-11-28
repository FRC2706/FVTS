package ca.team2706.vision.core;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ca.team2706.vision.core.params.Attribute;
import ca.team2706.vision.core.params.AttributeOptions;
import ca.team2706.vision.core.params.VisionParams;
import ca.team2706.vision.vision2019.Main;

public class ParamsSelector extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private VisionParams visionParams;
	/**
	 * The content panel
	 */
	private JPanel contentPane;
	public ParamsSelector() throws Exception {
		List<Attribute> attribs = new ArrayList<Attribute>();
		for (AttributeOptions o : Main.options) {
			Attribute a = new Attribute(o.getName(), "");
			attribs.add(a);
		}

		this.visionParams = new VisionParams(attribs, Main.options);

		new MainThread(visionParams);
	}
	
	private JButton btnUpdate,btnSave;

	/**
	 * Creates a new Parameters Selector
	 * 
	 * @throws Exception
	 */
	public ParamsSelector(VisionParams params) throws Exception {
		this.visionParams = params;

		init();
	}

	private void init() {
		// Makes the program exit when the X button on the window is pressed
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Sets the size of the window
		setBounds(100, 100, 600, 300);
		addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent componentEvent) {
		    	setupContent();
		    }
		});
		setupContent();
		// Makes the window visible
		setVisible(true);
	}
	private void setupContent() {

		// Initilizes the content panel
		contentPane = new JPanel();
		// Sets the window border
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Sets the layout to a abstract layout
		contentPane.setLayout(null);
		contentPane.setBounds(0, 0, getWidth(), getHeight()*10);
		
		int x = 100, y = 100;
		
		for (Attribute a : visionParams.getAttribs()) {
			JTextField field = new JTextField();
			field.setText(a.getValue());
			field.setToolTipText(a.getName());
			field.setBounds(x, y, 100, 40);
			contentPane.add(field);
			x += 120;
			if(x > getWidth()-150) {
				x = 100;
				y += 60;
			}
		}
		btnUpdate = new JButton("Apply");
		btnUpdate.setBounds(x, y, 100, 100);
		btnUpdate.addActionListener(this);
		contentPane.add(btnUpdate);
		x += 120;
		if(x > getWidth()-150) {
			x = 100;
			y += 120;
		}
		
		btnSave = new JButton("Save");
		btnSave.setBounds(x, y, 100, 100);
		btnSave.addActionListener(this);
		contentPane.add(btnSave);
		
		// Sets the content pane to the content pane
		setContentPane(contentPane);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnUpdate) {
			for(Component c : contentPane.getComponents()) {
				if(c instanceof JTextField) {
					// It is a text field
					String name = ((JTextField) c).getToolTipText();
					String value = ((JTextField) c).getText();
					this.visionParams.putAttrib(new Attribute(name, value));
				}
			}
		}else if(e.getSource() == btnSave) {
			try {
				Main.saveVisionParams(visionParams);
			} catch (Exception e1) {
				Log.e(e1.getMessage(), true);
				e1.printStackTrace();
			}
		}
	}
}
