package handler;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingConstants;


public class GUI 
{
	public static String directory = "E:\\MSIT\\Minisem 5\\CNF\\Week 4\\DownloadManager\\Files\\Client\\";
	public static int noOfConnections = 5;
	public static ArrayList<String> downloadedFiles = new ArrayList<>();
	public static int numberOfDownloads = 0;
	private int maxDownloads = 1;

	private JFrame frmDownloadManager;
	private JTextField dir;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmDownloadManager.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		frmDownloadManager = new JFrame();

		frmDownloadManager.setTitle("Download Manager");
		frmDownloadManager.setBounds(100, 100, 666, 430);
		frmDownloadManager.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmDownloadManager.getContentPane().setLayout(null);

		JButton btnNew = new JButton("New Download");
		btnNew.setBounds(25, 90, 140, 25);
		frmDownloadManager.getContentPane().add(btnNew);

		final JComboBox connections = new JComboBox();
		connections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				noOfConnections = Integer.parseInt(connections.getSelectedItem().toString());
			}
		});
		connections.setMaximumRowCount(10);
		connections.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));
		connections.setSelectedItem(connections.getItemAt(noOfConnections-1));
		connections.setBounds(521, 90, 117, 24);
		frmDownloadManager.getContentPane().add(connections);

		JLabel lblMaxConnections = new JLabel("Max Connections:");
		lblMaxConnections.setBounds(351, 95, 152, 15);
		frmDownloadManager.getContentPane().add(lblMaxConnections);

		JLabel lblDownloadDirectory = new JLabel("Default Download Directory: ");
		lblDownloadDirectory.setBounds(25, 12, 297, 25);
		frmDownloadManager.getContentPane().add(lblDownloadDirectory);

		dir = new JTextField();
		dir.setBounds(25, 49, 478, 25);
		frmDownloadManager.getContentPane().add(dir);
		dir.setColumns(10);
		dir.setText(directory);

		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				String direc = dir.getText();
				if(direc.charAt(direc.length()-1) != '/')
					direc += "/";
				try 
				{
					if(checkDir(direc) == 1)
					{
						JOptionPane.showMessageDialog(frmDownloadManager, "Directory changed", "Message", JOptionPane.INFORMATION_MESSAGE);
						directory = direc;
					}
					else
						throw new IOException();
				} 
				catch (IOException e1)
				{
					JOptionPane.showMessageDialog(frmDownloadManager, "Could not create directory", "Message", JOptionPane.ERROR_MESSAGE);
				}
				dir.setText(directory);
			}
		});
		btnUpdate.setBounds(521, 49, 117, 25);
		frmDownloadManager.getContentPane().add(btnUpdate);

		final JTextArea downloads = new JTextArea();
		downloads.setEditable(false);
		downloads.setBounds(11, 165, 627, 225);
		frmDownloadManager.getContentPane().add(downloads);

		JLabel lblMaxDownloads = new JLabel("Max Downloads");
		lblMaxDownloads.setBounds(351, 17, 152, 20);
		frmDownloadManager.getContentPane().add(lblMaxDownloads);

		final JComboBox max = new JComboBox();

		max.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));
		max.setMaximumRowCount(10);
		max.setBounds(521, 12, 117, 24);
		max.setSelectedItem(max.getItemAt(maxDownloads-1));
		frmDownloadManager.getContentPane().add(max);

		final JLabel maxReached = new JLabel("");
		maxReached.setHorizontalAlignment(SwingConstants.CENTER);
		maxReached.setForeground(Color.RED);
		maxReached.setBounds(35, 127, 592, 26);
		frmDownloadManager.getContentPane().add(maxReached);

		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(numberOfDownloads < maxDownloads)
				{
					maxReached.setText("");
					Handler.newDownload();
					numberOfDownloads += 1;
				}
				else
				{
					maxReached.setText("Limit reached....please wait while current download is finished.");
				}
			}
		});
		max.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				maxDownloads = Integer.parseInt(max.getSelectedItem().toString());
				maxReached.setText("");
			}
		});

		frmDownloadManager.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				downloads.setText("");
				String s = "Files Downloaded are\n";
				if(downloadedFiles.size() == 0)
					s = "Not downloaded anything....";
				for(int i=0; i<downloadedFiles.size(); i++)
					s += "\n=>" + downloadedFiles.get(i) + "\n";
				downloads.setText(s);
			}
		});
	}

	protected int checkDir(String dirpath) throws IOException
	{
		File f = new File(dirpath);
		if(f.exists())
		{
			if(f.isDirectory())
				return 1;
		}
		else
		{
			if(f.mkdirs())
				return 1;
		}
		return -1;
	}
}
