package client;
import handler.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Color;

import javax.swing.JComboBox;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;

import client.Client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class newDownload
{
	private JFrame frmNewDownload;
	private JLabel lblFilesList;
	private JTextField size;
	private JLabel lblHostAddress;
	private JTextField host;
	private JLabel lblPort;
	private JTextField port;
	private JButton getFiles;
	private JLabel lblSaveAs;
	private JTextField save;
	private JLabel wrongHost;
	private JButton btnNewButton;
	static String fileName = "";
	static String md5sum = "";

	/**
	 * Launch the application.
	 */
	public static String main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					newDownload window = new newDownload();
					window.frmNewDownload.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return fileName;
	}

	/**
	 * Create the application.
	 */
	public newDownload() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {
		frmNewDownload = new JFrame();
		frmNewDownload.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				GUI.numberOfDownloads -= 1;
			}
		});
		frmNewDownload.setTitle("New Download");
		frmNewDownload.setBounds(100, 100, 538, 351);
		frmNewDownload.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmNewDownload.getContentPane().setLayout(null);

		lblFilesList = new JLabel("Files List");
		lblFilesList.setBounds(12, 116, 70, 15);
		frmNewDownload.getContentPane().add(lblFilesList);

		size = new JTextField();
		size.setEditable(false);
		size.setText("0");
		size.setBounds(118, 159, 114, 19);
		frmNewDownload.getContentPane().add(size);
		size.setColumns(10);

		lblHostAddress = new JLabel("Host address: ");
		lblHostAddress.setBounds(12, 27, 137, 15);
		frmNewDownload.getContentPane().add(lblHostAddress);

		host = new JTextField();
		host.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e)
			{
				wrongHost.setEnabled(false);
				wrongHost.setText("");
			}
		});
		host.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String s = host.getText();
				String [] s_split = s.split(".");
				try 
				{
					if(s_split.length == 4)
					{
						Integer.parseInt(s_split[0]);
						Integer.parseInt(s_split[1]);
						Integer.parseInt(s_split[2]);
						Integer.parseInt(s_split[3]);

					}
					else
					{
						if(!s.equalsIgnoreCase("localhost"))
							throw new NumberFormatException();
					}
				}
				catch (NumberFormatException e1) 
				{
					wrongHost.setEnabled(true);
					wrongHost.setText("Enter proper host address");
				}
			}
		});
		host.setBounds(167, 25, 114, 19);
		frmNewDownload.getContentPane().add(host);
		host.setColumns(10);

		lblPort = new JLabel("Port:");
		lblPort.setBounds(299, 27, 70, 15);
		frmNewDownload.getContentPane().add(lblPort);

		port = new JTextField();
		port.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try
				{
					wrongHost.setEnabled(false);
					wrongHost.setText("");
					int x = Integer.parseInt(port.getText());
					if(x > 0 && x < 65533)
						getFiles.setEnabled(true);
				} 
				catch (NumberFormatException e1) 
				{
					wrongHost.setEnabled(true);
					wrongHost.setText("Enter proper host address");
				}
			}
		});
		port.setBounds(380, 25, 114, 19);
		frmNewDownload.getContentPane().add(port);
		port.setColumns(10);

		final JComboBox files = new JComboBox();

		files.setModel(new DefaultComboBoxModel(new String[] {""}));
		files.setBounds(118, 111, 376, 24);
		frmNewDownload.getContentPane().add(files);

		final JButton start = new JButton("Start Download");
		start.setEnabled(false);

		start.setBounds(68, 262, 160, 25);
		frmNewDownload.getContentPane().add(start);

		getFiles = new JButton("Get files");
		getFiles.setEnabled(false);
		getFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				Object [] arr = getFiles(host.getText(), Integer.parseInt(port.getText()));
				if(arr == null)
				{
					wrongHost.setEnabled(true);
					wrongHost.setText("Enter proper host address");
				}
				else
				{
					String [] a = new String [arr.length];
					for(int i=0; i<a.length; i++)
						a[i] = arr[i].toString();
					files.setModel(new DefaultComboBoxModel(a));
					host.setEditable(false);
					port.setEditable(false);
					getFiles.setEnabled(false);

				}
			}
		});
		getFiles.setBounds(334, 74, 160, 25);
		frmNewDownload.getContentPane().add(getFiles);

		lblSaveAs = new JLabel("Save as: ");
		lblSaveAs.setBounds(12, 198, 70, 15);
		frmNewDownload.getContentPane().add(lblSaveAs);

		save = new JTextField();
		save.setEditable(false);
		save.setBounds(118, 190, 375, 31);
		frmNewDownload.getContentPane().add(save);
		save.setColumns(10);

		JLabel lblFileSize = new JLabel("File Size:");
		lblFileSize.setBounds(12, 161, 70, 15);
		frmNewDownload.getContentPane().add(lblFileSize);

		wrongHost = new JLabel("");
		wrongHost.setForeground(Color.RED);
		wrongHost.setEnabled(false);
		wrongHost.setBounds(22, 74, 280, 24);
		frmNewDownload.getContentPane().add(wrongHost);

		btnNewButton = new JButton("Cancel Download");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				GUI.numberOfDownloads -= 1;
				frmNewDownload.dispose();
			}
		});
		btnNewButton.setBounds(276, 262, 183, 25);
		frmNewDownload.getContentPane().add(btnNewButton);
		
		final JLabel invalidFile = new JLabel("");
		invalidFile.setForeground(Color.RED);
		invalidFile.setBounds(262, 161, 232, 15);
		frmNewDownload.getContentPane().add(invalidFile);
		files.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = files.getSelectedItem().toString();
				if(!s.equalsIgnoreCase("Select A File"))
				{
					invalidFile.setText("");
					int filesize = getSize(host.getText(), Integer.parseInt(port.getText()), files.getSelectedItem().toString());
					size.setText("" + filesize);
					save.setText(s);
					save.setEditable(true);
					start.setEnabled(true);
				}
				else
				{
					invalidFile.setText("Please choose a file");
					save.setText("");
					size.setText("0");
				}
			}
		});
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				fileName = save.getText();
				GUI.downloadedFiles.add(fileName);
				createFiles(fileName);
				Client cl = new Client(host.getText(), Integer.parseInt(port.getText()), GUI.noOfConnections, files.getSelectedItem().toString(), fileName, Integer.parseInt(size.getText()), GUI.directory, md5sum);
				cl.start();
				frmNewDownload.dispose();
			}
		});
	}

	/**
	 * @param serverAddress
	 * @param serverPort
	 * @param file
	 * @return
	 */
	private int getSize(String serverAddress, int serverPort, String file)
	{
		try 
		{
			//Establish connection with Server
			Socket client = new Socket(serverAddress, serverPort);
			DataInputStream in = new DataInputStream(client.getInputStream());
			DataOutputStream out = new DataOutputStream(client.getOutputStream());

			//send request to server for selected file
			out.writeUTF(file + "#-1#-1");

			//Read filesize, checksum from server
			//Response will be sent as filesize#checksum
			String s = in.readUTF();
			//split based on delimiter #
			String s_split [] = s.split("#");
			
			int filesize = Integer.parseInt(s_split[0]);
			md5sum = s_split[1];

			client.close(); 
			return filesize;
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	static Object [] getFiles(String serverAddress, int serverPort)
	{
		try 
		{
			//Connect to server
			Socket client = new Socket();
			client.connect(new InetSocketAddress(serverAddress, serverPort), 5000);
			DataInputStream in = new DataInputStream(client.getInputStream());
			DataOutputStream out = new DataOutputStream(client.getOutputStream());

			//Send blank request for getting files
			out.writeUTF("null#-1#-1");

			//Receive file names
			String path1 = in.readUTF();
			String files[] = path1.split("\\#")[0].split("\\&");
			ArrayList<String> file = new ArrayList<String>();
			file.add("Select A File");
			for (int i = 0; i < files.length; i++) 
			{
				if(files[i] == null)
					continue;
				files[i].trim();
				if(files[i].length() <=0)
					continue;
				file.add(files[i]);
			}

			client.close();

			return file.toArray();
		}
		catch(SocketTimeoutException e)
		{
			return null;
		}
		catch (IOException e)
		{
			return null;
		}
	}

	/**
	 * Method for creation of temporary files
	 */
	private void createFiles(String fileName)
	{

		try
		{
			// Create file in user's directory
			File f = new File(GUI.directory  + fileName);
			f.delete();
			f.createNewFile();

			// Creating temporary file
			File ftemp = new File(GUI.directory  + fileName + ".tmp");
			ftemp.delete();
			ftemp.createNewFile();

			//fill temporary file with junk
			FileOutputStream fos = new FileOutputStream(ftemp);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			byte [] temp = new byte[Integer.parseInt(size.getText())];
			for(int i=0; i<temp.length; i++)
				temp[i] = '0';

			bos.write(temp);
			fos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
