package com.example.Autentificacion_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Autentificacion_service.model.Autentificacion;
import com.example.Autentificacion_service.service.AutentificacionService;

@RestController
@RequestMapping("api/v1/autentificacion")
public class AutentificacionController {
    @Autowired
    private AutentificacionService autentificacionService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Autentificacion autentificacion) {
        try {
            String respuesta = autentificacionService.login(autentificacion);
            return new ResponseEntity<>(respuesta, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error en el servidor", HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarLogins() {
        List<Autentificacion> logins = autentificacionService.listarLogins();
        if (logins.isEmpty()) {
            return new ResponseEntity<>("No existen registros de login", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(logins, HttpStatus.OK);
    }
}