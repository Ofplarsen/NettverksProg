import java.lang.Exception
import java.net.Socket
import java.util.*

// Task 1
class SocketThread(val startClient: ServerSocket) : Thread() {

    override fun run(){
        val server = startClient

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
    }
}