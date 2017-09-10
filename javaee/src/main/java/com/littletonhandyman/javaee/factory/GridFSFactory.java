package com.littletonhandyman.javaee.factory;

import javax.inject.*;
import javax.enterprise.context.*;
import javax.enterprise.inject.*;

import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import org.mongodb.morphia.*;

import org.apache.log4j.Logger;

@ApplicationScoped
public class GridFSFactory implements java.io.Serializable {

  protected final Logger logger = Logger.getLogger(this.getClass().getName());
  
  private static final long serialVersionUID = -44416514616012281L;

  //@Inject
  //private transient Datastore datastore;
  
  @Inject
  private transient DatastoreFactory datastoreFactory;
  
  private GridFS gridfs;

  @Produces
  //@ApplicationScoped
  public GridFS getGridFS() {
    if (gridfs == null) {
      logger.info("Creating GridFS...");
      try {
        gridfs = new GridFS(datastoreFactory.getDatastore().getDB());
      } catch (Exception e) {
        logger.error("Error Creating GridFS!", e);
      }
    } return gridfs;
  }
}