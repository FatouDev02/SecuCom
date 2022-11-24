package com.example.SecuCom.Service.implementation;

import com.example.SecuCom.Repository.CollRepo;
import com.example.SecuCom.Repository.RoleRepo;
import com.example.SecuCom.Service.CollService;
import com.example.SecuCom.models.Collaborateurs;
import com.example.SecuCom.models.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
//
// @AllArgsConstructor
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CollServiceImpl implements CollService, UserDetailsService {
    // UserDetailsServicelogique pour charger les détails de l'utilisateur par nom ou par e-mail à partir de la base de données
    @Autowired
    CollRepo collRepo;
    @Autowired
    RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Collaborateurs ajoutercoll(Collaborateurs collaborateurs) {
        //comment
        log.info("saving new user {} to database",collaborateurs.getUsername());
        //a l'enregistrement on recupère le passwor et le l'encode
        collaborateurs.setPassword(passwordEncoder.encode(collaborateurs.getPassword()));
        return collRepo.save(collaborateurs);
    }

    @Override
    public Role ajoutrole(Role role) {
        log.info("saving role user {} to database",role.getName());
        return roleRepo.save(role);
    }

    @Override
    public String suppcoll(Long id) {
        this.collRepo.deleteById(id);
        return "utilisateur supprimé";
    }

    @Override
    public Collaborateurs affichercoll(Long id) {

        return collRepo.findById(id).get();
    }

    @Override
    public Collaborateurs modifiercoll(Collaborateurs collaborateurs, Long id) {
        return collRepo.findById(id)
                .map(collaborateurs1 ->{
                    collaborateurs1.setNom(collaborateurs.getNom());
                    collaborateurs1.setPrenom(collaborateurs.getPrenom());
                    return collRepo.save(collaborateurs1);
                }).orElseThrow(()->new RuntimeException("cet utilisateur n'existe pas!!"));

    }



    @Override
    public Collaborateurs getuser(String username) {
        log.info("fecthing user {} ",username);

        return collRepo.findByUsername(username);

    }

    @Override
    public List<Collaborateurs> lister() {
        log.info("fecthing all users {} ");

        return collRepo.findAll();
    }

    @Override
    public Collaborateurs findByUsernameOrEmail(String usernameOrmail) {
        Collaborateurs coll = null;
        try {
            coll = collRepo.findByUsernameOrMail(usernameOrmail);
        } catch (Exception e) {
            throw e;
        }
        return coll;
    }

    @Override
    public void addroletoColl(String username, String roleName) {
        log.info("ADDing role {} to user {}",roleName,username);
        Collaborateurs coll=collRepo.findByUsername(username);
        Role role=roleRepo.findByName(roleName);
        //will executing because we have transactionnal
        coll.getRoles().add(role);

    }

// la  méthode loadUserByUsername(String username)est utilisé  pour rechercher  UserDetails(les details)  pour un  nom d' utilisateur donné
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.trim().isEmpty()) {
            throw new UsernameNotFoundException("username vide");
        }
        //recupere le collaborateurs par son username
        Collaborateurs collaborateurs=collRepo.findByUsername(username);
        if(collaborateurs == null){
            //si le coll n'existe pas retouner cette erreur
            log.error("Utilisateur non trouvé");

            throw new UsernameNotFoundException("Utilisateur non trouvé");
        } else{
            //sinon sil existe retouner ce messsage
            log.info("Utilisateur  trouvé",username);

        }


        //noonnnnnnnnnnnn compris
        Collection<SimpleGrantedAuthority> authorities= new ArrayList<>();
        collaborateurs.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new  org.springframework.security.core.userdetails.User(collaborateurs.getUsername(),collaborateurs.getPassword(),authorities);
        ////////////////////////
        // L'interface UserDetails  représente un objet
        // utilisateur authentifié et Spring Security fournit une implémentation prête à l'emploi de org.......
    }



}
