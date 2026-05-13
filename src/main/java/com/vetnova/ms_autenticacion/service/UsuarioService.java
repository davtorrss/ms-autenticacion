package com.vetnova.ms_autenticacion.service;

import com.vetnova.ms_autenticacion.model.Usuario;
import com.vetnova.ms_autenticacion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrarUsuario(Usuario usuario) throws Exception {

        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new Exception("Error: El nombre de usuario '" + usuario.getUsername() + "' ya existe.");
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // MÉTODO ACTUALIZAR
    public Usuario actualizarUsuario(Long id, Usuario datosNuevos) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombre(datosNuevos.getNombre());
            usuario.setEmail(datosNuevos.getEmail());
            usuario.setRol(datosNuevos.getRol());
            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    // MÉTODO ELIMINAR
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: ID " + id + " no existe.");
        }
        usuarioRepository.deleteById(id);
    }
}