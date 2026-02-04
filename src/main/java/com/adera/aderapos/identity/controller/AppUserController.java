package com.adera.aderapos.identity.controller;

import com.adera.aderapos.identity.dtos.AppUserDTO;
import com.adera.aderapos.identity.services.IdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:3000", "https://aderapos.netlify.app/"})
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AppUserController {
    private final IdentityService identityService;

    @PostMapping
    public ResponseEntity<AppUserDTO> createUser(@RequestBody AppUserDTO dto) {
        return ResponseEntity.ok(identityService.createUser(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUserDTO> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(identityService.getUser(id));
    }

    @GetMapping
    public ResponseEntity<List<AppUserDTO>> getAllUsers() {
        return ResponseEntity.ok(identityService.getAllUsers());
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<AppUserDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(identityService.getUserByUsernameDTO(username));
    }
}
