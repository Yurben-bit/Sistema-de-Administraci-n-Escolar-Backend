package com.tecmilenio.edutec.controller;

import com.tecmilenio.edutec.dto.request.PaymentRequestDTO;
import com.tecmilenio.edutec.dto.response.*;
import com.tecmilenio.edutec.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/init-session")
    public ResponseEntity<InitSessionResponseDTO> initSession(
            @RequestParam UUID idAlumno,
            @Valid @RequestBody PaymentRequestDTO request) {
        return ResponseEntity.ok(paymentService.iniciarSesionPago(idAlumno, request.getMontoAbono()));
    }

    @PostMapping("/create-order")
    public ResponseEntity<PayPalOrderResponseDTO> createOrder(@RequestParam UUID sessionToken) {
        return ResponseEntity.ok(paymentService.crearOrdenPayPal(sessionToken));
    }

    @GetMapping("/capture")
    public ResponseEntity<PagoResponseDTO> captureOrder(@RequestParam String orderId) {
        return ResponseEntity.ok(paymentService.procesarPagoExitoso(orderId));
    }

    @GetMapping("/estado-cuenta")
    public ResponseEntity<EstadoCuentaDTO> getEstadoCuenta(@RequestParam UUID idAlumno) {
        return ResponseEntity.ok(paymentService.obtenerEstadoCuenta(idAlumno));
    }
}
