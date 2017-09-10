package com.littletonhandyman.javaee.factory;

import java.util.Map;
import java.util.HashMap;


import javax.inject.*;
import javax.enterprise.context.*;
import javax.enterprise.inject.*;

import com.mongodb.MongoClient;
import org.mongodb.morphia.*;

import org.apache.log4j.Logger;

//@Singleton
//@ApplicationScoped
public class DatastoreFactory implements java.io.Serializable {

  protected final Logger logger = Logger.getLogger(this.getClass().getName());
  
  private static final long serialVersionUID = -44416514616012281L;
  
  //@Inject
  //private transient Morphia morphia;
  
  //@Inject
  //private transient MongoClient mongoClient;

  @Inject
  private transient MorphiaFactory morphiaFactory;
  
  @Inject
  private transient MongoClientFactory mongoClientFactory;
  
  private Map<String, Datastore> datastores = new HashMap<String, Datastore>();

  public Datastore getDatastore(String dbName) {
    if (dbName != null) {
      Morphia morphia = morphiaFactory.getMorphia();
      if (morphia != null) {
        Datastore newDatastore = morphia.createDatastore(mongoClientFactory.getMongoClient(), dbName);
        newDatastore.ensureIndexes();
        newDatastore.ensureCaps();
        return newDatastore;
      } return null;
    } return getDatastore();
  }


  @Produces
  public Datastore getDatastore() {
    return getDatastore("littletonhandyman");
  }

  public Datastore getDatastoreOld(String dbName) {
    if (dbName != null) {
      if (!datastores.containsKey(dbName)) {
        logger.info("Creating Datastore \""+dbName+"\"...");
        try {

          Morphia morphia = morphiaFactory.getMorphia();
          MongoClient mongoClient = mongoClientFactory.getMongoClient();

          if (morphia == null) {
            logger.info("Datastore \""+dbName+"\" Dependency morphia NULL!");
          }
          if (mongoClient == null) {
            logger.info("Datastore \""+dbName+"\" Dependency mongoClient NULL!");
          }

          Datastore newDatastore = morphia.createDatastore(mongoClient, dbName);
          newDatastore.ensureIndexes();
          newDatastore.ensureCaps();

          if (true) {
            return newDatastore;
          }
          
          logger.info("Putting datastore...");
          datastores.put(dbName, newDatastore); 
          logger.info("Put datastore...");

        } catch (Exception e) {
          logger.error("Error Creating Datastore!", e);
        }
      }
      
      Datastore datastore = datastores.get(dbName);
      if (datastore == null) {
        logger.warn("Vending Datastore \""+dbName+"\" as null!!!");
      }
      return datastore;
    } return getDatastore();
  }
}