package com.herbei.qr_inventory.controller;

import com.herbei.qr_inventory.model.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController 
{
    /**
     * Gibt Informationen zum aktuell eingeloggten Benutzer zurück
     */
    @GetMapping("/user-info")
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication authentication) 
    {
        if (authentication == null || !authentication.isAuthenticated()) 
        {
            return ResponseEntity.status(401).build();
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", authentication.getName());
        userInfo.put("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        // Prüfe ob Admin
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        userInfo.put("isAdmin", isAdmin);

        return ResponseEntity.ok(userInfo);
    }

    /**
     * Prüft ob der Benutzer eingeloggt ist
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkAuth(Authentication authentication) 
    {
        Map<String, Boolean> response = new HashMap<>();
        response.put("authenticated", authentication != null && authentication.isAuthenticated());
        return ResponseEntity.ok(response);
    }
}
