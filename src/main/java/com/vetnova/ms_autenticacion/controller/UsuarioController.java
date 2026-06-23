package com.vetnova.ms_autenticacion.controller;

import com.vetnova.ms_autenticacion.dto.LoginRequestDTO;
import com.vetnova.ms_autenticacion.dto.UsuarioRequestDTO;
import com.vetnova.ms_autenticacion.dto.UsuarioResponseDTO;
import com.vetnova.ms_autenticacion.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@Valid @RequestBody UsuarioRequestDTO dto) {
        usuarioService.registrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente");
    }

   
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO dto) {
        String token = usuarioService.login(dto.getUsername(), dto.getPassword());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/lista")
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }
}