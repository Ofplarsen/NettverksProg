import java.net.DatagramPacket;

public class TestClass {

    synchronized public static String testMethod(DatagramPacket d){
        return new String(d.getData(), 0, d.getLength());
    }
}
