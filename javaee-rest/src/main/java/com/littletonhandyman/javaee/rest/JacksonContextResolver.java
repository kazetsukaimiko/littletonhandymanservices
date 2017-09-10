package com.littletonhandyman.javaee.rest;

import javax.ejb.Stateless;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
/*
 *
 * Formats JSON for Pretty-print output
 *
 */

//@Stateless
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonContextResolver extends JacksonJaxbJsonProvider implements ContextResolver<ObjectMapper> {
  private ObjectMapper objectMapper;

  public JacksonContextResolver() throws Exception {
    this.objectMapper = new ObjectMapper();
    this.objectMapper
     .configure(MapperFeature.USE_ANNOTATIONS, true)
     .configure(MapperFeature.AUTO_DETECT_CREATORS, false)
     .configure(SerializationFeature.INDENT_OUTPUT, true)
     .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
     //.configure(SerializationFeature.WRITE_NULL_PROPERTIES, false) // Deprecated
     .configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true)
     .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public ObjectMapper getContext(Class<?> objectType) {
    return objectMapper;
  }
}