package networking_project;

import java.io.IOException;
import java.util.Date;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server {

	public static void main(String[] args) {
		// Keep track of seconds, sends new packet every second
		long last_packet_time = new Date().getTime()/1000;
		
        try (DatagramSocket serverSocket = new DatagramSocket(Globals.port)) {
            System.out.println("Fish Server started on port " + Globals.port);

            byte[] receiveData = new byte[1024];

            //Start by waiting for the connection confirmation packet
            DatagramPacket confirmPacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(confirmPacket); // Blocks until a packet is received
            
            byte[] fishData = { (byte) 0b10000000,
            					(byte) 0b00010000,
            					(byte) 0b01000000,
            					(byte) 0b00000100,
            					(byte) 0b00010000,
            					(byte) 0b00000001,
            					(byte) 0b01000000,
            					(byte) 0b00000100
            				}; //This needs to be seeded with some set of initial values
            
            while (true) {
            	//Wait 1 second
                while(last_packet_time + 1 > new Date().getTime()/1000) {
                }
                last_packet_time = new Date().getTime()/1000;
                
                // Move all the fish
                for(int i = 0; i < Globals.fish_count; i++) {
                	//Check if fish is at right edge
                	if(fishData[i] == (byte) 0b00000001) {
                		//Wrap around manually
                		fishData[i] = (byte) 0b10000000;
                	}
                	//If not at right edge
                	else { fishData[i] = (byte)(Byte.toUnsignedInt(fishData[i]) >>> 1); } //There is no unsigned 8 bit integers in java, so shifting is a pain, in fact working with bytes is a pain in general
                }
                

                // Send new fish data to the client
                DatagramPacket newFishPacket = new DatagramPacket(fishData, fishData.length,
                		confirmPacket.getAddress(), confirmPacket.getPort());
                serverSocket.send(newFishPacket);
            }
        } catch (SocketException e) {
            System.err.println("Socket error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
	}

}
