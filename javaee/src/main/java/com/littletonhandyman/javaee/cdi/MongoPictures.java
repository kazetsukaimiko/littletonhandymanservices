package com.littletonhandyman.javaee.cdi;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;


import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import javax.servlet.http.HttpServletRequest;


import net.coobird.thumbnailator.Thumbnails;

import org.mongodb.morphia.*;

import com.mongodb.*;
import com.mongodb.gridfs.*;
import org.bson.types.ObjectId;

import org.apache.log4j.Logger;

import com.littletonhandyman.entity.*;
import com.littletonhandyman.javaee.factory.*;

@Path("/site/pictures")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class MongoPictures {

  protected final Logger logger = Logger.getLogger(this.getClass().getName());

  @Inject
  private HttpServletRequest request;
  public HttpServletRequest getRequest() {
    return request;
  }

  @Inject
  private UserSession userSession;

  @Inject 
  private DatastoreFactory datastores;
  public Datastore getDatastore() {
    return datastores.getDatastore();
  }
  
  @Inject
  private GridFS gridFS;
  public GridFS getGridFS() {
    return gridFS;
  }
  
  @GET
  public Iterable<MongoPicture> getAllPictures() {
    List<MongoPicture> allPictures = new ArrayList<MongoPicture>();
    BasicDBObject query = new BasicDBObject();
    for(GridFSDBFile dbFile : gridFS.find(query)) {
      allPictures.add(new MongoPicture(dbFile));
    } return allPictures;
  }

  @GET
  @Path("/ckeditor")
  public Iterable<Map<String, String>> getCKEDITORPictures() {
    List<Map<String, String>> allPictures = new ArrayList<Map<String, String>>();
    BasicDBObject query = new BasicDBObject();
    for(GridFSDBFile dbFile : gridFS.find(query)) {
      Map<String, String> imageMap = new HashMap<String, String>();
      imageMap.put("image", (request.getContextPath() +  "/site/pictures/" + dbFile.getId().toString() + "/scaled"));
      imageMap.put("thumb", (request.getContextPath() + "/site/pictures/" + dbFile.getId().toString() + "/thumb"));
      allPictures.add(imageMap);
    } return allPictures;
  }

  @POST
  @Consumes("image/*")
  public Iterable<MongoPicture> addPicture(InputStream input) {
    createPicture(input); return getAllPictures();
    //return getPicture(mongoPicture.getId());
  }
  
  @GET
  @Path("/{id}")
  public Response getPicture(@PathParam("id") ObjectId anId) {
    GridFSDBFile file = gridFS.findOne(anId);
    if (file != null) {
      return Response
        .ok()
        .entity(file.getInputStream())
        .type(file.getContentType())
        .build();
    } return Response.status(Response.Status.NO_CONTENT).build();
  }

  @GET
  @Path("/{id}/thumb")
  public Response getThumbnail(@PathParam("id") ObjectId anId) throws java.io.IOException {
    GridFSDBFile file = gridFS.findOne(anId);
    if (file != null) {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      Thumbnails.of(file.getInputStream()).size(200,200).outputFormat("jpeg").toOutputStream(outputStream);
      return Response
        .ok()
        .entity(new ByteArrayInputStream(outputStream.toByteArray()))
        .type("image/jpeg")
        .build();
    } return Response.status(Response.Status.NO_CONTENT).build();
  }

  @GET
  @Path("/{id}/scaled")
  public Response getScaledDown(@PathParam("id") ObjectId anId) throws java.io.IOException {
    GridFSDBFile file = gridFS.findOne(anId);
    if (file != null) {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      Thumbnails.of(file.getInputStream()).size(1000,1000).outputFormat("jpeg").toOutputStream(outputStream);
      return Response
        .ok()
        .entity(new ByteArrayInputStream(outputStream.toByteArray()))
        .type("image/jpeg")
        .build();
    } return Response.status(Response.Status.NO_CONTENT).build();
  }

  
  @DELETE
  @Path("/{id}")
  @Consumes("*")
  public Response deletePicture(@PathParam("id") ObjectId anId) {
    if (userSession.isValid()) {
      gridFS.remove(anId);
      return getPicture(anId);
    } throw userSession.pleaseAuthenticate();
  }

  public MongoPicture createPicture(InputStream input) {
    if (userSession.isValid()) {
      GridFSInputFile inputFile = gridFS.createFile(input);
      inputFile.setContentType(getRequest().getContentType());
      inputFile.save(); // Now has Id.

      Object idObj = inputFile.getId();
      if (idObj instanceof ObjectId) {
        return makeMongoPicture((ObjectId) idObj);
      } return null;
    } throw userSession.pleaseAuthenticate();
  }

  public MongoPicture makeMongoPicture(ObjectId id) {
    return new MongoPicture(gridFS.findOne(id));
  }


}