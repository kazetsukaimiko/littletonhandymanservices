package com.littletonhandyman.entity;

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


@Entity
public class SiteSection {

  @Id
  @JsonSerialize(using = ObjectIdJsonSerializer.class)
  @JsonDeserialize(using = ObjectIdJsonDeserializer.class)
  private ObjectId id;
  public ObjectId getId() {
    return id;
  }
  public void setId(ObjectId anId) {
    id = anId;
  }

  @Property  
  private String sectionName;
  public String getSectionName() {
    return sectionName;
  }
  public void setSectionName(String aSectionName) {
    sectionName = aSectionName;
  }

  @Property  
  private String sectionDescription;
  public String getSectionDescription() {
    return sectionDescription;
  }
  public void setSectionDescription(String aSectionDescription) {
    sectionDescription = aSectionDescription;
  }

  public SiteSection(String aSectionName, String aSectionDescription) {
    setSectionName(aSectionName);
    setSectionDescription(aSectionDescription);
  }

  public SiteSection() {
  }
  
  

}
