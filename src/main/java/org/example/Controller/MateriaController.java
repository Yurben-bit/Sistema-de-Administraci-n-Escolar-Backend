package org.example.Controller;

import org.example.model.Materia;
import org.example.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica que esta clase es un punto de acceso para la web
@RequestMapping("/api/materias") // La dirección URL para entrar aquí será localhost:8080/api/materias
public class MateriaController {

    @Autowired // Conecta automáticamente con el repositorio que creamos antes
    private MateriaRepository materiaRepository;

    @GetMapping // Cuando alguien entre a la URL, le damos la lista de materias
    public List<Materia> obtenerTodas() {
        return materiaRepository.findAll();
    }

    @PostMapping // Cuando alguien envíe datos, los guardamos en la base de datos
    public Materia crearMateria(@RequestBody Materia materia) {
        return materiaRepository.save(materia);
    }
}