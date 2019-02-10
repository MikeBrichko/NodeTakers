const express = require('express')
const app = express()
const port = 8080
var mqtt = require('mqtt')
var bodyParser = require('body-parser')

app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());
let session;

function connect(url, username, password) {
  session = mqtt.connect(url, {
    username: username,
    password: password,
  });
  
  session.on('connect', () => {
    //document.getElementById("connecting").style.display = "none";
    //document.getElementById("connected").style.display = "block";
    console.log("connection establiched")
  });
  
  return false;
}

function publish(topic, message) {
  session.publish(topic, message);
  return false;
}

connect("wss://mr4b11zr953.messaging.mymaas.net:8443", "solace-cloud-client", "ucaltv4mc6q3kd2qfbibv0bpet");

app.post('/postPic', (req, res) => {
    console.log(req.body);
    publish(req.body.topic, req.body.message);
    res.json({ response: 'true' })
    res.send();
})

app.listen(port, () => console.log(`Example app listening on port ${port}!`))