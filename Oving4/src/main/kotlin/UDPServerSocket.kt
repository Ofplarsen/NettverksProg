import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.DatagramPacket
import java.net.DatagramSocket

class UDPServerSocket : Thread() {
    private val socket: DatagramSocket = DatagramSocket(1250)
    private val running = false
    private var buf = ByteArray(256)

    override fun run(){
        var running = true

        while (running){
            buf = "Please insert two numbers (num1 num2)".toByteArray()
            var packet = DatagramPacket(buf, buf.size)
            socket.receive(packet)
            var address = packet.address
            var port = packet.port
            packet = DatagramPacket(buf, buf.size, address, port)
            socket.send(packet)


            //Part 2
            buf = ByteArray(256)
            packet = DatagramPacket(buf, buf.size)
            socket.receive(packet)
            packet = DatagramPacket(buf, buf.size, address, port)
            var received = String(packet.data, 0, packet.length)

            var numbers = listOf<String>()
            try{
                numbers = StringConverter.getNumbers(received.trim())
            }catch (e: Exception){
            }

            buf = "Would you like to add or subtract? (Write + or -)".toByteArray()
            packet = DatagramPacket(buf, buf.size, address, port)
            socket.send(packet)

            //Part 3
            buf = ByteArray(256)
            packet = DatagramPacket(buf, buf.size)
            socket.receive(packet)
            packet = DatagramPacket(buf, buf.size, address, port)
            received = String(packet.data, 0, packet.length)

            try {
                var sub = StringConverter.subOrAdd(received[0])
                buf = if(sub){
                    MathSolver.sub(numbers[0].toInt(),numbers[1].toInt()).toString().toByteArray()

                }else{
                    MathSolver.add(numbers[0].toInt(),numbers[1].toInt()).toString().toByteArray()

                }
            }catch (e: Exception){
            }
            packet = DatagramPacket(buf, buf.size, address, port)
            socket.send(packet)
        }
        socket.close()
    }
}

class StringConverter(){
    companion object {
        @Throws(IllegalArgumentException::class)
        fun getNumbers(string: String): List<String> {
            var numbers = string.split(' ');
            if(numbers.size != 2){
                throw IllegalArgumentException("Too many numbers")
            }
            if(numbers.contains(".*[a-zA-Z].*")){
                throw IllegalArgumentException("Only numbers allowed")
            }
            return numbers
        }

        @Throws(IllegalArgumentException::class)
        fun subOrAdd(char: Char): Boolean{
            if(char.equals('-')){
                return true
            }else if (char.equals('+')){
                return false
            }
            throw IllegalArgumentException("Please only enter + or -")
        }
    }
}

class MathSolver(){
    companion object {
        fun add(a: Int, b: Int): Int {
            return a + b
        }

        fun sub(a: Int, b: Int): Int {
            return a - b
        }
    }
}

fun main(args: Array<String>) {
    val server = UDPServerSocket()
    server.start()


}