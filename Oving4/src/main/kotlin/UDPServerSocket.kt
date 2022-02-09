import java.net.DatagramPacket
import java.net.DatagramSocket

class UDPServerSocket : Thread() {
    private val socket: DatagramSocket = DatagramSocket(1250)
    private val running = false
    private var buf = ByteArray(256)

    override fun run(){
        var running = true

        while (running){
            buf = ByteArray(256)
            var packet = DatagramPacket(buf, buf.size)
            socket.receive(packet)

            var address = packet.address
            var port = packet.port
            packet = DatagramPacket(buf, buf.size, address, port)
            var received = String(packet.data, 0, packet.length)

            if(received == "end"){
                running = false
                continue
            }
            socket.send(packet)
        }
        socket.close()
    }
}

fun main(args: Array<String>) {
    val server = UDPServerSocket()
    server.start()


}