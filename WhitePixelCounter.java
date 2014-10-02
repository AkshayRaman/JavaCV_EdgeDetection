import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class WhitePixelCounter {

	static int MIN_WHITE_PIXEL_COUNT = 100;
	boolean isBlank;
	IplImage img;
	boolean verbose;
	
	WhitePixelCounter(IplImage img,boolean verbose)
	{
		this.img = img;
		this.verbose = verbose;
		isBlank=false;
	}
		
	private int getWhitePixelCount(BufferedImage image) {
	      int count=0;
	      int height = image.getHeight();
	      int width = image.getWidth();
	    		

	      for (int row = 0; row < height; row++) 
	         for (int col = 0; col < width; col++) 
	            if(image.getRGB(col, row) == -1)
	            	count++;
	      
	      return count;
	   }
	
	public String showPixelColors()
	{
		BufferedImage c;
				
		String message = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(new Date());
		IplImage image = img;
		
		c = img.getBufferedImage();
		
		int wp = getWhitePixelCount(c);
		
		
		
		if (verbose){
				message+="\n";
				//message+= "Number of pixels is "+(image.height()*image.width())+"\n";
				message += "Edge pixel count = "+wp+", percentage = "+((float)wp*100/(image.height()*image.width()))+"%\n";
		}
		
		if(wp < MIN_WHITE_PIXEL_COUNT)
	    {
			//message += (" --> RESULT: The image could be blank!\n");
	    	isBlank=true;
	    }
	    else
	    {
	    	//message += ("  --> RESULT: The image is not blank!\n");
	    }
		
		return message;
		
	}
}	
