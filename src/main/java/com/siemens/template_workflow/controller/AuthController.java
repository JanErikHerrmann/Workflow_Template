package com.siemens.template_workflow.controller;

import com.siemens.template_workflow.exception.ResourceNotFoundException;
import com.siemens.template_workflow.model.Employee;
import com.siemens.template_workflow.repository.EmployeeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final EmployeeRepository repo;
    private final PasswordEncoder passwordEncoder;

    public AuthController(EmployeeRepository repo, PasswordEncoder passwordEncoder) { this.repo = repo; this.passwordEncoder = passwordEncoder; }

    record RegisterRequest(String username, String password, String email, String role) {}
    record LoginRequest(String username, String password) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        // basic validation
        if (req.username() == null || req.username().isBlank()) return ResponseEntity.badRequest().body(Map.of("error","username required"));
        if (req.password() == null || req.password().length() < 6) return ResponseEntity.badRequest().body(Map.of("error","password too short"));
        if (req.email() == null || req.email().isBlank()) return ResponseEntity.badRequest().body(Map.of("error","email required"));
        if (repo.findByUsername(req.username()).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","username taken"));
        if (repo.findByEmail(req.email()).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","email taken"));

        Employee e = new Employee();
        e.setUsername(req.username());
        e.setPasswordHash(passwordEncoder.encode(req.password()));
        e.setEmail(req.email());
        e.setRole(req.role() == null ? "staff" : req.role());
        e.setCreatedAt(Instant.now()); e.setUpdatedAt(Instant.now());
        Employee saved = repo.save(e);
        return ResponseEntity.ok(Map.of("id", saved.getId(), "username", saved.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpServletRequest request) {
        Employee e = repo.findByUsername(req.username()).orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));
        if (!passwordEncoder.matches(req.password(), e.getPasswordHash())) throw new ResourceNotFoundException("Invalid username or password");
        // create session
        HttpSession session = request.getSession(true);
        session.setAttribute("employeeId", e.getId());
        session.setAttribute("username", e.getUsername());
        session.setMaxInactiveInterval(30 * 60); // 30 minutes
        return ResponseEntity.ok(Map.of("message","logged in", "username", e.getUsername()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        if (s != null) s.invalidate();
        return ResponseEntity.ok(Map.of("message","logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        if (s == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","not logged in"));
        Object id = s.getAttribute("employeeId");
        Object username = s.getAttribute("username");
        return ResponseEntity.ok(Map.of("id", id, "username", username));
    }
}


