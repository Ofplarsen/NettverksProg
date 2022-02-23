const express = require('express');
const bodyParser = require('body-parser');
const app = express();
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({extended:true}))
const cors = require('cors')
app.use(cors({origin: "http://localhost:8080"}))
const fs = require('fs')

app.post('/compile', (req, res) => {
    writeFile(req.body["code"])
    const { exec } = require("child_process")
    exec("docker build \"./cpp/\" -t ubuntu_image",(err,stdout, stderr) => {

        if(err){

            console.log(getError(stdout))
            res.status(200).send(JSON.stringify({result:getError(stdout)}))
        }else {
            exec("docker run --rm ubuntu_image", (error, stdout, stderr) => {
                console.log(stdout)
                console.log(stderr)
                res.status(200).send(JSON.stringify({result: stdout}))
            })
        }
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

function getError(string){
    lines = string.split("\n")
    found = false
    result = ""
    for (let i = 0; i < lines.length; i++){

        if(found){
            result += lines[i] + "\n"
        }

        if(lines[i].includes("Running in")){
            found = true
        }
    }
    return result
}