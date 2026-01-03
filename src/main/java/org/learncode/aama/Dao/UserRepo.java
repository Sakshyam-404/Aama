package org.learncode.aama.Dao;

import org.learncode.aama.entites.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<Users,Long> {
    Users findUsersByName(String name);



}
