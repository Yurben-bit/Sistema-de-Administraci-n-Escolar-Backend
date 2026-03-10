package com.tecmilenio.edutec.controllers.payment;

import com.tecmilenio.edutec.dtos.request.payment.PaymentRequestDTO;
import com.tecmilenio.edutec.services.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * PASO 1 DEL FLUJO: El frontend solicita crear una orden de pago.
     *
     * SEGURIDAD: El frontend SOLO envía el UUID de la deuda.
     * El monto NUNCA viaja desde el cliente. El backend lo consulta
     * directamente en BD usando ese UUID.
     */
    @PostMapping("/create-order")
    public ResponseEntity<String> createOrder(@RequestBody PaymentRequestDTO request) {
        String orderId = paymentService.createOrder(request.getIdDeuda());
        return ResponseEntity.ok(orderId);
    }

    /**
     * PASO 2 DEL FLUJO: El frontend notifica que el usuario aprobó el pago
     * en el popup de PayPal.
     *
     * SEGURIDAD: El backend NO confía en que el pago fue exitoso solo porque
     * el frontend lo dice. Aquí se hace la captura real y verificación contra BD.
     */
    @PostMapping("/capture-order/{orderId}")
    public ResponseEntity<String> captureOrder(@PathVariable String orderId) {
        String resultado = paymentService.captureOrder(orderId);
        return ResponseEntity.ok(resultado);
    }
}