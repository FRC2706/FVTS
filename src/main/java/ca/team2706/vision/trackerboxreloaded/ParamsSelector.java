package ca.team2706.vision.trackerboxreloaded;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ParamsSelector extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	/**
	 * Creates a new Parameters Selector
	 */
	public ParamsSelector() {
		//Makes the program exit when the X button on the window is pressed
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Sets the size of the window
		setBounds(100, 100, 150, 100);
		
		//Initilizes the content panel
		contentPane = new JPanel();
		//Sets the window border
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//Sets the layout to a abstract layout
		contentPane.setLayout(null);
		//Sets the content pane to the content pane
		setContentPane(contentPane);
		
		JButton btnNewButton = new JButton("Reload");
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Main.reloadConfig();
			}
			
		});
		btnNewButton.setBounds(22, 26, 98, 25);
		contentPane.add(btnNewButton);
		
		//Makes the window visible
		setVisible(true);
	}
}
