package com.vetnova.ms_autenticacion.service;

import com.vetnova.ms_autenticacion.dto.UsuarioRequestDTO;
import com.vetnova.ms_autenticacion.dto.UsuarioResponseDTO;
import com.vetnova.ms_autenticacion.model.Usuario;
import com.vetnova.ms_autenticacion.repository.UsuarioRepository;
import com.vetnova.ms_autenticacion.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El usuario ya existe");
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(dto.getRol());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        return convertirAResponse(usuarioRepository.save(usuario));
    }

    public String login(String username, String rawPassword) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (passwordEncoder.matches(rawPassword, usuario.getPassword())) {
            return jwtUtil.generateToken(usuario.getUsername(), usuario.getRol());
        } else {
            throw new RuntimeException("Credenciales incorrectas");
        }
    }

    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(this::convertirAResponse).collect(Collectors.toList());
    }

    // NUEVO MÉTODO: Buscar por ID
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertirAResponse(usuario);
    }

    // NUEVO MÉTODO: Actualizar Usuario
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getUsername().equals(dto.getUsername()) && usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El nuevo nombre de usuario ya está en uso");
        }
        
        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El nuevo email ya está registrado");
        }

        usuario.setUsername(dto.getUsername());
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(dto.getRol());
        
        // Solo actualiza la contraseña si viene en el DTO
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return convertirAResponse(usuarioRepository.save(usuario));
    }

    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado, no se puede eliminar");
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioResponseDTO convertirAResponse(Usuario u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRol());
        return dto;
    }
}