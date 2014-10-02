import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GAUSSIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCanny;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvLaplace;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSobel;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.VideoInputFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class GrabImage implements Runnable {
    IplImage image;
    CanvasFrame canvas1;
    static int SELECTED_DEVICE_NUMBER;
    static String DEVICE_DESCRIPTION;

   public GrabImage(){
    	
    	SELECTED_DEVICE_NUMBER = MainWindow.camList.getSelectedIndex();
    	    	
    	if(SELECTED_DEVICE_NUMBER<0)
    	{
    		System.out.println("ERROR:No cameras found!\nProgram will exit!");
    		JOptionPane.showMessageDialog(null, "No cameras found!\nProgram will exit!", "Error", JOptionPane.ERROR_MESSAGE);
    		System.exit(0);
    	}
    	
    	canvas1 = new CanvasFrame("");
    	canvas1.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        canvas1.setMaximumSize(new Dimension(500, 500));
    }
   
   public ImageIcon createIcon(IplImage x)
   {
	   ImageIcon mwIcon = new ImageIcon(x.getBufferedImage());
   	   Image t = mwIcon.getImage().getScaledInstance(MainWindow.imgLabel.getWidth(), MainWindow.imgLabel.getHeight(),  java.awt.Image.SCALE_SMOOTH);
   	   mwIcon = new ImageIcon(t);
   	   return mwIcon;
   	
   }

    @
    Override
    public void run() {

        IplImage img = new IplImage();
        FrameGrabber grabber = new OpenCVFrameGrabber(SELECTED_DEVICE_NUMBER);
        grabber.setImageHeight(320);
        grabber.setImageWidth(480);
        
        try {
            grabber.start();
            DEVICE_DESCRIPTION = VideoInputFrameGrabber.getDeviceDescriptions()[SELECTED_DEVICE_NUMBER];
            
            while(MainWindow.runThreads) {
                img = grabber.grab();
                if (img != null) {
                	
                	//Show Monitor               	
                    if (MainWindow.chckbxShowMonitor.isSelected()) {
                        canvas1.setVisible(true);
                    	
                    	//Delete the below line
                        //MainWindow.imgLabel.setIcon(createIcon(img));

                        if (MainWindow.chckbxEdgedetection.isSelected()) {
                        	
                        	IplImage grayImage = cvCreateImage(cvGetSize(img), img.depth(), 1);
                        	IplImage canny = cvCreateImage(cvGetSize(img), img.depth(), 1);
                        	                        	                       	
                        	String choice = MainWindow.algoList.getSelectedValue();
                        	canvas1.setTitle(DEVICE_DESCRIPTION+" - "+choice);
                        	
                        	switch(choice){
                        	
                        	case "Canny Edge Detection":{
                        		//Canny Edge Detection
                        		cvCvtColor(img, grayImage, CV_BGR2GRAY);   
                        		cvSmooth(grayImage,grayImage,CV_GAUSSIAN, 7, 7, 0, 0);
                        		cvCanny(grayImage, canny, 10, 300, 5);
                        		canvas1.showImage(canny);
                        		//MainWindow.imgLabel.setIcon(createIcon(canny));
                        		break;
                        	}
                        	
                        	case "Laplacian Edge Detection":{
                        		//Laplacian Edge Detection
                        		IplImage temp = new IplImage(img);
                        		cvLaplace(temp, temp, 1);
                        		canvas1.showImage(temp);
                        		//MainWindow.imgLabel.setIcon(createIcon(temp));
                        		break;
                        	}
                        		
                        	case "Sobel Edge Detection":{
                        		//Sobel Edge Detection
                        		IplImage temp = new IplImage(img);
                            	cvSobel(temp, temp, 1, 1, 1);
                            	canvas1.showImage(temp);
                            	//MainWindow.imgLabel.setIcon(createIcon(temp));
                            	break;
                        		}
                        	}

                        	//canvas1.setTitle(DEVICE_DESCRIPTION+" - "+choice);
                        } 
                        
                        else {
                            canvas1.showImage(img);
                            canvas1.setTitle(DEVICE_DESCRIPTION+" - Webcam capture");
                        	//MainWindow.imgLabel.setIcon(createIcon(img));
                        }
                    } 
                    
                    else{
                        canvas1.setVisible(false);
                    }
                    
                  //Perform tests
                	if(MainWindow.chckbxPerformTests.isSelected()){
                    	
                    	/* To save and delete the image*/
        				//final String grabbedImage = System.currentTimeMillis()+".jpg";
        				//cvSaveImage(grabbedImage , img);
        				//final BlankImageDetect b = new BlankImageDetect(img,MainWindow.chckbxEnableDetailedOutput.isSelected());
                    	
                    	IplImage grayImage = cvCreateImage(cvGetSize(img), img.depth(), 1);
                    	IplImage canny = cvCreateImage(cvGetSize(img), img.depth(), 1);
                    	cvCvtColor(img, grayImage, CV_BGR2GRAY);   
                		cvSmooth(grayImage,grayImage,CV_GAUSSIAN, 7, 7, 0, 0);
                		cvCanny(grayImage, canny, 10, 300, 5);
                    	
                		final WhitePixelCounter b = new WhitePixelCounter(canny, true);
        				
        				new Thread(new Runnable(){
        				public void run(){
        					MainWindow.textArea.append(b.showPixelColors());
        					MainWindow.textArea.setCaretPosition(MainWindow.textArea.getDocument().getLength());
        					ProcessRunner.runProcess(b.isBlank? MainWindow.blankTextField.getText():MainWindow.notBlankTextField.getText());
        					//delete(grabbedImage);
        					}
        				}).start();
        				
            			MainWindow.textArea.setCaretPosition(MainWindow.textArea.getDocument().getLength());
                    	
                    }
                }

                long sleepTime = Long.parseLong(MainWindow.intervalTextField.getValue().toString());
                //System.out.println("Sleeping for "+sleepTime+" ms");
                Thread.sleep(sleepTime);
            }

            grabber.stop();
            canvas1.dispose();
            System.gc();
            System.out.println("Done. Exit.");
            System.exit(0);
            
        } catch (Exception e) {
        	System.out.println("ERROR: GrabImage "+e.getMessage());
        	e.printStackTrace();
        }
    }
}
