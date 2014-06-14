package client;
import handler.GUI;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;

import client.DownloadProgress;

@SuppressWarnings("resource")
public class Client implements Runnable 
{
	private String serverAddress = "localhost";
	private int serverPort= 6666;
	private int noOfConnections = 1;// no of connections
	private String file = "";// name of the file to be downloaded
	private File f;
	private File ftemp;	//temporary file
	private Socket client;
	private int filesize = 0;
	private int chunksize = 0;
	private int toOff = -1;
	private int fromOff = -1;
	private String md5sum = "";

	/**
	 * Constructor for Threads
	 * 
	 * @param file
	 * @param filesize
	 * @param chunksize
	 * @param toOff
	 * @param fromOff
	 * @param f
	 * @param ftemp
	 * @param serverAddress
	 * @param serverPort
	 */
	public Client(String file, int filesize, int chunksize, int toOff, int fromOff, File f, File ftemp, String serverAddress, int serverPort)
	{
		this.file = file;
		this.filesize = filesize;
		this.chunksize = chunksize;
		this.toOff = toOff;
		this.fromOff = fromOff;
		this.f = f;
		this.ftemp = ftemp;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}

	/**
	 * Constructor to be called from GUI
	 * 
	 * @param serverAddress
	 * @param serverPort
	 * @param noOfConnections
	 * @param file
	 * @param saveFile
	 * @param filesize
	 * @param directory
	 * @param md5sum
	 */
	public Client(String serverAddress, int serverPort, int noOfConnections, String file, String saveFile, int filesize, String directory, String md5sum)
	{
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.noOfConnections = noOfConnections;
		this.file = file;
		this.filesize = filesize;
		this.chunksize = (int) Math.ceil(((double)filesize/noOfConnections));
		this.ftemp = new File(directory + saveFile + ".tmp");
		this.f = new File(directory + saveFile);
		this.md5sum = md5sum;
	}

	/**
	 * start method
	 */
	public void start()
	{
		createThreads();
	}


	/**
	 *  Method for creating threads for multithreaded download
	 *  Each thread will download one chunk of the file
	 */
	private void createThreads()
	{
		Thread t[] = new Thread[noOfConnections];
		DownloadProgress dp = new DownloadProgress(file, 0);
		//Split file into chunks
		for (int i = 0; i < noOfConnections; i++) 
		{
			//Create chunk. FromOffset to To Offset
			int fromOffset = (i) * chunksize;
			int toOffset;
			
			//Until file end is not reached, create chunk of size chunksize
			if ( i < noOfConnections-1) 
				toOffset = fromOffset + chunksize - 1;
			else 	//else chunk will be till end of file
				toOffset = filesize-1;
				

			String n = "" + (i + 1);
			//Create new thread
			t[i] = new Thread(new Client(file, filesize, chunksize, toOffset, fromOffset, f, ftemp, serverAddress, serverPort), n);
			//Start it
			t[i].start();
		}
		
		//After threads done, join them to end them
		//This loop will only end when all the threads are finished
		for(int i = 0; i < t.length; i++)
		{
			try {
				t[i].join();
				//Set the progress bar in the GUI
				double progress = (i+1)*100/(double)t.length;
				dp.setProgress(progress);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//Once all threads are done, build the file
		buildFile();
		
		//call the checkFile method in Downloadprogress. It will check integrity of file
		DownloadProgress.close(checkFile());
		
		//Decrement no of ongoing downloads from GUI
		GUI.numberOfDownloads -= 1;
	}

	/**
	 * Method to check the file integrity
	 * @return
	 */
	private boolean checkFile()
	{
		try {
			//open downloaded file
			FileInputStream fis =  new FileInputStream(f);

			byte[] buffer = new byte[1024];
			
			//call Java inbuilt function for MD5 sum
			MessageDigest complete = MessageDigest.getInstance("MD5");
			
			int numRead;
			do 
			{
				numRead = fis.read(buffer);
				if (numRead > 0)
					complete.update(buffer, 0, numRead);
			}
			while (numRead != -1);
			fis.close();

			byte[] b = complete.digest();
			String result = "";

			//convert MD5 sum to string for comparing
			for (int i=0; i < b.length; i++) 
				result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
			
			//if checksum matches
			if(md5sum.equals(result))
				return true;
			else
				return false;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	
	/**
	 * Initial method. Thread execution starts here
	 */
	public void run()
	{
		try	{
			//Open socketchannel and connect to server
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.connect(new InetSocketAddress(serverAddress, serverPort));
			
			//get the socket from the socket channel
			client = socketChannel.socket();
			DataOutputStream out = new DataOutputStream(client.getOutputStream());

			//Send chunk request to server
			String offset = "#"+fromOff+ "#" + toOff;
			out.writeUTF(file+""+offset);
			out.flush();

			//Open input socketchannel
			SocketChannel in = client.getChannel();
			if(in == null)
				System.out.println("There is no in-channel");

			//Open file for random writing
			RandomAccessFile fromFile = new RandomAccessFile(ftemp.getAbsolutePath(), "rw");
			
			//open filechannel to write to file
			FileChannel fout = fromFile.getChannel();
			
			//write the file
			fout.transferFrom(in, fromOff, toOff-fromOff + 1);

			//close connections
			fout.close();
			fromFile.close();
			in.close();
			client.close();
		} catch (IOException e)	{
			e.printStackTrace();
		}
	}


	/**
	 * Method to build the final file
	 * 
	 */
	private void buildFile()
	{
		try	{
			//open both files
			FileChannel f1 = new FileInputStream(ftemp).getChannel();
			FileChannel f2 = new FileOutputStream(f).getChannel();

			f1.transferTo(0, f1.size(), f2);	//copy the file

			//close channels
			f2.close();
			f1.close();
			ftemp.delete();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}



}