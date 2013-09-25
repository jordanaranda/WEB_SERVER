package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import utils.Properties;
import utils.SocketManager;

/**
 * @author Jordan Aranda Tejada
 */
public class Server implements Runnable 
{
	public static Server		server;
	private boolean				running; 
	private static ServerSocket	serverSocket;

	/**
	 * Creates the server
	 */
	private Server(final int PORT, final int backlog, final InetAddress IP)
	{
		try
		{
			serverSocket = new ServerSocket(PORT);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("No se ha establecido la conexión");
			e.printStackTrace();
		}
		running = false;
	}

	public void run()
	{
		while(running)
		{
			SocketManager clientSocket;
			HttpRequest httprequest = null;
			try {
				//Aceptar la nueva petición y crear el SocketManager para gestionar el Socket obtenido
				System.out.println("Esperando...");
				clientSocket = new SocketManager(serverSocket.accept());
				//Crear un objeto HttpRequest para gestionar la petición
				httprequest = new HttpRequest(clientSocket);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
	
			//Crear un Thread para el objeto HttpRequest
			Thread clientThread = new Thread(httprequest);
			
			//Arrancar el Thread
			clientThread.start();
		}
	}
	
	public void connect()
	{
		try {
			serverSocket = new ServerSocket(Properties.getServerPORT(), 100, InetAddress.getByName(Properties.getServerIP()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnection()
	{
		try
		{
			serverSocket.close();
		}
		catch (IOException excepcionES)
		{
			excepcionES.printStackTrace();
		}
	}
	
	public static Server getInstance()
	{
		if(server == null)
		{
			try {
				server = new Server(Properties.getServerPORT(), 100, InetAddress.getByName(Properties.getServerIP()));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return server;
	}
	
	public ServerSocket getServerSocket() 
	{
		return serverSocket;
	}
	
	public boolean isRunning() 
	{
		return running;
	}

	public void setRunning(boolean running) 
	{
		this.running = running;
	}

	/**
	 * @param args Arguments
	 */
	public static void main(String ... args)
	{
		Server server = null;
		try
		{
			server = new Server(Properties.getServerPORT(), 3000,
			InetAddress.getByName(Properties.getServerIP()));
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		Thread server_thread = new Thread(server);
		server_thread.run();
	}
}