//queue class
class Queue {

    constructor(){
      this.running = false;
  
      this.queue = [];
    };
  
    add_function(callback) { 
        var _this = this;
        //add callback to the queue
        this.queue.push(function(){
            var finished = callback();
            if(typeof finished === "undefined" || finished) {
               //  if callback returns `false`, then you have to 
               //  call `next` somewhere in the callback
               _this.next();
            }
        });
  
        if(!this.running) {
            // if nothing is running, then start the engines!
            this.next();
        }
  
        return this; // for chaining fun!
    }
  
    next(){
        this.running = false;
        //get the first element off the queue
        var shift = this.queue.shift(); 
        if(shift) { 
            this.running = true;
            shift(); 
        }
      }
    };