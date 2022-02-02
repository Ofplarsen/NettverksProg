import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.Socket
import java.net.ServerSocket as SS


class ServerSocket() {

    constructor(PORT: Int) : this() {
        this.PORT = PORT
    }

    constructor(server: SS) : this() {
        this.server = server
    }
    private var PORT: Int = 0
    private var server = SS(PORT)
    private lateinit var connection: Socket
    private lateinit var readerConnection: InputStreamReader
    private lateinit var reader: BufferedReader
    private lateinit var writer: PrintWriter

    fun openConnection(): Boolean{
        try {

            readerConnection = InputStreamReader(connection.getInputStream())
            reader = BufferedReader(readerConnection)
            writer = PrintWriter(connection.getOutputStream(), true)
        }catch (e : Exception){
            println(e.message)
            return false;
        }
        return true;
    }

    fun acceptConnection(): Boolean{
        connection = server.accept();
        return true;
    }

    fun sendMessage(message: String){
        writer.println(message)
    }

    fun readLine(): String{
        return reader.readLine();
    }

    fun closeConnection(){
        readerConnection.close()
        reader.close()
        writer.close()
    }
    fun getServer(): java.net.ServerSocket {
        return server
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

@Throws(IOException::class)
@Synchronized fun main(args: Array<String>) {
    /*
    val server = ServerSocket(1250)

    println("Logg for tjenersiden. Nå venter vi...")

    if(server.acceptConnection() && server.openConnection()){
        server.sendMessage("Hei, du har kontakt med tjenersiden!")
    }

    /* Mottar data fra klienten */
    var line = "  "// mottar en linje med tekst
    while (line != null) {  // forbindelsen på klientsiden er lukket
        server.sendMessage("Write two numbers you want to add or subtract (use single space between them)")

        line = server.readLine()

        var numbers = listOf<String>()
        try{
            numbers = StringConverter.getNumbers(line.trim())
        }catch (e: Exception){
            server.sendMessage("Error in code: $line. ${e.message}")
        }

        server.sendMessage("Would you like to add or subtract? (Write + or -)")

        line = server.readLine()

        try {
            var sub = StringConverter.subOrAdd(line[0])
            if(sub){
                server.sendMessage(MathSolver.sub(numbers[0].toInt(),numbers[1].toInt()).toString())
            }else{
                server.sendMessage(MathSolver.add(numbers[0].toInt(),numbers[1].toInt()).toString())
            }
        }catch (e: Exception){
            server.sendMessage("Error in code: $line. ${e.message}")
        }

    }

    server.closeConnection()
     */

    var mainServerSocket = ServerSocket(1250)
    while (true){
        var socket: java.net.ServerSocket
        if(mainServerSocket.acceptConnection()){
            socket = mainServerSocket.getServer()
        }else{
            break;
        }

        SocketThread(ServerSocket(socket)).run()
    }

}