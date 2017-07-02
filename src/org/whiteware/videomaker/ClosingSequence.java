package org.whiteware.videomaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ClosingSequence extends FrameMaker {

	public ClosingSequence(int fps) {
		super(fps);
	}

	public BufferedImage getImage() {
		// very simply just rendering the same screen for now until time permits the fade
		
		BufferedImage image = super.getPriorImage();
		Font font = new Font("Monospaced", Font.BOLD, 16);
		
		Graphics2D g2D = (Graphics2D) image.getGraphics();
		g2D.setColor(Color.GRAY);
		// g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		TextLayout textual = new TextLayout("Closing sequence goes here", font, g2D.getFontRenderContext());
		Rectangle2D textBounds = textual.getBounds();
		
		// position text in the middle of the available width
		float x = ((float)image.getWidth()/2.0f) - (float)textBounds.getX() - ((float)textBounds.getWidth()/2.0f);
		
		// position baseline text 1/2 of the way down the available height
		float y = ((float)image.getHeight()/2.0f);
		
		textual.draw(g2D, x, y);
		g2D.dispose();
		
		return image;
	}

	@Override
	public int getFrameCount() {
		// 3 second fade
		return 3 * super.getFps();
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
