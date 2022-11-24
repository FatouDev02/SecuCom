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


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // super.configure(auth);
       //  userdetailserv logique pour charger les détails de l'utilisateur par nom ou par e-mail à partir de la base de données
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    //auth et autorisation
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // super.configure(http);
        //processus de filtrage
        CustonAuthentificationFilter custonAuthentificationFilter=new CustonAuthentificationFilter(authenticationManagerBean());
        //setFilterProcessesUrl indique l'URL à laquelle ce filtre répondra.
        custonAuthentificationFilter.setFilterProcessesUrl("/login");


        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeRequests().antMatchers("/login/**","/refreshtoken/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/coll/**").hasAnyAuthority("Role_USER");
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/coll/afficher/**").hasAnyAuthority("Role_USER");
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/coll/addcoll/**").hasAnyAuthority("Role_ADMIN");
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .oauth2Login();

               /* //newwwwwww
                .and().formLogin().loginPage("/login").defaultSuccessUrl("/welcom").failureUrl("/login?error=true").permitAll()
                        .and().logout().deleteCookies("JSESSIONID").logoutUrl("/logout").logoutSuccessUrl("/login");
*/
        //http.authorizeRequests().anyRequest().permitAll();
        // http.addFilter(new CustonAuthentificationFilter(authenticationManagerBean()));
        http.addFilter(custonAuthentificationFilter);
        // UsernamePasswordAuthenticationFilter Traite une soumission de formulaire d'authentification
        http.addFilterBefore(new customAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }


    @Override
    @Bean
    //authmanager pour traiter les jetons de demande d'authentification créés par l'implémentation des classes.
    public AuthenticationManager authenticationManagerBean() throws  Exception{
        return super.authenticationManagerBean();
    }

}
