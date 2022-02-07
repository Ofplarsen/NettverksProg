import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket

fun main(){
    val PORT = 1250
    val server = ServerSocket(PORT)

    val socket = server.accept()
    val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    val writer = PrintWriter(socket.getOutputStream(), true)

    var headers = ""
    var line = reader.readLine()
    while (line != "") {
        headers += "<li>$line</li>\n"
        line = reader.readLine()
    }
    writer.println("HTTP/1.0 200 OK")
    writer.println("Content-Type: text/html; charset=utf-8")
    writer.println("")
    writer.println("<!DOCTYPE html><html><body>")
    writer.println("<h1> Welcome! </h1>")
    writer.println("<ul>")
    writer.println(headers)
    writer.println("</ul>")
    writer.println("</body></html>")
    writer.flush()

    server.close()
}