package com.api.parkingcontrol.configs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfigV2 {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
        Stateless session enables authentication for every request. This would help ease the demonstration
        of this tutorial on Postman.
        */
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.DELETE, "/api/parking-spot/{id}").hasAnyRole("ADMIN") // Admin should be able to delete
                .antMatchers(HttpMethod.PUT, "/api/parking-spot/{id}").hasAnyRole("ADMIN") // Admin should be able to update
                .antMatchers("/api/parking-spot/add").hasAnyRole("USER") // Admin should be able to add parking-spot.
                .antMatchers("/api/parking-spot").permitAll() // All three users should be able to get all parking-spot.
                .antMatchers("/api/parking-spot/{id}").permitAll() // All three users should be able to get a parking-spot by id.
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
