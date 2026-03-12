package org.example.repository;

import org.example.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository; // Importa funciones de guardado
import org.springframework.stereotype.Repository;

@Repository // Indica que esta clase maneja la comunicación con la BD
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    // Aquí ya tenemos métodos como .save() o .findAll() heredados
}