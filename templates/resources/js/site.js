function startup() {
  setEvents();
  setRestoreHash();
  showCanvas();
}

function hideLoadingIcon() {
  var loadingIcon = document.getElementById("loadingIcon");
  loadingIcon.addEventListener("transitionend", function(){
    this.removeEventListener("transitionend", arguments.callee);
    addClass(this, "gone");
  }); addClass(loadingIcon, "hidden");
}

function showLoadingIcon() {
  var loadingIcon = document.getElementById("loadingIcon");
  removeClass(loadingIcon, "gone");
  removeClass(loadingIcon, "hidden");
}


function showCanvas() {
  addClass(document.getElementById("canvas"), "visible");
  hideLoadingIcon();
}

function hideCanvas() {
  removeClass(document.getElementById("canvas"), "visible");
}

function clearSearch() {
 document.getElementById("searchBarInput").value = '';
 window.location.hash = '';
 document.getElementById("searchPane").innerHTML = '';
}

function setEvents() {
  document.getElementById("searchBarInput").addEventListener("keyup", function(evt) {
    var evt = evt || window.event;
    var target = evt.target;
    if (!(target.value == '')) {
      if ((evt.keyCode == 13)){
        sendSearch(target.value);
        target.blur();
      }
    } else {
      clearSearch();
    }
  });
  document.getElementById("searchBarInput").addEventListener("focus", function(evt) {
    addClass(document.getElementById("canvas"), "inputFocus");
    addClass(document.getElementById("backgroundFader"), "inputFocus");
  });
  document.getElementById("searchBarInput").addEventListener("blur", function(evt) {
    removeClass(document.getElementById("canvas"), "inputFocus");
    removeClass(document.getElementById("backgroundFader"), "inputFocus");
  });
}

function restoreHash() {
  var hashValue = window.location.hash.substring(1);
  if (hashValue != '') {
    searchAPI(JSON.parse(hashValue));
  }
  // Do something with te hash
}
function setRestoreHash() {
  window.onhashchange = function() {
    if (!currently_setting_hash) {
      restoreHash();
    } else {
      currently_setting_hash = false;
    }
  }; restoreHash();
}

function setHash(hashValue) {
  currently_setting_hash = true;
  window.location.hash = hashValue;
}

function sendSearch(searchQuery) {
  searchAPI({ "queryString" : searchQuery });
}

function searchAPI(requestPacket) {
  var callback = function(data) {
    document.getElementById("searchPane").innerHTML = data.html;
    setHash(JSON.stringify(data.packet));
    searchPaneEvents();
    console.log(data);
    hideLoadingIcon();
  };
  showLoadingIcon();
  var searcher = new ajaxRequest($context.getContextPath()+"/filesystem/api", callback)
    .POST()
    .accept("application/json")
    .handleErrors(function(xhttp) {
      document.getElementById("searchBarInput").blur();
    })
    .send("JSON", JSON.stringify(requestPacket));
}

function searchPaneEvents() {
  var searchPane = document.getElementById("searchPane");
  var imagesToSwap = document.getElementsByClassName("imageSwap");
  console.log("Entering Image Swap for "+imagesToSwap.length+" images...");
  for (var i=0; i<imagesToSwap.length; i++) {
  console.log("Swapper " + i);
    setupImageSwap(imagesToSwap[i]);
  }  
  var ajaxLinks = document.getElementsByClassName("ajaxLink");
  for (var i=0; i<ajaxLinks.length; i++) {
    setupAjaxLink(ajaxLinks[i]);
  }
}

function setupAjaxLink(link) {
  link.addEventListener("click", function() { showLoadingIcon(); var ajaxLink = new ajaxRequest(link.dataset.link, function() { hideLoadingIcon(); }).GET().send("RAW"); });
}

function setupImageSwap(imageToSwap) {
  var img = new Image();
  var parent = imageToSwap.parentElement;
  img.onload = function() {
    parent.replaceChild(img, imageToSwap);
    console.log("Swapping image: ");
    console.log(imageToSwap.dataset.src);
  };
  /*
  for (var i=0; i < imageToSwap.attributes.length; i++) {
    var attr = imageToSwap.attributes[i];
    img.setAttribute(attr.name, attr.value);
  }*/
  img.setAttribute("src", imageToSwap.dataset.src);
  console.log("Swap setup.");
}