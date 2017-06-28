package org.whiteware.videomaker;

import java.awt.image.BufferedImage;

public class TitleSequence extends FrameMaker {

	public TitleSequence(int fps) {
		super(fps);
	}

	@Override
	public BufferedImage getFrame(long elapsedTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFrameCount() {
		// We want a 5 second intro
		return 5 * super.getFps();
	}

}
