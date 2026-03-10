package com.tecmilenio.edutec.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "usuarios")
@Data // Genera Getters y Setters
@NoArgsConstructor // Constructor sin argumentos (Obligatorio para JPA)
@AllArgsConstructor // Constructor con todos los argumentos
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;
}