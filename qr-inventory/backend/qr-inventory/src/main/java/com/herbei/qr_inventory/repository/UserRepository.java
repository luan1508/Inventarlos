package com.herbei.qr_inventory.repository;

import com.herbei.qr_inventory.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> 
{
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
