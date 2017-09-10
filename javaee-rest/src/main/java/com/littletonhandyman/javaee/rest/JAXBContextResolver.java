package com.littletonhandyman.javaee.rest;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import java.lang.annotation.Annotation;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.Produces;

import org.reflections.Reflections;
import org.apache.log4j.Logger;


@Provider
@Produces({"application/xml","application/json"})
public class JAXBContextResolver implements ContextResolver<JAXBContext> {

  private final Logger logger = Logger.getLogger(this.getClass().getName());
  
  private JAXBContext jc;
  
  public JAXBContextResolver() {
    
    if (true) {
      try {
        jc = JAXBContext.newInstance();
      } catch (JAXBException e) {
        jc = null;
      }
      //return;
    }/**/
    // Classes to register with the JAXBContext
    Set<Class<?>> classList = new HashSet<Class<?>>();

    // Yay, more reflecton!
    Reflections reflections = new Reflections("com.littletonhandyman");
    
    // Annotations we look for to register with the JAXBContext
    List<Class<? extends Annotation>> annotationKlazzes = new ArrayList<Class<? extends Annotation>>();
    annotationKlazzes.add(javax.persistence.Entity.class);
    annotationKlazzes.add(javax.xml.bind.annotation.XmlType.class);
    annotationKlazzes.add(javax.xml.bind.annotation.XmlRootElement.class);
    annotationKlazzes.add(javax.xml.bind.annotation.XmlRootElement.class);
    annotationKlazzes.add(org.mongodb.morphia.annotations.Entity.class);
    // Look for all classes with those annotations, add them to our set
    for(Class<? extends Annotation> annotationKlazz : annotationKlazzes) {
      Set<Class<?>> klazzesWithAnnotation = reflections.getTypesAnnotatedWith(annotationKlazz);
      for(Class<?> annotatedKlazz : klazzesWithAnnotation) {
        //logger.info("Registering: " + annotatedKlazz.getName());
        try {
          //JAXBContext.newInstance(annotatedKlazz);
          classList.add(annotatedKlazz);
        } catch (Exception e) {
          logger.error(e);
        }
      }
    }
    
    // Try to create a JAXBContext with the above classes
    // Puke errors if you're unsuccessful
    // PS: JAXB hates my Morphia object graph, which I cannot change
    try {
      //logger.info("By itself: " + JAXBContext.newInstance().toString());
      jc = JAXBContext.newInstance(classList.toArray(new Class<?>[classList.size()]));
      //logger.info("Created:: " + jc.toString());
    } catch (JAXBException e) {
      jc = null;
      logger.error(e);
    }
  }
  
  @Override
  public JAXBContext getContext(Class<?> type)  {
    return jc;
  }
}

