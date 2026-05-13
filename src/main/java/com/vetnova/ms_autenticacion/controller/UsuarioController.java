package com.vetnova.ms_autenticacion.controller;

import com.vetnova.ms_autenticacion.model.Usuario;
import com.vetnova.ms_autenticacion.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;


   @PostMapping("/registro")
public ResponseEntity<?> registrar(@Valid @RequestBody Usuario usuario) {
    Usuario nuevo;
    try {
        nuevo = usuarioService.registrarUsuario(usuario);
    } catch (Exception e) {
        return new ResponseEntity<>(
            "Error: El nombre de usuario o el correo ya existen en nuestra base de datos.", 
            HttpStatus.CONFLICT
        );
    }
    return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
}
    // Listar Usuarios
    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    // Actualizar Usuario
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuario));
    }

    // Eliminar Usuario
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok("Usuario con ID " + id + " eliminado correctamente.");
    }
}