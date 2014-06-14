package client;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;


/**
 * For displaying download progress
 */
public class DownloadProgress  {


	private static JFrame frameDownloadProgress;

	private static JProgressBar progressBar;

	private static JButton buttonClose;
	private static JLabel building;


	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public DownloadProgress(String file, int progress) {
		initialize(file, progress);
		frameDownloadProgress.setVisible(true);
	}



	/**
	 * Initialize the contents of the frame.
	 * @wbp.parser.entryPoint
	 */
	public void initialize(String file, int progress)
	{
		frameDownloadProgress = new JFrame();
		frameDownloadProgress.getContentPane().addContainerListener(new ContainerAdapter() {
			@Override
			public void componentAdded(ContainerEvent e) {

			}
		});
		frameDownloadProgress.setVisible(true);

		frameDownloadProgress.setTitle("Download Progress");
		frameDownloadProgress.setBounds(100, 100, 366, 206);
		frameDownloadProgress.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frameDownloadProgress.getContentPane().setLayout(null);

		JLabel lblFile = new JLabel("File:");
		lblFile.setBounds(38, 24, 70, 15);
		frameDownloadProgress.getContentPane().add(lblFile);

		JLabel fileName = new JLabel(file);
		fileName.setBounds(91, 24, 220, 15);
		frameDownloadProgress.getContentPane().add(fileName);

		progressBar  = new JProgressBar();
		progressBar.setForeground(new Color(34, 139, 34));
		progressBar.setBounds(29, 51, 303, 31);
		frameDownloadProgress.getContentPane().add(progressBar);
		progressBar.setValue(progress);

		buttonClose = new JButton("Close");
		buttonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frameDownloadProgress.dispose();
			}
		});
		buttonClose.setEnabled(false);
		buttonClose.setBounds(91, 139, 117, 25);
		frameDownloadProgress.getContentPane().add(buttonClose);

		building = new JLabel("");
		building.setForeground(new Color(128, 0, 0));
		building.setBounds(29, 105, 303, 22);
		frameDownloadProgress.getContentPane().add(building);
		frameDownloadProgress.revalidate();
	}

	/**
	 * Set the progress
	 * @param progress
	 */
	public  void setProgress(double progress)
	{
		System.out.println("Download progress: " + progress);
		progressBar.setValue((int)progress);
	}

	/**
	 * Check the file for integrity and close
	 * @param check
	 */
	public static void close(boolean check)
	{
		buttonClose.setEnabled(true);
		
		if(check)
			building.setText("Checking downloaded file....OK");
		else
			building.setText("Checking downloaded file....Failed");
	}

	public static int getProgress()
	{
		return progressBar.getValue();
	}

	public static void destroy()
	{
		frameDownloadProgress.dispose();
	}
}
