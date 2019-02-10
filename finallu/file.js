//document.getElementById("connected").style.display = "none";
console.log("javascript connected");

var doc = document.getElementById('doc');
doc.contentEditable = true;
doc.focus();

let session;

function connect(url, username, password) {
  session = mqtt.connect(url, {
    username: username,
    password: password,
  });
  
  session.on('connect', () => {
    //document.getElementById("connecting").style.display = "none";
    //document.getElementById("connected").style.display = "block";
  });
  
  session.on('message', (topic, message) => {
    let table = document.getElementById("received");
    let row = table.insertRow();
    let topicCell = row.insertCell(0);
    let messageCell = row.insertCell(1);
    topicCell.innerHTML = topic;
    messageCell.innerHTML = message;
  });
  
  return false;
}

function publish(topic, message) {
  session.publish(topic, message);
  return false;
}

function subscribe(topic) {
  session.subscribe(topic);
  return false;
}

connect("wss://mr4b11zr953.messaging.mymaas.net:8443", "solace-cloud-client", "ucaltv4mc6q3kd2qfbibv0bpet");