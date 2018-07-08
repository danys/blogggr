package com.blogggr.security;

import com.blogggr.entities.User;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Daniel Sunnen on 01.06.18.
 */
public class UserPrincipal implements UserDetails {

  @Getter
  private User user;

  public UserPrincipal(User user){
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities(){
    return new ArrayList<>();
  }

  @Override
  public String getPassword(){
    return this.user.getPasswordHash();
  }

  @Override
  public String getUsername(){
    return this.user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired(){
    return true;
  }

  @Override
  public boolean isAccountNonLocked(){
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired(){
    return true;
  }

  @Override
  public boolean isEnabled(){
    return true;
  }
}
