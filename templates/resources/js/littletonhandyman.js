
function setupUI() {
  loadSectionsAndPanels(function() {
    setTimeout(function() { postLoad(); restorePanels(); }, 500);
  });
}

function loadSectionsAndPanels(callback) {
  if (typeof(callback) != "function") {
    var callback = function() {};
  }
  fetchAndLoadSections(function() {
    fetchAndLoadPanels(callback);
  });
}

function postLoad(callback) {
  var postLoadElements = document.getElementsByClassName("postLoad");
  for (var i=0; i<postLoadElements.length; i++) {
    postLoadElements[i].addEventListener("transitionend", function() {
      addClass(this, "loadedDone");
      this.removeEventListener("transitionend", arguments.callee);
    }); addClass(postLoadElements[i], "loaded");
  }
}

function fetchAndLoadSections(callback) {
  if (typeof(callback) != "function") {
    var callback = function() {};
  }
  
  var ajax = fs.ajax()
    .GET(window.contextPath+"/site/sections")
    .handle(200, function(xhr, request) {
      var data = JSON.parse(xhr.responseText);
      constructSections(data);
      callback();
    })
    .json();
}

function constructSections(sections) {
  var navContainer = document.getElementById("navContainer");
  while(navContainer.hasChildNodes()) {
    navContainer.removeChild(navContainer.lastChild);
  }
  
  for (var i=0; i<sections.length; i++) {
    navContainer.appendChild(constructSectionNav(sections[i]));
  }
  if (window.adminMode) {
    /*
    var newSectionDOM = document.createElement("DIV");
    newSectionDOM.textContent = "New Section";
    newSectionDOM.setAttribute("title", section.sectionDescription);
    newSectionDOM.setAttribute("class", "sectionNav navButton");
    */
    
  }
}



function constructSectionNav(section) {
  var sectionDOM = document.createElement("DIV");
  sectionDOM.textContent = section.sectionName;
  sectionDOM.setAttribute("title", section.sectionDescription);
  sectionDOM.setAttribute("class", "sectionNav navButton");
  sectionDOM.dataset['id'] = section.id;
  sectionDOM.dataset['name'] = section.sectionName;
  
  sectionDOM.activateControl = function() {
    deactivateOtherSectionNav();
    window.location.hash = this.dataset['name'];
    addClass(this,"active");
  };
  sectionDOM.addEventListener("click", function() {
    if (!hasClass(this, "active")) {
      this.activateControl();
      restorePanels(this.dataset['id']);
    }
  });
  
  if (window.adminMode) {
    sectionDOM.addEventListener("contextmenu", function(evt) {
      var evt = evt || window.event;
      evt.preventDefault();
      
      allClassElements("sectionNav", function(elem) {
        removeClass(elem, "editing");
      });
      addClass(this,"editing");
      editSection(this.dataset['id']);
      
      return false;
    });
  }

  /*  
  if (window.adminMode) {
    var editSectionDOM = document.createElement("DIV");
    editSectionDOM.className = "editSection";
    editSectionDOM.dataset['sectionId'] = section.id;
    var deleteSectionDOM = document.createElement("DIV");
    deleteSectionDOM.className = "deleteSection";
    deleteSectionDOM.dataset['sectionId'] = section.id;
    sectionDOM.appendChild(editSectionDOM);
    sectionDOM.appendChild(deleteSectionDOM);
  }*/
  return sectionDOM;
}

function fetchAndLoadPanels(callback) {
  if (typeof(callback) != "function") {
    var callback = function() {};
  }
  
  var ajax = fs.ajax()
    .GET(window.contextPath+"/site/panels?motd=" +new Date)
    .handle(200, function(xhr, request) {
      var data = JSON.parse(xhr.responseText);
      constructPanels(data);
      callback();
    })
    .json();
}

function constructPanels(panels) {
  var contentPane = document.getElementById("contentPane");
  while(contentPane.hasChildNodes()) {
    contentPane.removeChild(contentPane.lastChild);
  }
  
  for (var i=0; i<panels.length; i++) {
    contentPane.appendChild(constructPanel(panels[i]));
  }



}

function constructPanel(panel) {
  var panelContainerDOM = document.createElement("DIV");
  panelContainerDOM.setAttribute("class", "contentPanelContainer");
  panelContainerDOM.dataset['id'] = panel.id;
  panelContainerDOM.dataset['sectionid'] = panel.section.id;

  var panelDOM = document.createElement("DIV");
  panelDOM.innerHTML = panel.panelHTML;

  panelDOM.setAttribute("class", "contentPanel");
  for(var i=0; i<panel.panelClasses.length; i++) {
    addClass(panelDOM, panel.panelClasses[i]);
  }

  panelDOM.dataset['id'] = panel.id;
  panelDOM.dataset['sectionid'] = panel.section.id;
  
  if (window.adminMode) {
    panelDOM.setAttribute("contenteditable", "true");
    panelDOM.addEventListener("focus", function(evt) {
      for (name in CKEDITOR.instances) {
        if (CKEDITOR.instances[name].element.$ === this) {
          // Already an instance
          return true;
        }
      }
      var evt = evt || window.event;
      this.editor = CKEDITOR.inline(this);
      this.dataset.saveState = this.editor.getData();
      this.saveCallback = function(editor, data) {
        var element = editor.element.$;
        var panelId = element.dataset.id;
        savePanel(panelId, data);
        element.dataset.saveState = editor.getData();
        element.restoreAndClose();
      };
    });
    panelDOM.restoreAndClose = function() {
      this.editor.setData(this.dataset.saveState);
      delete this.dataset.saveState;
      for (name in CKEDITOR.instances) {
        if (CKEDITOR.instances[name].element.$ === this) {
          CKEDITOR.instances[name].destroy();
        }
      } this.blur();
    }
  }
  
  panelContainerDOM.appendChild(panelDOM);
  
  return panelContainerDOM;
}

function getActiveSectionId() {
  var hash = window.location.hash.substr(1);
  var sections = document.getElementsByClassName("sectionNav");
  for(var i=0; i<sections.length; i++) {
    if (hash == "") {
      sections[i].activateControl();
      return sections[i].dataset['id'];
    } else {
      if (hash == sections[i].dataset['id'] || hash == sections[i].dataset['name']) {
        sections[i].activateControl();
        return sections[i].dataset['id'];
      }
    }
  }
}

function deactivateOtherSectionNav() {
  var sections = document.getElementsByClassName("sectionNav");
  for(var i=0; i<sections.length; i++) {
    removeClass(sections[i], "active");
  }
}



function hideAllPanels() {
  var panels = document.getElementsByClassName("contentPanelContainer");
  for (var i=0; i<panels.length; i++) {
    removeClass(panels[i], "active");
  }
}


function activatePanels(sectionId) {
  if (typeof(sectionId) == "undefined") {
    var sectionId = getActiveSectionId();
  }
  var panels = document.getElementsByClassName("contentPanelContainer");
  for (var i=0; i<panels.length; i++) {
    if (panels[i].dataset['sectionid'] == sectionId) {
      addClass(panels[i], "active");
    }
  }
}

function restorePanels(sectionId) {
  if (typeof(sectionId) == "undefined") {
    var sectionId = getActiveSectionId();
  }
  var scrollKiller = document.getElementById("scrollKiller");
  var hideComplete = function() {
    hideAllPanels();
    activatePanels(sectionId);
  };
  
  if (hasClass(scrollKiller, "loaded")) {
    scrollKiller.addEventListener("transitionend", function() {
      hideComplete();
      addClass(scrollKiller, "loaded");
      this.removeEventListener("transitionend", arguments.callee);
    }); removeClass(scrollKiller, "loaded");
  } else {
    hideComplete();
    addClass(scrollKiller, "loaded");
  }

}


