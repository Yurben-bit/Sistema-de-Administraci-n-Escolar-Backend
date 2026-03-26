package com.tecmilenio.edutec.repositories.payment;

import com.tecmilenio.edutec.models.payment.MovimientoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovimientoCuentaRepository
        extends JpaRepository<MovimientoCuenta, UUID> {

    // Historial completo del alumno, del más reciente al más antiguo.
    List<MovimientoCuenta> findAllByIdAlumnoOrderByFechaDesc(UUID idAlumno);
}