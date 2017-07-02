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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.RenderingHints;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
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
		
		JPanel mainPanel = new JPanel();
		add(mainPanel);
		mainPanel.setBorder(null);
		mainPanel.setOpaque(false);
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		progressTextField = new JFormattedTextField();
		mainPanel.add(progressTextField, BorderLayout.CENTER);
		progressTextField.setFocusable(false);
		progressTextField.setRequestFocusEnabled(false);
		progressTextField.setBorder(null);
		progressTextField.setOpaque(false);
		progressTextField.setBackground(new Color(0, 0, 0, 0));
		progressTextField.setHorizontalAlignment(SwingConstants.CENTER);
		progressTextField.setColumns(120);
		progressTextField.setEditable(false);
		
		JPanel panel = new JPanel();
		mainPanel.add(panel, BorderLayout.SOUTH);
		panel.setFocusable(false);
		panel.setRequestFocusEnabled(false);
		panel.setBorder(null);
		panel.setOpaque(false);
		
		progressBar = new JProgressBar();
		panel.add(progressBar);
		progressBar.setFocusable(false);
		progressBar.setRequestFocusEnabled(false);
		progressBar.setBorderPainted(false);
		progressBar.setOpaque(false);
		progressBar.setBackground(new Color(0, 0, 0, 0));
		
		Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
		add(rigidArea);
		
		JPanel buttonPanel = new JPanel();
		add(buttonPanel);
		buttonPanel.setFocusable(false);
		buttonPanel.setRequestFocusEnabled(false);
		buttonPanel.setBorder(null);
		buttonPanel.setOpaque(false);
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		
		progressCancelButton = new JButton("Cancel");
		buttonPanel.add(progressCancelButton);
		progressCancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		progressCancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 40));
		add(rigidArea_1);
		rigidArea_1.setFocusable(false);
		rigidArea_1.setFocusTraversalKeysEnabled(false);

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

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		float[] fractions = { 0.1f, 0.3f };
		Color[] colors = { new Color(255, 255, 255, 235), 
				new Color(Color.LIGHT_GRAY.getRed(),Color.LIGHT_GRAY.getGreen(),Color.LIGHT_GRAY.getBlue(), 235) };
		LinearGradientPaint lgp = new LinearGradientPaint(
				(float)getX(), (float)getY(),
				(float)getBounds().getMaxX(), (float)getBounds().getMaxY(),
				fractions, colors, MultipleGradientPaint.CycleMethod.REFLECT );
		
		g2D.setPaint(lgp);
		g2D.fillRect(0, 0, getWidth(), getHeight());
	}
}
