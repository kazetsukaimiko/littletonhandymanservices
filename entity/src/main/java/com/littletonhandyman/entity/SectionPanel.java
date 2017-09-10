package com.littletonhandyman.entity;

import java.util.List;
import java.util.ArrayList;

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
public class SectionPanel {

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

  @Reference
  private SiteSection section;
  public SiteSection getSection() {
    return section;
  }
  public void setSection(SiteSection aSection) {
    section = aSection;
  }

  @Property  
  private List<String> panelClasses = new ArrayList<String>();
  public List<String> getPanelClasses() {
    return panelClasses;
  }
  public void setPanelClasses(List<String> somePanelClasses) {
    if (somePanelClasses != null) {
      panelClasses = somePanelClasses;
    } else {
      panelClasses = new ArrayList<String>();
    }
  }

  @Property  
  private String panelHTML;
  public String getPanelHTML() {
    return panelHTML;
  }
  public void setPanelHTML(String aPanelHTML) {
    panelHTML = aPanelHTML;
  }

  public SectionPanel(SiteSection aSection, String aPanelHTML, List<String> somePanelClasses) {
    setSection(aSection);
    setPanelHTML(aPanelHTML);
    setPanelClasses(somePanelClasses);
  }

  public SectionPanel(SiteSection aSection, String aPanelHTML) {
    setSection(aSection);
    setPanelHTML(aPanelHTML);
  }

  public SectionPanel(SiteSection aSection) {
    setSection(aSection);
  }

  public SectionPanel() {
  }
  
  

}
