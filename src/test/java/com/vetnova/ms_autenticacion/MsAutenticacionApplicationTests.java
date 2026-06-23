package com.vetnova.ms_autenticacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetnova.ms_autenticacion.dto.UsuarioRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class MsAutenticacionApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Integración: Flujo completo de Registro y Login exitoso")
    void registroYLogin_FlujoCompleto() throws Exception {
        UsuarioRequestDTO nuevoUsuario = new UsuarioRequestDTO();
        nuevoUsuario.setUsername("doctor_integracion");
        nuevoUsuario.setNombre("Doctor Prueba");
        nuevoUsuario.setEmail("doc@vetnova.cl");
        nuevoUsuario.setPassword("Segura123!");
        nuevoUsuario.setRol("Veterinario");

        mockMvc.perform(post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuario registrado exitosamente"));

        UsuarioRequestDTO loginRequest = new UsuarioRequestDTO();
        loginRequest.setUsername("doctor_integracion");
        loginRequest.setPassword("Segura123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Integración: Falla al registrar un usuario que ya existe")
    void registro_FallaUsuarioExistente() throws Exception {
        UsuarioRequestDTO nuevoUsuario = new UsuarioRequestDTO();
        nuevoUsuario.setUsername("usuario_repetido");
        nuevoUsuario.setNombre("Usuario Repetido");
        nuevoUsuario.setEmail("repetido@vetnova.cl");
        nuevoUsuario.setPassword("Segura123!");
        nuevoUsuario.setRol("Recepcionista");

        // Primer registro pasa bien
        mockMvc.perform(post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isCreated());

        // El segundo registro con el mismo usuario debe fallar
        mockMvc.perform(post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El usuario ya existe"));
    }

    @Test
    @DisplayName("Integración: Falla al hacer login con contraseña incorrecta")
    void login_FallaCredenciales() throws Exception {
        UsuarioRequestDTO nuevoUsuario = new UsuarioRequestDTO();
        nuevoUsuario.setUsername("usuario_login_malo");
        nuevoUsuario.setNombre("Usuario Login Malo");
        nuevoUsuario.setEmail("loginmalo@vetnova.cl");
        nuevoUsuario.setPassword("Segura123!");
        nuevoUsuario.setRol("Recepcionista");

        mockMvc.perform(post("/api/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isCreated());

        UsuarioRequestDTO loginRequest = new UsuarioRequestDTO();
        loginRequest.setUsername("usuario_login_malo");
        loginRequest.setPassword("ClaveEquivocada!"); // Contraseña mala a propósito

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Credenciales incorrectas"));
    }

    @Test
    @DisplayName("Integración: Verifica que el método main se ejecuta sin lanzar excepciones")
    void main() {
        
        MsAutenticacionApplication.main(new String[] {});
    }
}