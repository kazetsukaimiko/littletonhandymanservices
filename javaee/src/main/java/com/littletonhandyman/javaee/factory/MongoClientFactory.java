package com.littletonhandyman.javaee.factory;

import javax.inject.Singleton;
import javax.enterprise.context.*;
import javax.enterprise.inject.*;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

import org.apache.log4j.Logger;

//@ApplicationScoped
@Singleton
//@ApplicationScoped
public class MongoClientFactory implements java.io.Serializable {

  protected final Logger logger = Logger.getLogger(this.getClass().getName());
  
  private static final long serialVersionUID = -44416514616012281L;
  
  private MongoClient mongoClient;
  
  public MongoClient forceNewMongoClient() {
    mongoClient = createMongoClient();
    return mongoClient;
  }

  public MongoClient createMongoClient() {
    logger.info("Creating MongoClient...");
    try {
      MongoClient client = new MongoClient(
        "localhost",
        (new MongoClientOptions.Builder())
          //.minConnectionsPerHost(10)
          .connectionsPerHost(10)
          .maxWaitTime(10000)
          //.socketTimeout(10000)
          .socketKeepAlive(true)
          //.threadsAllowedToBlockForConnectionMultiplier(5000)
          .build()
      ); return client;
    } catch (Exception e) {
      logger.error("Exception creating MongoClient!" , e);
    } return null;
  }

  @Produces
  public MongoClient getMongoClient() {
    //logger.info("Vending MongoClient...");
    if (mongoClient == null) {
      logger.info("Creating MongoClient...");
      mongoClient = createMongoClient();
      if (mongoClient == null) {
        logger.warn("MongoClient vending null!");
      }
    } return mongoClient;
  }
}