import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_GAUSSIAN;
import static org.bytedeco.javacpp.opencv_imgproc.cvCanny;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvLaplace;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import static org.bytedeco.javacpp.opencv_imgproc.cvSobel;

import java.awt.Dimension;

import javax.swing.JOptionPane;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.VideoInputFrameGrabber;

public class GrabImage implements Runnable {
	IplImage image;
	CanvasFrame canvas1;
	static int SELECTED_DEVICE_NUMBER;
	static String DEVICE_DESCRIPTION;

	public GrabImage() {

		canvas1 = new CanvasFrame("");
		SELECTED_DEVICE_NUMBER = MainWindow.camList.getSelectedIndex();

		if (SELECTED_DEVICE_NUMBER < 0) {
			System.out.println("ERROR:No cameras found!\nProgram will exit!");
			JOptionPane.showMessageDialog(null, "No cameras found!\nProgram will exit!", "Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		canvas1.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
		canvas1.setMaximumSize(new Dimension(500, 500));
		// canvas1.setVisible(false);
	}

	@Override
	public void run() {

		IplImage img = new IplImage();
		FrameGrabber grabber = new OpenCVFrameGrabber(SELECTED_DEVICE_NUMBER);
		grabber.setImageHeight(320);
		grabber.setImageWidth(480);
		OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();

		try {
			grabber.start();
			DEVICE_DESCRIPTION = VideoInputFrameGrabber.getDeviceDescriptions()[SELECTED_DEVICE_NUMBER];

			while (MainWindow.runThreads) {
				img = grabberConverter.convert(grabber.grab());
				if (img != null) {

					/*
					 * BufferedImage bi = img.getBufferedImage();
					 * ByteArrayOutputStream out = new ByteArrayOutputStream();
					 * ImageIO.write(bi, "PNG", out); byte[] bytes =
					 * out.toByteArray();
					 * 
					 * String base64bytes = Base64.encode(bytes); String src =
					 * "data:image/png;base64," + base64bytes;
					 * System.out.println(src);
					 */

					if (MainWindow.chckbxShowMonitor.isSelected()) {
						canvas1.setVisible(true);

						if (MainWindow.chckbxEdgedetection.isSelected()) {
							IplImage grayImage = cvCreateImage(cvGetSize(img), img.depth(), 1);
							IplImage canny = cvCreateImage(cvGetSize(img), img.depth(), 1);

							String choice = MainWindow.algoList.getSelectedValue();

							switch (choice) {

							case "Canny Edge Detection": {
								// Canny Edge Detection
								cvCvtColor(img, grayImage, CV_BGR2GRAY);
								cvSmooth(grayImage, grayImage, CV_GAUSSIAN, 7, 7, 0, 0);
								cvCanny(grayImage, canny, 10, 300, 5);
								canvas1.showImage(grabberConverter.convert(canny));
								break;
							}

							case "Laplacian Edge Detection": {
								// Laplacian Edge Detection
								IplImage temp = new IplImage(img);
								cvLaplace(temp, temp, 3);
								canvas1.showImage(grabberConverter.convert(temp));
								break;
							}

							case "Sobel Edge Detection": {
								// Sobel Edge Detection
								IplImage temp = new IplImage(img);
								cvSobel(temp, temp, 1, 0, 3);
								canvas1.showImage(grabberConverter.convert(temp));
								break;
							}
							}

							canvas1.setTitle(DEVICE_DESCRIPTION + " - " + choice);
						}

						else {
							canvas1.showImage(grabberConverter.convert(img));
							canvas1.setTitle(DEVICE_DESCRIPTION + " - Webcam capture");
						}
					}

					else {
						canvas1.setVisible(false);
					}

					if (MainWindow.chckbxPerformTests.isSelected()) {

						/* Edge counting algorithm here */

						MainWindow.textArea.setCaretPosition(MainWindow.textArea.getDocument().getLength());

					}

				}

				long sleepTime = Long.parseLong(MainWindow.intervalTextField.getValue().toString());
				// System.out.println("Sleeping for "+sleepTime+" ms");
				Thread.sleep(sleepTime);
			}

			grabber.stop();
			System.out.println("Done. Exit.");
			System.exit(0);

		} catch (Exception e) {
			System.out.println("ERROR: GrabImage " + e.getMessage());
			e.printStackTrace();
		} finally {
			canvas1.dispose();
			img.close();
			System.gc();
		}
	}
}
