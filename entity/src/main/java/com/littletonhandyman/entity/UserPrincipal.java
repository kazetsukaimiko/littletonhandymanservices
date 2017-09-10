package com.littletonhandyman.entity;

import java.util.Objects;

public class UserPrincipal implements java.io.Serializable {

  private String username;
  public String getUsername() {
    return username;
  }
  public void setUsername(String aUsername) {
    username = aUsername;
  }

  private String password;
  public String getPassword() {
    return password;
  }
  public void setPassword(String aPassword) {
    password = aPassword;
  }
  
  public UserPrincipal(String aUsername, String aPassword) {
    setUsername(aUsername);
    setPassword(aPassword);
  }
  public UserPrincipal() {
  }

  @Override
  public boolean equals(Object o) {
    if (o != null && o instanceof UserPrincipal) {
      UserPrincipal u = (UserPrincipal) o;
      return (Objects.equals(u.getUsername(), getUsername()) && Objects.equals(u.getPassword(), getPassword()));
    } return false;
  }

}
