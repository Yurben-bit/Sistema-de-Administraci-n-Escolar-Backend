package com.tecmilenio.edutec.repository;

import com.tecmilenio.edutec.model.MovimientoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovimientoCuentaRepository extends JpaRepository<MovimientoCuenta, UUID> {
    List<MovimientoCuenta> findByIdAlumnoOrderByFechaDesc(UUID idAlumno);
}