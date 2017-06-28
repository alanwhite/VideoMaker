package org.whiteware.videomaker;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;

public class ControlPanel extends JPanel {
	private JTextField txtMovieTitle;
	private JPanel panelCue;
	private JButton btnRecordAudio;
	private JButton btnGenerate;
	private JPanel panelButtons;

	/**
	 * Create the panel.
	 */
	public ControlPanel() {
		setLayout(new BorderLayout(0, 0));
		
		panelButtons = new JPanel();
		panelButtons.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(panelButtons, BorderLayout.NORTH);
		
		JLabel lblTitle = new JLabel("Title ");
		
		txtMovieTitle = new JTextField();
		lblTitle.setLabelFor(txtMovieTitle);
		txtMovieTitle.setText("My First Movie");
		txtMovieTitle.setColumns(10);
		
		btnRecordAudio = new JButton("Record Audio");
		
		btnGenerate = new JButton("Generate ...");
		panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));
		panelButtons.add(lblTitle);
		panelButtons.add(txtMovieTitle);
		panelButtons.add(btnRecordAudio);
		panelButtons.add(btnGenerate);
		
		panelCue = new JPanel();
		panelCue.setBackground(Color.BLACK);
		add(panelCue, BorderLayout.CENTER);
		panelCue.setLayout(new BorderLayout(0, 0));

	}

	protected JPanel getPanelCue() {
		return panelCue;
	}
	protected JTextField getTxtMovieTitle() {
		return txtMovieTitle;
	}
	protected JButton getBtnRecordAudio() {
		return btnRecordAudio;
	}
	protected JButton getBtnGenerate() {
		return btnGenerate;
	}
	public JPanel getPanelButtons() {
		return panelButtons;
	}
}
