package com.osvaldo.ldapxml.controller;

import com.osvaldo.ldapxml.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/importar")
    public ResponseEntity<String> importar(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("usuarios", ".xml");
            file.transferTo(tempFile);

            usuarioService.importarUsuarios(tempFile);

            return ResponseEntity.ok("Usuários importados com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }
}
