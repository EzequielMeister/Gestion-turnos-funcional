package com.ezequiel.gestion_turnos.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/me")
    public Map<String, Object> usuarioActual(@AuthenticationPrincipal UserDetails user) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("username", user.getUsername());
        resp.put("rol", user.getAuthorities().stream().findFirst().get().getAuthority().replace("ROLE_", "").toLowerCase());
        return resp;
    }
}
