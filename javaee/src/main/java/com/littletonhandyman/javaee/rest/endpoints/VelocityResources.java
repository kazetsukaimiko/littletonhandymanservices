package com.littletonhandyman.javaee.rest.endpoints;

import javax.inject.Inject;

import javax.ws.rs.*;

import com.littletonhandyman.javaee.util.VelocityRenderer;

import javax.servlet.http.HttpServletRequest;


@Path("/")
public class VelocityResources {

  @Inject
  private VelocityRenderer vr;
  
  @Inject HttpServletRequest request;
  

  @GET
  public String getIndex() {
    return vr.template("index.html").put("contextPath",request.getContextPath()).render();
  }  

  @GET
  @Path("/admin")
  public String getNewIndex() {
    return vr.template("index.html").put("tryLogin", true).put("contextPath",request.getContextPath()).render();
  }  

  @GET
  @Path("/resources/{resourceType}/{relativePath:.+}")
  public String getResource(@PathParam("resourceType") String resourceType, @PathParam("relativePath") String relativePath) {
    return vr.template("resources" + "/" + resourceType + "/" + relativePath).put("contextPath",request.getContextPath()).render();
  }  





}