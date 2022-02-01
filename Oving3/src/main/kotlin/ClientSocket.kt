import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.lang.Exception
import java.net.Socket
import java.util.*


class ClientSocket(private var PORT: Int) {
    private lateinit var connection: Socket
    private lateinit var readerConnection: InputStreamReader
    private lateinit var reader: BufferedReader
    private lateinit var writer: PrintWriter

    fun createConnection(serverIP: String): Boolean{
        try {
            connection = Socket(serverIP, PORT);
            readerConnection = InputStreamReader(connection.getInputStream())
            reader = BufferedReader(readerConnection)
            writer = PrintWriter(connection.getOutputStream(), true)
        }catch (e : Exception){
            return false;
        }
        return true;
    }

    fun disconnect(){
        connection.close()
        readerConnection.close()
        reader.close()
        writer.close()
    }

    fun getStartIntroFromServer(): String{
        val intro1 = reader.readLine()
        return (
            """
            $intro1
            """.trimIndent())
    }

    fun sendMessageToServer(message:String): String{
        writer.println(message) // sender teksten til tjeneren
        val response = reader.readLine() // mottar respons fra tjeneren
        return ("Fra tjenerprogrammet: $response")
    }

    fun getMessage():String {
        return ("Fra tjenerprogrammet: ${reader.readLine()}")
    }
}

@Throws(IOException::class)

fun main(args: Array<String>) {
    val clientReader = Scanner(System.`in`)
    print("Oppgi navnet på maskinen der tjenerprogrammet kjører: ")
    var serverIP = clientReader.nextLine()
    val client = ClientSocket(1250);

    if(client.createConnection(serverIP)){
        println("Nå er forbindelsen opprettet.")

        println(client.getStartIntroFromServer())
    }

    /* Leser tekst fra kommandovinduet (brukeren) */
    var line = " "
    while (line != "") {
        println(client.getMessage())

        line = clientReader.nextLine()

        println(client.sendMessageToServer(line))

        line = clientReader.nextLine()
        println(client.sendMessageToServer(line))

    }

    /* Lukker forbindelsen */
    client.disconnect()
}