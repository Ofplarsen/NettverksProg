const net = require('net');
const crypto = require('crypto')
// Simple HTTP server responds with a simple WebSocket client test
const httpServer = net.createServer((connection) => {
    connection.on('data', () => {
        let content = `<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
  </head>
  <body>
    <input type="text" id="message"/>
    <button onclick="sendMessage()">Send Message</button>
    <script>
      let ws = new WebSocket('ws://localhost:3001');
        
      ws.onmessage = event => alert('Message from server: ' + event.data.toString());
      //ws.onopen = () => ws.send('hello');
      
      function sendMessage(){
          console.log(document.getElementById("message").value)
          ws.send(document.getElementById("message").value)
      }
    </script>
  </body>
</html>
`;
        connection.write('HTTP/1.1 200 OK\r\nContent-Length: ' + content.length + '\r\n\r\n' + content);
        connection.on("data", (data) => {
            console.log("This is reciveved from server: ", data.toString())
        })
    });
});
httpServer.listen(3000, () => {
    console.log('HTTP server listening on port 3000');
});

// Incomplete WebSocket server
const wsServer = net.createServer((connection) => {
    console.log('Client connected');
    let sha = crypto.createHash("sha1")
    let magicString = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
    let serverHandshakeResponse = ""
    let clientKey = ""
    let hashKey = ""
    let base64Key = ""
    let keys = []
    let connections = []
    let handsahekComplete = false

    connection.on('data', (data) => {
        sha = crypto.createHash("sha1")
        let test = data.toString().includes("Sec-WebSocket-Key:")
        if(test) {
            const d = data.toString().split("\n")
            for (let i = 0; i < d.length; i++) {
                if (d[i].toString().includes("Sec-WebSocket-Key:")) {
                    clientKey = d[i].replace("Sec-WebSocket-Key: ", "")
                    clientKey = clientKey.replace("\r", "")
                    keys.push(clientKey)
                    break
                }
            }
            console.log(clientKey)
            let key = clientKey + magicString
            console.log(key)
            hashKey = sha.update(key)
            base64Key = sha.digest('base64')
            serverHandshakeResponse = "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Accept: " + base64Key + "\r\n\r\n"
            connection.write(serverHandshakeResponse)
            handsahekComplete = true
            connections.push(connection)
        }else{
            let bytes = data;
            let length = bytes[1] & 127;
            let maskStart = 2;
            let dataStart = maskStart + 4;
            for (let i = dataStart; i < dataStart + length; i++) {
                let byte = bytes[i] ^ bytes[maskStart + ((i -dataStart) % 4)];
                console.log(String.fromCharCode(byte));
            }
            let msg = Buffer.from([0x81, 0x0b, 68]);
            msg = msg
            connection.write(msg)
        }

    });




    connection.on('end', () => {
        console.log('Client disconnected');
    });
});
wsServer.on('error', (error) => {
    console.error('Error: ', error);
});
wsServer.listen(3001, () => {
    console.log('WebSocket server listening on port 3001');
});
