package com.example.SecuCom.Service;

import com.example.SecuCom.models.Collaborateurs;
import com.example.SecuCom.models.Role;

import java.util.List;
//@ Buider pour un dto sert a mapper un userclass a un userdto "recuperer les inf de userclzasss"
public interface CollService {
    Collaborateurs ajoutercoll(Collaborateurs collaborateurs );
    String suppcoll(Long id);
    Collaborateurs affichercoll(Long id);
    Collaborateurs modifiercoll(Collaborateurs collaborateurs,Long id);
    Role ajoutrole(Role role);
    Collaborateurs getuser(String username);
    List<Collaborateurs> lister();
    Collaborateurs findByUsernameOrEmail(String usernameOrEmail);
    void addroletoColl(String username,String roleName);
}
