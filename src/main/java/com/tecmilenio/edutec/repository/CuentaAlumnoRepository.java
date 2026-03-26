package com.tecmilenio.edutec.repository;

import com.tecmilenio.edutec.model.CuentaAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CuentaAlumnoRepository extends JpaRepository<CuentaAlumno, UUID> {

    // Lectura simple para consultas que no modifican saldos.
    Optional<CuentaAlumno> findByIdAlumno(UUID idAlumno);

    // Lectura con bloqueo exclusivo para operaciones de escritura.
    // Mientras esta transacción no termine, ninguna otra puede
    // leer este registro con intención de escribirlo.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CuentaAlumno c WHERE c.idAlumno = :idAlumno")
    Optional<CuentaAlumno> findByIdAlumnoParaEscritura(
            @Param("idAlumno") UUID idAlumno);
}