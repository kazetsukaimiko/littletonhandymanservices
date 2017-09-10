
function tryLogin() {
  adminEvents();
  verifySession();
}

function verifySession() {
  var ajax = fs.ajax()
    .GET(window.contextPath + "/session")
    .handle(200, function(xhr, request) {
      var content = JSON.parse(xhr.responseText);
      greetUser(content);
      console.log(content);
      loggedIn();
    })
    .handle(401, function(xhr, request) {
      loggedOut();
    })
    .json();
}

function greetUser(principal) {
  var adminWelcomes = document.getElementsByClassName("adminWelcome");
  for(var i=0; i<adminWelcomes.length; i++) {
    adminWelcomes[i].textContent = 'Welcome, ' + ucfirst(principal.username);
  }
}

function tryAuthenticate(evt) {
  var evt = evt || window.event;
  var target = evt.target || evt.toElement || evt.relatedTarget;
  if ((evt.keyCode == 13)) {
    disableLoginPrompt();
    var ajax = fs.ajax()
      .POST(window.contextPath + "/session")
      .handle(200, function(xhr, request) {
        var content = JSON.parse(xhr.responseText);
        greetUser(content);
        console.log(content);
        loggedIn();
      })
      .handle(401, function(xhr, request) {
        setTimeout(loggedOut, 500);
      })
      .json({
        'username' : document.getElementById("usernameInput").value,
        'password' : document.getElementById("passwordInput").value
      });
  }
}

function adminEvents() {
  
  allClassElements("authenticator", function(elem) {
    elem.addEventListener("keyup", tryAuthenticate);
  });

  allClassElements("showPictures", function(elem) {
    elem.addEventListener("click", function(evt) {
      if (hasClass(this, "active")) {
        hidePictures();
      } else {
        showPictures();
      }
    });
  });

  allClassElements("newSection", function(elem) {
    elem.addEventListener("click", function(evt) {
      newSection();
    });
  });
  
  allClassElements("picturesBar", function(elem) {
    elem.addEventListener("mousewheel", function(evt) {
      var evt = evt || window.event;
      var target = evt.target;
      if (target.doScroll) {
        target.doScroll(evt.wheelDelta>0?"left":"right");
      } else if ((evt.wheelDelta || evt.detail) > 0) {
        target.scrollLeft -= 10;
      } else {
        target.scrollLeft += 10;
      } return false;
    });
  });
}

function newPictureClickListener(elem) {
  var fileSelector = document.createElement("input");
  fileSelector.setAttribute("type", "file");
  fileSelector.setAttribute("multiple", "multiple");
  fileSelector.setAttribute("accept", "image/*");
  fileSelector.addEventListener("change", function(evt) {
    var evt = evt || window.event;
    if (evt.target.files instanceof FileList) {
      uploadPictures(evt.target.files);
    }
  });
  fileSelector.click();
}

function uploadPictures(fileList) {
  for(var i=0; i<fileList.length; i++) {
    uploadPicture(fileList[i]);
  }
}

function handleDragover(evt) {
  var evt = evt || window.event;
  evt.stopPropagation();
  evt.preventDefault();
  addClass(this, "drag");
}
function handleDragleave(evt) {
  var evt = evt || window.event;
  evt.stopPropagation();
  evt.preventDefault();
  removeClass(this, "drag");
}

function handleFileDrop(evt) {
  var evt = evt || window.event;
  evt.stopPropagation();
  evt.preventDefault();
  removeClass(this, "drag");
  var files = evt.dataTransfer.files;
  if (evt.dataTransfer.files instanceof FileList) {
    uploadPictures(evt.dataTransfer.files);
  }
}

function updatePictures(picturesList) {
  allClassElements("picturesBar", function(picturesBar) {
    while(picturesBar.hasChildNodes()) {
      picturesBar.removeChild(picturesBar.lastChild);
    }
    console.log("Removed all children");
    var newPicture = document.createElement("DIV");
    newPicture.setAttribute("class", "picture newPicture");
    newPicture.addEventListener("click", newPictureClickListener);
    newPicture.addEventListener("drop", handleFileDrop);
    newPicture.addEventListener("dragover", handleDragover);
    newPicture.addEventListener("dragleave", handleDragleave);
    picturesBar.appendChild(newPicture);


    for(var i=0; i<picturesList.length; i++) {
      console.log("Adding picture: "+ picturesList[i].id);
      var pictureDiv = document.createElement("DIV");
      pictureDiv.style.backgroundImage = ('url(\'' + window.contextPath + '/site/pictures/' + picturesList[i].id + '/thumb' + '\')');
      pictureDiv.style.backgroundSize = 'contain';
      pictureDiv.style.backgroundRepeat = 'no-repeat';
      pictureDiv.style.backgroundPosition = 'center';
      pictureDiv.setAttribute("class", "picture");
      pictureDiv.dataset['pictureId'] = picturesList[i].id;
      
      var deleteDiv = document.createElement("DIV");
      deleteDiv.setAttribute("class", "deletePicture");
      deleteDiv.dataset['pictureId'] = picturesList[i].id;
      pictureDiv.appendChild(deleteDiv);
      picturesBar.appendChild(pictureDiv);
    }
    
  });

  allClassElements("deletePicture", function(elem) {
    elem.addEventListener("click", function(evt) {
      deletePicture(this.dataset['pictureId']);
      this.parentNode.parentNode.removeChild(this.parentNode);
    });
  });
}

function deletePicture(pictureId) {
  var ajax = fs.ajax()
    .DELETE(window.contextPath+'/site/pictures/'+pictureId)
    .handle(200, function(xhr, request) {
      // Failed
    })
    .handle(204, function(xhr, request) {
      // Success
      refreshPicturesBar();
    })
    .handle(401, function(xhr, request) {
      loggedOut(function() { deletePicture(pictureId); });
    })
    .json();
}

function uploadPicture(picture) {
  if (picture.type.match(/image.*/)) {
    var ajax = fs.ajax()
      .POST(window.contextPath+"/site/pictures")
      .handle(200, function(xhr, request) {
        updatePictures(JSON.parse(xhr.responseText));
      })
      .handle(401, function(xhr, request) {
        uploadPicture(picture);
      })
      .upload(picture);
  }
}

function refreshPicturesBar(callback) {
  if (typeof(callback) != 'function') {
    var callback = function() {};
  }
  var ajax = fs.ajax()
    .GET(window.contextPath+"/site/pictures")
    .handle(200, function(xhr, request) {
      updatePictures(JSON.parse(xhr.responseText));
      callback();
    })
    .handle(401, function(xhr, request) {
      refreshPicturesBar(callback);
    })
    .json();
}

function showPictures() {
  refreshPicturesBar(function() {
    allClassElements("showPictures", function(elem) {
      addClass(elem, "active");
    });
    allClassElements("picturesBar", function(elem) {
      addClass(elem, "active");
    });
  });
}

function hidePictures() {
  allClassElements("showPictures", function(elem) {
    removeClass(elem, "active");
  });
  allClassElements("picturesBar", function(elem) {
    removeClass(elem, "active");
  });
}



function clearLoginPrompt() {
  document.getElementById("usernameInput").value = "";
  document.getElementById("passwordInput").value = "";
}
     
function focusLoginPrompt() {
  showLoginPane();
  clearLoginPrompt();
  enableLoginPrompt();
  document.getElementById("usernameInput").focus();
}
function disableLoginPrompt() {
  addClass(document.getElementById("loginPrompt"), "disabled");
  document.getElementById("usernameInput").readOnly = true;
  document.getElementById("passwordInput").readOnly = true;
  document.getElementById("usernameInput").disabled = true;
  document.getElementById("passwordInput").disabled = true;  
}
function enableLoginPrompt() {
  removeClass(document.getElementById("loginPrompt"), "disabled");
  document.getElementById("usernameInput").readOnly = false;
  document.getElementById("passwordInput").readOnly = false;
  document.getElementById("usernameInput").disabled = false;
  document.getElementById("passwordInput").disabled = false;  
}
function showLoginPane() {
  addClass(document.getElementById("loginPane"), "active");
}
function hideLoginPane() {
  removeClass(document.getElementById("loginPane"), "active");
}

function showAdminBar() {
  var adminBars = document.getElementsByClassName("adminBar");
  for (var i=0; i<adminBars.length; i++) {
    addClass(adminBars[i], "loggedIn");
  }
}
function hideAdminBar() {
  var adminBars = document.getElementsByClassName("adminBar");
  for (var i=0; i<adminBars.length; i++) {
    removeClass(adminBars[i], "loggedIn");
  }
}


function showEditors() {
 /*
 allClassElements("contentPanel", function(elem) { 
  elem.editor = CKEDITOR.inline(elem); 
  elem.dataset.saveState = elem.editor.getData();
  
  elem.saveCallback = function(editor, data) {
   var element = editor.element.$;
   var panelId = element.dataset.id;
   savePanel(panelId, data);
   
   element.dataset.saveState = editor.getData();
  };
  /*
  elem.cancelCallback = function(editor, data) {
  };
  */
  
  /*
  elem.editor.on("blur", function (ckevt) {
   var editor = ckevt.editor;
   var element = editor.element.$;
   var panelId = element.dataset.id;
   var panelData = editor.getData();
   
   savePanel(panelId, panelData);
   
  });
 });
  */
}

function getPanel(panelId, callback) {
  var ajax = fs.ajax()
    .GET(window.contextPath + "/site/panels/" + panelId + "?motd=" +new Date)
    .handle(200, function(xhr, request) {
      var panel = JSON.parse(xhr.responseText);
      callback(panel);
    })
    .handle(401, function(xhr, request) {
      loggedOut(function() { getPanel(panelId, callback); });
    })
    .json();
}

function savePanel(panelId, panelData) {
  console.log("Saving Panel: " + panelId);
  console.log(panelData);

  getPanel(panelId, function(panel) {
  
    panel.panelHTML = panelData;
    var ajax = fs.ajax()
      .POST(window.contextPath + "/site/panels/" + "?motd=" +new Date)
      .handle(200, function(xhr, request) {
        var panel = JSON.parse(xhr.responseText);
        console.log("Saved Panel: ");
        console.log(panel);
      })
      .handle(401, function(xhr, request) {
        loggedOut(function() { savePanel(panelId, panelData); });
      }).json(panel);
  });
}

function hideEditors() {
 /*
 for(name in CKEDITOR.instances) {
  CKEDITOR.instances[name].destroy();
 }*/
}

function loggedOut(callback) {
  hideAdminBar();
  hideEditors();
  focusLoginPrompt();
  if (typeof(callback) == "function") {
    window.loginCallback = callback;
  }
}

function loggedIn() {
  clearLoginPrompt();
  hideLoginPane();
  showAdminBar();
  showEditors();
  if (typeof(window.loginCallback) == "function") {
    var callback = window.loginCallback;
    window.loginCallback = function() {};
    callback();
  }
}

function showSectionEditor() {
  allClassElements("sectionEditor", function(elem) {
    addClass(elem, "active");
  });
}
function hideSectionEditor() {
  allClassElements("sectionEditor", function(elem) {
    removeClass(elem, "active");
  }); 
  allClassElements("navButton", function(elem) {
    removeClass(elem, "editing");
  });
  emptySectionEditor();
}

function sectionEditorVisible() {
  return hasClass(document.getElementById("sectionEditor"), "active");
}

function emptySectionEditor() {
  var sectionEditor = document.getElementById("sectionEditor");
  while(sectionEditor.hasChildNodes()) {
    sectionEditor.removeChild(sectionEditor.lastChild);
  } return sectionEditor;
}

function freezeSectionEditor() {
  allClassElements("sectionFieldInput", function(elem) {
    elem.readOnly = true;
    elem.disabled = true;
  });
}

function unfreezeSectionEditor() {
  allClassElements("sectionFieldInput", function(elem) {
    elem.readOnly = false;
    elem.disabled = false;
  });
}

function saveSection(sectionId, callback) {
  if (typeof(callback) != 'function') {
    var callback = function() {};
  }
  fs.ajax()
    .POST(window.contextPath + '/site/sections')
    .handle(200, function(xhr, request) {
      var data = JSON.parse(xhr.responseText);
      constructSections(data);
      callback();
    })
    .handle(401, function(xhr, request) {
      saveSection(sectionId, callback);
    })
    .json({
      "id": sectionId,
      "sectionName" : document.getElementById("sectionName").value,
      "sectionDescription" : document.getElementById("sectionDescription").value,
    });
}

function makeSectionEditor(sectionData) {
  var sectionEditor = emptySectionEditor();

  var dialog = document.createElement("DIV");
  dialog.className = 'dialog';
  
  console.log("sectionName", sectionData.sectionName);
  console.log(sectionData);
  dialog.appendChild(createSectionField("sectionName", "Section Name", "text", sectionData.sectionName));
  dialog.appendChild(createSectionField("sectionDescription", "Section Description", "text", sectionData.sectionDescription));

  var submit = document.createElement("INPUT");
  submit.setAttribute("type", "button");
  submit.value = 'Save';
  submit.addEventListener("click", function(evt) {
    var evt = evt || window.event;
    saveSection(sectionData.id, function() {
      fetchAndLoadSections(function() {
        hideSectionEditor();
      });
    });
  });
    
  dialog.appendChild(submit);

  var cancel = document.createElement("INPUT");
  cancel.setAttribute("type", "button");
  cancel.value = 'Cancel';
  cancel.addEventListener("click", function(evt) {
    hideSectionEditor();
  });
    
  dialog.appendChild(cancel);

  if (typeof(sectionData.id) != "undefined" && sectionData.id != null) {
    var deleteButton = document.createElement("INPUT");
    deleteButton.setAttribute("type", "button");
    deleteButton.value = 'Delete';
    deleteButton.addEventListener("click", function(evt) {
      deleteSection(sectionData.id);
      hideSectionEditor();
    });
    dialog.appendChild(deleteButton);
  }
    
  
  sectionEditor.appendChild(dialog);
  showSectionEditor();
}

function deleteSection(sectionId, callback) {
  if (typeof(callback) != 'function') {
    var callback = function() {};
  }
  fs.ajax()
    .DELETE(window.contextPath + '/site/sections/' + sectionId)
    .handle(204, function(xhr, request) {
      fetchAndLoadSections(callback);
    })
    .handle(401, function(xhr, request) {
      deleteSection(sectionId, callback);
    })
    .json();
}

function newSection() {
 editSection(null);
}

function editSection(sectionId) {
  console.log("editing section:" );
  console.log(sectionId);
  if (typeof(sectionId) != "undefined" && sectionId != null) {
   fs.ajax()
     .GET(window.contextPath + '/site/sections/' + sectionId)
     .handle(200, function(xhr, request) {
       var sectionData = JSON.parse(xhr.responseText);
       makeSectionEditor(sectionData);
     })
     .handle(401, function(xhr, request) {
       editSection(sectionId);
     })
     .json();
  } else {
    makeSectionEditor({
     "sectionName" : "",
     "sectionDescription" : "",
     "id" : null
    });
  }
}

function createSectionField(fieldId, fieldDescr, fieldType, fieldValue) {
  var field = document.createElement("DIV");
  field.className = 'fieldContainer';

  var fieldDescription = document.createElement("DIV");
  fieldDescription.className = 'fieldDescription';
  fieldDescription.textContent = fieldDescr;

  field.appendChild(fieldDescription);
  
  var fieldInput = document.createElement("INPUT");
  fieldInput.className = 'fieldInput sectionFieldInput';
  fieldInput.setAttribute("id", fieldId);
  fieldInput.setAttribute("name", fieldId);
  fieldInput.setAttribute("type", fieldType);
  fieldInput.setAttribute("autocomplete", "off");  
  
  console.log(fieldId + " : " + fieldValue);
  if (typeof(fieldValue) != "undefined" && fieldValue != null) {
    fieldInput.value = fieldValue;
  }
  
  field.appendChild(fieldInput);
  
  return field;
}


