package org.whiteware.videomaker;

import java.awt.image.BufferedImage;

public abstract class FrameMaker {

	private int fps;
	private BufferedImage priorImage;
	
	public FrameMaker(int fps) {
		this.setFps(fps);
	}

	public abstract BufferedImage getFrame(long elapsedTime);
	public abstract BufferedImage getFrame(int frameNumber);
	public abstract int getFrameCount();

	/**
	 * @return the fps
	 */
	public int getFps() {
		return fps;
	}

	/**
	 * @param fps the fps to set
	 */
	public void setFps(int fps) {
		this.fps = fps;
	}

	/**
	 * @return the priorImage
	 */
	public BufferedImage getPriorImage() {
		return priorImage;
	}

	/**
	 * @param priorImage the priorImage to set
	 */
	public void setPriorImage(BufferedImage priorImage) {
		this.priorImage = priorImage;
	}
}
