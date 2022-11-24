package com.example.SecuCom;

import com.example.SecuCom.Service.CollService;
import com.example.SecuCom.models.Collaborateurs;
import com.example.SecuCom.models.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class SecuComApplication {

	public static void main(String[] args) {

		SpringApplication.run(SecuComApplication.class, args);
	}
	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();

	}

	@Bean
	CommandLineRunner run(CollService collService){
		return args -> {
			//crée des roles des linitialisation de lapp
			collService.ajoutrole(new Role(null,"Role_USER"));
			collService.ajoutrole(new Role(null,"Role_ADMIN"));

			//crée des roles des linitialisation de lapp
			collService.ajoutercoll(new Collaborateurs(null,"coul","fatou","fcusername","fc@gmail.com","fcpassword",new ArrayList<>()));
			collService.ajoutercoll(new Collaborateurs(null,"coul","lyd","lydusername","lyd@gmail.com","lydpassword",new ArrayList<>()));

			//attribuer un role a un user
			collService.addroletoColl("fcusername","Role_ADMIN");
			collService.addroletoColl("fcusername","Role_USER");

			collService.addroletoColl("lydusername","Role_USER");

		};

	}
}
