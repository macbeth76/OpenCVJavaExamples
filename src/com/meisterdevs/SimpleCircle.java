package com.meisterdevs;

import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class SimpleCircle {
	
	public static int WIDTH = 400;
	public static int HEIGHT = 300;
	
	
	
	
	public static void main(String arg[]) {
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String window_name = "Find Circle";
		JFrame frame = new JFrame(window_name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(SimpleCircle.WIDTH+20, SimpleCircle.HEIGHT+20);
		My_Panel my_panel = new My_Panel();
		frame.setContentPane(my_panel);
		frame.setVisible(true);
		
		JFrame frame2 = new JFrame("H");
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.setSize(SimpleCircle.WIDTH+20, SimpleCircle.HEIGHT+20);
		My_Panel my_panel2 = new My_Panel();
		frame2.setContentPane(my_panel2);
		frame2.setVisible(true);
		// -- 2. Read the video stream
		Mat webcam_image = new Mat();
		VideoCapture capture = new VideoCapture(-1);
		capture.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, SimpleCircle.HEIGHT);
		capture.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, SimpleCircle.WIDTH);
		if (capture.isOpened()) {
			while (true) {
				capture.read(webcam_image);
				if (!webcam_image.empty()) {
					
					//resize(webcam_image);
					frame.setSize(2 * webcam_image.width() + 20, 2 * webcam_image.height() + 20);
					// -- 3. Apply the classifier to the captured image
				
					int iCannyUpperThreshold = 100;
					int iMinRadius = 1;
					int iMaxRadius = 400;
					int iAccumulator = 300;
					// ## Lower thickness
					// int iLineThickness = 100;
					int iLineThickness = 5;
					Mat circles = new Mat();
					Mat thresholdImage = new Mat();
					// ## TODO: Uninitialized? Should at least be allocated
					// webcam_image.size, or shallow/deep copy webcam_image
					
					Imgproc.cvtColor(webcam_image, thresholdImage, Imgproc.COLOR_BGR2GRAY);
					Imgproc.GaussianBlur(thresholdImage, thresholdImage, new Size(9, 9), 2, 2);
					Imgproc.HoughCircles(thresholdImage, circles, Imgproc.CV_HOUGH_GRADIENT, 2.0,
							thresholdImage.rows() / 8, iCannyUpperThreshold, iAccumulator, iMinRadius, iMaxRadius);

					if (circles.cols() > 0)
						for (int x = 0; x < circles.cols(); x++) {
							double vCircle[] = circles.get(0, x);

							if (vCircle == null)
								break;
							
							Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
							int radius = (int) Math.round(vCircle[2]);
							Core.circle(webcam_image, pt, radius, new Scalar(0, 255, 0), iLineThickness);
							my_panel.MatToBufferedImage(webcam_image);
						}
					else {
						// -- 4. Display the image
						my_panel.MatToBufferedImage(webcam_image);
					}
					frame2.setSize(2 * thresholdImage.width() + 20, 2 * thresholdImage.height() + 20);
					my_panel2.MatToBufferedImage(thresholdImage);
					my_panel2.repaint();
					// ## Redraw panel with new circled image.
					my_panel.repaint();
				} else {
					System.out.println(" --(!) No captured frame -- Break!");
					break;
				}
			}
		}
		return;
	}




	private static void resize(Mat image) {
		Imgproc.resize(image, image, new Size(), 0.5, 0.5, 2);
		
	}
}
