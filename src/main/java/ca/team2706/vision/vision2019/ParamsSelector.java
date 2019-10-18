package ca.team2706.vision.vision2019;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ca.team2706.vision.vision2019.params.Attribute;
import ca.team2706.vision.vision2019.params.AttributeOptions;
import ca.team2706.vision.vision2019.params.VisionParams;

public class ParamsSelector extends JFrame implements Runnable, ActionListener {

	private static final long serialVersionUID = 1L;

	private VisionParams visionParams;
	/**
	 * The content panel
	 */
	private JPanel contentPane;

	private Map<String,JTextField> fields = new HashMap<String,JTextField>();

	/**
	 * Creates a new Parameters Selector
	 * 
	 * @throws Exception
	 */
	public ParamsSelector() throws Exception {
		List<Attribute> attribs = new ArrayList<Attribute>();
		for (AttributeOptions o : Main.options) {
			Attribute a = new Attribute(o.getName(), "");
			attribs.add(a);
		}

		visionParams = new VisionParams(attribs, Main.options);

		new MainThread(visionParams);

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

		for (Attribute a : attribs) {
			JTextField field = new JTextField();
			field.setText(a.getValue());
			field.setToolTipText(a.getName());
			contentPane.add(field);
			fields.put(a.getName(),field);
		}
		// Sets the content pane to the content pane
		setContentPane(contentPane);

		// Makes the window visible
		setVisible(true);

		// Starts the update thread
		new Thread(this).start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}
	private Map<String,String> last = new HashMap<String,String>();

	@Override
	public void run() {
		while(true) {
			for(String s : fields.keySet()) {
				if(last.get(s) == null || !last.get(s).equals(fields.get(s).getText())){
					last.put(s, fields.get(s).getText());
					visionParams.getByName(s).setValue(fields.get(s).getText());
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
