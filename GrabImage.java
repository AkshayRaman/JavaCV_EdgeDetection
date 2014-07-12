import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.VideoInputFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import java.awt.*;
import com.googlecode.javacv.OpenCVFrameGrabber;

public class GrabImage implements Runnable {
    IplImage image;
    CanvasFrame canvas1 = new CanvasFrame("");
    static int DEVICE_NUMBER = 0;

    public GrabImage() {
        canvas1.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        canvas1.setMaximumSize(new Dimension(500, 500));
        //canvas1.setVisible(false);
    }

    @
    Override
    public void run() {

        IplImage img = new IplImage();
        FrameGrabber grabber = new OpenCVFrameGrabber(DEVICE_NUMBER);
        grabber.setImageHeight(320);
        grabber.setImageWidth(480);
        
        try {
            grabber.start();
            String device_description = VideoInputFrameGrabber.getDeviceDescriptions()[DEVICE_NUMBER];
            
            while(MainWindow.runThreads == true) {
                img = grabber.grab();
                if (img != null) {
                    if (MainWindow.chckbxShowMonitor.isSelected()) {
                        canvas1.setVisible(true);

                        if (MainWindow.chckbxEdgedetection.isSelected()) {
                        	IplImage grayImage = cvCreateImage(cvGetSize(img), img.depth(), 1);
                        	IplImage canny = cvCreateImage(cvGetSize(img), img.depth(), 1);
                        	
                        	String choice = MainWindow.algoList.getSelectedValue();
                        								
                        	switch(choice){
                        	
                        	case "Canny Edge Detection":{
                        		//Canny Edge Detection
                        		cvCvtColor(img, grayImage, CV_BGR2GRAY);   
                        		cvSmooth(grayImage,grayImage,CV_GAUSSIAN, 7, 7, 0, 0);
                        		cvCanny(grayImage, canny, 10, 300, 5);
                        		canvas1.showImage(canny);
                        		break;
                        	}
                        	
                        	case "Laplacian Edge Detection":{
                        		//Laplacian Edge Detection
                        		IplImage temp = new IplImage(img);
                        		cvLaplace(temp, temp, 3);
                        		canvas1.showImage(temp);	
                        		break;
                        	}
                        		
                        	case "Sobel Edge Detection":{
                        		//Sobel Edge Detection
                        		IplImage temp = new IplImage(img);
                            	cvSobel(temp, temp, 1, 0, 3);
                            	canvas1.showImage(temp);
                            	break;
                        	}
                        	}

                        	canvas1.setTitle(device_description+" - "+choice);
                        } 
                        
                        else {
                            canvas1.showImage(img);
                            canvas1.setTitle(device_description+" - Webcam capture");
                        }
                    } 
                    
                    else{
                        canvas1.setVisible(false);
                    }

                    if(MainWindow.chckbxPerformTests.isSelected()){
                    	MainWindow.textArea.append("Feature to be updated soon.\n");
            			MainWindow.textArea.setCaretPosition(MainWindow.textArea.getDocument().getLength());
                    	
                    }

                }

                long sleepTime = Long.parseLong(MainWindow.intervalTextField.getValue().toString());
                //System.out.println("Sleeping for "+sleepTime+" ms");
                Thread.sleep(sleepTime);
            }

            grabber.stop();
            canvas1.dispose();
            System.out.println("Done. Exit.");
            System.exit(0);
            
        } catch (Exception e) {
        	System.out.println("ERROR: GrabImage "+e.getMessage());
        	e.printStackTrace();
        }
    }
}
