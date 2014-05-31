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

@SuppressWarnings("resource")
public class Client implements Runnable 
{
	private String serverAddress = "localhost";
	private int serverPort= 6666;
	private int noOfConnections = 1;// no of connections
	private String file = "";// name of the file to be downloaded
	private File f;
	private File ftemp;
	private Socket client;
	private int filesize = 0;
	private int chunksize = 0;
	private int toOff = -1;
	private int fromOff = -1;
	private String md5sum = "";


	//public static DownloadProgress dp;


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
	Client(String file, int filesize, int chunksize, int toOff, int fromOff, File f, File ftemp, String serverAddress, int serverPort)
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
	 * @param filesize
	 * @param directory
	 */
	Client(String serverAddress, int serverPort, int noOfConnections, String file, String saveFile, int filesize, String directory, String md5sum)
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

	public void start()
	{
		createThreads();
	}


	/**
	 *  Method for creating threads
	 * 
	 */
	private void createThreads()
	{
		Thread t[] = new Thread[noOfConnections];
		DownloadProgress dp = new DownloadProgress(file, 0);
		for (int i = 0; i < noOfConnections; i++) 
		{
			int fromoff = (i) * chunksize;
			int tooff;
			if ( i == noOfConnections-1) 
			{
				tooff = filesize-1;
			}
			else 
			{
				tooff = fromoff + chunksize - 1;
			}

			//System.out.println("tname: " + i + " chunk size: " + chunksize + " fromoff: " + fromoff + " tooff: " + tooff);

			String n = "" + (i + 1);

			t[i] = new Thread(new Client(file, filesize, chunksize, tooff, fromoff, f, ftemp, serverAddress, serverPort), n);
			t[i].start();
		}
		for(int i = 0; i < t.length; i++)
		{
			try 
			{
				t[i].join();
				double progress = (i+1)*100/(double)t.length;
				dp.setProgress(progress);

			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		buildFile();
		DownloadProgress.close(checkFile());
		GUI.numberOfDownloads -= 1;
	}

	private boolean checkFile()
	{
		try
		{
			FileInputStream fis =  new FileInputStream(f);

			byte[] buffer = new byte[1024];
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

			for (int i=0; i < b.length; i++) 
				result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
			
			if(md5sum.equals(result))
				return true;
			else
				return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Initial method. Main execution starts here
	 */

	public void run()
	{
		try
		{
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.connect(new InetSocketAddress(serverAddress, serverPort));
			client = socketChannel.socket();

			DataOutputStream out = new DataOutputStream(client.getOutputStream());

			String offset = "#"+fromOff+ "#" + toOff;
			out.writeUTF(file+""+offset);
			out.flush();

			SocketChannel in = client.getChannel();
			if(in == null)
				System.out.println("there is no in channel");

			RandomAccessFile fromFile = new RandomAccessFile(ftemp.getAbsolutePath(), "rw");
			FileChannel fout = fromFile.getChannel();
			fout.transferFrom(in, fromOff, toOff-fromOff + 1);

			fout.close();
			fromFile.close();
			in.close();
			client.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}


	private void buildFile()
	{
		try
		{
			FileChannel f1 = new FileInputStream(ftemp).getChannel();
			FileChannel f2 = new FileOutputStream(f).getChannel();

			f1.transferTo(0, f1.size(), f2);	//copy the file

			f2.close();
			f1.close();
//			FileOutputStream fs = new FileOutputStream(ftemp);
			ftemp.delete();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}



}