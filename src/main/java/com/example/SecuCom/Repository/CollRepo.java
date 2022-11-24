package com.example.SecuCom.Repository;

import com.example.SecuCom.models.Collaborateurs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CollRepo extends JpaRepository<Collaborateurs,Long> {
    Collaborateurs findByUsername(String username);

    @Query("SELECT c FROM Collaborateurs c WHERE c.username=:usernameOrmail OR c.mail=:usernameOrmail")
    Collaborateurs findByUsernameOrMail(String usernameOrmail);
}
