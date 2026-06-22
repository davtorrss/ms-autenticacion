package com.vetnova.ms_autenticacion.service;

import com.vetnova.ms_autenticacion.dto.UsuarioRequestDTO;
import com.vetnova.ms_autenticacion.dto.UsuarioResponseDTO;
import com.vetnova.ms_autenticacion.model.Usuario;
import com.vetnova.ms_autenticacion.repository.UsuarioRepository;
import com.vetnova.ms_autenticacion.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDTO requestDTO;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        // Preparamos datos falsos para usar en las pruebas
        requestDTO = new UsuarioRequestDTO();
        requestDTO.setUsername("admin_david");
        requestDTO.setNombre("David Torrealba");
        requestDTO.setEmail("david@vetnova.cl");
        requestDTO.setPassword("Password123!");
        requestDTO.setRol("Administrador");

        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setUsername("admin_david");
        usuarioMock.setNombre("David Torrealba");
        usuarioMock.setEmail("david@vetnova.cl");
        usuarioMock.setPassword("contrasena_encriptada_falsa");
        usuarioMock.setRol("Administrador");
    }

    @Test
    @DisplayName("Registro exitoso: Guarda el usuario con contraseña encriptada")
    void registrarUsuario_Exito() {
        // Arrange: Simulamos que el usuario NO existe en la BD
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        // Simulamos la encriptación
        when(passwordEncoder.encode(anyString())).thenReturn("contrasena_encriptada_falsa");
        // Simulamos el guardado en la BD
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // Act: Ejecutamos el método
        UsuarioResponseDTO response = usuarioService.registrarUsuario(requestDTO);

        // Assert: Verificamos que todo salió bien
        assertNotNull(response);
        assertEquals("admin_david", response.getUsername());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registro fallido: Lanza error si el username ya existe")
    void registrarUsuario_UsuarioYaExiste() {
        // Arrange: Simulamos que la BD dice "Sí, ya existe"
        when(usuarioRepository.existsByUsername("admin_david")).thenReturn(true);

        // Act & Assert: Verificamos que el sistema explote con el error correcto
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrarUsuario(requestDTO);
        });
        assertEquals("El usuario ya existe", exception.getMessage());
    }

    @Test
    @DisplayName("Login exitoso: Retorna el token JWT real")
    void login_Exito() {
        // Arrange: Simulamos que encontramos al usuario
        when(usuarioRepository.findByUsername("admin_david")).thenReturn(Optional.of(usuarioMock));
        // Simulamos que la contraseña hace match
        when(passwordEncoder.matches("Password123!", "contrasena_encriptada_falsa")).thenReturn(true);
        // Simulamos la fábrica de tokens
        when(jwtUtil.generateToken("admin_david", "Administrador")).thenReturn("token_falso_vip");

        // Act
        String tokenRespuesta = usuarioService.login("admin_david", "Password123!");

        // Assert
        assertEquals("token_falso_vip", tokenRespuesta);
    }

    @Test
    @DisplayName("Login fallido: Credenciales incorrectas")
    void login_CredencialesIncorrectas() {
        // Arrange: Encontramos al usuario, pero la contraseña no hace match
        when(usuarioRepository.findByUsername("admin_david")).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches("ClaveMala!", "contrasena_encriptada_falsa")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.login("admin_david", "ClaveMala!");
        });
        assertEquals("Credenciales incorrectas", exception.getMessage());
    }
}