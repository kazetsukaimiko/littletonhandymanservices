package com.littletonhandyman.entity;

import java.util.List;
import java.util.ArrayList;

import java.util.Set;
import java.util.HashSet;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;

import org.bson.types.ObjectId;

import com.mongodb.gridfs.*;  
import com.mongodb.*;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Embedded;   
import org.mongodb.morphia.annotations.Transient;   
import org.mongodb.morphia.annotations.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlTransient;

public class MongoPicture implements java.io.Serializable {

  private static final long serialVersionUID = -1L;

  @JsonProperty
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  @JsonDeserialize(using = ObjectIdJsonDeserializer.class)
  public ObjectId getId() {
    if (getGridFSFile() != null && getGridFSFile().getId() instanceof ObjectId) {
      return (ObjectId) getGridFSFile().getId();
    } return null;
  }

  @JsonIgnore
  public String getFilename() {
    if (getGridFSFile() != null) {
      return getGridFSFile().getFilename();
    } return null;
  }

  public String getContentType() {
    if (getGridFSFile() != null) {
      return getGridFSFile().getContentType();
    } return null;
  }
  
  @JsonIgnore
  private GridFSDBFile gridFSFile;
  public GridFSDBFile getGridFSFile() {
    return gridFSFile;
  }
  public void setGridFSFile(GridFSDBFile aGridFSFile) {
    gridFSFile = aGridFSFile;
  }
  
  public MongoPicture(GridFSDBFile aGridFSFile) {
    setGridFSFile(aGridFSFile);
  }

  public MongoPicture() {
  }

}