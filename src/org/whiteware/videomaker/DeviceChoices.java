package org.whiteware.videomaker;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

/**
 * 
 * Example for obtaining list of audio devices
 * There is no platform independent way to obtain a list of video capture devices (as of JavaCV 3.2)
 * 
 */
public class DeviceChoices {

	public DeviceChoices() {
		
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		
		for (Mixer.Info info: mixerInfos){
			Mixer m = AudioSystem.getMixer(info);
			Line.Info[] lineInfos = m.getTargetLineInfo();
			
			/*
			 * The TargetDataLine class represents a microphone
			 */
			if( lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class) ){
				
				System.out.println("Line Name: " + info.getName());
				System.out.println("Line Description: " + info.getDescription());
				
				for (Line.Info lineInfo:lineInfos){
					System.out.println (" "+lineInfo);
					Line line;
					try {
						line = m.getLine(lineInfo);
					} catch (LineUnavailableException e) {
						e.printStackTrace();
						return;
					}
					System.out.println(" "+line);
				}
			}
		}

	}

}
