package com.example.demoauth.repos;


import com.example.demoauth.models.ERole;
import com.example.demoauth.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Все остальное делает магия спринга
 */
@Repository
public interface RoleRepo  extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
