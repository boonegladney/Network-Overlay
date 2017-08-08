package cs455.overlay.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import cs455.overlay.node.*;

public class ConsoleInputReader implements Runnable{
	private Registry registry = null;
	private boolean readyToStop = false;
	private MessagingNode msgNode = null;
	
	/**
	 * constructor for use by the Registry class.
	 * @param registry
	 */
	public ConsoleInputReader(Registry registry)
	{
		this.registry = registry;
	}
	
	/**
	 * constructor for use by a MessagingNode
	 * @param msgNode
	 */
	public ConsoleInputReader(MessagingNode msgNode)
	{
		this.msgNode = msgNode;
	}
	
	/**
	 * listen for input from the console
	 */
	public void run() {
		BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
		String input;
		try
		{
			while(readyToStop == false)
			{
				input = consoleInput.readLine();
				handleInput(input);
			}
		}catch(IOException e){
			System.out.println("An error occured during an attempt to read from the console");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * handles the message received during the run method.
	 * @param input
	 */
	public void handleInput(String input)
	{
		if(registry!=null)
		{
			if(input.contains("setup-overlay"))
			{
				String[] temp = input.split(" ");
				if(temp.length == 2)
					registry.setupOverlay(Integer.parseInt(temp[1]));
				else
					System.out.println("INVALID INPUT. PLEASE TRY AGAIN.");
			}
			else if(input.contains("list-messaging-nodes"))
			{
				registry.printMessagingNodes();
			}
			else if(input.equals("list-weights"))
			{
				registry.printLinkWeights();
			}
			else if(input.equals("send-overlay-link-weights"))
			{
				registry.sendLinkWeights();
			}
			else if(input.contains("start"))
			{
				String[] temp = input.split(" ");
				if(temp.length == 2)
				{
				registry.startNodeTasks(Integer.parseInt(temp[1]));
				}
				else
					System.out.println("INVALID INPUT. PLEASE TRY AGAIN.");
			}
			else if(input.contains("shutdown"))
			{
				try {
					registry.shutdown();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("INVALID INPUT. PLEASE TRY AGAIN.");
			}
		}
		else if(msgNode!=null)
		{
			if(input.contains("print-shortest-path"))
			{
				msgNode.printShortestPath();
			}
			else if(input.contains("exit-overlay"))
			{
				try {
					msgNode.shutdown();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("INVALID INPUT. PLEASE TRY AGAIN.");
			}
		}
		else
		{
			System.out.println("INPUT FROM THE CONSOLE COULD NOT BE READ.");
		}
	}
	
	public void shutdown()
	{
		readyToStop = true;
	}

}
