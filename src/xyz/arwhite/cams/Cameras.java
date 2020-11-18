package xyz.arwhite.cams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

import org.bytedeco.ffmpeg.avformat.AVInputFormat;
import org.bytedeco.ffmpeg.avutil.LogCallback;
import org.bytedeco.ffmpeg.global.avdevice;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import static org.bytedeco.ffmpeg.global.avutil.*;

public class Cameras {

	static class CamLogCallback extends LogCallback {
		static final CamLogCallback instance = new CamLogCallback().retainReference();
		public static CamLogCallback getInstance() { return instance; }
		public static void set() { setLogCallback(getInstance()); }
		@Override public void call(int level, BytePointer msg) {
			System.err.println("CAPTURED: "+msg.getString());
		}
	}

	public static void main(String[] args) {
		var origErr = System.err;
		avdevice.avdevice_register_all();
		CamLogCallback.set();

		File tmp = null; 
		AVInputFormat inp = null;	
		while ( (inp = avdevice.av_input_video_device_next(inp)) != null ) {
			try  {
				tmp = File.createTempFile(Long.toString(System.currentTimeMillis()), null);
				System.setErr(new PrintStream(tmp));

				FFmpegFrameGrabber lister = new FFmpegFrameGrabber(""); 
				lister.setFormat(inp.name().getString()); 
				lister.setOption("list_devices", "true"); 
				lister.start(); 
			} catch(Exception e) {};

			System.setErr(origErr);
			printFile(tmp);
			inp.close();
		}
	}

	private static void printFile(File file) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));

			String line;
			while ((line = br.readLine()) != null) {
				System.out.println("STDERR: "+line);
			}
		} catch (Exception e) {}
	}
}	



