package com.tecmilenio.edutec.controller.auth;

import com.tecmilenio.edutec.dto.LoginRequest;
import com.tecmilenio.edutec.model.User;
import com.tecmilenio.edutec.repository.UserRepository;
import com.tecmilenio.edutec.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController // Indica que esta clase responderá con datos (JSON) y no con páginas HTML.
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectamos el codificador

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        if (userOptional.isPresent()) {
            String passwordEnviado = loginRequest.getPassword();
            String passwordEnDb = userOptional.get().getPassword();

            boolean coinciden = passwordEncoder.matches(passwordEnviado, passwordEnDb);

            if (coinciden) {
                String token = jwtService.generateToken(loginRequest.getUsername());
                return ResponseEntity.ok(token);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos");
    }

}
