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

    
    public Usuario guardarUsuario(Usuario usuario) {
       
        return usuarioRepository.save(usuario);
    }

   
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }
}