package com.tecmilenio.edutec.controllers.payment;

import com.tecmilenio.edutec.models.payment.MovimientoCuenta.ConceptoCargo;
import com.tecmilenio.edutec.services.payment.PaymentService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private PaymentService paymentService;

    // Genera un cargo al alumno y aplica saldo a favor automáticamente.
    @PostMapping("/cuenta/cargo")
    public ResponseEntity<String> crearCargo(
            @Valid @RequestBody CargoRequestDTO request) {

        paymentService.crearCargo(
                request.getIdAlumno(),
                request.getConcepto(),
                request.getMonto());
        return ResponseEntity.ok("Cargo registrado correctamente.");
    }

    // ── DTO interno ───────────────────────────────────────────────────────────

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CargoRequestDTO {

        @NotNull(message = "El idAlumno es obligatorio.")
        private UUID idAlumno;

        @NotNull(message = "El concepto es obligatorio.")
        private ConceptoCargo concepto;

        @NotNull(message = "El monto es obligatorio.")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero.")
        @Digits(integer = 7, fraction = 2, message = "El monto no puede tener más de dos decimales.")
        private BigDecimal monto;
    }
}