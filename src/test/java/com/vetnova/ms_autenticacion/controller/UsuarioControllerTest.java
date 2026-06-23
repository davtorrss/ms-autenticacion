package com.vetnova.ms_autenticacion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.ms_autenticacion.dto.UsuarioRequestDTO;
import com.vetnova.ms_autenticacion.dto.UsuarioResponseDTO;
import com.vetnova.ms_autenticacion.security.JwtFilter;
import com.vetnova.ms_autenticacion.security.JwtUtil;
import com.vetnova.ms_autenticacion.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtFilter jwtFilter;

    private UsuarioRequestDTO requestDTO;
    private UsuarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new UsuarioRequestDTO();
        requestDTO.setUsername("admin_test");
        requestDTO.setNombre("Test User");
        requestDTO.setEmail("test@vetnova.cl");
        requestDTO.setPassword("Password123!");
        requestDTO.setRol("Administrador");

        responseDTO = new UsuarioResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setUsername("admin_test");
        responseDTO.setNombre("Test User");
        responseDTO.setEmail("test@vetnova.cl");
        responseDTO.setRol("Administrador");
    }

    @Test
    @DisplayName("POST /api/auth/registro - Retorna 201 Created")
    void registrar_Retorna201() throws Exception {
        when(usuarioService.registrarUsuario(any(UsuarioRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuario registrado exitosamente"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Retorna el token JWT y 200 OK")
    void login_Retorna200() throws Exception {
        when(usuarioService.login(anyString(), anyString())).thenReturn("eyTokenFalso.123.ABC");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("eyTokenFalso.123.ABC"));
    }

    @Test
    @DisplayName("GET /api/auth/lista - Retorna lista de usuarios")
    void listar_Retorna200() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/auth/lista")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin_test"));
    }

    @Test
    @DisplayName("GET /api/auth/{id} - Busca por ID y retorna 200 OK")
    void buscarPorId_Retorna200() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/auth/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Test User"));
    }

    @Test
    @DisplayName("PUT /api/auth/{id} - Actualiza y retorna 200 OK")
    void actualizar_Retorna200() throws Exception {
        when(usuarioService.actualizarUsuario(eq(1L), any(UsuarioRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/auth/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Test User"));
    }

    @Test
    @DisplayName("DELETE /api/auth/eliminar/{id} - Elimina y retorna 200 OK")
    void eliminar_Retorna200() throws Exception {
        mockMvc.perform(delete("/api/auth/eliminar/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario eliminado correctamente"));
    }
}