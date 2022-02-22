const express = require('express');
const bodyParser = require('body-parser');
const app = express();
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({extended:true}))
const cors = require('cors')
app.use(cors({origin: "http://localhost:3000"}))
const fs = require('fs')

app.post('/compile', (req, res) => {
    writeFile(req.body["code"])
    const { exec } = require("child_process")
    exec("docker build \"./cpp/\" -t ubuntu_image",() => {
        exec("docker run --rm ubuntu_image", (error, stdout, stderr) => {
            console.log(stdout)
            console.log(stderr)
            res.status(200).send(JSON.stringify({result:stdout}))
        })
    })
})

const server = app.listen(8990, () => {
    console.log("Listening to port %s", server.address().port)
})

function writeFile(code){
    fs.writeFile('./cpp/main.cpp', code, function (err){
        if(err){
            console.log(err)
        }
    })
}