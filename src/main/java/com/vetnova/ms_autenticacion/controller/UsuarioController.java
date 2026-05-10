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
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody Usuario u) {
        
        Usuario nuevo = usuarioService.guardarUsuario(u);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> lista = usuarioService.obtenerTodos();
        return ResponseEntity.ok(lista);
    }
}