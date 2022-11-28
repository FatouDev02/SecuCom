package com.example.SecuCom.filter;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustonAuthentificationFilter extends UsernamePasswordAuthenticationFilter {

    //Gère les modules d’authentification appelés pendant le processus d’authentification du client.
    private final AuthenticationManager authenticationManager;

    public CustonAuthentificationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    //CustonAuthentificationFilter est appelé dans SecurityConfig dans la requete du filter

            //gestion de l'authentification attemptAuthenticationattemptAuthentication
            // Effectue l'authentification réelle.

            @Override
            public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
                // return super.attemptAuthentication(request, response);
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                log.info("username is {}",username);
                log.info("password is {}",password);

                UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(username,password);
                return authenticationManager.authenticate(authenticationToken);
            //authenticationManager est appelé dans SecurityConfig comme bean et est reutilise dans la requete du filtre(custonnnn)

            }
    //gestion  jwt Oautth
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
       // provient de security.core.userdetails elle implemente l'interface UserDetails et assure la gestion d'autentification
       // avec le username le passwd et les authorithies
        User user= (User)authentication.getPrincipal();
        // Algo est une classe abstraite qui definis plsrs algo de......
        Algorithm algorithm=Algorithm.HMAC256("secret".getBytes());

        // notre token
        String access_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);



        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles",user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
//        response.setHeader("access_token",access_token);
//        response.setHeader("refresh_token",refresh_token);
        Map<String,String> tokens=new HashMap<>();
        tokens.put("access_token",access_token);
        tokens.put("refresh_token",refresh_token);
        //tokens.put("message", "Bienvenue "+ user.getUsername());
        tokens.put("message", "Bienvenue "+ user.getAuthorities());


        //defintion du format sous laquelle la reponse doit s'afficher
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(),tokens);
    }
}
