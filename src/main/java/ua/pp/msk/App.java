package ua.pp.msk;
import java.util.Arrays;
import java.io.*;
import java.net.*;

/**
 * Hello world!
 *
 */
public class App 
{
 public static int PORT = 9;
 public static int ALTERNATIVE_PORT=7;

 private byte[] getBytesFromString(String macAddr) throws IllegalArgumentException{
 	byte[] bytes = new byte[6];
	 String[] hex = macAddr.split("\\:|\\-");
	if (hex.length != 6){
		throw new IllegalArgumentException("Mac Address must contain 6 bytes separeted by colon or dash");
	}
	try {
	for (int i=0; i < 6; i++){
		bytes[i] = (byte) Integer.parseInt(hex[i], 16);
	}
	} catch (NumberFormatException nex) {
		throw new IllegalArgumentException("Cannot parse hexademical number. Illegal character.");
	}
	return bytes;
 }

 public byte[] getMagic(String macAddr) throws IllegalArgumentException {
		byte[] bts = getBytesFromString(macAddr);
 	return getMagic(bts);
 }
public byte[] getMagic(byte[] bts) throws IllegalArgumentException{
	byte[] wakeUp = new byte[102];
		for (int i = 0; i < 6; i++) {
			wakeUp[i]=(byte)0xff;
		}
		for (int i = 6; i < wakeUp.length; i += 6){
			wakeUp[i] = bts[i % 6];
		}
 	return wakeUp;
}

 public void wakeOnLan(InetAddress ip, byte[] macBytes) throws IllegalArgumentException{
        
	byte[] magic = getMagic(macBytes);
	
	DatagramSocket socket = null;
	try { socket = new DatagramSocket();
	DatagramPacket dPack = new DatagramPacket(magic, magic.length, ip, PORT);
	socket.send(dPack);
	System.out.println("WakeOnLan packet sent");
	}catch (Exception e) {
		//FIXME
		throw new IllegalArgumentException("Failed to sent datagram packet.", e);
	} finally {
		socket.close();
	}
 }

 public void wakeOnLan(InetAddress ip, String macAddr) throws IllegalArgumentException{
	 wakeOnLan(ip, getMagic(macAddr));
 }
    public static void main( String[] args )
    {
        System.out.println( "WakeOnLan java utility" );
	System.out.println( "Usage: java -jar wakeonlan-<ver>.jar  <MAC address> [host or broadcast address]");

	App app = new App();
	try {
		InetAddress ip = InetAddress.getByName("172.19.2.255");
		app.wakeOnLan(ip, "50:e5:49:39:c0:bc");
    	} catch (Exception e) {
		System.err.println("Error: " + e.getMessage());
	}

    }
}
