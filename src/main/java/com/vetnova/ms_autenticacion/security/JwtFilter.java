package com.vetnova.ms_autenticacion.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Obtener la cabecera "Authorization" de la petición HTTP
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Revisar si la cabecera existe y empieza con "Bearer " (el estándar de JWT)
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // Quitamos la palabra "Bearer " para dejar solo el token
            try {
                username = jwtUtil.extractUsername(token); // Intentamos sacar el nombre del usuario
            } catch (Exception e) {
                // Si falla (token expirado o falso), el username queda nulo y no entra
                System.out.println("Token inválido o expirado");
            }
        }

        // 3. Si encontramos un usuario y aún no está logueado en el contexto de Spring...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 4. Validamos matemáticamente el token con nuestra clave secreta
            if (jwtUtil.validateToken(token)) {
                // 5. Le damos el "Pase VIP" a Spring Security para que lo deje pasar
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 6. Continúa el viaje hacia el Controlador (o lo bloquea si no tuvo el pase)
        filterChain.doFilter(request, response);
    }
}