package org.whiteware.videomaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class VideoContent extends FrameMaker {


	public VideoContent(int fps) {
		super(fps);
	}

	public BufferedImage getImage() {
		// very simply just rendering the same screen for now until time permits to jazz up
		
		BufferedImage image = super.getPriorImage();
		// Font font = new Font("Monospaced", Font.BOLD, 16);
		
		Graphics2D g2D = (Graphics2D) image.getGraphics();
		g2D.setColor(Color.BLUE.darker());
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2D.fillRect(0, 0, image.getWidth(), image.getHeight());
		g2D.dispose();
		
		return image;
	}

	@Override
	public int getFrameCount() {
		// we want a 1 second startup then 4 seconds to match the captured audio
		return 5 * super.getFps();
	}

	@Override
	public BufferedImage getFrame(long elapsedTime) {
		return getImage();
	}
	
	@Override
	public BufferedImage getFrame(int frameNumber) {
		return getImage();
	}

}
