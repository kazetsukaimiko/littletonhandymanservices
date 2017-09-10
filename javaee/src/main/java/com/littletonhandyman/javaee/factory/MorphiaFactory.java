package com.littletonhandyman.javaee.factory;

import javax.inject.*;
import javax.enterprise.context.*;
import javax.enterprise.inject.*;

import com.mongodb.MongoClient;
import org.mongodb.morphia.*;

import org.apache.log4j.Logger;

// LAZY LOADING:
import net.sf.cglib.proxy.Enhancer;
import com.thoughtworks.proxy.toys.hotswap.HotSwapping;

//@Singleton
//@ApplicationScoped
public class MorphiaFactory implements java.io.Serializable {

  protected final Logger logger = Logger.getLogger(this.getClass().getName());
  
  private static final long serialVersionUID = -44416514616012281L;
  
  private Morphia morphia;

  @Produces
  public Morphia getMorphia() {
    //logger.info("Vending Morphia...");
    if (morphia == null) {
      //logger.info("Creating Morphia...");
      try {
        morphia = new Morphia();
        morphia.mapPackage("com.littletonhandyman.entity");
      } catch (Exception e) {
        logger.error("Error Creating Morphia!", e);
      }
    } 
    if (morphia == null) {
      logger.warn("Morphia vending null!");
    } return morphia;
  }
}