package com.osvaldo.ldapxml.controller;

import com.osvaldo.ldapxml.model.Usuario;
import com.osvaldo.ldapxml.service.UsuarioService;
import com.osvaldo.ldapxml.service.XmlUsuarioReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/importar")
    public ResponseEntity<List<Usuario>> importarUsuarios(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            List<Usuario> usuarios = XmlUsuarioReader.lerUsuarios(inputStream);
            return ResponseEntity.ok(usuarios);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
