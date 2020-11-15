package xyz.arwhite.cams;

import org.bytedeco.javacpp.avdevice.AVDeviceInfo;
import org.bytedeco.javacpp.avdevice.AVDeviceInfoList;

public class Cameras {

	public Cameras() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		System.out.println("hello world");

		AVDeviceInfoList avlist = new AVDeviceInfoList(5);
		
		int n = 0;
		n = avlist.nb_devices();
		System.out.println("number of devices == "+n);
		
		for (int i = 0; i < n; i++) {
		   AVDeviceInfo info = avlist.devices(i);

		   System.out.println(info.device_name());
		   System.out.println(info.device_description());
		   
		}
		
		avlist.close();
		
		
//	       avdevice.AVDeviceInfoList devInfoList = new avdevice.AVDeviceInfoList();
//	        avformat.AVFormatContext context = avformat.avformat_alloc_context();
//	        avdevice.avdevice_list_devices(context, devInfoList);
//	        System.out.println("devInfoList = ");
//	        avformat.avformat_free_context(context);
		
//		FFmpegFrameGrabber lister = new FFmpegFrameGrabber("dummy"); 
//		lister.setFormat("dshow"); 
//		lister.setOption("list_devices", "true"); 
//		try { 
//		lister.start(); 
//		} catch (Exception e) { 
//		// cannot open "dummy": ignore exception 
//		} 
//		
//		try {
//			Thread.sleep(8000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		AVOutputFormat avf = new AVOutputFormat();
//		AVOutputFormat.Get_device_list_AVFormatContext_AVDeviceInfoList l = avf.get_device_list();
//		
//		System.out.println(l);
		
//		int n = videoInput.listDevices();
//		
//		System.out.println("num device "+n);
//
//		for (int i = 0; i < n; i++) {
//		   String info = videoInput.getDeviceName(i).getString();
//
//		   System.out.println(info);
//		}
	}

}
