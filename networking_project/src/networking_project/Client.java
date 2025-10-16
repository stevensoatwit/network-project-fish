package networking_project;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import networking_project.Globals;

public class Client {

	public static void main(String[] args) {
		String serverHostname = "localhost"; // The server's hostname or IP address

		try (DatagramSocket clientSocket = new DatagramSocket()) {
			InetAddress serverAddress = InetAddress.getByName(serverHostname);

			String message = "Connected";
			byte[] sendData = message.getBytes();

			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, Globals.port);
			clientSocket.send(sendPacket);
			System.out.println("Sent confirmation packet to server...");

			byte[] receiveData = new byte[1024];

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			//DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length + 1);
			
			
			while(true) {
				clientSocket.receive(receivePacket); // Blocks until a packet is received
				System.out.println("packet");
				displayFish(receivePacket.getData()); //Displays fish
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
