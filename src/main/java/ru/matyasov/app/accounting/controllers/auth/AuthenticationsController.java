package ru.matyasov.app.accounting.controllers.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.matyasov.app.accounting.services.auth.AuthenticationsService;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationsController {

    private final AuthenticationsService service;

    @Autowired
    public AuthenticationsController(AuthenticationsService service) {
        this.service = service;
    }

    @GetMapping("/check-authentication")
    public ResponseEntity<Map<String, Boolean>> checkAuthentication(Principal principal) {

        final Map<String, Boolean> result = new HashMap<>();

        if (principal == null) {
            result.put("isAuthenticationNeeded", true);
        } else {
            result.put("isAuthenticationNeeded", false);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {

        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {

        return ResponseEntity.ok(service.authenticate(request));
    }
}
