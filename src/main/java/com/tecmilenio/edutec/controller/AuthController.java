package com.tecmilenio.edutec.controller;

import com.tecmilenio.edutec.dto.LoginRequest;
import com.tecmilenio.edutec.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController // Indica que esta clase responderá con datos (JSON) y no con páginas HTML.
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private JwtService jwtService;

    // Cuando alguien envíe sus datos a /auth/login, el método se activa.
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        return jwtService.generateToken(loginRequest.getUsername()); // el controlador le pide al servicio que genere el
                                                                     // token
    }
}
