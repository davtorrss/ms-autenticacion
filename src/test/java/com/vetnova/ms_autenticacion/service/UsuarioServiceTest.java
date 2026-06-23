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

import java.util.List;
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
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("contrasena_encriptada_falsa");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        UsuarioResponseDTO response = usuarioService.registrarUsuario(requestDTO);

        assertNotNull(response);
        assertEquals("admin_david", response.getUsername());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registro fallido: Lanza error si el username ya existe")
    void registrarUsuario_UsuarioYaExiste() {
        when(usuarioRepository.existsByUsername("admin_david")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrarUsuario(requestDTO);
        });

        assertEquals("El usuario ya existe", exception.getMessage());
    }

    @Test
    @DisplayName("Registro fallido: Lanza error si el email ya existe")
    void registrarUsuario_EmailYaExiste() {
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrarUsuario(requestDTO);
        });

        assertEquals("El email ya está registrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Login exitoso: Retorna el token JWT real")
    void login_Exito() {
        when(usuarioRepository.findByUsername("admin_david")).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches("Password123!", "contrasena_encriptada_falsa")).thenReturn(true);
        when(jwtUtil.generateToken("admin_david", "Administrador")).thenReturn("token_falso_vip");

        String tokenRespuesta = usuarioService.login("admin_david", "Password123!");

        assertEquals("token_falso_vip", tokenRespuesta);
    }

    @Test
    @DisplayName("Login fallido: Credenciales incorrectas")
    void login_CredencialesIncorrectas() {
        when(usuarioRepository.findByUsername("admin_david")).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches("ClaveMala!", "contrasena_encriptada_falsa")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.login("admin_david", "ClaveMala!");
        });

        assertEquals("Credenciales incorrectas", exception.getMessage());
    }

    @Test
    @DisplayName("Login fallido: Usuario no encontrado")
    void login_UsuarioNoEncontrado() {
        when(usuarioRepository.findByUsername("admin_david")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.login("admin_david", "Password123!");
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Debe listar todos los usuarios")
    void listarUsuarios_Exito() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioMock));

        List<UsuarioResponseDTO> lista = usuarioService.listarUsuarios();

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertEquals("admin_david", lista.get(0).getUsername());
    }

    @Test
    @DisplayName("Debe buscar un usuario por ID exitosamente")
    void buscarPorId_Exito() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        UsuarioResponseDTO response = usuarioService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals("David Torrealba", response.getNombre());
    }

    @Test
    @DisplayName("Buscar por ID fallido: Lanza error si no existe")
    void buscarPorId_NoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.buscarPorId(99L);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Debe actualizar un usuario exitosamente")
    void actualizarUsuario_Exito() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        requestDTO.setNombre("David Actualizado");

        UsuarioResponseDTO response = usuarioService.actualizarUsuario(1L, requestDTO);

        assertNotNull(response);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Actualizar fallido: Usuario no encontrado")
    void actualizarUsuario_NoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarUsuario(99L, requestDTO);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Actualizar fallido: Username ya está en uso por otro")
    void actualizarUsuario_UsernameEnUso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        
        requestDTO.setUsername("otro_admin");
        when(usuarioRepository.existsByUsername("otro_admin")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarUsuario(1L, requestDTO);
        });

        assertEquals("El nuevo nombre de usuario ya está en uso", exception.getMessage());
    }

    @Test
    @DisplayName("Actualizar fallido: Email ya está registrado por otro")
    void actualizarUsuario_EmailEnUso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        
        requestDTO.setUsername("admin_david");
        requestDTO.setEmail("otro_correo@vetnova.cl");
        when(usuarioRepository.existsByEmail("otro_correo@vetnova.cl")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarUsuario(1L, requestDTO);
        });

        assertEquals("El nuevo email ya está registrado", exception.getMessage());
    }

    @Test
    @DisplayName("Actualizar exitoso: Sin cambiar la contraseña")
    void actualizarUsuario_SinCambiarPassword() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);
        
        requestDTO.setPassword("");

        UsuarioResponseDTO response = usuarioService.actualizarUsuario(1L, requestDTO);

        assertNotNull(response);
        verify(passwordEncoder, never()).encode(anyString()); 
    }

    @Test
    @DisplayName("Actualizar exitoso: Cambiando username, email por unos libres y con password null")
    void actualizarUsuario_CambioValidoYPasswordNull() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));
        
        requestDTO.setUsername("nuevo_admin");
        requestDTO.setEmail("nuevo_correo@vetnova.cl");
        requestDTO.setPassword(null);
        
        when(usuarioRepository.existsByUsername("nuevo_admin")).thenReturn(false);
        when(usuarioRepository.existsByEmail("nuevo_correo@vetnova.cl")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        UsuarioResponseDTO response = usuarioService.actualizarUsuario(1L, requestDTO);

        assertNotNull(response);
        verify(usuarioRepository, times(1)).existsByUsername("nuevo_admin");
        verify(usuarioRepository, times(1)).existsByEmail("nuevo_correo@vetnova.cl");
        verify(passwordEncoder, never()).encode(anyString()); 
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe eliminar un usuario exitosamente")
    void eliminarUsuario_Exito() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar fallido: Usuario no encontrado")
    void eliminarUsuario_NoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.eliminarUsuario(99L);
        });

        assertEquals("Usuario no encontrado, no se puede eliminar", exception.getMessage());
        verify(usuarioRepository, never()).deleteById(anyLong());
    }
}