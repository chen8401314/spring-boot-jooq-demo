package com.example.demo.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AddResponseHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
 /*       response.addHeader("Access-Control-Expose-Headers","Authorization");
       response.addHeader("Access-Control-Allow-Origin", "*");
     response.addHeader("Access-Control-Allow-Credentials", "true");
       response.addHeader("Access-Control-Allow-Methods", "GET,POST,PATCH,PUT,OPTIONS,DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Origin,Content-Type,Cookie,Accept,Token");*/
        filterChain.doFilter(request, response);
    }

}
