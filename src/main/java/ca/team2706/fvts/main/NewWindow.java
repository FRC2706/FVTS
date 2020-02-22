package ca.team2706.fvts.main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ca.team2706.fvts.core.Log;
import ca.team2706.fvts.core.ParamsSelector;
import ca.team2706.fvts.core.VisionCameraServer;

public class NewWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnNewButton;

	public NewWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		btnNewButton = new JButton("New window");
		contentPane.add(btnNewButton, BorderLayout.NORTH);
		btnNewButton.addActionListener(this);

		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnNewButton) {

			try {
				new ParamsSelector(null);
			} catch (Exception e1) {
				Log.e(e1.getMessage(), true);
			}

		}

	}
	
	public static void main(String[] args) {
		System.loadLibrary("opencv_java310");
		VisionCameraServer.startServer();
		
		if(args.length > 0) {
			Main.serverIp = args[0];
		}
		
		new NewWindow();
	}

}
