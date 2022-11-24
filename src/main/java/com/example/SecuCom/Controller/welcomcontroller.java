package com.example.SecuCom.Controller;


import aj.org.objectweb.asm.ConstantDynamic;
import com.example.SecuCom.Repository.CollRepo;
import com.example.SecuCom.Service.CollService;
import com.example.SecuCom.models.Collaborateurs;
import com.example.SecuCom.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@CrossOrigin()
@Controller
@RequestMapping("/api")
public class welcomcontroller {

    @Autowired
    CollService collService;
    @Autowired
    CollRepo collRepo;
    @Autowired
    private AuthenticationManager authenticationManager;


    @PreAuthorize("hasRole('Role_USER')  or hasRole('Role_ADMIN')")
    @GetMapping("/login")
    public ResponseEntity<?> authenticateUser(Principal c){


       // return new ResponseEntity<>("bienvenue"+ collService.getuser((c.getName())).getRoles(), HttpStatus.OK);

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(collService.getuser((c.getName())).getUsername(),collService.getuser((c.getName())).getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user= (User)authentication.getPrincipal();
        List<String> roles = user.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        List<String> entite= new ArrayList<>(); entite.add("Role_USER");
        if(roles.equals(entite)){
            return ResponseEntity.ok().body("Bienvenu User");
        }else{
            return ResponseEntity.ok().body("Bienvenu admin");

        }

    }

    @PreAuthorize("hasRole('Role_ADMIN')")
     @GetMapping("welcomadmin")
    public String welcomAdmin(String usernameoremail) {

        Collaborateurs collaborateurs = (Collaborateurs) collService.findByUsernameOrEmail(usernameoremail).getRoles();
         return "Bienvenue ADMIN";
    }
    @RolesAllowed("Role_USER")
    @RequestMapping("/**")
    public String getUser()
    {
        return "Welcome User";
    }

    @RolesAllowed({"Role_ADMIN"})
    @RequestMapping("/admin")
    public String getAdmin()
    {
        return "Welcome Admin";
    }

    @RequestMapping("/*")
    public String getGithub()
    {
        return "Welcome Github user!";
    }

}
