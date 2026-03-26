package com.tecmilenio.edutec.controller;

import com.tecmilenio.edutec.model.MovimientoCuenta.ConceptoCargo;
import com.tecmilenio.edutec.services.PaymentService;
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

    @PostMapping("/cuenta/cargo")
    public ResponseEntity<String> crearCargo(@Valid @RequestBody CargoRequestDTO request) {
        paymentService.crearCargo(
                request.getIdAlumno(),
                request.getConcepto(),
                request.getMonto());
        return ResponseEntity.ok("Cargo registrado correctamente.");
    }

    public static class CargoRequestDTO {
        @NotNull private UUID idAlumno;
        @NotNull private ConceptoCargo concepto;
        @NotNull @DecimalMin("0.01") @Digits(integer = 7, fraction = 2) private BigDecimal monto;

        public CargoRequestDTO() {}
        public UUID getIdAlumno() { return idAlumno; }
        public void setIdAlumno(UUID idAlumno) { this.idAlumno = idAlumno; }
        public ConceptoCargo getConcepto() { return concepto; }
        public void setConcepto(ConceptoCargo concepto) { this.concepto = concepto; }
        public BigDecimal getMonto() { return monto; }
        public void setMonto(BigDecimal monto) { this.monto = monto; }
    }
}