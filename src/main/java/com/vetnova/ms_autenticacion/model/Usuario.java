package com.vetnova.ms_autenticacion.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Column(unique = true) 
    private String username;

    @NotBlank(message = "El nombre no puede estar vacío ni contener solo espacios")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ingresar un formato de email válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotBlank(message = "Debe asignar un rol (ej: ADMIN, VET)")
    private String rol;
}