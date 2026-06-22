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
    private JwtUtil jwtUtil; // Inyectamos nuestra fábrica de tokens

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

    // AHORA DEVUELVE UN STRING (EL TOKEN) EN LUGAR DE UN BOOLEAN
    public String login(String username, String rawPassword) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Compara la contraseña encriptada
        if (passwordEncoder.matches(rawPassword, usuario.getPassword())) {
            // Si todo está bien, genera y devuelve el Token VIP
            return jwtUtil.generateToken(usuario.getUsername(), usuario.getRol());
        } else {
            throw new RuntimeException("Credenciales incorrectas");
        }
    }

    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado, no se puede eliminar");
        }
        usuarioRepository.deleteById(id);
    }

    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(this::convertirAResponse).collect(Collectors.toList());
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