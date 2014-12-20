package ua.pp.msk;

import java.io.*;
import java.net.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class WakeOnLan {

    public static int PORT = 9;
    public static int ALTERNATIVE_PORT = 7;
    public static String BROADCASTADDR = "255.255.255.255";

    private byte[] getBytesFromString(String macAddr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macAddr.split("\\:|\\-");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Mac Address must contain 6 bytes separeted by colon or dash");
        }
        try {
            for (int i = 0; i < 6; i++) {
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

    public byte[] getMagic(byte[] bts) throws IllegalArgumentException {
        byte[] wakeUp = new byte[102];
        for (int i = 0; i < 6; i++) {
            wakeUp[i] = (byte) 0xff;
        }
        //This was substituted by System.arraycopy assuming it is more efficient
//		for (int i = 6; i < wakeUp.length; i++ ){
//			wakeUp[i] = bts[i % 6];
//		}
        System.out.println("bts len" + bts.length);
        System.out.println("wakeUp len" + wakeUp.length);
        for (int i = 6; i < wakeUp.length; i += bts.length) {
            System.arraycopy(bts, 0, wakeUp, i, bts.length);
        }
        return wakeUp;
    }

    public void wakeOnLan(InetAddress ip, byte[] macBytes) throws IOException {

        byte[] magic = getMagic(macBytes);

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            DatagramPacket dPack = new DatagramPacket(magic, magic.length, ip, PORT);
            socket.send(dPack);
            System.out.println("WakeOnLan packet sent");
        } catch (IOException e) {
            throw new IOException("Failed to sent datagram packet.", e);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    public void wakeOnLan(InetAddress ip, String macAddr) throws IllegalArgumentException, IOException {
        wakeOnLan(ip, getBytesFromString(macAddr));
    }

    public static void main(String[] args) {
        System.out.println("WakeOnLan java utility");
        System.out.println("Usage: java -jar wakeonlan-<ver>.jar  <MAC address> [host or broadcast address]");
        String addrString = BROADCASTADDR;
        InetAddress ip;
        WakeOnLan app = new WakeOnLan();
        Options cmdOpts = new Options();
        //TODO move description to a resource bundle to make the application localizable
        cmdOpts.addOption("H", "host", true, "IP address to send a wake on lan magic package");
        cmdOpts.addOption("M", "mac", true, "MAC address to send a magic packet");
        cmdOpts.addOption("h", "help", false, "Show help information");
        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cl = parser.parse(cmdOpts, args);
            if (cl.hasOption("help")) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("Usage: java -jar wakeonlan-<version>.jar <options>", cmdOpts);
                System.exit(0);
            }
            if (!cl.hasOption("mac")) {
                Logger.getLogger(WakeOnLan.class.getName()).error("Error: You must specify mac address. Use -h option to get usage info.");
                System.exit(1);
            } else {
                if (cl.hasOption("host")) {
                    addrString = cl.getOptionValue("host");
                }
                //Perhaps I should do a set log level check before writing to debug
                Logger.getLogger(WakeOnLan.class.getName()).debug("Setting destination IP address to " + addrString);
                ip = InetAddress.getByName(addrString);
                String macString = cl.getOptionValue("mac");
                Logger.getLogger(WakeOnLan.class.getName()).debug("Setting destination MAC address to " + macString);
                app.wakeOnLan(ip, macString);
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(WakeOnLan.class.getName()).error("Cannot convert String " + addrString + " to InetAddress", ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(WakeOnLan.class.getName()).error("Error:", ex);
        } catch (IOException ex) {
            Logger.getLogger(WakeOnLan.class.getName()).error("Error:", ex);
        } catch (ParseException ex) {
            Logger.getLogger(WakeOnLan.class.getName()).error("Cannot parse options", ex);
        }

    }
}
