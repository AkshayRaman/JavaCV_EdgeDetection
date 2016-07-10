import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.bytedeco.javacv.VideoInputFrameGrabber;

public class MainWindow {

	private JFrame frmImageProcessing;
	String[] suffices = ImageIO.getReaderFileSuffixes();
	public static JTextField blankTextField;
	public static JTextField notBlankTextField;
	public static JTextArea textArea;
	public static JSpinner intervalTextField;
	public static JCheckBox chckbxEdgedetection;
	public static JCheckBox chckbxShowMonitor;
	public static JCheckBox chckbxPerformTests;
	public static boolean runThreads;
	public static JList<String> algoList;
	public static String[] algorithms;
	public static JList<String> camList;
	public static JButton btnStart;
	public static JLabel imgLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// EventQueue.invokeLater(new Runnable() {
		// public void run() {
		try {
			MainWindow window = new MainWindow();
			System.out.println("Starting the Main Window...");
			window.frmImageProcessing.setVisible(true);
		} catch (Exception e) {
			System.out.println("ERROR: MainWindow " + e.getMessage());
			e.printStackTrace();
		}
		// }
		// });
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("serial")
	private void initialize() {

		runThreads = true;
		algorithms = new String[] { "Canny Edge Detection", "Laplacian Edge Detection", "Sobel Edge Detection" };

		frmImageProcessing = new JFrame();
		frmImageProcessing.setResizable(false);
		frmImageProcessing.setTitle("Image Processing");
		frmImageProcessing.setBounds(100, 100, 640, 560);
		frmImageProcessing.getContentPane().setLayout(null);

		frmImageProcessing.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				runThreads = false;
				// System.exit(0);
			}
		});

		frmImageProcessing.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		btnStart = new JButton("START");
		btnStart.setToolTipText("");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent args0) {
				btnStart.setText("Running...");
				btnStart.setEnabled(false);
				intervalTextField.setEnabled(false);
				GrabImage g = new GrabImage();
				Thread t = new Thread(g);
				t.start();
			}
		});
		btnStart.setBounds(27, 21, 200, 105);
		frmImageProcessing.getContentPane().add(btnStart);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(27, 347, 580, 153);
		frmImageProcessing.getContentPane().add(scrollPane);

		textArea = new JTextArea();
		textArea.setLocation(27, 0);
		scrollPane.setViewportView(textArea);

		JLabel lblClearConsole = new JLabel("<html><u>Clear console</u></html>");
		lblClearConsole.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				textArea.setText("");
				System.out.println("Console cleared.");
			}
		});
		lblClearConsole.setForeground(Color.BLUE);
		lblClearConsole.setBounds(515, 322, 92, 14);
		frmImageProcessing.getContentPane().add(lblClearConsole);

		JButton btnNewButton = new JButton("If blank, run");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("."));
				int ret = fc.showDialog(frmImageProcessing, "Ok");
				if (ret == JFileChooser.APPROVE_OPTION) {
					blankTextField.setText(fc.getSelectedFile().toString());
				} else
					blankTextField.setText("");
			}
		});
		btnNewButton.setBounds(10, 255, 170, 23);
		frmImageProcessing.getContentPane().add(btnNewButton);

		JButton btnNotBlank = new JButton("If not blank, run");
		btnNotBlank.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("."));
				int ret = fc.showDialog(frmImageProcessing, "Ok");
				if (ret == JFileChooser.APPROVE_OPTION) {
					notBlankTextField.setText(fc.getSelectedFile().toString());
				} else
					notBlankTextField.setText("");
			}
		});
		btnNotBlank.setBounds(10, 287, 170, 23);
		frmImageProcessing.getContentPane().add(btnNotBlank);

		blankTextField = new JTextField();
		blankTextField.setEditable(false);
		blankTextField.setBounds(209, 255, 398, 23);
		frmImageProcessing.getContentPane().add(blankTextField);
		blankTextField.setColumns(10);

		notBlankTextField = new JTextField();
		notBlankTextField.setEditable(false);
		notBlankTextField.setColumns(10);
		notBlankTextField.setBounds(209, 288, 398, 23);
		frmImageProcessing.getContentPane().add(notBlankTextField);

		JLabel lblScriptOptions = new JLabel("Script options (set this before starting camera)");
		lblScriptOptions.setBounds(157, 226, 320, 14);
		frmImageProcessing.getContentPane().add(lblScriptOptions);

		JSeparator separator = new JSeparator();
		separator.setBounds(27, 213, 580, 2);
		frmImageProcessing.getContentPane().add(separator);

		JLabel lblCredits = new JLabel("Created by: Akshay");
		lblCredits.setBounds(449, 511, 112, 14);
		frmImageProcessing.getContentPane().add(lblCredits);

		intervalTextField = new JSpinner();
		intervalTextField.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		intervalTextField.setBounds(147, 137, 80, 20);
		frmImageProcessing.getContentPane().add(intervalTextField);

		JLabel lblCaptureInterval = new JLabel("Capture Interval (ms):");
		lblCaptureInterval.setBounds(10, 140, 127, 14);
		frmImageProcessing.getContentPane().add(lblCaptureInterval);

		chckbxEdgedetection = new JCheckBox("Edge Detection");
		chckbxEdgedetection.setBounds(268, 48, 153, 23);
		frmImageProcessing.getContentPane().add(chckbxEdgedetection);

		chckbxShowMonitor = new JCheckBox("Show Monitor Window");
		chckbxShowMonitor.setSelected(true);
		chckbxShowMonitor.setBounds(268, 21, 160, 23);
		frmImageProcessing.getContentPane().add(chckbxShowMonitor);

		chckbxPerformTests = new JCheckBox("Perform tests");
		chckbxPerformTests.setSelected(true);
		chckbxPerformTests.setBounds(27, 166, 133, 23);
		frmImageProcessing.getContentPane().add(chckbxPerformTests);

		algoList = new JList<String>();
		algoList.setBorder(new LineBorder(new Color(0, 0, 0)));
		algoList.setVisibleRowCount(3);
		algoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		algoList.setModel(new AbstractListModel<String>() {
			String[] values = algorithms;

			public int getSize() {
				return values.length;
			}

			public String getElementAt(int index) {
				return values[index];
			}
		});
		algoList.setSelectedIndex(0);
		algoList.setBounds(449, 50, 170, 139);
		frmImageProcessing.getContentPane().add(algoList);

		camList = new JList<String>();
		camList.setBorder(new LineBorder(new Color(0, 0, 0)));
		camList.setVisibleRowCount(3);

		try {
			camList.setModel(new AbstractListModel<String>() {
				String[] values = VideoInputFrameGrabber.getDeviceDescriptions();

				public int getSize() {
					return values.length;
				}

				public String getElementAt(int index) {
					return values[index];
				}
			});
			camList.setSelectedIndex(0);
		}

		catch (Exception ex) {
		}

		camList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		camList.setBounds(268, 101, 144, 88);
		frmImageProcessing.getContentPane().add(camList);

		JLabel lblWebcamsDetected = new JLabel("Cameras detected:");
		lblWebcamsDetected.setBounds(268, 76, 147, 14);
		frmImageProcessing.getContentPane().add(lblWebcamsDetected);

		JLabel lblNewLabel = new JLabel("Algorithms:");
		lblNewLabel.setBounds(454, 25, 153, 14);
		frmImageProcessing.getContentPane().add(lblNewLabel);

		imgLabel = new JLabel("");
		imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
		imgLabel.setIcon(null);
		imgLabel.setBounds(27, 347, 222, 153);
		frmImageProcessing.getContentPane().add(imgLabel);

	}
}
