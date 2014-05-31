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


public class DownloadProgress  {


	private static JFrame frmDownloadProgress;

	private static JProgressBar progressBar;

	private static JButton btnClose;
	private static JLabel building;


	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public DownloadProgress(String file, int progress) {
		initialize(file, progress);
		frmDownloadProgress.setVisible(true);
	}



	/**
	 * Initialize the contents of the frame.
	 * @wbp.parser.entryPoint
	 */
	public void initialize(String file, int progress)
	{
		frmDownloadProgress = new JFrame();
		frmDownloadProgress.getContentPane().addContainerListener(new ContainerAdapter() {
			@Override
			public void componentAdded(ContainerEvent e) {

			}
		});
		frmDownloadProgress.setVisible(true);

		frmDownloadProgress.setTitle("Download Progress");
		frmDownloadProgress.setBounds(100, 100, 366, 206);
		frmDownloadProgress.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmDownloadProgress.getContentPane().setLayout(null);

		JLabel lblFile = new JLabel("File:");
		lblFile.setBounds(38, 24, 70, 15);
		frmDownloadProgress.getContentPane().add(lblFile);

		JLabel fileName = new JLabel(file);
		fileName.setBounds(91, 24, 220, 15);
		frmDownloadProgress.getContentPane().add(fileName);

		progressBar  = new JProgressBar();
		progressBar.setForeground(new Color(34, 139, 34));
		progressBar.setBounds(29, 51, 303, 31);
		frmDownloadProgress.getContentPane().add(progressBar);
		progressBar.setValue(progress);

		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmDownloadProgress.dispose();
			}
		});
		btnClose.setEnabled(false);
		btnClose.setBounds(91, 139, 117, 25);
		frmDownloadProgress.getContentPane().add(btnClose);

		building = new JLabel("");
		building.setForeground(new Color(128, 0, 0));
		building.setBounds(29, 105, 303, 22);
		frmDownloadProgress.getContentPane().add(building);
		frmDownloadProgress.revalidate();
	}

	public  void setProgress(double progress)
	{
		System.out.println("Download progress: " + progress);
		progressBar.setValue((int)progress);
	}

	public static void setCheck(boolean check)
	{

	}

	public static void close(boolean check)
	{
		btnClose.setEnabled(true);
		
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
		frmDownloadProgress.dispose();
	}
}
