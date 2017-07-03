package org.whiteware.videomaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class TitleSequence extends FrameMaker {

	String title;
	private int second = 0;
	private int frame = 0;
	private float xSlide = 0.0f;
	private boolean titleWritten = false;
	private TextLayout textual;
	private Rectangle2D textBounds;
	private float x;
	private float y;
	
	public TitleSequence(int fps, String title) {
		super(fps);
		this.title = title;
	}

	private BufferedImage getImage() {
		// rendering the title string in the middle and a frame counter

		BufferedImage image = super.getPriorImage();
		Font font = new Font("Monospaced", Font.BOLD, 16);

		Graphics2D g2D = (Graphics2D) image.getGraphics();
		g2D.setColor(Color.GRAY);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

		if ( !titleWritten ) {
			titleWritten = true;
			textual = new TextLayout(title, font, g2D.getFontRenderContext());
			textBounds = textual.getBounds();

			// position text in the middle of the available width
			x = ((float)image.getWidth()/2.0f) - (float)textBounds.getX() - ((float)textBounds.getWidth()/2.0f);

			// position baseline text 1/2 of the way down the available height
			y = ((float)image.getHeight()/2.0f);

			textual.draw(g2D, x, y);
		}

		textual = new TextLayout(Integer.toString(++frame), font, g2D.getFontRenderContext());
		textBounds = textual.getBounds();
		x = 50.0f+xSlide;
		y = 50.0f+(float)(second*(textBounds.getHeight()+4.0f));
		textual.draw(g2D, x, y);

		xSlide += textBounds.getWidth()+5.0f;

		if ( frame >= this.getFps() ) {
			frame = 0;
			second++;
			xSlide = 0.0f;
		}

		g2D.dispose();

		return image;
	}

	@Override
	public int getFrameCount() {
		// We want a 5 second intro
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
