/* FSX Simple AJAX
 * Trying to understand why JavaScript just does things the way it does
 * One line at a time
 *
 * XHR Guide
 * 0      The request is not initialized
 * 1      The request has been set up
 * 2      The request has been sent
 * 3      The request is in process
 * 4      The request is complete
 *
 */

function bytesToHigher(byteVal) {
  var unitStr = ' KMGTPPEZY';
  for (var unit = 0; unit < unitStr.length; unit++) {
    if (byteVal<1024) {
      break;
    }
    byteVal = byteVal / 1024;
  }
  byteVal = Math.round(byteVal*Math.pow(10,2))/Math.pow(10,2);
  return byteVal.toString() + unitStr.charAt(unit) + 'B';
}

function ajaxRequest(query, callback) {

  // User Defined
  this.query = query;
  this.callback = function() {};
  if (typeof(callback) !== "undefined") {
    this.callback = callback;
  }
  this.authenticationCallback = function() {};
  this.callMethod = 'GET';
  this.formData = null;
  this.rawMode = false;
  this.opened = false;
  this.done = false;
  this.aborted = false;
  this.cacheSize = 0;
  this.acceptType = null;
  this.objectList = {};
  
  this.cachedResults = {};
  
  this.accept = function(stringType) {
    this.acceptType = stringType;
    return this;
  }

  this.POST = function() {
    this.callMethod = 'POST'; return this;
  }

  this.GET = function() {
    this.callMethod = 'GET'; return this;
  }
  
  this.handleErrors = function(callable) {
    this.errorHandler = callable || function() {}; return this;
  }
    
  // Legacy forms
  this.legBoundary = Math.random().toString().substr(2);
  this.formLegacyMode = false;
  
  this.setupXHTTP = function() {
    if (window.XMLHttpRequest) {
      this.aborted = false;
      this.opened = false;
      this.xhttp = new XMLHttpRequest();
      var that = this;
      this.xhttp.onreadystatechange=function() {
        if ((typeof(that) == 'undefined') || (that == null)) {
          return;
        }
        that.readyState = that.xhttp.readyState;
        if (that.xhttp.readyState == 4) {
          if (that.xhttp.status == 200) {
            that.cacheSize += byteCount(that.xhttp.responseText);
            if (that.rawMode == false) {
              that.parseResponse(that.xhttp.responseText,that.xhttp);
            } else {
              that.callback(that.xhttp.responseText);    
            }
          } else {
            that.statusCode = that.xhttp.status;
            that.errorHandler(that.xhttp);
          }
        }
      }
      //this.xhttp.open(this.callMethod,this.query,true);
    } else {// Nothing to do
      this.alertHandler("No XMLHttpRequest present, upgrade your browser...");
    }
  }

  if (!this.callback) {
    this.callback = function(response){alert((new XMLSerializer()).serializeToString(response));};
  }
  
  // Override for jQuery Events, etc...
  this.alertHandler = function(message){alert(message);};
  this.errorHandler = function(){};

  //  Functions...  // 
  // Sends Queries
  this.send = function(mode, content) {
    if (typeof(mode) == "undefined") {
      mode = "FORM";
    }
    var hash = this.getRequestHash();
    if ((hash) && (typeof(this.cachedResults[hash]) != 'undefined')) {
      return this.parseResponse(this.cachedResults[hash]);
    } else {
      if (!this.query) { // Nothing to do
        this.alertHandler("No query specified.");
        return;
      } else  {
        this.setupXHTTP();
        var attachments = this.compileAttachments();
        // Post the response if nothing else to do.  
        if ((this.opened == false) && (this.aborted == false)) {
          this.xhttp.open(this.callMethod,this.query,true);
          if (this.acceptType != null) {
            this.xhttp.setRequestHeader('Accept', this.acceptType);
          }
        }
        switch(mode) {
          case "JSON":
            if (this.acceptType != null) {
              this.xhttp.setRequestHeader('Content-Type', "application/json");
            }
            var content = content || JSON.stringify(this.objectList);
            this.xhttp.send(content);
            break;
          case "RAW":
            this.rawMode = true;
            if (this.callMethod == "GET") {
              this.xhttp.send();
            } else {
              this.xhttp.send(attachments);
            }
            break;
          case "FORM":
          default:
            this.xhttp.send(attachments);
            break;
        }

      }
    } return this;
  }
  
  this.abort = function() {
    if (typeof(this.xhttp) != 'undefined') {
      this.aborted = true;
      this.xhttp.abort();
    }
  }
  this.reset = function() {
    this.callMethod = 'GET';
    this.formData = null;
    this.rawMode = false;
    this.opened = false;
    this.done = false;
    this.aborted = false;
    this.objectList = {};
    delete this.formData;
    delete this.xhttp;
    delete this.multipart;
    delete this.formLegacyMode;
    this.setupXHTTP();
  }
  
  this.setErrorHandler = function(callback) {
    this.errorHandler = callback;
  }

  this.set = function(objectName, objectValue) {
    // Don't attach if aborted...
    console.log("Attaching " + objectName);
    if (this.aborted == true) {
      console.log("Aborted! Stop!");
      return this;
    }
    this.objectList[objectName] = objectValue;
    return this;
  }
  
  this.compileAttachments = function() {
    for (var objectName in this.objectList) {
      var objectValue = this.objectList[objectName];
      // Old way of attaching (Multipart/form)
      if (!window.FormData) {
        this.legacyAttach(objectName, objectValue);
      } else {
        // New way of attaching (FormData)
        this.formLegacyMode = false;
        if (this.callMethod !== 'POST') {
          this.callMethod = 'POST';
        }
        if (this.formData == null) {
          this.formData = new FormData();
        }
        this.formData.append(objectName, JSON.stringify(objectValue));
      }
    }

    if (this.formLegacyMode == true) {
     return this.legacyFormData();
    } else {
     return this.formData;
   }
  }
  
  this.legacyAttach = function(objectName, objectValue) {
    this.formLegacyMode = true;

    if (this.callMethod != 'POST') {
      this.callMethod = 'POST';
    }

    if ((this.opened == false) && (this.aborted == false)) {
      this.xhttp.open(this.callMethod,this.query,true);
      this.xhttp.setRequestHeader("Content-Type","multipart/form-data;charset=utf-8;boundary="+this.legBoundary);
      this.opened = true;
    }

    if (typeof(this.multipart)=='undefined') {
      this.multipart = [];
    }
    this.multipart[objectName] = JSON.stringify(objectValue);    
  }
  
  this.legacyFormData = function() {   
    var multi = '';
    for(var key in this.multipart) {
      multi+='--'+this.legBoundary+"\nContent-Disposition: form-data; name=\""+key+"\"\n\n"+this.multipart[key]+"\n"; //"\r\ncontent-type: application/octet-stream\r\n\r\n"+this.multipart[key]+"\r\n";
    }
    return multi;
  }
  
  this.getRequestHash = function() {
    if (typeof(CryptoJS) != "undefined") {
     var hash = CryptoJS.SHA256(JSON.stringify(this.objectList));
    } else {
      var hash ="UNIMPL";
    }
    return hash;
  }

  // Parses complete responses
  this.parseResponse = function(responseText, addl) {  
    if ((typeof(responseText) == 'undefined') || (this == null) || (responseText == null)) {
      return;
    }

    var that = this;
    
    this.objects = JSON.parse(responseText);    
    this.callback(that.objects);
    
  }

  // Parses complete responses
  this.parse = function(responseText, addl) {  
    //console.log(responseXML);
    if ((typeof(responseText) == 'undefined') || (this == null) || (responseText == null)) {
      return responseText;
    }
    var that = this;
    this.objects = JSON.parse(responseText);    
    return that.objects;
  }
}

function byteCount(s) {
 return encodeURI(s).split(/%..|./).length - 1;
}