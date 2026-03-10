package com.tecmilenio.edutec.repository;

import com.tecmilenio.edutec.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Este método buscará en la tabla 'usuarios' por la columna 'username'
    Optional<User> findByUsername(String username);
}