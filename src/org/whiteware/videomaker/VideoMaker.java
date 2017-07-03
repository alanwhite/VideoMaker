package org.whiteware.videomaker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

@SuppressWarnings("serial")
public class VideoMaker extends JFrame implements PropertyChangeListener {

	private static int FPS = 30;
	private static int BPM = 60;
	private String title;
	
	private ControlPanel controlPanel = new ControlPanel();
	private WaitView waitPanel = new WaitView();
	
	private AudioCapture audioCapture = new AudioCapture(FPS, BPM);
	private VideoWriter videoWriter = null;
	private File audioFile = null;
	
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public VideoMaker() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		controlPanel.getPanelCue().setPreferredSize(new Dimension(640,480));
		controlPanel.getPanelCue().add(audioCapture, BorderLayout.CENTER);

		controlPanel.getBtnRecordAudio().addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( "Record Audio".equals(controlPanel.getBtnRecordAudio().getText()) )
					audioCapture.start();
				else
					audioCapture.cancel();
			}
		});

		controlPanel.getBtnGenerate().addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				videoWriter = new VideoWriter(FPS, title);
				videoWriter.addPropertyChangeListener(VideoMaker.this);
				
				FileDialog fd = new FileDialog(VideoMaker.this,"Save Movie File As ...",FileDialog.SAVE);
				fd.setFilenameFilter(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						if ( name.endsWith(".mp4"))
							return true;
						return false;
					}
				});
				
				
				fd.setFile("GeneratedVideoFile.mp4");
				fd.setVisible(true);
				String fn = fd.getFile();
				if ( fn == null )
					return;
				
				String videoFile = fd.getDirectory()+fn;
				waitPanel.getProgressTextField().setText("Render movie to "+videoFile);
				waitPanel.getProgressBar().setValue(0);
				
				videoWriter.setVideoFile(new File(videoFile));
				videoWriter.setAudioFile(audioFile);
				
				videoWriter.execute();
			}
		});

		title = controlPanel.getTxtMovieTitle().getText();
		controlPanel.getTxtMovieTitle().addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				title = controlPanel.getTxtMovieTitle().getText();
			}
		});
		
		audioCapture.addPropertyChangeListener(this);

		setContentPane(controlPanel);
		setGlassPane(waitPanel);
		waitPanel.setVisible(false);
		
		pack();
		setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new VideoMaker();
			}
		});
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();

		if ( evt.getSource() == audioCapture ) {
			switch (propertyName) {
			case "capturingAudio": 
				boolean capturingAudio = (boolean) evt.getNewValue();
				controlPanel.getBtnGenerate().setEnabled(!capturingAudio);
				controlPanel.getBtnRecordAudio().setText(capturingAudio ? "Cancel" : "Record Audio");
				break;
			case "audioFile":
				audioFile = (File) evt.getNewValue();
				System.out.println(getTime()+" Recording to file: "+audioFile);
				break;
			default:
				System.out.println(getTime()+" Warning: Unhandled property "+propertyName+" ignored");

			}
			return;
		} 

		if ( evt.getSource() == videoWriter ) {
			switch (propertyName) {
			case "progress": 
				waitPanel.getProgressBar().setValue((Integer)evt.getNewValue());
				break;
			case "state":
				SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();
				if ( state == SwingWorker.StateValue.STARTED ) {
					System.out.println(getTime()+" Rendering started");
					waitPanel.setVisible(true);
				
				} else
					if ( state == SwingWorker.StateValue.DONE ) {
						// clear the screen blocker and declare done
						System.out.println(getTime()+" Rendering Finished");
						waitPanel.setVisible(false);
						
						// best check it was successful in generating the video
						String renderOutcome = "Unknown Render Outcome";
						try {
							renderOutcome = videoWriter.get();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						System.out.println(getTime()+" "+renderOutcome);
					}
				break;
			default:
				System.out.println(getTime()+" Warning: Unhandled property "+propertyName+" ignored");

			}
		}

	}
	
	private String getTime() {
		Date date = new Date();
		return dateFormat.format(date); 
	}

}
