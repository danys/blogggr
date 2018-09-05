package com.blogggr.config;

import com.blogggr.exceptions.CustomizedAuthenticationEntryPoint;
import com.blogggr.filters.CredentialsAuthenticationFilter;
import com.blogggr.filters.InternationalizationFilter;
import com.blogggr.filters.JwtAuthenticationFilter;
import com.blogggr.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by Daniel Sunnen on 01.06.18.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserService userDetailsService;

  public SecurityConfig() {
    super();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(authenticationProvider());
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider
        = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(encoder());
    return authProvider;
  }

  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder(11);
  }

  @Bean
  public CredentialsAuthenticationFilter credentialsAuthenticationFilter() throws Exception {
    CredentialsAuthenticationFilter authenticationFilter
        = new CredentialsAuthenticationFilter();
    authenticationFilter.setAuthenticationManager(authenticationManagerBean());
    return authenticationFilter;
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
  }

  @Bean
  public InternationalizationFilter internationalizationFilter() {
    return new InternationalizationFilter();
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return new CustomizedAuthenticationEntryPoint();
  }

  /**
   * Filter order: JWT filter -> credentials filter -> internationalization filter ->
   * UsernamePasswordAuthenticationFilter
   */
  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .httpBasic().disable()
        .authorizeRequests()
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
        .antMatchers(HttpMethod.GET, "/*").permitAll()
        .antMatchers(HttpMethod.POST, "/api/v*/sessions", "/api/v*/users").permitAll()
        .antMatchers(HttpMethod.GET, "/api/v*/users/*/enable").permitAll()
        .antMatchers(HttpMethod.GET, "/api/v*/userimages/*").permitAll()
        .anyRequest().authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .addFilterBefore(internationalizationFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(credentialsAuthenticationFilter(), InternationalizationFilter.class)
        .addFilterBefore(jwtAuthenticationFilter(), CredentialsAuthenticationFilter.class)
        .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());
  }
}
