package org.whiteware.videomaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Implements the audio capture flow:
 * - display 4 beat countdown at the specified beats per minute
 * - displays 4 words to be spoken and thus recorded
 * 
 * @author alanwhite
 *
 */
public class AudioCapture extends JPanel implements ActionListener {

	// implementing bean mechanism to notify components of state change
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	private boolean capturingAudio = false;
	
	private long startTime = 0;
	private long nextBeatMillis = 0;
	private int currentBeat = 0;
	
	private long millisPerBeat = 0;
	private Timer timer = null;
	
	private BufferedImage geekText = null;
	private BufferedImage currentFrame = null;
	
	private String[] prompts = { "One", "Two", "Three", "Four" };
	
	private TargetDataLine targetDataLine = null;
	private AudioFileFormat.Type targetType;
	private AudioInputStream audioInputStream;
	private File recordingFile;
	private Thread recordingThread;
	private boolean cancelNextInvocation = false;
	
	/**
	 * Constructor
	 * @param fps The frames per second to animate for countdown and during recording
	 * @param bpm The beats per minute that the countdown and subsequent script are shown at
	 */
	public AudioCapture(int fps, int bpm) {
		setLayout(null);
		addComponentListener(resizeListener);
		millisPerBeat = 60000 / bpm;
		timer = new Timer(1000/fps, this);
		
		AudioFormat	audioFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				44100.0F, 16, 2, 4, 44100.0F, false);
		
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

		try {
			targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
			targetDataLine.open(audioFormat);
		} catch (LineUnavailableException e) {
			System.out.println("failed to get a recording line");
			e.printStackTrace();
			return;
		}
		
		targetType = AudioFileFormat.Type.WAVE;
		audioInputStream = new AudioInputStream(targetDataLine);
		

	}

	/**
	 * Resize the image buffers to match window resizing
	 */
	private ComponentListener resizeListener = new ComponentAdapter() {

		@Override
		public void componentResized(ComponentEvent e) {
			super.componentResized(e);
			
			// rebuild geekText
			geekText = e.getComponent().getGraphicsConfiguration().createCompatibleImage(e.getComponent().getWidth(), e.getComponent().getHeight());			
			Font font = new Font("Monospaced", Font.PLAIN, 14);
			
			Graphics2D g2D = (Graphics2D) geekText.getGraphics();
			g2D.setColor(Color.GREEN);
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			TextLayout textual = new TextLayout("Press Record Audio to get a 4 beat countdown and the words to speak!",
					font, g2D.getFontRenderContext());
			Rectangle2D textBounds = textual.getBounds();
			
			// position text in the middle of the available width
			float x = ((float)geekText.getWidth()/2.0f) - (float)textBounds.getX() - ((float)textBounds.getWidth()/2.0f);
			
			// position baseline text 1/3 of the way down the available height
			float y = ((float)geekText.getHeight()/3.0f);
			
			textual.draw(g2D, x, y);
			g2D.dispose();
			
			// reset currentFrame
			currentFrame = e.getComponent().getGraphicsConfiguration().createCompatibleImage(e.getComponent().getWidth(), e.getComponent().getHeight());
			g2D = (Graphics2D) currentFrame.getGraphics();
			g2D.setColor(Color.DARK_GRAY);
			g2D.fillRect(0, 0, currentFrame.getWidth(), currentFrame.getHeight());
			g2D.dispose();
			
		}
	};
	
	protected void start() {
		
		// use a new file each run
		try {
			setRecordingFile(File.createTempFile("AC-", "-audio"));
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		recordingThread = new Thread() {
			public void run() {
				try {
					AudioSystem.write(audioInputStream,
							targetType, recordingFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		cancelNextInvocation = false;
		currentBeat = 0;
		startTime = System.currentTimeMillis();
		nextBeatMillis = startTime + millisPerBeat;
		targetDataLine.start();
		timer.start();
		setCapturingAudio(true);
	}
	
	protected void cancel() {
		timer.stop();
		targetDataLine.stop();
		targetDataLine.drain();
		targetDataLine.close(); // causes recordingThread to encounter EOF and terminate
		setCapturingAudio(false);
	}
	
	/**
	 * Called each time a new video frame is to be generated
	 * 
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if ( cancelNextInvocation ) {
			cancel();
			repaint();
			return;
		}
		
		long now = System.currentTimeMillis();
		
		// determine if have we switched beats with this invocation
		if ( now > nextBeatMillis ) {
			nextBeatMillis += millisPerBeat;
			
			// increment count and check if we're done
			if ( ++currentBeat > 8 ) {
				// we need to allow a frames worth of audio to trail into the file
				// to balance out any potential latency encountered when we triggered
				// the recording to start
				cancelNextInvocation = true;
				repaint();
				return;
			}
		}
		
		// find out what percentage of the way through the beat we are
		long thisBeatStart = nextBeatMillis - millisPerBeat;
		long elapsedInBeat = now - thisBeatStart;
		float progress = (float)elapsedInBeat / (float)millisPerBeat;
		
		// System.out.println(thisBeatStart+", "+elapsedInBeat+", "+progress);
		
		if ( currentBeat == 0 ) {
			// warmup
			renderStartupImage(currentFrame, progress);
			
		} else if ( currentBeat < 5) {
			// countdown phase
			renderCountdownImage(currentFrame, currentBeat, progress);
		} else {
			// recording audio 
			if ( !recordingThread.isAlive() ) {
				targetDataLine.flush();
				recordingThread.start();
			}
			
			renderPromptImage(currentFrame, prompts[currentBeat-5], progress);
		}
		
		repaint();
		
	}
	
	private void renderStartupImage(BufferedImage image, float progress) {
		renderImage(image, "Startup", Color.WHITE, Color.DARK_GRAY, progress);
	}
	
	private void renderCountdownImage(BufferedImage image, int count, float progress) {
		renderImage(image, Integer.toString(count), Color.WHITE, Color.BLUE, progress);
	}
	
	private void renderPromptImage(BufferedImage image, String prompt, float progress) {
		renderImage(image, prompt, Color.BLACK, Color.LIGHT_GRAY, progress);
	}
 
	/**
	 * DRY helper for rendering content in different phases of the animation
	 * @param image
	 * @param text
	 * @param foreground
	 * @param background
	 * @param progress
	 */
	private void renderImage(BufferedImage image, String text, Color foreground, Color background, float progress ) {
		Graphics2D g2D = image.createGraphics();
		g2D.setColor(background);
		g2D.fillRect(0, 0, image.getWidth(), image.getHeight());
		g2D.setColor(foreground);
		g2D.drawString(text, 50, 50);
		g2D.dispose();
	}
	
	/**
	 * Short-circuit some of the Swing conventions for this pane by over-riding
	 * at the paint() level rather than paintComponent(). 
	 * 
	 * @param g
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics g2D = ( Graphics2D) g;
		
		if ( timer.isRunning() ) {
			g2D.drawImage(currentFrame, 0, 0, null);
		} else {
			g2D.drawImage(geekText, 0, 0, null);
		}
	}
	
	/*
	 * Manage property change listeners
	 */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /*
     * Getters & Setters
     */
	public boolean isCapturingAudio() {
		return capturingAudio;
	}

	public void setCapturingAudio(boolean capturingAudio) {
		this.capturingAudio = capturingAudio;
		changeSupport.firePropertyChange("capturingAudio", !this.capturingAudio, this.capturingAudio);
	}

	public File getRecordingFile() {
		return recordingFile;
	}

	public void setRecordingFile(File recordingFile) {
		this.recordingFile = recordingFile;
		changeSupport.firePropertyChange("audioFile", null, recordingFile);
	}

}
