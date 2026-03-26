package com.tecmilenio.edutec.controllers.payment;

import com.tecmilenio.edutec.dtos.request.payment.PaymentRequestDTO;
import com.tecmilenio.edutec.dtos.response.payment.*;
import com.tecmilenio.edutec.services.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Paso 1: el alumno indica cuánto quiere pagar.
    @PostMapping("/init-session")
    public ResponseEntity<InitSessionResponseDTO> initSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentRequestDTO request) {

        UUID idAlumno = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(
                paymentService.initSession(idAlumno, request.getMontoAbono()));
    }

    // Paso 2: el backend crea la orden en PayPal y devuelve el orderId.
    @PostMapping("/create-order/{sessionToken}")
    public ResponseEntity<String> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID sessionToken) {

        UUID idAlumno = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(
                paymentService.createOrder(idAlumno, sessionToken));
    }

    // Webhook: PayPal notifica que el pago fue procesado en sus servidores.
    // En producción este endpoint debe verificar la firma del webhook de PayPal
    // usando el header PAYPAL-TRANSMISSION-SIG antes de procesar el payload.
    @PostMapping("/webhook/capture")
    public ResponseEntity<Void> webhookCaptura(@RequestBody String payload) {
        String orderId = extraerOrderId(payload);
        paymentService.procesarWebhookCaptura(orderId);
        return ResponseEntity.ok().build();
    }

    // El frontend consulta el resultado para mostrar el recibo al alumno.
    @GetMapping("/confirmar/{orderId}")
    public ResponseEntity<PagoResponseDTO> confirmarPago(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String orderId) {

        UUID idAlumno = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(
                paymentService.confirmarPago(idAlumno, orderId));
    }

    // Estado de cuenta: saldos e historial de movimientos del alumno.
    @GetMapping("/estado-cuenta")
    public ResponseEntity<EstadoCuentaDTO> getEstadoCuenta(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID idAlumno = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(paymentService.getEstadoCuenta(idAlumno));
    }

    // ── Utilidad privada ──────────────────────────────────────────────────────

    // Extrae el orderId del payload JSON del webhook.
    // En producción usar ObjectMapper para un parsing robusto.
    private String extraerOrderId(String payload) {
        int inicio = payload.indexOf("\"id\":\"") + 6;
        int fin = payload.indexOf("\"", inicio);
        return payload.substring(inicio, fin);
    }
}
