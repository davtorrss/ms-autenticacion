package com.vetnova.ms_autenticacion.dto;

import lombok.Data;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private String nombre;
    private String email;
    private String rol;
}