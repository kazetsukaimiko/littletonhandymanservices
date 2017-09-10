package com.littletonhandyman.javaee.cdi;

import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.enterprise.context.SessionScoped;

import com.littletonhandyman.entity.UserPrincipal;


@SessionScoped
@Path("/session")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class UserSession implements java.io.Serializable {

  private UserPrincipal userPrincipal;

  @GET
  public UserPrincipal getUserPrincipal() {
    if (userPrincipal != null) {
      UserPrincipal noPassword = new UserPrincipal(userPrincipal.getUsername(), "********");
      return noPassword;
    } throw pleaseAuthenticate();
  }

  @POST
  public UserPrincipal authenticate(UserPrincipal somePrincipal) {
    userPrincipal = null;
    if (somePrincipal != null) {
      if (
           somePrincipal.equals(new UserPrincipal("admin","ultim4"))
        || somePrincipal.equals(new UserPrincipal("jim","deskpro2")) 
        || somePrincipal.equals(new UserPrincipal("judy","garden5")) 
      ) {
        userPrincipal = somePrincipal;
      }
    } return getUserPrincipal();
  }
  
  @Path("/valid")
  @GET
  public boolean isValid() {
    return (userPrincipal != null);
  }
  
  public WebApplicationException pleaseAuthenticate() {
    return new WebApplicationException(Response
     .status(401)
     .entity(new UserPrincipal("Please", "Authenticate"))
     .type(MediaType.APPLICATION_JSON)
     .build()
    );
  }

  @Path("/env")
  @GET
  public Map<String, String> getEnv() {
    return System.getenv();
  }

  public UserSession() {
  }

}
