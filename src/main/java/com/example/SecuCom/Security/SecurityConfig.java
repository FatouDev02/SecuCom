package com.example.SecuCom.Security;

import com.example.SecuCom.filter.CustonAuthentificationFilter;
import com.example.SecuCom.filter.customAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
//enableweb est la principale annotation de sécurité Spring utilisée pour activer la sécurité Web dans un projet
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private final UserDetailsService userDetailsService;
// BCryptPasswordEncoder fournie par la sécurité Spring pour chiffrer les mots de passe.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

//AuthenticationManagerBuilder utilisé pour créer un AuthenticationManager.
//AuthenticationManager permet de garder en mémoire les details de lutilisateur qui s'est connecte et gere comment l'authentification doit se passer en ldap jdbc ou
// Permet d'intégrer facilement l'authentification en mémoire,
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // super.configure(auth);
       //  userdetailserv logique pour charger les détails de l'utilisateur par nom ou par e-mail à partir de la base de données
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    //auth et autorisation

    /*configure(HttpSecurity) permet la configuration de la sécurité basée sur le Web à un niveau de ressource,
     basé sur une correspondance de sélection - par ex. L'exemple ci-dessous restreint les URL commençant par /admin/
    Aux utilisateurs ayant rôle ADMIN, et déclare que toutes les autres URL doivent être authentifiées avec succès.*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // super.configure(http);
        //processus de filtrage
        CustonAuthentificationFilter custonAuthentificationFilter=new CustonAuthentificationFilter(authenticationManagerBean());
        //setFilterProcessesUrl indique l'URL à laquelle ce filtre répondra.
        //desactiver parceque 8080 ne s'affiche pas
        //custonAuthentificationFilter.setFilterProcessesUrl("/login");

        // csrf il nécessite qu'un jeton soit inclus dans la demande.
        http.csrf().disable();
        //desactiver parceque github ne se redirigepas vers bienvenu
        //http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeRequests().antMatchers("/login","/refreshtoken/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/coll/**").hasAnyAuthority("Role_USER");
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/coll/afficher/**").hasAnyAuthority("Role_USER");
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/coll/addcoll/**").hasAnyAuthority("Role_ADMIN");
        http.authorizeRequests().anyRequest().authenticated();
        //configurons l'URL de la page de connexion en tant que /login,
        // cela indique à Spring Security d'appeler cette URL avant d'afficher la page de connexion
               http.formLogin();
                http.oauth2Login();

        // http.addFilter(new CustonAuthentificationFilter(authenticationManagerBean()));
        http.addFilter(custonAuthentificationFilter);
        // UsernamePasswordAuthenticationFilter Traite une soumission de formulaire d'authentification
        http.addFilterBefore(new customAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }


    @Override
    @Bean
    //AuthenticationManager est une classe statiquequi gère les modules d’authentification qu’une application utilise.
    //authmanager pour traiter les jetons de demande d'authentification créés par l'implémentation des classes.
    public AuthenticationManager authenticationManagerBean() throws  Exception{
        return super.authenticationManagerBean();
    }

}
