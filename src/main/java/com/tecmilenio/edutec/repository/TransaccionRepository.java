package com.tecmilenio.edutec.repository;

import com.tecmilenio.edutec.model.Transaccion;
import com.tecmilenio.edutec.model.Transaccion.EstatusTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransaccionRepository
        extends JpaRepository<Transaccion, UUID> {

    // Usado por el webhook para encontrar la transacción correspondiente.
    Optional<Transaccion> findByPaypalOrderId(String paypalOrderId);

    // Usado para validar el token de sesión antes de crear la orden en PayPal.
    Optional<Transaccion> findByPaymentSessionToken(UUID paymentSessionToken);

    // Verifica si el alumno ya tiene un pago activo antes de crear una sesión
    // nueva.
    Optional<Transaccion> findByIdAlumnoAndEstatus(
            UUID idAlumno, EstatusTransaccion estatus);
}