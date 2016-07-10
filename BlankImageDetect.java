import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

public class BlankImageDetect {

	static int MIN_COLORS = 100;
	static int MAX_FREQUENCY = 95;
	boolean isBlank;
	IplImage img;
	boolean verbose;
	OpenCVFrameConverter.ToIplImage conv;
	Java2DFrameConverter paintConverter;

	BlankImageDetect(IplImage img, boolean verbose) {
		this.img = img;
		this.verbose = verbose;
		isBlank = false;
		conv = new OpenCVFrameConverter.ToIplImage();
		paintConverter = new Java2DFrameConverter();
	}

	private static Integer[] getPixelRGB(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		Integer[] result = new Integer[height * width];

		int count = 0;

		for (int row = 0; row < height; row++)
			for (int col = 0; col < width; col++)
				result[count++] = image.getRGB(col, row);

		return result;
	}

	public String showPixelColors() {

		String message = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(new Date());
		IplImage image = img;
		Frame frame = conv.convert(img);

		BufferedImage c = paintConverter.getBufferedImage(frame, 1);// image.getBufferedImage();

		int freq = 0, colour = 0;

		Integer pixels[] = getPixelRGB(c);

		Set<Integer> pixelSet = new HashSet<Integer>();
		Collections.addAll(pixelSet, pixels);

		List<Integer> sortedPixelList = new ArrayList<>();
		Collections.addAll(sortedPixelList, pixels);
		Collections.sort(sortedPixelList);

		for (int i = 0; i < sortedPixelList.size() - 1; i++) {
			int temp = 0;
			while (i < sortedPixelList.size() - 1 && sortedPixelList.get(i).equals(sortedPixelList.get(i + 1))) {
				++temp;
				i++;
			}
			if (temp > freq) {
				freq = temp + 1;
				colour = (Integer) (sortedPixelList.get(i - 1));
			}
		}

		double frequency_percentage = (double) 100 * freq / (image.height() * image.width());
		int uniq_colors = pixelSet.size();

		if (verbose) {
			message += "\n";
			message += "Number of pixels is " + (image.height() * image.width()) + "\n" + "Number of unique colours = "
					+ uniq_colors + "\n" + "Max frequency of any pixel in = " + freq + "\n"
					+ "Percentage of colour with maximum frequency " + frequency_percentage + "%\n"
					+ "Colour with maximum frequency " + colour + "\n\n";
		}

		if (frequency_percentage > MAX_FREQUENCY || uniq_colors < MIN_COLORS) {
			message += (" --> RESULT: The image could be blank!\n");
			isBlank = true;
		} else {
			message += ("  --> RESULT: The image is not blank!\n");
		}

		return message;

	}
}
