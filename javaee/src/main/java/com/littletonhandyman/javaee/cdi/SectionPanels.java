package com.littletonhandyman.javaee.cdi;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.mongodb.morphia.*;
import org.bson.types.ObjectId;

import com.littletonhandyman.entity.*;
import com.littletonhandyman.javaee.factory.*;

@Path("/site/panels")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class SectionPanels {

  @Inject 
  private DatastoreFactory datastores;
  
  @Inject
  private SiteSections siteSections;
  
  @Inject
  private UserSession userSessions;

  private Datastore getDatastore() {
    return datastores.getDatastore();
  }

  public void ensureAtLeastOnePanelPerSection() {
    for (SiteSection siteSection : siteSections.getAllSections()) {
      if (getDatastore().createQuery(SectionPanel.class).field("section").equal(siteSection).countAll() == 0) {
        getDatastore().save(new SectionPanel(siteSection));
      }
    }
  }


  @GET
  public Iterable<SectionPanel> getAllPanels() {
    ensureAtLeastOnePanelPerSection();
    return getDatastore().createQuery(SectionPanel.class);
  }

  @GET
  @Path("/{id}")
  public SectionPanel getPanelById(@PathParam("id") ObjectId anId) {
    return getDatastore().get(SectionPanel.class, anId);
  }

  @POST
  public SectionPanel savePanel(SectionPanel panel) {
    getDatastore().save(panel); return panel;
  }
  
  @GET
  @Path("/bySection/{sectionId}")
  public Iterable<SectionPanel> getPanelsOfSection(@PathParam("sectionId") ObjectId sectionId) {
    ensureAtLeastOnePanelPerSection();
    SiteSection siteSection = getDatastore().get(SiteSection.class, sectionId);
    if (siteSection != null) {
      return getDatastore().createQuery(SectionPanel.class).field("section").equal(siteSection);
    } return new ArrayList<SectionPanel>();
  }


  @DELETE
  @Path("/bySection/{sectionId}")
  public Iterable<SectionPanel> deletePanelsOfSection(@PathParam("sectionId") ObjectId sectionId) {
    if (userSessions.isValid()) {
      SiteSection siteSection = getDatastore().get(SiteSection.class, sectionId);
      if (siteSection != null) {
        getDatastore().delete(
          getDatastore().createQuery(SectionPanel.class).field("section").equal(siteSection)
        );
        return getAllPanels();
      }
    } throw userSessions.pleaseAuthenticate();
  }
  
  
  
  @GET
  @Path("/seed")
  public Iterable<SectionPanel> seedPanels() {
    if (userSessions.isValid()) {
      getDatastore().delete(getDatastore().createQuery(SiteSection.class));
      
      SiteSection home = new SiteSection("Home", "The Littleton Handyman Homepage.");
      getDatastore().save(home);
      SiteSection construction = new SiteSection("Construction", "The Littleton Handyman Construction Page.");
      getDatastore().save(construction);
      SiteSection painting = new SiteSection("Painting", "The Littleton Handyman Painting Page.");
      getDatastore().save(painting);
    
      getDatastore().delete(getDatastore().createQuery(SectionPanel.class));
      getDatastore().save(new SectionPanel(home, "The Littleton Handyman Homepage."));  
      getDatastore().save(new SectionPanel(construction, "The Littleton Handyman Construction Page."));  
      getDatastore().save(new SectionPanel(painting, "The Littleton Handyman Painting Page."));
      return getAllPanels();
    } throw userSessions.pleaseAuthenticate();
  }




}