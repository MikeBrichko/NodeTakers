//document.getElementById("connected").style.display = "none";
console.log("javascript connected");

// var doc = document.getElementById('doc');
// doc.contentEditable = true;
// doc.focus();

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
    // console.log(message);
    // var object = new TextDecoder("utf-8").decode(message);
    // console.log(object);
    // if(!typeof(object)=='string'){
    //   // object = JSON.parse(object);
    //   // quill.insertText(object.index,object.char+"");
    // }
    // else{
    //   // console.log("iciii2")
    //   // let length = quill.getLength();
    //   // console.log("iciiiiiiiii");
    //   // console.log(length);
    //   // console.log(object);
    //   // quill.insertText(length, object);
    // }
    // console.log(object);

    // topicCell.innerHTML = topic;
    // messageCell.innerHTML = message;
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

$('#editor').keypress(function(event){
  console.log(event.target.innerText.substring(event.target.innerText.length-1));
  var range = quill.getSelection();
  console.log(range);
  // var string = String.fromCharCode(event.which) + "hello";
  // console.log(string);
  var toPass = JSON.stringify({'char':String.fromCharCode(event.which),'index':range.index,'replaceAmount':range.length});
  console.log(toPass);
  publish('typing', toPass );
})

// "{'char':"+String.fromCharCode(event.which)+",'index':"+ range.index+", 'replaceAmount': "+range.length+"}"