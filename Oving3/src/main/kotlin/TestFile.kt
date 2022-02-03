import java.io.*
import java.net.Socket
import java.net.ServerSocket


object ThreadedEchoServer {
    const val PORT = 1250
    @JvmStatic
    fun main(args: Array<String>) {
        var serverSocket: ServerSocket? = null
        var socket: Socket? = null
        try {
            serverSocket = ServerSocket(PORT)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        while (true) {
            try {
                if (serverSocket != null) {
                    socket = serverSocket.accept()
                }
            } catch (e: IOException) {
                println("I/O error: $e")
            }
            // new thread for a client
            if (socket != null) {
                EchoThread(socket).start()
            }
        }
    }
}

class EchoThread(protected var socket: Socket) : Thread() {
    override fun run() {
        var inp: InputStream? = null
        var brinp: BufferedReader? = null
        var out: DataOutputStream? = null
        try {
            inp = socket.getInputStream()
            brinp = BufferedReader(InputStreamReader(inp))
            out = DataOutputStream(socket.getOutputStream())
        } catch (e: IOException) {
            return
        }
        var line: String?
        while (true) {
            try {
                line = brinp.readLine()
                if (line == null || line.equals("QUIT", ignoreCase = true)) {
                    socket.close()
                    return
                } else {
                    out.writeBytes(
                        """
                            $line
                            
                            
                            """.trimIndent()
                    )
                    out.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
        }
    }
}