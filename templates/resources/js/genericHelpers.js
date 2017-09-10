if (!String.prototype.trim) {
 String.prototype.trim=function(){return this.replace(/^\s\s*/, '').replace(/\s\s*$/, '');};
}
if (!String.prototype.ltrim) {
 String.prototype.ltrim=function(){return this.replace(/^\s+/,'');}
}
if (!String.prototype.rtrim) {
 String.prototype.rtrim=function(){return this.replace(/\s+$/,'');}
}
if (!String.prototype.fulltrim) {
 String.prototype.fulltrim=function(){return this.replace(/(?:(?:^|\n)\s+|\s+(?:$|\n))/g,'').replace(/\s+/g,' ');}
}

var isEventSupported = (function(){
  var TAGNAMES = {
    'select':'input','change':'input',
    'submit':'form','reset':'form',
    'error':'img','load':'img','abort':'img',
    'transitionend':'div','webkitTransitionEnd':'div'
  }
  function isEventSupported(eventName) {
    var el = document.createElement(TAGNAMES[eventName] || 'div');
    eventName = 'on' + eventName;
    var isSupported = (eventName in el);
    if (!isSupported) {
      el.setAttribute(eventName, 'return;');
      isSupported = typeof el[eventName] == 'function';
    }
    el = null;
    return isSupported;
  }
  return isEventSupported;
})();


function type(x) {
 // e.g.  type(/asdf/g) --> "[object RegExp]"
 return Object.prototype.toString.call(x);
}

function zip(arrays) {
 return arrays[0].map(function(_,i){
  return arrays.map(function(array){return array[i]})
 });
}

function allCompareEqual(array) {
 // e.g.  allCompareEqual([2,2,2,2]) --> true
 // does not work with nested arrays or objects
 return array.every(function(x){return x==array[0]});
}

function isArray(x){ return type(x)==type([]) }
function isObject(x){ return type(x)==type({}) }
function getLength(x){ return x.length }
function allTrue(array){ return array.reduce(function(a,b){return a&&b},true) }

function allDeepEqual(things) {
    // works with nested arrays
    if( things.every(isArray) )
     return allCompareEqual(things.map(getLength))     // all arrays of same length
      && allTrue(zip(things).map(allDeepEqual)); // elements recursively equal
    else
     return allCompareEqual(things);
}


function objectsEqual(a,b) {
 if (type(a) === type(b)) {
  if (isObject(a) && isObject(b)) {
   for(var i in a) {
    if (!b.hasOwnProperty(i)) {
     return false;
    }
    if (!objectsEqual(a[i],b[i])) {
     return false;
    }
   } return true;
  } else if (a === b) {
   return true;
  }
 } return false;
}

function microtime (get_as_float) {
  // Returns either a string or a float containing the current time in seconds and microseconds  
  // 
  // version: 1109.2015
  // discuss at: http://phpjs.org/functions/microtime
  // +   original by: Paulo Freitas
  // *     example 1: timeStamp = microtime(true);
  // *     results 1: timeStamp > 1000000000 && timeStamp < 2000000000
  var now = new Date().getTime() / 1000;
  var s = parseInt(now, 10);
  return (get_as_float) ? now : (Math.round((now - s) * 1000) / 1000) + ' ' + s;
}

function rand(min, max) {
 var argc = arguments.length;
 if (argc === 0) {
  min = 0;
  max = 2147483647;
 } else if (argc === 1) {
  throw new Error('Warning: rand() expects exactly 2 parameters, 1 given');
 }
 return Math.floor(Math.random() * (max - min + 1)) + min;
}

function getDropDownValue(element) {
 var efX = document.getElementById(element);
 if (efX.selectedIndex < 0) {
  efX.selectedIndex = 0;
 }
 return efX.options[efX.selectedIndex].value;
}

function setDropDownValue(element, value) {
 var efX = document.getElementById(element);
 for(var index = 0; index < efX.options.length; index++) {  
  if(efX.options[index].value == value) {
   efX.selectedIndex = index;
   return true;
  }
 }
 return false;
}

function removeClassHolders(className) {
 var elements = document.getElementsByClassName(className);
 for (var ic=0;ic<elements.length;ic++) {
  var child = elements[ic];
  var parent = child.parentNode;
  parent.removeChild(child);
 }
}

function removeElement(elem) {
 if ((typeof(elem)!='undefined')&&(elem!=null)) {
 var child = elem;
 var parent = child.parentNode;
 parent.removeChild(child);
 }
}

function pollHashChange(hash,callback) {
 document.getElementById('content').innerHTML = hash;
 if (hash != window.location.hash) {
  callback();
 }
 var newHash = window.location.hash;
 setTimeout(function(){pollHashChange(newHash,callback);},100);
}

function hasClass(el, name) {
 return new RegExp('(\\s|^)'+name.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&")+'(\\s|$)').test(el.className);
}

function addClass(el, name) {
 if (!hasClass(el, name)) { el.className += (el.className ? ' ' : '') +name; }
}

function removeClass(el, name) {
 if (hasClass(el, name)) {
  el.className=el.className.replace(new RegExp('(\\s|^)'+name+'(\\s|$)'),' ').replace(/^\s+|\s+$/g, '');
 }
}

function toggleClass(el, name) {
 if (hasClass(el, name)) {
  removeClass(el, name);
 } else {
  addClass(el, name);
 }
}



function elementSupportsAttribute(element,attribute) {
 var test = document.createElement(element);
 if (attribute in test) {
  return true;
 } else {
  return false;
 }
}

function inputSupportsType(test) {
 var input = document.createElement('input');
 input.setAttribute('type',test); 
 if (input.type == 'text') {
  return false;
 } else {
  return true;
 }
}

function elementVisible( ele ){
 while (ele.nodeName!='HTML') {
  if(ele.style.display == 'none' || ele.style.visibility == 'hidden' ){
   return false;
  } ele = ele.parentElement;
 } return true;
}

function setCookie(cname,cvalue,exdays) {
 if (typeof(exdays) == 'undefined') {
  var expires = "";
 } else {
  var d = new Date();
  d.setTime(d.getTime()+(exdays*24*60*60*1000));
  var expires = "; expires="+d.toGMTString();
 } document.cookie = cname + "=" + cvalue + expires;
}

function getCookie(cname, alt) {
 var name = cname + "=";
 var ca = document.cookie.split(';');
 for(var i=0; i<ca.length; i++) {
   var c = ca[i].trim();
   if (c.indexOf(name)==0) return c.substring(name.length,c.length);
 }
 if (typeof(alt) == "undefined") {
  return null;
 } else {
  return alt;
 }
}

var absolutePath = function(href) {
 var link = document.createElement("a");
 link.href = href;
 return (link.protocol+"//"+link.host+link.pathname+link.search+link.hash);
}

var ucfirst = function(str) {
 if (str.length > 0) {
  return str.charAt(0).toUpperCase() + str.substr(1);
 } return str;
}

var allClassElements = function(className, callback) {
 if (typeof(callback) != "function") {
  var callback = function() { };
 }
 if (typeof(className) == "string") {
  var classElements = document.getElementsByClassName(className);
  for(var i=0; i<classElements.length; i++) {
   callback(classElements[i]);
  }
 } else if (className instanceof Array) {
  for(var i=0; i<className.length; i++) {
   allClassElements(className[i], callback);
  }
 }
}
