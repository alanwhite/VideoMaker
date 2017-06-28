package org.whiteware.videomaker;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;
import javax.swing.JLayeredPane;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

public class WaitView extends JPanel {
	private JFormattedTextField progressTextField;
	private JProgressBar progressBar;
	private JButton progressCancelButton;

	/**
	 * Create the panel.
	 */
	public WaitView() {
		setOpaque(false);

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		JPanel transPanel = new JPanel();
		transPanel.setFocusable(false);
		transPanel.setRequestFocusEnabled(false);
		transPanel.setBorder(null);
		transPanel.setBackground(new Color(236, 236, 236, 230));
		add(transPanel);
		transPanel.setLayout(new BoxLayout(transPanel, BoxLayout.PAGE_AXIS));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setFocusable(false);
		buttonPanel.setRequestFocusEnabled(false);
		buttonPanel.setBorder(null);
		transPanel.add(buttonPanel);
		buttonPanel.setOpaque(false);
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		
		Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
		buttonPanel.add(rigidArea);
		
		progressCancelButton = new JButton("Cancel");
		buttonPanel.add(progressCancelButton);
		progressCancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(null);
		transPanel.add(mainPanel);
		mainPanel.setOpaque(false);
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		progressTextField = new JFormattedTextField();
		progressTextField.setFocusable(false);
		progressTextField.setRequestFocusEnabled(false);
		progressTextField.setBorder(null);
		progressTextField.setOpaque(false);
		progressTextField.setBackground(new Color(0, 0, 0, 0));
		mainPanel.add(progressTextField, BorderLayout.CENTER);
		progressTextField.setHorizontalAlignment(SwingConstants.CENTER);
		progressTextField.setColumns(120);
		progressTextField.setEditable(false);
		
		JPanel panel = new JPanel();
		panel.setFocusable(false);
		panel.setRequestFocusEnabled(false);
		panel.setBorder(null);
		panel.setOpaque(false);
		mainPanel.add(panel, BorderLayout.SOUTH);
		
		progressBar = new JProgressBar();
		progressBar.setFocusable(false);
		progressBar.setRequestFocusEnabled(false);
		progressBar.setBorderPainted(false);
		progressBar.setOpaque(false);
		progressBar.setBackground(new Color(0, 0, 0, 0));
		panel.add(progressBar);
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 40));
		rigidArea_1.setFocusable(false);
		rigidArea_1.setFocusTraversalKeysEnabled(false);
		transPanel.add(rigidArea_1);

	}

	protected JFormattedTextField getProgressTextField() {
		return progressTextField;
	}
	protected JProgressBar getProgressBar() {
		return progressBar;
	}
	protected JButton getProgressCancelButton() {
		return progressCancelButton;
	}
}
