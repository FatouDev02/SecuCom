package com.example.SecuCom.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

import static javax.persistence.FetchType.EAGER;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Collaborateurs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    private String prenom;
    private String username;
    private String mail;
    private String password;

    @ManyToMany(fetch = EAGER)
    private Collection<Role> roles= new ArrayList<>();

}
