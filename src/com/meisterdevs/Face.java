package com.meisterdevs;

import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class Face {
	public static void main(String arg[]) {
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String window_name = "Capture - Face detection";
		JFrame frame = new JFrame(window_name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		processor my_processor = new processor();
		My_Panel my_panel = new My_Panel();
		frame.setContentPane(my_panel);
		frame.setVisible(true);
		// -- 2. Read the video stream
		Mat webcam_image = new Mat();
		VideoCapture capture = new VideoCapture(-1);
		if (capture.isOpened()) {
			while (true) {
				//capture.set(, value)
				capture.read(webcam_image);
				if (!webcam_image.empty()) {
					Mat dst = new Mat();
					Imgproc.resize(webcam_image, dst, new Size(), 0.5, 0.5, 2);
					frame.setSize(2 * dst.width() + 40, 2 * dst.height() + 60);
					// -- 3. Apply the classifier to the captured image
					//webcam_image = my_processor.detect(dst);
					int iCannyUpperThreshold = 100;
					int iMinRadius = 20;
					int iMaxRadius = 400;
					int iAccumulator = 300;
          // ## Lower thickness
          // int iLineThickness = 100;
					int iLineThickness = 5;
					Mat circles = new Mat();
					Mat thresholdImage = new Mat();
          // ## TODO: Uninitialized? Should at least be allocated webcam_image.size, or shallow/deep copy webcam_image
					Mat destination = new Mat();
					Imgproc.cvtColor(dst, thresholdImage, Imgproc.COLOR_BGR2GRAY);
					Imgproc.HoughCircles(thresholdImage, circles, Imgproc.CV_HOUGH_GRADIENT, 2.0,
							thresholdImage.rows() / 8, iCannyUpperThreshold, iAccumulator, iMinRadius, iMaxRadius);

					if (circles.cols() > 0)
						for (int x = 0; x < circles.cols(); x++) {
							double vCircle[] = circles.get(0, x);

							if (vCircle == null)
								break;

							Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
							int radius = (int) Math.round(vCircle[2]);

              // draw the found circle...
              // Core.circle(destination, pt, radius, new Scalar(0, 255, 0), iLineThickness);
              // Core.circle(destination, pt, 3, new Scalar(0, 0, 255), iLineThickness);
              // ## Onto webcam_image, not destination (since not allocated) with green.
              Core.circle(webcam_image, pt, radius, new Scalar(0, 255, 0), iLineThickness);
              // ## Buffer circled webcam_image to panel
              // my_panel.MatToBufferedImage(destination);
              my_panel.MatToBufferedImage(webcam_image);
						}
					else {
						// -- 4. Display the image
						my_panel.MatToBufferedImage(dst);
					}
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
}
