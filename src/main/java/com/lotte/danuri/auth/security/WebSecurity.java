package com.lotte.danuri.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lotte.danuri.auth.AuthService;
import com.lotte.danuri.auth.common.exceptions.filter.ExceptionHandlerFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Slf4j
@Order(0)
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final Environment env;
    private final AuthService authService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("Call WebSecurity configure");

        http.cors().disable();
        http.csrf().disable();

        http
            .antMatcher("/users")
            .antMatcher("/login")
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .addFilter(getAuthenticationFilter());

        http.addFilterBefore(getExceptionHandlerFilter(), AuthenticationFilter.class);

    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        log.info("Call WebSecurity getAuthenticationFilter");
        AuthenticationFilter authenticationFilter =
            new AuthenticationFilter(authenticationManager(), authService, env, tokenProvider);

        return authenticationFilter;
    }

    private ExceptionHandlerFilter getExceptionHandlerFilter() throws Exception {
        log.info("Call WebSecurity getExceptionHandlerFilter");
        return new ExceptionHandlerFilter(new ObjectMapper());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.info("Call WebSecurity configure");
        auth.userDetailsService(authService).passwordEncoder(bCryptPasswordEncoder);
    }
}
