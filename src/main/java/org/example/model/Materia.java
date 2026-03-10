package org.example.model;

// Importa las herramientas para crear tablas

import lombok.Data; // Importa el asistente para no escribir Getters y Setters

import javax.persistence.*;

@Entity // Le dice a Spring que esto es una tabla de Base de Datos
@Table(name = "materias") // Nombre que tendrá la tabla en MySQL
@Data // Genera automáticamente los métodos para leer/escribir datos
public class Materia {

    @Id // Marca este campo como el ID único (llave primaria)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Hace que el ID sea 1, 2, 3... automático
    private Long id;

    private String nombre; // Nombre de la asignatura

    private Integer cupo; // Cantidad máxima de alumnos

    private String profesor; // Nombre del docente asignado
}