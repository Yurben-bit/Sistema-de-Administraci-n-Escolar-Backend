# Guía Técnica: Sistema de Pagos con PayPal (EduTec)

Este documento explica cómo utilizar y probar el módulo de pagos integrado en el backend de EduTec.

## 🚀 Cómo funciona el Flujo de Pago

Hemos implementado un flujo basado en sesiones para garantizar la seguridad y el seguimiento de cada transacción.

### 1. Iniciar Sesión de Pago
Antes de crear una orden en PayPal, el frontend debe solicitar una sesión de pago. Esto calcula cuánto se aplicará a la deuda actual y cuánto quedará como saldo a favor.

*   **Endpoint:** `POST /api/v1/payment/init-session`
*   **Parámetros:** `idAlumno` (UUID), `montoAbono` (en el cuerpo del JSON).
*   **Respuesta:** Retorna un `sessionToken` único y el monto distribuido.

### 2. Crear Orden en PayPal
Con el `sessionToken`, se genera el link de pago oficial de PayPal.

*   **Endpoint:** `POST /api/v1/payment/create-order`
*   **Parámetro:** `sessionToken` (UUID).
*   **Respuesta:** Retorna el `id` de la orden de PayPal y los links (`approve`) necesarios para el frontend.

### 3. Capturar y Procesar Pago
Una vez que el usuario aprueba el pago en la interfaz de PayPal, el sistema debe "capturar" el dinero y actualizar la base de datos de la escuela.

*   **Endpoint:** `GET /api/v1/payment/capture`
*   **Parámetro:** `orderId` (obtenido de PayPal después de la aprobación).
*   **Efectos:**
    *   Actualiza el saldo de la cuenta del alumno.
    *   Registra el movimiento en `MovimientoCuenta`.
    *   Marca la `Transaccion` como completada.

---

## 📊 Lógica de Distribución de Dinero

El sistema es "inteligente":
*   Si un alumno debe $500 y paga $600:
    *   $500 se aplican para saldar su deuda.
    *   $100 se guardan en su **Saldo a Favor**.
*   Si en el futuro se genera un nuevo cargo (ej: colegiatura), el sistema buscará primero en el **Saldo a Favor** antes de aumentar la deuda.

---

## 🛠️ Configuración (application.properties)

Para que funcione, asegúrate de tener estas propiedades configuradas con tus credenciales de [PayPal Developer](https://developer.paypal.com/):

```properties
paypal.client.id=TU_CLIENT_ID
paypal.client.secret=TU_CLIENT_ID_SECRET
paypal.mode=sandbox
```

---

## 📋 Endpoints Rápidos (API Reference)

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/api/v1/payment/init-session` | Inicia la sesión de pago. |
| `POST` | `/api/v1/payment/create-order` | Genera la orden de PayPal. |
| `GET` | `/api/v1/payment/capture` | Confirma el pago y actualiza saldos. |
| `GET` | `/api/v1/payment/estado-cuenta` | Consulta el saldo y movimientos del alumno. |
