package org.whiteware.videomaker;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.SwingWorker;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
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
		
		FrameMaker currentFM = null;
		TitleSequence titleSequence = new TitleSequence(fps);
		VideoContent videoContent = new VideoContent(fps);
		ClosingSequence closingSequence = new ClosingSequence(fps);
		BufferedImage currentFrame = new BufferedImage(videoWidth, videoHeight, BufferedImage.TYPE_4BYTE_ABGR);
		int totalFrames = titleSequence.getFrameCount() + videoContent.getFrameCount() + closingSequence.getFrameCount();
		long totalMovieMillis = 1000 * (totalFrames/fps);
		
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
		
		/*
		 * Thinking is we loop on however many frames per second setting the current frameMaker as
		 * time moves on
		 * 
		 * currentFM.setPriorFrame(image)
		 * currentFM.getFrame(time) etc.....
		 * convert frame to needed format
		 * send to recorder
		 */
		
		// set up all the needed JavaCV stuff on this thread
		
		// ensure we ignore audio if audio file is null
		
		// calculate how many frames we will be writing
		// title sequence
		// frames for the duration of the audio
		// credits sequence
		
		for ( long i = 0; i < totalSleep && !isCancelled(); i += iterationSleep) {
			setProgress((int) ( (100*i)/totalSleep));
			try {
				Thread.sleep(iterationSleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
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
