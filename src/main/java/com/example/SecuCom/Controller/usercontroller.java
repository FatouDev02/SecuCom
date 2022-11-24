package com.example.SecuCom.Controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.SecuCom.Service.CollService;
import com.example.SecuCom.models.Collaborateurs;
import com.example.SecuCom.models.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin()
@Controller
@RequestMapping("/api")
public class usercontroller {

    @Autowired
    CollService collService;

    @PreAuthorize("hasRole('Role_ADMIN')")
    @PostMapping("/coll/addcoll")
    public ResponseEntity<Collaborateurs> adduser(@RequestBody Collaborateurs collaborateurs){
        URI uri=URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/adduser").toUriString());
        //return ResponseEntity.ok().body( userservice.ajouter(us));
        return ResponseEntity.created(uri).body( collService.ajoutercoll(collaborateurs));
    }
    @PreAuthorize("hasRole('Role_ADMIN')")
    @PostMapping("/addrole")
    public ResponseEntity<Role> addRole(@RequestBody Role role ){
        return ResponseEntity.ok().body( collService.ajoutrole(role));
    }
    @PreAuthorize("hasRole('Role_ADMIN')")
    @PostMapping("/addroletoColl")
    public ResponseEntity<?> addroletouser(@RequestBody RoletoColl roletoColl ){
        URI uri=URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/addroletocoll").toUriString());
        //return ResponseEntity.ok().body( userservice.ajouter(us));
        collService.addroletoColl(roletoColl.getUsername(),roletoColl.getRolename());
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('Role_ADMIN')")
    @GetMapping("/refreshtoken")
    public void refreshtoken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("b ")){

            try {
                String refresh_token= authorizationHeader.substring("b ".length());
                Algorithm algorithm=Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT=verifier.verify(refresh_token);
                //gestion des authorizations
                String username=decodedJWT.getSubject();
                Collaborateurs collaborateurs = collService.getuser(username);


                String access_token = JWT.create()
                        .withSubject(collaborateurs.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles",collaborateurs.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String,String> tokens=new HashMap<>();
                tokens.put("access_token",access_token);
                tokens.put("refresh_token",refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),tokens);
            } catch (Exception e){
                //log.error("error loggingg",e.getMessage());
                response.setHeader("error",e.getMessage());
                response.setStatus(FORBIDDEN.value());

                //response.sendError(FORBIDDEN.value());
                Map<String,String> error=new HashMap<>();
                error.put("error_message",e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),error);
            }

        } else {
            throw new RuntimeException("Refresh token is missing");
        }


    }

    @PreAuthorize("hasRole('Role_USER')  or hasRole('Role_ADMIN')")
    @GetMapping("/coll/all")
    public ResponseEntity<List<Collaborateurs>>getcolls(){

        return  ResponseEntity.ok().body(collService.lister());
    }
    @PreAuthorize("hasRole('Role_ADMIN')")
    @GetMapping("/coll/afficher/{id}")
    public Collaborateurs read(@PathVariable Long id){

        return collService.affichercoll(id);

    }
    @PreAuthorize("hasRole('Role_ADMIN')")
    @GetMapping("/coll/read/{username}")
    public Collaborateurs readbyusername(@PathVariable String username){

        return collService.getuser(username);

    }
    @PreAuthorize("hasRole('Role_ADMIN')")
    @PostMapping("/coll/update/{id}")
    public  Collaborateurs update(@RequestBody  Collaborateurs coll,@PathVariable Long id){
        return  collService.modifiercoll(coll,id);
    }
}


@Data
class RoletoColl{
    private String username;
    private String rolename;
}

