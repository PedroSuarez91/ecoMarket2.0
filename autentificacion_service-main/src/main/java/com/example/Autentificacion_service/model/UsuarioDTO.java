package com.example.Autentificacion_service.model;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String emailUsuario; // Adaptado según el diagrama de clases
    private String password;
    private String rol;
}