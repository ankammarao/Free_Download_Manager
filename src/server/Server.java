package server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * NewThread class for making server multithreaded
 *
 */
class NewThread extends Thread
{
	//Enter directory path here
	public final static String dirpath = "E:\\MSIT\\Minisem 5\\CNF\\Week 4\\DownloadManager\\Files";
	private Socket server;
	ServerSocketChannel serverSocketChannel;

	/**
	 * Constructor
	 * @param socket
	 * @throws IOException
	 */
	public NewThread(Socket socket, ServerSocketChannel serverSocketChannel) throws IOException 
	{
		server = socket;
		this.serverSocketChannel = serverSocketChannel;
	}

	/** 
	 * Run Method
	 */
	@SuppressWarnings("resource")
	public void run() 
	{
		try 
		{
			DataInputStream in = new DataInputStream(server.getInputStream());
			DataOutputStream out = new DataOutputStream(server.getOutputStream());

			String s = in.readUTF();

			//get the options from the client
			String [] s_split = s.split("\\#");

			String filename = dirpath + "\\Server\\" + s_split[0]; // reading absolute file path
			File f = new File(filename);

			if(!f.isFile())	
			{
				//if file does not exist
				//				System.out.println("Sending file names...");
				out.writeUTF(files());
				out.flush();
			}
			else if(Integer.parseInt(s_split[1])<0)
			{
				//If file exists, send file size and md5 checksum
				int fileSize = (int) f.length();


				FileInputStream fis =  new FileInputStream(f);								//calculating md5 check sum
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
				String result = fileSize + "#";

				for (int i=0; i < b.length; i++) 
					result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
				
				out.writeUTF(result);
				out.flush();
			}
			else
			{
				//				System.out.println("Accepting Server sockets");
				SocketChannel toChannel = server.getChannel();

				long fromoff = Long.parseLong(s_split[1]);
				long tooff = Long.parseLong(s_split[2]);

				RandomAccessFile fromFile = new RandomAccessFile(f.getAbsolutePath(), "rw");
				FileChannel fromChannel = fromFile.getChannel();

				//				System.out.println("From Channel is open: "+fromChannel.isOpen());

				long position = fromoff;
				long count = tooff - fromoff + 1;

				fromChannel.transferTo(position, count, toChannel);

			}

			//			in.close();
			//			out.close();
		}
		catch (SocketTimeoutException s)
		{
			System.out.println("Socket timed out!");
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Files method to get list of files to clients
	 */
	private static String files()
	{
		String path = dirpath + "\\Server\\";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		String msg1 = "";

		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile())
			{
				msg1 = msg1 + "&" + listOfFiles[i].getName();
			}
		}
		msg1 = msg1 + "#" + path + "\\";
		return msg1;
	}
}


/**
 * Server class
 * 
 * Main execution starts here
 *
 */
public class Server
{
	public static void main(String[] args) throws IOException, InterruptedException {

		//Start server
		ServerSocketChannel ss = ServerSocketChannel.open();
		ss.bind(new InetSocketAddress(6666));
		ServerSocket serverSocket = ss.socket();
		int i = 99999;
		Thread [] t = new Thread [i];
		i=0;
		while(i<t.length)
		{
			Socket server = serverSocket.accept();
			t[i] = new Thread(new NewThread(server, ss));
			t[i].start();
			i++;
		}
	}
}
