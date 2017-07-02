package org.whiteware.videomaker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.SwingWorker;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.FrameRecorder.Exception;

/*
 * Takes the given audio file and merges with generated video images into an mp4 file
 */
public class VideoWriter extends SwingWorker<String, String> {

	private int fps = 30;
	private String title;
	private int videoWidth = 1280;
	private int videoHeight = 720;
	File audioFile = null;
	File videoFile = null;
	
	public VideoWriter(int fps, String title) {
		this.fps = fps;
		this.title = title;
	}
	
	@Override
	protected String doInBackground() throws Exception {
		
		long totalSleep=5000;
		long iterationSleep=1000;

		final TitleSequence titleSequence = new TitleSequence(fps, title);
		final VideoContent videoContent = new VideoContent(fps);
		final ClosingSequence closingSequence = new ClosingSequence(fps);
		final int titleFrames = titleSequence.getFrameCount();
		final int videoFrames = videoContent.getFrameCount();
		final int closingFrames = closingSequence.getFrameCount();
		final int totalFrames = titleFrames + videoFrames + closingFrames;
		final long totalMovieMillis = 1000 * (totalFrames/fps);
		final long frameTimeMillis = 1000/fps;
		
		FrameMaker currentFM = null;
		BufferedImage currentFrame = new BufferedImage(videoWidth, videoHeight, BufferedImage.TYPE_4BYTE_ABGR);
		
		int framesProcessed = 0;
		long startTime = 0;
		long nextFrameTime = 0;
		
		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(videoFile, videoWidth, videoHeight, 2);
		Java2DFrameConverter imageConverter = new Java2DFrameConverter();
			
		recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
		recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
		recorder.setFormat("mp4");
		recorder.setFrameRate(fps);
		try {
			recorder.start();
		} catch (Exception e) {
			System.out.println("Error starting recorder");
			e.printStackTrace();
			return "ERROR: Unable to start video frame writer "+e.getMessage();
		}
		
		currentFM = titleSequence;
		startTime = System.currentTimeMillis();
		nextFrameTime = startTime + frameTimeMillis;
		
		while ( framesProcessed++ < totalFrames ) {
			// System.out.println("loop "+framesProcessed);
			setProgress((int) ( (100*framesProcessed)/totalFrames));
			
			currentFM.setPriorImage(currentFrame);
			currentFrame = currentFM.getFrame(framesProcessed);
			recorder.record(imageConverter.convert(currentFrame), avutil.AV_PIX_FMT_ABGR);
			
			if ( framesProcessed >= (videoFrames+titleFrames) ) {
				// System.out.println("Switching to closing sequence");
				currentFM = closingSequence;
			} else if ( framesProcessed >= titleFrames ) {
				// System.out.println("Switching to video content");
				currentFM = videoContent;
			}
			
		}
		
		/*
		 * Now is the strategy that we ask the frame makers to produce the next frame due, or the frame that's due at that real time moment?
		 * If we can find some way to pull the audio on a frame by frame basis then a high quality video can be produced, ie less risk of 
		 * varying the frame rate or skipping frames.
		 * 
		 * I think we can set the time that the recorder should associate with a video frame, ensuring the quality.
		 * 
		 * If we can't do that for the audio stream, we need to adopt a strategy where we are led by the audio timestamp.
		 * 
		 * Let's investigate how audio is captured by JavaCV.
		 * 
		 * 
		 */
		
		// ensure we ignore audio if audio file is null
		
		try {
			recorder.stop();
		} catch (Exception e) {
			System.out.println("error stopping mp4 recorder");
			e.printStackTrace();
		}
		
		if ( !isCancelled() )
			setProgress(100);
		
		return "SUCCESS";
	}

	/*
	 * Getters & Setters
	 */
	public File getVideoFile() {
		return videoFile;
	}

	public void setVideoFile(File videoFile) {
		this.videoFile = videoFile;
	}

	public File getAudioFile() {
		return audioFile;
	}

	public void setAudioFile(File audioFile) {
		this.audioFile = audioFile;
	}

}
