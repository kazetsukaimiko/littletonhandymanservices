package com.littletonhandyman.javaee.cdi;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.mongodb.morphia.*;
import org.bson.types.ObjectId;

import com.littletonhandyman.entity.*;
import com.littletonhandyman.javaee.factory.*;

@Path("/site/sections")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class SiteSections {

  @Inject
  private UserSession userSession;

  @Inject 
  private DatastoreFactory datastores;
  
  @Inject
  private SectionPanels sectionPanels;

  private Datastore getDatastore() {
    return datastores.getDatastore();
  }

  @GET
  public Iterable<SiteSection> getAllSections() {
    return getDatastore().createQuery(SiteSection.class);
  }

  @POST
  public Iterable<SiteSection> saveSection(SiteSection siteSection) {
    if (userSession.isValid()) {
      getDatastore().save(siteSection); return getAllSections();
    } throw userSession.pleaseAuthenticate();
  }
  
  @GET
  @Path("/{id}")
  public Response getSection(@PathParam("id") ObjectId anId) {
    SiteSection siteSection = getDatastore().get(SiteSection.class, anId);
    if (siteSection != null) {
      return Response
        .ok()
        .entity(siteSection)
        .type(MediaType.APPLICATION_JSON)
        .build();
    } return Response.status(Response.Status.NO_CONTENT).build();
  }

  @DELETE
  @Path("/{id}")
  public Response deleteSection(@PathParam("id") ObjectId anId) {
    if (userSession.isValid()) {
      SiteSection siteSection = getDatastore().get(SiteSection.class, anId);
      if (siteSection != null) {
        sectionPanels.deletePanelsOfSection(siteSection.getId());
        getDatastore().delete(SiteSection.class, anId); 
      } return getSection(anId);
    } throw userSession.pleaseAuthenticate();
  }
  
  @GET
  @Path("/seed")
  public Iterable<SiteSection> seedSections() {
    /*
    getDatastore().delete(getDatastore().createQuery(SiteSection.class));
    getDatastore().save(new SiteSection("Home", "The Littleton Handyman Homepage."));  
    getDatastore().save(new SiteSection("Construction", "The Littleton Handyman Construction Page."));  
    getDatastore().save(new SiteSection("Painting", "The Littleton Handyman Painting Page."));  
    */
    return getAllSections();
  }




}