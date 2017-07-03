package org.whiteware.videomaker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.SwingWorker;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;


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

		/*
		 * We separate out the video frame content producers to separate classes for clarity
		 */
		final TitleSequence titleSequence = new TitleSequence(fps, title);
		final VideoContent videoContent = new VideoContent(fps);
		final ClosingSequence closingSequence = new ClosingSequence(fps);
		
		/*
		 * We ask each of those how many frames they will write and calculate the total
		 */
		final int titleFrames = titleSequence.getFrameCount();
		final int videoFrames = videoContent.getFrameCount();
		final int closingFrames = closingSequence.getFrameCount();
		final int totalFrames = titleFrames + videoFrames + closingFrames;
		
		/*
		 * Track how many frames we've produced
		 */
		int framesProcessed = 0;
		
		/*
		 * We will switch which of the video frame content producers we need as we go
		 */
		FrameMaker currentFM = null;
		
		/*
		 * Provide an initial buffer for the video frame, producers could return different ones
		 */
		BufferedImage currentFrame = new BufferedImage(videoWidth, videoHeight, BufferedImage.TYPE_4BYTE_ABGR);

		/*
		 * We read the audio file in as needed
		 */
		SourceDataLine line = null;
		AudioInputStream audioInputStream = null;
		
		/*
		 * Calculate audio buffer size aligned to video frame rate
		 */
		final int sampleSizeBytes = 4; // hardcoded as the 2 channels and 16 bit audio when we recorded it
		final int samplesPerSec = 44100; // also as per when we recorded it
		final int sampleBytesPerFrame = (samplesPerSec * sampleSizeBytes) / fps;
		byte[] audioSamples = new byte[sampleBytesPerFrame];
		short[] silentSamples = new short[sampleBytesPerFrame/2];
		ShortBuffer silentBuffer = ShortBuffer.wrap(silentSamples);
		
		/*
		 * Set up the mp4 capture capability
		 */
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
			recorder.close();
			return "ERROR: Unable to start video frame writer "+e.getMessage();
		}
		
		currentFM = titleSequence;
		
		/*
		 * Set up access to the audio track to merge to the content phase only
		 */
		if ( audioFile != null ) {

			try {
				audioInputStream = AudioSystem.getAudioInputStream(audioFile);
				AudioFormat	audioFormat = audioInputStream.getFormat();
				DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(audioFormat);
				line.start();
			} catch (Exception e) {
				e.printStackTrace();
				return "ERROR: Unable to start audio feed"+e.getMessage();
			} 
			
		}
		
		while ( framesProcessed++ < totalFrames ) {
			/*
			 * Send the progress to the EDT
			 */
			setProgress((int) ( (100*framesProcessed)/totalFrames));
			
			/*
			 * Obtain and write the video frame to the mp4
			 */
			currentFM.setPriorImage(currentFrame);
			currentFrame = currentFM.getFrame(framesProcessed);
			recorder.record(imageConverter.convert(currentFrame), avutil.AV_PIX_FMT_ABGR);
			
			/*
			 * Add audio if needed
			 */
			if ( audioFile != null ) {
				
				if ( currentFM == videoContent ) {
					int bytesRead = 0;
					try	{
						bytesRead = audioInputStream.read(audioSamples, 0, sampleBytesPerFrame);
					} catch (IOException e) {
						e.printStackTrace();
						return "ERROR: Unable to read audio feed"+e.getMessage();
					} 

					if ( bytesRead != sampleBytesPerFrame) {
						System.out.println("Frames Processed "+framesProcessed+" out of "+totalFrames+", switch out due at "+(videoFrames+titleFrames));
						System.out.println("ERROR: Incorrect data length read from audio feed "+bytesRead);
					}

					int samplesRead = bytesRead / 2;
					short[] samples = new short[samplesRead];
					ByteBuffer.wrap(audioSamples).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
					ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, samplesRead);

					recorder.recordSamples(44100, 2, sBuff);
				} else {
					// add silence
					recorder.recordSamples(44100, 2, silentBuffer);
				}
			}
			
			/*
			 * Work out if we need to switch video frame producer
			 */
			if ( framesProcessed >= (videoFrames+titleFrames) ) {
				currentFM = closingSequence;
			} else if ( framesProcessed >= titleFrames ) {
				currentFM = videoContent;
			}
			
		}
		
		/*
		 * Tidy up
		 */
		try {
			recorder.stop();
			recorder.close();
			if ( audioFile != null ) {
				line.stop();
				line.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "WARNING: exception closing lines "+e.getMessage();
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
