package org.learncode.aama.Dao;

import org.learncode.aama.entites.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<Users,Long> {
    Users findUsersByName(String name);


    String name(String name);

    Double countUsersByRole(String role);

    List<Users> findByRole(String user);

    Users getUsersByUserID(Long userID);
}
