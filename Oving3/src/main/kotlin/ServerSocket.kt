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
        server = SS(PORT)
    }

    constructor(server: Socket) : this() {
        this.connection = server
    }
    private var PORT: Int = 0
    private lateinit var server: SS
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

    fun acceptConnection(): Socket{
        connection = server.accept();
        return connection;
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
fun main(args: Array<String>) {

    var socket: Socket ?= null
    var mainServerSocket = ServerSocket(1250)
    println("Logg for tjenersiden. Nå venter vi...")
    while (true){
        try {
            socket =  mainServerSocket.acceptConnection()
        }catch (e: Exception){
            println("Error: " + e.message)
        }

        socket?.let { ServerSocket(it) }?.let { SocketThread(it) }?.start()
    }

}