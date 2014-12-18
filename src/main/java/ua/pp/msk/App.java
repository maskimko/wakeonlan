package ua.pp.msk;
import java.util.Arrays;

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

    public static void main( String[] args )
    {
        System.out.println( "WakeOnLan java utility" );

	App app = new App();
	try{
		byte[] bts = app.getBytesFromString("aa:bb:cc:dd:ee:ff");
		System.out.println(Arrays.toString(bts));
	} catch (IllegalArgumentException aex) {
		System.err.println("Error: " + aex.getMessage());
	}
    }
}
