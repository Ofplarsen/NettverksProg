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
    <!-- <textarea style="width: 200px; height: 400px; white-space: pre-wrap;" id="logs"></textarea> -->
    <canvas style=" border: black solid" onmouseup="disableDraw()" onmousedown="enableDraw()" onmousemove="sendMessage(event)" id="can"></canvas>
    <br>
    <!--<input type="text" id="message" placeholder="Message"/>
    <button onclick="sendMessage()">Send Message</button>-->
    
    <script>
      
      let ws = new WebSocket('ws://localhost:3001');
      let log = []
      let startx = -1
      let starty = -1
      let drawing = false
      let canvas =  document.getElementById('can');
      const canvasOffsetX = canvas.offsetLeft;
      const canvasOffsetY = canvas.offsetTop;

      canvas.width = 700;
      canvas.height = 700;
      
      ws.onmessage = event => {
          console.log(event.data)
          getMessage(event.data)
      }
      
      function enableDraw(){
          drawing = true
      }
      
      function disableDraw(){
          startx = -1
          starty = -1
          drawing = false
      }
      
      function getMousePos(evt) {
            let canvas =  document.getElementById('can');
            ctx = canvas.getContext("2d");
            var rect = canvas.getBoundingClientRect();
            let t = []
            
            return {
                x: Math.round(evt.clientX - canvasOffsetX),
                y: Math.round(evt.clientY)
            };
        }
      
      function sendMessage(evt){
          if(drawing)
          ws.send(getMousePos(evt).x + "," + getMousePos(evt).y)
      }
      
      function getMessage(data){
          
          const json = data.toString("utf8");
          msg = JSON.parse(json);
          let coor = msg.split(",")
          if(startx === -1 && starty === -1){
              startx = coor[0]
              starty = coor[1]
          }else{
              
          }
          draw(coor[0], coor[1])
          startx = coor[0]
          starty = coor[1]
      }
      
      function draw(x, y){
       let canvas =  document.getElementById('can');
       ctx = canvas.getContext("2d");
       
        ctx.beginPath();
        ctx.lineWidth = 5
        ctx.lineCap = "round"
        ctx.moveTo(startx, starty);
        //console.log(x,y)
        ctx.lineTo(x, y);
        ctx.stroke();
        ctx.closePath();
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
let connections = []
const wsServer = net.createServer((connection) => {
    console.log('Client connected');
    let sha = crypto.createHash("sha1")
    let magicString = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
    let serverHandshakeResponse = ""
    let clientKey = ""
    let hashKey = ""
    let base64Key = ""
    let keys = []

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
            connections.push(connection)
        }else{
            let bytes = data;
            let length = bytes[1] & 127;
            let maskStart = 2;
            let dataStart = maskStart + 4;
            let str = ""
            for (let i = dataStart; i < dataStart + length; i++) {
                let byte = bytes[i] ^ bytes[maskStart + ((i -dataStart) % 4)];
                str += String.fromCharCode(byte)
            }
            console.log(str)
            for(let i = 0; i < connections.length; i++){
                connections[i].write(createReply(str))
            }
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

function createReply(data) {
    const json = JSON.stringify(data);
    const jsonByteLength = Buffer.byteLength(json);
    const lengthByteCount = jsonByteLength < 126 ? 0 : 2;
    const payloadLength = lengthByteCount === 0 ? jsonByteLength : 126;
    //Gir de to første bytesene til type og den andre verdien
    const buffer = Buffer.alloc(2 + lengthByteCount + jsonByteLength);

    buffer.writeUInt8(0b10000001, 0);
    buffer.writeUInt8(payloadLength, 1);

    let payloadOffset = 2;
    if (lengthByteCount > 0) {
        buffer.writeUInt16BE(jsonByteLength, 2);
        payloadOffset += lengthByteCount;
    }

    buffer.write(json, payloadOffset);
    return buffer;
};
