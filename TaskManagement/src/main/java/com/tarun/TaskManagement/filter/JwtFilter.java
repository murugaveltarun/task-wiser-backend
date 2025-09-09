package com.tarun.TaskManagement.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarun.TaskManagement.exception.ApiResponseModel;
import com.tarun.TaskManagement.service.JwtService;
import com.tarun.TaskManagement.service.MyUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService service;

    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;


        try{
                if(authHeader != null && authHeader.startsWith("Bearer")){
                    token = authHeader.substring(7);
                    username = service.extractUserName(token);

                }

                if(username != null && SecurityContextHolder.getContext().getAuthentication() == null ){

                    UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);

                    if(service.validateToken(token,userDetails)){
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }

                }
                filterChain.doFilter(request,response);
            }catch (ExpiredJwtException e){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");

                ApiResponseModel<Void> errorResponse = new ApiResponseModel<>(false,"JWT Token Expired. Please login again",HttpStatus.UNAUTHORIZED.value(), null);
                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.writeValueAsString(errorResponse));
        }


    }


    }
