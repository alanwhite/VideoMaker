package org.whiteware.videomaker;

import java.awt.image.BufferedImage;

public class VideoContent extends FrameMaker {


	public VideoContent(int fps) {
		super(fps);
	}

	@Override
	public BufferedImage getFrame(long elapsedTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFrameCount() {
		// we want a 1 second startup then 4 seconds to match the captured audio
		return 5 * super.getFps();
	}

}
