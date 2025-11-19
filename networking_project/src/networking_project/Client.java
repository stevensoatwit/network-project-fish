package networking_project;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.imageio.ImageIO;

public class Client extends Frame {
	
	public Client(){
      super("Virtual Internet Aquarium");
      prepareGUI();
   }
	
   private void prepareGUI(){
      setSize(Globals.fish_size*8*Globals.graphics_scale,Globals.fish_size*Globals.fish_count*Globals.graphics_scale);
      addWindowListener(new java.awt.event.WindowAdapter(){
		  public void windowClosing(java.awt.event.WindowEvent windowEvent) {
			  System.exit(0);
		  }
	  });
   }
   
   private static byte[] fishData = new byte[Globals.fish_count];
	   
	public void paint (Graphics g) {
	    //Determine size of drawing surface
	    final int drawWidth = 8 * Globals.fish_size;
	    final int drawHeight = Globals.fish_count * Globals.fish_size;
	    
	    //Create drawing surface, this will be resized to fit the window later
	    BufferedImage ds = new BufferedImage(drawWidth, drawHeight, BufferedImage.TYPE_INT_ARGB);
	    //Create a graphics object for the drawing surface
	    Graphics2D drawG = ds.createGraphics();
	    
	    //Load fish and water images, we probably dont have to do this every time but meh
	    BufferedImage fishImg = null;
	    try {
	    	fishImg = ImageIO.read(new File("networking_project/fish.png"));
	    } catch (IOException e) {
	    	System.out.printf("Couldnt load fish.png :(\n");
	    }
	    BufferedImage waterImg = null;
	    try {
	    	waterImg = ImageIO.read(new File("networking_project/water.png"));
	    } catch (IOException e) {
	    	System.out.printf("Couldnt load water.png :(\n");
	    }
	    //Draw background
	    drawG.drawImage(waterImg, 0, 0, drawWidth, drawHeight,
			     0, 0, 256, 256,
			     null);
	    //Loop thru each fish byte
	    for(int i = 0; i < Globals.fish_count; i++) {
	    	//Loop thru each bit in the byte
	    	for(int j = 0; j < 8; j++) {
	    		//Check if theres a fish at this bit
	    		if(((fishData[i] >> j) & 1) == 1) {
	    			final int dsize = Globals.fish_size;
	    			final int x = (7-j)*dsize;
	    			final int y = (i*dsize);
	    			drawG.drawImage(fishImg, x, y, x+dsize, y+dsize,
	    					     0, 0, Globals.fish_size, Globals.fish_size,
	    					     null);
	    		}
	    	}
	    }
	    
	    //Draw draw surface to the window surface, scaled to fit
	    final Rectangle windowB = g.getClipBounds();
	    g.drawImage(ds, 0, 0, windowB.width, windowB.height,
	                0, 0, drawWidth, drawHeight, null);
	    
	    drawG.dispose();
	    g.dispose();
	}
	
	public static void main(String[] args) {
		//Stuff for graphics
		Client c  = new Client();
		c.setVisible(true);
		String serverHostname = "localhost"; // The server's hostname or IP address

		try (DatagramSocket clientSocket = new DatagramSocket()) {
			InetAddress serverAddress = InetAddress.getByName(serverHostname);

			String message = "Connected";
			byte[] sendData = message.getBytes();

			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, Globals.port);
			clientSocket.send(sendPacket);
			System.out.println("Sent confirmation packet to server...");

			DatagramPacket receivePacket = new DatagramPacket(fishData, fishData.length);
			//DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length + 1);
			
			
			while(true) {
				clientSocket.receive(receivePacket); // Blocks until a packet is received
				fishData = receivePacket.getData();
				// displayFish(fishData); //Displays fish in text
				c.repaint(); //Paints graphics
			}

		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + e.getMessage());
		} catch (SocketException e) {
			System.err.println("Socket error: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("I/O error: " + e.getMessage());
		}
	}
	
	private static void displayFish(byte[] fishData) {
		//Loop thru fish
		for(int i = 0; i < Globals.fish_count; i++) {
			int fishValue = Math.abs(fishData[i]); //Get the value of the fish byte
			//Determine trailing spaces
			int trailingSpaces = (int)(Math.log(fishValue) / Math.log(2)); //Log base 2 of fishValue
			//Determine leading spaces
			int leadingSpaces = 8 - trailingSpaces;
			
			//System.out.printf("%d: %d/%d", fishValue, leadingSpaces, trailingSpaces);
			//Print leading spaces
			for(int j = 0; j < leadingSpaces; j++) {
				System.out.printf(" ");
			}
			//Print fish
			System.out.printf(">->-O");
			//Print trailing spaces
			for(int j = 0; j < trailingSpaces; j++) {
				System.out.printf(" ");
			}
			
			//Print new line
			System.out.printf("\n");
		}
	}

}
