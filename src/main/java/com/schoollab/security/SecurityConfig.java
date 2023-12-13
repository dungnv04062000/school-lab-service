package com.schoollab.security;

import com.schoollab.common.constants.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl(Constants.URL_PATH_LOGIN);

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","PATCH"));
//        corsConfiguration.setExposedHeaders(List.of("Authorization"));

        http.cors().configurationSource(request -> corsConfiguration);
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //only production------------------
//        http.authorizeRequests()
//                .antMatchers("/v1/login", "/v1/register", "/v1/auth/refresh-token").permitAll()
//                .antMatchers(HttpMethod.PATCH, "/v1/users/role/**").hasAnyAuthority(Constants.ROLE_ADMIN)
//                .antMatchers(HttpMethod.GET, "/v1/users/**").hasAnyAuthority(Constants.ROLE_ADMIN)
//                .antMatchers(HttpMethod.PATCH, "/v1/users/nickname/**")
//                    .hasAnyAuthority(Constants.ROLE_COUNSELOR, Constants.ROLE_CUSTOMER)
//                .anyRequest().authenticated();
        //----------------------------------

        //only test in development
        http.authorizeRequests().anyRequest().permitAll();


        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return new CustomAuthenticationManager();
    }
}
