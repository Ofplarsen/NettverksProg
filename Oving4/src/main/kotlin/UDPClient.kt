
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.*


class UDPClient() {
    private val host = "localhost"
    private val socket = DatagramSocket()
    private val address = InetAddress.getByName(host)

    private var buf: ByteArray? = null

    fun send(msg: String): String{
        buf = msg.toByteArray()
        var packet = DatagramPacket(buf, buf!!.size, address, 1250)
        socket.send(packet)
        packet = DatagramPacket(buf, buf!!.size)
        socket.receive(packet)
        var received = String(packet.data, 0, packet.length)
        return received
    }

    fun close(){
        socket.close()
    }
}

fun main(args: Array<String>) {
    /*
    val client = UDPClient()
    var test = client.send("Hello")
    println(test)
    test = client.send("Server is working")
    println(test)
    client.send("end")
    client.close()

     */

    val clientReader = Scanner(System.`in`)
    val client = UDPClient();
    var line = ""
    while (line != "") {
        println(client.getMessage())

        line = clientReader.nextLine()

        println(client.sendMessageToServer(line))

        line = clientReader.nextLine()
        println(client.sendMessageToServer(line))

    }

}