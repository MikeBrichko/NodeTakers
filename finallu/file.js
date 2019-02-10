//document.getElementById("connected").style.display = "none";
console.log("javascript connected");

// var doc = document.getElementById('doc');
// doc.contentEditable = true;
// doc.focus();

let session;
var queue = new Queue();

var quillText = quill.getText();


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
    console.log(message);
    var object = new TextDecoder("utf-8").decode(message);
    console.log(object);
    var newObject=null;
    try{
      newObject=JSON.parse(object);
      console.log("try working")
      if(newObject.char==8){
        console.log("backspace called");
        quill.deleteText(newObject.index-1, 1, 'api');
        console.log("backspace done");
      }
      else{
        quill.insertText(newObject.index,newObject.char);
      }
    }
    catch(e){
      console.log("in the catch")
      newObject=object;
      let length = quill.getLength();
      quill.insertText(length,newObject);
    }
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
$('#editor').on("keydown", function(e){


  console.log("keydown");
    if( e.which == 8 ){ // 8 == backspace
      var range = quill.getSelection();
      var toPass = JSON.stringify({'char':e.which,'index':range.index,'replaceAmount':0});
      queue.add_function(function(){
        publish('typing', toPass);
      });
      e.preventDefault();
      e.stopImmediatePropagation();
      e.stopPropagation();
    
    }
});

$('#editor').keypress(function(event){
  event.handled = true;
  console.log(event.target.innerText.substring(event.target.innerText.length-1));
  var range = quill.getSelection();
  console.log(range);
  var toPass = JSON.stringify({'char':String.fromCharCode(event.which),'index':range.index,'replaceAmount':range.length});
  console.log(toPass);
  queue.add_function(function(){
    publish('typing', toPass);
  });
  quillText = quill.getText();
  return false;
})

subscribe('typing');

