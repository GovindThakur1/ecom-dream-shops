package com.govind.dreamshops.repository;

import com.govind.dreamshops.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    User getUserByEmail(String email);

    User findByEmail(String email);
}
